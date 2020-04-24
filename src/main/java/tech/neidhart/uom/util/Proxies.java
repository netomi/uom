/*
 * Copyright (c) 2020 Thomas Neidhart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.neidhart.uom.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.Map;
import java.util.stream.Stream;

public final class Proxies {

    // hide constructor.
    private Proxies() {}

    @SuppressWarnings("unchecked")
    public static <T> T delegatingProxy(Object delegate, Class<T> iface, Class<?>... otherIfaces) {
        Class<?>[] ifaces =
                Stream.concat(Stream.of(iface), Stream.of(otherIfaces)).distinct().toArray(Class<?>[]::new);

        return (T) Proxy.newProxyInstance(iface.getClassLoader(), ifaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (!method.isDefault()) {
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getCause();
                    }
                }

                return DefaultMethodHandler.getMethodHandle(method).bindTo(proxy).invokeWithArguments(args);
            }
        });
    }

    // Note: code for this class has been extracted from the spring data commons library.

    /**
     * Handler for default methods.
     * <p>
     * Original code from the spring project.
     *
     * @author Oliver Gierke
     * @author Jens Schauder
     * @author Mark Paluch
     * @author Thomas Neidhart
     */
    static final class DefaultMethodHandler {

        private static final MethodHandleLookup        methodHandleLookup = MethodHandleLookup.getMethodHandleLookup();
        private static final Map<Method, MethodHandle> methodHandleCache  =
                new ConcurrentReferenceHashMap<>(10,
                                                 ConcurrentReferenceHashMap.ReferenceType.WEAK,
                                                 ConcurrentReferenceHashMap.ReferenceType.WEAK);

        public static MethodHandle getMethodHandle(Method method) throws Exception {
            MethodHandle handle = methodHandleCache.get(method);

            if (handle == null) {
                handle = methodHandleLookup.lookup(method);
                methodHandleCache.put(method, handle);
            }

            return handle;
        }

        /**
         * Strategies for {@link MethodHandle} lookup.
         */
        enum MethodHandleLookup {

            /**
             * Encapsulated {@link MethodHandle} lookup working on Java 9.
             */
            ENCAPSULATED {
                private final Method privateLookupIn = findMethod(MethodHandles.class,
                        "privateLookupIn", Class.class, MethodHandles.Lookup.class);

                @Override
                MethodHandle lookup(Method method) throws ReflectiveOperationException {
                    if (privateLookupIn == null) {
                        throw new IllegalStateException("Could not obtain MethodHandles.privateLookupIn!");
                    }

                    return doLookup(method, getLookup(method.getDeclaringClass(), privateLookupIn));
                }

                @Override
                boolean isAvailable() {
                    return privateLookupIn != null;
                }

                private MethodHandles.Lookup getLookup(Class<?> declaringClass, Method privateLookupIn) {
                    MethodHandles.Lookup lookup = MethodHandles.lookup();

                    try {
                        return (MethodHandles.Lookup) privateLookupIn.invoke(MethodHandles.class, declaringClass, lookup);
                    } catch (ReflectiveOperationException e) {
                        return lookup;
                    }
                }

                private Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
                    try {
                        return clazz.getDeclaredMethod(name, paramTypes);
                    } catch (NoSuchMethodException ex) {
                        return null;
                    }
                }
            },

            /**
             * Open (via reflection construction of {@link MethodHandles.Lookup}) method handle lookup.
             * Works with Java 8 and with Java 9 permitting illegal access.
             */
            OPEN {
                private final LazySupplier<Constructor<MethodHandles.Lookup>> constructor =
                        LazySupplier.of(MethodHandleLookup::getLookupConstructor);

                @Override
                MethodHandle lookup(Method method) throws ReflectiveOperationException {
                    if (!isAvailable()) {
                        throw new IllegalStateException("Could not obtain MethodHandles.lookup constructor!");
                    }

                    Constructor<MethodHandles.Lookup> constructor = this.constructor.get();
                    return constructor.newInstance(method.getDeclaringClass()).unreflectSpecial(method, method.getDeclaringClass());
                }

                @Override
                boolean isAvailable() {
                    return constructor.orElse(null) != null;
                }
            },

            /**
             * Fallback {@link MethodHandle} lookup using {@link MethodHandles#lookup() public lookup}.
             */
            FALLBACK {
                @Override
                MethodHandle lookup(Method method) throws ReflectiveOperationException {
                    return doLookup(method, MethodHandles.lookup());
                }

                @Override
                boolean isAvailable() {
                    return true;
                }
            };

            private static MethodHandle doLookup(Method method, MethodHandles.Lookup lookup)
                    throws NoSuchMethodException, IllegalAccessException {
                MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());

                if (Modifier.isStatic(method.getModifiers())) {
                    return lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType);
                }

                return lookup.findSpecial(method.getDeclaringClass(), method.getName(), methodType, method.getDeclaringClass());
            }

            /**
             * Lookup a {@link MethodHandle} given {@link Method} to look up.
             *
             * @param method must not be {@literal null}.
             * @return the method handle.
             * @throws ReflectiveOperationException
             */
            abstract MethodHandle lookup(Method method) throws ReflectiveOperationException;

            /**
             * @return {@literal true} if the lookup is available.
             */
            abstract boolean isAvailable();

            /**
             * Obtain the first available {@link MethodHandleLookup}.
             *
             * @return the {@link MethodHandleLookup}
             * @throws IllegalStateException if no {@link MethodHandleLookup} is available.
             */
            public static MethodHandleLookup getMethodHandleLookup() {
                for (MethodHandleLookup it : MethodHandleLookup.values()) {
                    if (it.isAvailable()) {
                        return it;
                    }
                }

                throw new IllegalStateException("No MethodHandleLookup available!");
            }

            private static Constructor<MethodHandles.Lookup> getLookupConstructor() {
                try {
                    Constructor<MethodHandles.Lookup> constructor =
                            MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                    if (!constructor.isAccessible()) {
                        constructor.setAccessible(true);
                    }

                    return constructor;
                } catch (Exception ex) {

                    // this is the signal that we are on Java 9 (encapsulated) and can't use the
                    // accessible constructor approach.
                    if (ex.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
                        return null;
                    }

                    throw new IllegalStateException(ex);
                }
            }
        }
    }
}
