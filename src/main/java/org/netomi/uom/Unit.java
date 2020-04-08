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

import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Fraction;
import org.netomi.uom.quantity.Quantities;
import org.netomi.uom.unit.*;
import org.netomi.uom.util.TypeUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a unit of measurement to express the magnitude of a quantity.
 *
 * @param <Q> the quantity type
 *
 * @see <a href="https://en.wikipedia.org/wiki/Unit_of_measurement">Wikipedia: Unit of measurement</a>
 *
 * @author Thomas Neidhart
 */
public abstract class Unit<Q extends Quantity<Q>> {

    /**
     * Returns the symbol associated with this unit.
     *
     * @return the symbol of the unit.
     */
    public abstract String getSymbol();

    /**
     * Returns the name associated with this unit, optional.
     *
     * @return the name of the unit.
     */
    public abstract String getName();

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
    public abstract Dimension getDimension();

    /**
     * Returns whether this unit is compatible with the provided unit.
     * <p>
     * Two units are considered to be compatible if they have the same
     * dimension.
     *
     * @param that the unit to check for compatibility.
     * @return {@code true} if the two units are compatible, {@code false} otherwise.
     */
    public boolean isCompatible(Unit<?> that) {
        // Two units are compatible if their dimensions are equal.
        Dimension thisDimension = this.getDimension();
        Dimension thatDimension = that.getDimension();

        return thisDimension.equals(thatDimension);
    }

    public boolean isSystemUnit() {
        return getSystemConverter().isIdentity();
    }

    public abstract Unit<Q> getSystemUnit();

    public abstract UnitConverter getSystemConverter();

    public UnitConverter getConverterTo(Unit<Q> unit) {
        TypeUtil.requireCommensurable(this, unit);

        UnitConverter thisConverter = getSystemConverter();
        UnitConverter thatConverter = unit.getSystemConverter().inverse();

        return thisConverter.andThen(thatConverter);
    }

    public UnitConverter getConverterToAny(Unit<?> unit) {
        return getConverterTo((Unit) unit);
    }

    public abstract Map<? extends Unit<?>, Fraction> getBaseUnits();

    // builder methods thats return a new unit.

    public Unit<Q> shift(double offset) {
        return transform(UnitConverters.shift(offset));
    }

    public Unit<Q> multiply(double multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    public Unit<Q> multiply(BigDecimal multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    public Unit<Q> multiply(long numerator, long denominator) {
        return transform(UnitConverters.multiply(numerator, denominator));
    }

    public Unit<Q> transform(UnitConverter converter) {
        return TransformedUnit.of(this, converter);
    }

    public Unit<?> multiply(Unit<?> that) {
        return this == Units.ONE ? that :
               that == Units.ONE ? this :
                       ProductUnit.ofProduct(this, Fraction.ONE, that, Fraction.ONE);
    }

    public Unit<?> divide(Unit<?> that) {
        return that == Units.ONE ? this :
               ProductUnit.ofProduct(this, Fraction.ONE, that, Fraction.of(-1));
    }

    public Unit<?> pow(int n) {
        return n == 0 ? Units.ONE :
               n == 1 ? this      :
                        ProductUnit.ofProduct(this, Fraction.of(n));
    }

    public Unit<?> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer.");
        }

        return n == 1 ? this :
               ProductUnit.ofProduct(this, Fraction.of(1, n));
    }

    public Unit<?> inverse() {
        return this == Units.ONE ? this :
               ProductUnit.ofProduct(Units.ONE, Fraction.ONE, this, Fraction.of(-1));
    }

    public Unit<Q> withSymbol(String symbol) {
        return NamedUnit.withSymbol(this, symbol);
    }

    public Unit<Q> withName(String name) {
        return NamedUnit.withName(this, name);
    }

    public Unit<Q> withPrefix(Prefix prefix) {
        return PrefixedUnit.withPrefix(this, prefix);
    }

    /**
     * Casts this unit to the specified quantity type if it is compatible.
     *
     * @param quantityClass the class of the quantity type to cast to.
     * @param <T> the quantity type.
     * @return a unit of the specified quantity type.
     * @throws IncommensurableException if the dimension of this unit does
     * not match the dimension of the quantity.
     */
    public <T extends Quantity<T>> Unit<T> forQuantity(Class<T> quantityClass) {
        // try to create a quantity of the requested type with this unit.
        // if it works, we can safely cast.
        Quantities.createQuantity(0, (Unit) this, quantityClass);
        @SuppressWarnings("unchecked")
        Unit<T> castUnit = (Unit<T>) this;
        return castUnit;
    }

    // for internal use only.

    public abstract UnitElement[] getUnitElements();

    @Override
    public int hashCode() {
        return Objects.hash(getDimension(), getSystemConverter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;

        // TODO: implement a meaningful equals also for dimensionless units.

        Unit<?> otherUnit = (Unit<?>) o;
        return Objects.equals(getDimension(),       otherUnit.getDimension()) &&
               Objects.equals(getSystemConverter(), otherUnit.getSystemConverter());
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", getSymbol(), getDimension());
    }
}
