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
package com.github.netomi.uom.unit;

import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.UnitConverter;
import com.github.netomi.uom.function.UnitConverters;
import com.github.netomi.uom.math.BigFraction;
import com.github.netomi.uom.math.Fraction;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a {@link Unit} which is transformed with a {@link UnitConverter}
 * from another {@link Unit}.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class TransformedUnit<Q extends Quantity<Q>> extends DelegateUnit<Q> {

    protected final String symbol;
    protected final String name;

    private final UnitConverter converterToDelegate;

    static <Q extends Quantity<Q>> Unit<Q> of(Unit<Q> unit, UnitConverter converterToDelegate) {
        Unit<Q>       delegateUnit = unit;
        UnitConverter converter    = converterToDelegate;

        if (unit instanceof TransformedUnit<?>) {
            TransformedUnit<Q> transformedUnit = (TransformedUnit<Q>) unit;

            delegateUnit = transformedUnit.getDelegateUnit();
            converter = converterToDelegate.andThen(transformedUnit.converterToDelegate);
        }

        return converter == UnitConverters.identity() ?
                delegateUnit :
                new TransformedUnit<>(delegateUnit, converter);
    }

    protected TransformedUnit(Unit<Q> delegateUnit, UnitConverter converterToDelegate) {
        this(delegateUnit, null, null, converterToDelegate);
    }

    protected TransformedUnit(Unit<Q> delegateUnit, String symbol, String name, UnitConverter converterToDelegate) {
        super(delegateUnit);
        Objects.requireNonNull(converterToDelegate);

        this.symbol = symbol;
        this.name   = name;

        this.converterToDelegate = converterToDelegate;
    }

    @Override
    public String getSymbol() {
        if (symbol != null) {
            return symbol;
        }

        StringBuilder sb = new StringBuilder();
        if (converterToDelegate.isLinear()) {
            BigFraction fraction = getSystemConverter().scaleAsFraction();
            sb.append(fraction.getNumerator());
            if (fraction.getDenominator().compareTo(BigInteger.ONE) != 0) {
                sb.append('|');
                sb.append(fraction.getDenominator());
            }
            if (!getDelegateUnit().equals(Units.ONE)) {
                sb.append(super.getSymbol());
            }
        } else {
            sb.append(String.format("(transform %s %s)", super.getSymbol(), getSystemConverter()));
        }

        return sb.toString();
    }

    @Override
    public String getName() {
        return name != null ? name : super.getName();
    }

    @Override
    public boolean isSystemUnit() {
        return converterToDelegate.isIdentity() && getDelegateUnit().isSystemUnit();
    }

    @Override
    public UnitConverter getSystemConverter() {
        return converterToDelegate.andThen(super.getSystemConverter());
    }

    @Override
    public UnitElement[] getUnitElements() {
        return new UnitElement[] { new UnitElement(this, Fraction.ONE) };
    }

    @Override
    public Unit<?> multiply(Unit<?> that) {
        return getDelegateUnit() == Units.ONE ?
                Units.transformedWith(that, getSystemConverter()) :
                super.multiply(that);
    }

    @Override
    public TransformedUnit<Q> withSymbol(String symbol) {
        return new TransformedUnit<>(getDelegateUnit(), symbol, this.name, converterToDelegate);
    }

    @Override
    public TransformedUnit<Q> withName(String name) {
        return new TransformedUnit<>(getDelegateUnit(), this.symbol, name, converterToDelegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDelegateUnit(), getSystemConverter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransformedUnit)) return false;

        TransformedUnit<?> otherUnit = (TransformedUnit<?>) o;
        return Objects.equals(getDelegateUnit(),    otherUnit.getDelegateUnit()) &&
               Objects.equals(getSystemConverter(), otherUnit.getSystemConverter());
    }

    @Override
    public String toString() {
        return symbol != null ?
                super.toString() :
                getSymbol();
    }
}
