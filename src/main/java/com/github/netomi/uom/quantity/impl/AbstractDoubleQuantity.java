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
package com.github.netomi.uom.quantity.impl;

import com.github.netomi.uom.*;
import com.github.netomi.uom.quantity.Quantities;
import com.github.netomi.uom.unit.Units;
import com.github.netomi.uom.util.Preconditions;

import java.math.BigDecimal;
import java.util.Objects;

import static com.github.netomi.uom.quantity.impl.GenericDoubleQuantity.ONE;

/**
 *
 * @param <Q>
 *
 * @author Thomas Neidhart
 */
abstract class AbstractDoubleQuantity<Q extends Quantity<Q>> implements DoubleQuantity<Q>, TypedQuantity<Q> {

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
    @SuppressWarnings("unchecked")
    public <R extends Quantity<R>> R multiply(Quantity<?> multiplier, Class<R> quantityClass) {
        Unit<R> calculatedSystemUnit = (Unit<R>) unit.multiply(multiplier.getUnit()).getSystemUnit();

        Unit<R> systemUnit = (Unit<R>) Quantities.Type.systemUnitFor(quantityClass, calculatedSystemUnit);
        if (systemUnit != calculatedSystemUnit) {
            Preconditions.requireCommensurable(calculatedSystemUnit, systemUnit);
            calculatedSystemUnit = systemUnit;
        }

        return Quantities.create(multiplyInternal(this, multiplier), calculatedSystemUnit, quantityClass);
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
    @SuppressWarnings("unchecked")
    public <R extends Quantity<R>> R divide(Quantity<?> divisor, Class<R> quantityClass) {
        Unit<R> calculatedSystemUnit = (Unit<R>) unit.divide(divisor.getUnit()).getSystemUnit();

        Unit<R> systemUnit = (Unit<R>) Quantities.Type.systemUnitFor(quantityClass, calculatedSystemUnit);
        if (systemUnit != calculatedSystemUnit) {
            Preconditions.requireCommensurable(calculatedSystemUnit, systemUnit);
            calculatedSystemUnit = systemUnit;
        }

        return Quantities.create(divideInternal(this, divisor), calculatedSystemUnit, quantityClass);
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
    @SuppressWarnings("unchecked")
    public Q to(Unit<Q> toUnit) {
        if (getUnit().equals(toUnit)) {
            return (Q) this;
        }
        Preconditions.requireCommensurable(this, toUnit);
        UnitConverter converter = unit.getConverterTo(toUnit);
        return with(converter.convert(value), toUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
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

    public Class<?> getQuantityClass() {
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends Quantity<R>> R asQuantity(Class<R> quantityType) {
        Objects.requireNonNull(quantityType);

        Class<?> quantityClass = getQuantityClass();
        if (quantityClass != null &&
            quantityType.isAssignableFrom(quantityClass)) {
            return (R) this;
        } else {
            try {
                Unit<Q> systemUnit          = getSystemUnit();
                Unit<R> requestedSystemUnit = (Unit<R>) Quantities.Type.systemUnitFor(quantityType, (Unit) unit);

                double quantityValue = this.value;
                Unit<R> quantityUnit = (Unit<R>) this.unit;

                if (!systemUnit.equals(requestedSystemUnit)) {
                    Preconditions.requireCommensurable(systemUnit, requestedSystemUnit);

                    quantityValue = quantityUnit.getConverterToAny(requestedSystemUnit).convert(quantityValue);
                    quantityUnit  = requestedSystemUnit;
                }

                R quantity = Quantities.create(quantityValue, quantityUnit, quantityType);
                return (R) quantity;
            } catch (UnsupportedOperationException ex) {
                throw new IncommensurableException("Incompatible quantity class: " + quantityType.getSimpleName() +
                                                   " has not overridden its getSystemUnit() method.");
            }
        }
    }

    private Quantity<?> genericDoubleQuantity(double value, Unit<?> unit) {
        Class<?> quantityClass = Quantities.getQuantityType(unit);
        return quantityClass == null ?
            new GenericDoubleQuantity(value, unit) :
            Quantities.create(value, unit);
    }

    @Override
    public String toString() {
        return Quantities.defaultFormatter().format(this);
    }
}
