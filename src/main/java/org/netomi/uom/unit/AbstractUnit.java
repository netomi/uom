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
package org.netomi.uom.unit;

import org.netomi.uom.*;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Fraction;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @param <Q> the quantity type
 * @author Thomas Neidhart
 */
abstract class AbstractUnit<Q extends Quantity<Q>> implements Unit<Q> {

    @Override
    public UnitConverter getConverterTo(Unit<Q> unit) {
        if (!isCompatible(unit)) {
            throw new IncommensurableException(IncommensurableException.ERROR_UNIT_DIMENSION_MISMATCH, this, unit);
        }

        UnitConverter thisConverter = getSystemConverter();
        UnitConverter thatConverter = unit.getSystemConverter().inverse();

        return thisConverter.andThen(thatConverter);
    }

    @Override
    public UnitConverter getConverterToAny(Unit<?> unit) {
        return getConverterTo((Unit) unit);
    }

    @Override
    public Unit<Q> shift(double offset) {
        return transform(UnitConverters.shift(offset));
    }

    @Override
    public Unit<Q> multiply(double multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    @Override
    public Unit<Q> multiply(BigDecimal multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    @Override
    public Unit<Q> multiply(long numerator, long denominator) {
        return transform(UnitConverters.multiply(numerator, denominator));
    }

    @Override
    public Unit<Q> transform(UnitConverter converter) {
        return Units.buildFrom(this).transformedBy(converter).build();
    }

    @Override
    public Unit<?> multiply(Unit<?> that) {
        return this == Units.ONE ? that :
               that == Units.ONE ? this :
                                   DerivedUnit.ofProduct(this, Fraction.ONE, that, Fraction.ONE);
    }

    @Override
    public Unit<?> divide(Unit<?> that) {
        return that == Units.ONE ? this :
                                   DerivedUnit.ofProduct(this, Fraction.ONE, that, Fraction.of(-1));
    }

    @Override
    public Unit<?> pow(int n) {
        return n == 0 ? Units.ONE :
               n == 1 ? this      :
                        DerivedUnit.ofProduct(this, Fraction.of(n));
    }

    @Override
    public Unit<?> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer.");
        }

        return n == 1 ? this :
                        DerivedUnit.ofProduct(this, Fraction.of(1, n));
    }

    @Override
    public Unit<?> inverse() {
        return this == Units.ONE ? this :
                                   DerivedUnit.ofProduct(Units.ONE, Fraction.ONE, this, Fraction.of(-1));
    }

    @Override
    public boolean isCompatible(Unit<?> that) {
        // Two units are compatible if their dimensions are equal.
        Dimension thisDimension = this.getDimension();
        Dimension thatDimension = that.getDimension();

        return thisDimension.equals(thatDimension);
    }

    @Override
    public boolean isSystemUnit() {
        return false;
    }

    @Override
    public <T extends Quantity<T>> Unit<T> asType(Class<T> clazz) {
        return (Unit<T>) this;
    }

    @Override
    public Unit<Q> withSymbol(String symbol) {
        return Units.buildFrom(this).withSymbol(symbol).build();
    }

    @Override
    public Unit<Q> withName(String name) {
        return Units.buildFrom(this).withName(name).build();
    }

    @Override
    public Unit<Q> withPrefix(Prefix prefix) {
        return Units.buildFrom(this).withPrefix(prefix).build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDimension(), getSystemConverter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUnit)) return false;

        AbstractUnit<?> otherUnit = (AbstractUnit<?>) o;
        return Objects.equals(getDimension(),       otherUnit.getDimension()) &&
               Objects.equals(getSystemConverter(), otherUnit.getSystemConverter());
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", getSymbol(), getDimension());
    }
}
