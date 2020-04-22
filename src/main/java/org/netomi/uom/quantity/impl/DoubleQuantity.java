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
package org.netomi.uom.quantity.impl;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;

/**
 * An extension of the {@link Quantity} interface for quantities with double precisions.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public interface DoubleQuantity<Q extends Quantity<Q>> extends Quantity<Q> {

    static <Q extends Quantity<Q>> GenericDoubleQuantityFactory<Q> factory() {
        return GenericDoubleQuantity.factory();
    }

    /**
     * Returns a new {@link DoubleQuantityFactory} for the specified quantity class.
     * <p>
     * The returned factory creates instances with double precision.
     *
     * @param quantityClass the quantity class
     * @param <Q> the quantity type
     * @return a factory that creates quantities with double precision which implement the
     * specified quantity class.
     * @throws IllegalArgumentException if the specified class is not a {@link Quantity}.
     */
    static <Q extends Quantity<Q>> DoubleQuantityFactory<Q> factory(Class<Q> quantityClass) {
        if (!Quantity.class.isAssignableFrom(quantityClass)) {
            throw new IllegalArgumentException(quantityClass + " is not a Quantity.");
        }

        return ProxyDoubleQuantity.factory(quantityClass);
    }

    Q with(double value, Unit<Q> unit);
}
