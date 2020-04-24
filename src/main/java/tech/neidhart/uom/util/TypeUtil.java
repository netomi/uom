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

import tech.neidhart.uom.IncommensurableException;
import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;

/**
 * A utility to perform type / dimension checks between entities like {@link Quantity} and {@link Unit}.
 *
 * @author Thomas Neidhart
 */
public final class TypeUtil {

    /** Error message for units whose dimensions does not match. */
    private static final String ERROR_UNIT_DIMENSION_MISMATCH = "Dimension mismatch for units {0} and {1}: {2} != {3}.";

    /** Error message for units and quantities whose dimension does not match. */
    private static final String ERROR_UNIT_QUANTITY_DIMENSION_MISMATCH = "Incompatible system units: trying to use unit {0}/{1}'{'{2}'}' for quantity of type {3}'{'{4}'}'.";

    // hide constructor
    private TypeUtil() {}

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
        Class<?>[] interfaces = quantity.getClass().getInterfaces();

        for (Class<?> itf : interfaces) {
            if (Quantity.class.isAssignableFrom(itf)) {
                return itf;
            }
        }

        // did not find a quantity interface, return the class itself.
        return quantity.getClass();
    }
}
