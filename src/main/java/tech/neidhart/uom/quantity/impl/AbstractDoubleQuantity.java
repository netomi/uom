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

import tech.neidhart.uom.IncommensurableException;
import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.quantity.Quantities;
import tech.neidhart.uom.unit.Units;

import java.math.BigDecimal;

/**
 *
 * @param <Q>
 *
 * @author Thomas Neidhart
 */
abstract class AbstractDoubleQuantity<Q extends Quantity<Q>>
    implements DoubleQuantity<Q> {

    private static final Quantity<?> ONE = new GenericDoubleQuantity(1.0, Units.ONE);

    protected final double  value;
    protected final Unit<Q> unit;

    protected AbstractDoubleQuantity(double value, Unit<Q> unit) {
        this.value = value;
        this.unit  = unit;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public Unit<Q> getUnit() {
        return unit;
    }

    @Override
    public int compareTo(Quantity<Q> other) throws IncommensurableException {
        if (this.unit.equals(other.getUnit())) {
            return Double.compare(this.value, other.doubleValue());
        } else {
            UnitConverter converter = other.getUnit().getConverterTo(this.unit);

            return Double.compare(this.value, converter.convert(other.doubleValue()));
        }
    }

    @Override
    public boolean isEqual(Quantity<Q> other, double epsilon) {
        double otherValue;

        if (this.unit.equals(other.getUnit())) {
            otherValue = other.doubleValue();
        } else {
            UnitConverter converter = other.getUnit().getConverterTo(this.unit);
            otherValue = converter.convert(other.doubleValue());
        }

        return Math.abs(otherValue - this.value) <= epsilon;
    }

    @Override
    public boolean isZero(double epsilon) {
        return Math.abs(value) <= epsilon;
    }

    @Override
    public boolean isZero(Unit<Q> inUnit, double epsilon) {
        double thisValue;

        if (this.unit.equals(inUnit)) {
            thisValue = value;
        } else {
            UnitConverter converter = unit.getConverterTo(inUnit);
            thisValue = converter.convert(value);
        }

        return Math.abs(thisValue) <= epsilon;
    }

    @Override
    public boolean isStrictlyZero() {
        return value == 0;
    }

    @Override
    public Q add(Quantity<Q> addend) {
        Quantity<Q> scaledQuantity = addend.to(unit);
        return with(value + scaledQuantity.doubleValue(), unit);
    }

    @Override
    public Q subtract(Quantity<Q> subtrahend) {
        Quantity<Q> scaledQuantity = subtrahend.to(unit);
        return with(value - scaledQuantity.doubleValue(), unit);
    }

    @Override
    public Q negate() {
        return with(-value, unit);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        Unit<?> combinedSystemUnit = unit.multiply(multiplier.getUnit()).getSystemUnit();
        return genericDoubleQuantity(multiplyInternal(this, multiplier), combinedSystemUnit);
    }

    @Override
    public <R extends Quantity<R>> R multiply(Quantity<?> multiplier, Class<R> quantityClass) {
        @SuppressWarnings("unchecked")
        Unit<R> combinedSystemUnit = (Unit<R>) unit.multiply(multiplier.getUnit()).getSystemUnit();
        return Quantities.create(multiplyInternal(this, multiplier), combinedSystemUnit, quantityClass);
    }

    private double multiplyInternal(Quantity<?> multiplicand, Quantity<?> multiplier) {
        double multiplicandInSystemUnit = toSystemUnitValue(multiplicand);
        double multiplierInSystemUnit   = toSystemUnitValue(multiplier);

        return multiplicandInSystemUnit * multiplierInSystemUnit;
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        Unit<?> combinedSystemUnit = unit.divide(divisor.getUnit()).getSystemUnit();
        return genericDoubleQuantity(divideInternal(this, divisor), combinedSystemUnit);
    }

    @Override
    public <R extends Quantity<R>> R divide(Quantity<?> divisor, Class<R> quantityClass) {
        @SuppressWarnings("unchecked")
        Unit<R> combinedSystemUnit = (Unit<R>) unit.divide(divisor.getUnit()).getSystemUnit();
        return Quantities.create(divideInternal(this, divisor), combinedSystemUnit, quantityClass);
    }

    private double divideInternal(Quantity<?> dividend, Quantity<?> divisor) {
        double dividendInSystemUnit = toSystemUnitValue(dividend);
        double divisorInSystemUnit  = toSystemUnitValue(divisor);

        return dividendInSystemUnit / divisorInSystemUnit;
    }

    @Override
    public Quantity<?> reciprocal() {
        return genericDoubleQuantity(divideInternal(ONE, this), unit.inverse().getSystemUnit());
    }

    public Quantity<?> one() {
        return ONE;
    }

    @Override
    public Quantity<Q> zero() {
        return with(0, unit);
    }

    @Override
    public Q to(Unit<Q> toUnit) {
        UnitConverter converter = unit.getConverterTo(toUnit);
        return with(converter.convert(value), toUnit);
    }

    @Override
    public Q toSystemUnit() {
        if (unit.isSystemUnit()) {
            Unit<Q> namedUnit = Units.getNamedUnitIfPresent(unit);
            return namedUnit == unit ?
                    (Q) this :
                    with(value, namedUnit);
        }

        UnitConverter converter = unit.getSystemConverter();
        return with(converter.convert(value), unit.getSystemUnit());
    }

    private double toSystemUnitValue(Quantity<?> quantity) {
        return quantity.getUnit().isSystemUnit() ?
                quantity.doubleValue() :
                quantity.getUnit().getSystemConverter().convert(quantity.doubleValue());
    }

    private Quantity<?> genericDoubleQuantity(double value, Unit<?> unit) {
        Class<?> quantityClass = Quantities.getQuantityType(unit);
        return quantityClass == null ?
            new GenericDoubleQuantity(value, unit) :
            Quantities.create(value, unit);
    }

    @Override
    public String toString() {
        return String.format("%e %s", doubleValue(), getUnit().getSymbol());
    }
}
