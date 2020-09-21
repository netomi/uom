/*
 * Copyright 2016-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.netomi.uom.util;

// Note: code for this class has been extracted from the spring data commons library.

import java.util.function.Supplier;

/**
 * Simple value type to delay the creation of an object using a {@link Supplier}
 * returning the produced object for subsequent lookups. Note, that no concurrency
 * control is applied during the lookup of {@link #get()}, which means in
 * concurrent access scenarios, the provided {@link Supplier} can be called multiple times.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
class LazySupplier<T> implements Supplier<T> {

    private final Supplier<? extends T> supplier;

    private T       value    = null;
    private boolean resolved = false;

    /**
     * Creates a new {@link LazySupplier} to produce an object lazily.
     *
     * @param <T> the type of which to produce an object of eventually.
     * @param supplier the {@link Supplier} to create the object lazily.
     */
    public static <T> LazySupplier<T> of(Supplier<? extends T> supplier) {
        return new LazySupplier<>(supplier);
    }

    private LazySupplier(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the value created by the configured {@link Supplier}.
     * Will return the calculated instance for subsequent lookups.
     */
    public T get() {
        T value = getNullable();
        if (value == null) {
            throw new IllegalStateException("Expected lazy evaluation to yield a non-null value but got null!");
        }
        return value;
    }

    /**
     * Returns the value of the lazy computation or the given default value in case the computation yields
     * {@literal null}.
     */
    public T orElse(T value) {
        T nullable = getNullable();
        return nullable == null ? value : nullable;
    }

    /**
     * Returns the value of the lazy evaluation.
     */
    public T getNullable() {
        T value = this.value;

        if (this.resolved) {
            return value;
        }

        value = supplier.get();

        this.value    = value;
        this.resolved = true;

        return value;
    }
}
