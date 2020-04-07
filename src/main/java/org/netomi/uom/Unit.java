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

import org.netomi.uom.math.Fraction;
import org.netomi.uom.unit.Dimension;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents a unit of measurement to express the magnitude of a quantity.
 *
 * @param <Q> the quantity type
 *
 * @see <a href="https://en.wikipedia.org/wiki/Unit_of_measurement">Wikipedia: Unit of measurement</a>
 *
 * @author Thomas Neidhart
 */
public interface Unit<Q extends Quantity<Q>> {

    /**
     * Returns the symbol associated with this unit.
     *
     * @return the symbol of the unit.
     */
    String getSymbol();

    /**
     * Returns the name associated with this unit, optional.
     *
     * @return the name of the unit.
     */
    String getName();

    /**
     * Returns the {@link Dimension} of this unit.
     * <p>
     * Conversion of quantities between different units requires that
     * the dimensions of the involved units is equal. Furthermore, units
     * can only be used for quantities whose dimensions match. Any operation
     * involving entities expressed in different dimensions results in
     * an {@link IncommensurableException} thrown at runtime.
     *
     * @return the dimension associated with this unit.
     */
    Dimension getDimension();

    /**
     * Returns whether this unit is compatible with the provided unit.
     * <p>
     * Two units are considered to be compatible if they have the same
     * dimension.
     *
     * @param unit the unit to check for compatibility.
     * @return {@code true} if the two units are compatible, {@code false} otherwise.
     */
    boolean isCompatible(Unit<?> unit);

    /**
     * Casts this unit to the specified quantity type if compatible.
     *
     * @param quantityClass the class of the quantity type to cast to.
     * @param <T> the quantity type.
     * @return a unit of the specified quantity type.
     * @throws IncommensurableException if the dimension of this unit does
     * not match the dimension of the quantity.
     */
    <T extends Quantity<T>> Unit<T> forQuantity(Class<T> quantityClass);

    boolean isSystemUnit();

    Unit<Q> getSystemUnit();

    UnitConverter getSystemConverter();

    UnitConverter getConverterTo(Unit<Q> unit);

    UnitConverter getConverterToAny(Unit<?> unit);

    Unit<Q> shift(double offset);

    Unit<Q> multiply(double multiplier);

    Unit<Q> multiply(BigDecimal multiplier);

    Unit<Q> multiply(long numerator, long denominator);

    Unit<Q> transform(UnitConverter converter);

    Unit<?> multiply(Unit<?> that);

    Unit<?> divide(Unit<?> that);

    Unit<?> pow(int n);

    Unit<?> root(int n);

    Unit<?> inverse();

    Map<? extends Unit<?>, Fraction> getBaseUnits();

    Unit<Q> withSymbol(String symbol);

    Unit<Q> withName(String name);

    Unit<Q> withPrefix(Prefix prefix);
}
