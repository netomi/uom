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
package org.netomi.uom.util;

import org.netomi.uom.IncommensurableException;
import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;

public class TypeUtil {

    /** Error message for units whose dimensions does not match. */
    private static final String ERROR_UNIT_DIMENSION_MISMATCH = "Dimension mismatch for units {0} and {1}: {2} != {3}.";

    /** Error message for units and quantities whose dimension does not match. */
    private static final String ERROR_UNIT_QUANTITY_DIMENSION_MISMATCH = "Dimension mismatch when trying to use unit {0}/{1}'{'{2}'}' for quantity of type {3}'{'{4}'}'.";

    // hide constructor
    private TypeUtil() {}

    public static void requireCommensurable(Unit<?> unit, Unit<?> otherUnit) {
        if (!unit.isCompatible(otherUnit)) {
            throw new IncommensurableException(ERROR_UNIT_DIMENSION_MISMATCH,
                                               unit,
                                               otherUnit,
                                               unit.getDimension(),
                                               otherUnit.getDimension());
        }
    }

    public static void requireCommensurable(Quantity<?> quantity, Unit<?> unit) {
        if (!quantity.isCompatible(unit)) {
            throw new IncommensurableException(ERROR_UNIT_QUANTITY_DIMENSION_MISMATCH,
                                               unit.getSymbol(),
                                               unit.getName(),
                                               unit.getDimension(),
                                               quantity.getClass().getSimpleName(),
                                               quantity.getDimension());
        }
    }
}
