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

package org.netomi.uom.quantity;

import org.netomi.uom.Quantity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Quantities {

    static Map<Class<? extends Quantity<?>>, Function<Quantity<?>, Quantity<?>>> factory;

    static {
        factory = new HashMap<>();

        factory.put(Length.class, Length::of);
        factory.put(Speed.class, Speed::of);
        factory.put(Area.class, Area::of);
    }

    public static <T extends Q, Q extends Quantity<Q>> T getQuantityAsType(Quantity<?> quantity, Class<T> clazz) {
        return (T) factory.get(clazz).apply(quantity);
    }
}
