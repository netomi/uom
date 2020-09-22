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
package com.github.netomi.uom.util;

import com.github.netomi.uom.IncommensurableException;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.impl.DecimalQuantity;
import com.github.netomi.uom.quantity.impl.DoubleQuantity;

import java.lang.reflect.Proxy;

/**
 * Some utilities to perform type / dimension checks between entities like {@link Quantity} and {@link Unit}.
 *
 * @author Thomas Neidhart
 */
public final class Preconditions {

    /** Error message for units whose dimensions does not match. */
    private static final String ERROR_UNIT_DIMENSION_MISMATCH = "Dimension mismatch for units {0} and {1}: {2} != {3}.";

    /** Error message for units and quantities whose dimension does not match. */
    private static final String ERROR_UNIT_QUANTITY_DIMENSION_MISMATCH = "Incompatible system units: trying to use unit {0}/{1}'{'{2}'}' for quantity of type {3}'{'{4}'}'.";

    // hide constructor
    private Preconditions() {}

    /**
     * Checks if the two provided {@link Unit}'s are compatible. If not,
     * an {@link IncommensurableException} is thrown.
     *
     * @param unit       the unit to check for compatibility.
     * @param otherUnit  the other unit to check for compatibility.
     * @throws IncommensurableException if the two units are not compatible.
     */
    public static void requireCommensurable(Unit<?> unit, Unit<?> otherUnit) {
        if (!unit.isCompatible(otherUnit)) {
            throw new IncommensurableException(ERROR_UNIT_DIMENSION_MISMATCH,
                                               unit,
                                               otherUnit,
                                               unit.getDimension(),
                                               otherUnit.getDimension());
        }
    }

    /**
     * Checks if the provided {@link Unit} is compatible with the given
     * {@link Quantity}. If not, an {@link IncommensurableException} is thrown.
     *
     * @param quantity   the quantity to check for compatibility.
     * @param unit       the unit to check for compatibility.
     * @throws IncommensurableException if the two objects are not compatible.
     */
    public static void requireCommensurable(Quantity<?> quantity, Unit<?> unit) {
        if (!quantity.isCompatible(unit)) {
            throw new IncommensurableException(ERROR_UNIT_QUANTITY_DIMENSION_MISMATCH,
                                               unit.getSymbol(),
                                               unit.getName(),
                                               unit.getSystemUnit(),
                                               getQuantityClass(quantity).getSimpleName(),
                                               quantity.getSystemUnit());
        }
    }

    public static Class<?> getQuantityClass(Quantity<?> quantity) {
        Class<?> quantityClass = quantity.getClass();

        if (Proxy.isProxyClass(quantityClass)) {
            Class<?>[] interfaces = quantityClass.getInterfaces();

            for (Class<?> itf : interfaces) {
                if (Quantity.class.isAssignableFrom(itf)) {
                    return itf;
                }
            }
        } else if (quantity instanceof DoubleQuantity<?>) {
            quantityClass = ((DoubleQuantity<?>) quantity).getQuantityClass();
        } else if (quantity instanceof DecimalQuantity<?>) {
            quantityClass = ((DecimalQuantity<?>) quantity).getQuantityClass();
        }

        // did not find a quantity interface, return the class itself.
        return quantityClass != null ? quantityClass : quantity.getClass();
    }
}
