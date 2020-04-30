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
package tech.neidhart.uom.quantity.impl;

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * An extension of the {@link Quantity} interface for quantities with decimal precisions.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public interface DecimalQuantity<P extends Q, Q extends Quantity<Q>> extends Quantity<Q> {

    static <Q extends Quantity<Q>> GenericDecimalQuantityFactory<Q> factory() {
        return GenericDecimalQuantity.factory();
    }

    /**
     * Returns a new {@link DecimalQuantityFactory} for the specified quantity class.
     * <p>
     * The returned factory creates instances with decimal precision.
     *
     * @param quantityClass the quantity class
     * @param <Q> the quantity type
     * @return a factory that creates quantities with decimal precision which implement the
     * specified quantity class.
     * @throws IllegalArgumentException if the specified class is not a {@link Quantity}.
     */
    static <P extends Q, Q extends Quantity<Q>> DecimalQuantityFactory<P, Q> factory(Class<P> quantityClass) {
        if (!Quantity.class.isAssignableFrom(quantityClass)) {
            throw new IllegalArgumentException(quantityClass + " is not a Quantity.");
        }

        return ProxyDecimalQuantity.factory(quantityClass);
    }

    /**
     * Returns a new {@link DecimalQuantityFactory} for the specified quantity class.
     * <p>
     * The returned factory creates instances with a fixed decimal precision.
     *
     * @param mc            the {@link MathContext} to use for all created instances.
     * @param quantityClass the quantity class
     * @param <Q> the quantity type
     * @return a factory that creates quantities with decimal precision which implement the
     * specified quantity class.
     * @throws IllegalArgumentException if the specified class is not a {@link Quantity}.
     */
    static <P extends Q, Q extends Quantity<Q>> DecimalQuantityFactory<P, Q> factory(MathContext mc, Class<P> quantityClass) {
        if (!Quantity.class.isAssignableFrom(quantityClass)) {
            throw new IllegalArgumentException(quantityClass + " is not a Quantity.");
        }

        return ProxyDecimalQuantity.factory(mc, quantityClass);
    }

    MathContext getMathContext();

    Q to(Unit<Q> toUnit, MathContext mc);

    default Q toSystemUnit() {
        return toSystemUnit(getMathContext());
    }

    Q toSystemUnit(MathContext mc);

    Class<?> getQuantityClass();

    default P with(BigDecimal value, Unit<Q> unit) {
        return with(value, getMathContext(), unit);
    }

    P with(BigDecimal value, MathContext mc, Unit<Q> unit);
}
