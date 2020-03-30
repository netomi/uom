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

import java.math.BigDecimal;
import java.util.Objects;

abstract class AbstractUnit<Q extends Quantity<Q>> implements Unit<Q> {

    @Override
    public UnitConverter getConverterTo(Unit<Q> unit) {
        if (!isCompatible(unit)) {
            throw new IllegalArgumentException("invalid unit");
        }

        UnitConverter thisConverter = getSystemConverter();
        UnitConverter thatConverter = unit.getSystemConverter().inverse();

        return thisConverter.andThen(thatConverter);
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
        return new TransformedUnit<>(this, converter);
    }

    @Override
    public Unit<?> multiply(Unit<?> that) {
        if (this == Units.ONE) {
            return that;
        } else if (that == Units.ONE) {
            return this;
        }
        return DerivedUnit.getProductInstance(this, that);
    }

    @Override
    public Unit<?> divide(Unit<?> that) {
        return multiply(that.inverse());
    }

    @Override
    public Unit<?> pow(int n) {
        if (n > 0)
            return this.multiply(this.pow(n - 1));
        else if (n == 0)
            return Units.ONE;
        else
            // n < 0
            return Units.ONE.divide(this.pow(-n));
    }

    @Override
    public Unit<?> root(int n) {
        if (n > 0)
            return DerivedUnit.ofRoot(this, n);
        else if (n == 0)
            throw new ArithmeticException("Root's order of zero");
        else
            // n < 0
            return Units.ONE.divide(this.root(-n));
    }

    @Override
    public Unit<?> inverse() {
        if (this == Units.ONE) {
            return this;
        }
        return DerivedUnit.getQuotientInstance(Units.ONE, this);
    }

    @Override
    public boolean isCompatible(Unit<?> that) {
        // two units are compatible if their dimensions
        // are equal.

        Dimension thisDimension = this.getDimension();
        Dimension thatDimension = that.getDimension();
        if (thisDimension.equals(thatDimension)) {
            return true;
        }

        return false;
    }

    public <T extends Quantity<T>> Unit<T> asType(Class<T> clazz) {
        return (Unit<T>) this;
    }

    @Override
    public Unit<Q> withSymbolAndName(String symbol, String name) {
        return new NamedUnit<>(symbol, name, this);
    }

    public Unit<Q> withPrefix(Prefix prefix) {
        return new PrefixedUnit<>(prefix, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUnit)) return false;
        AbstractUnit<?> otherUnit = (AbstractUnit<?>) o;
        return Objects.equals(getDimension(), otherUnit.getDimension()) &&
               Objects.equals(getSystemConverter(), otherUnit.getSystemConverter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDimension(), getSystemConverter());
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", getSymbol(), getDimension());
    }
}