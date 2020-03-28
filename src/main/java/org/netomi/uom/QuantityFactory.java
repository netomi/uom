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

package org.netomi.uom;

import org.netomi.uom.quantity.Length;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class QuantityFactory<Q extends Quantity<Q>> {

    private final BiFunction<Double, Unit, Quantity> ctor;

    static <P extends Quantity<P>> QuantityFactory<P> of(Class<P> clazz) {
        BiFunction<Double, Unit, Quantity> ctor = DefaultQuantityFactory.getCtor(clazz);

        return new QuantityFactory<P>(ctor);
    }

    private QuantityFactory(BiFunction<Double, Unit, Quantity> function) {
        this.ctor = function;
    }

    Q createQuantity(double value, Unit<Q> unit) {
        return (Q) ctor.apply(value, unit);
    }

    static class DefaultQuantityFactory {
        private static Map<Class<?>, BiFunction<Double, Unit, Quantity>> ctors = new HashMap<>();

        static {
            ctors.put(Length.class, Length::of);
        }

        static BiFunction<Double, Unit, Quantity> getCtor(Class<? extends Quantity<?>> quantityType) {
            return ctors.get(quantityType);
        }
    }
}
