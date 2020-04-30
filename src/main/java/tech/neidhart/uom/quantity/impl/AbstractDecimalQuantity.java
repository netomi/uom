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

import tech.neidhart.uom.*;
import tech.neidhart.uom.quantity.Quantities;
import tech.neidhart.uom.unit.Units;
import tech.neidhart.uom.util.TypeUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 *
 * @param <Q>
 *
 * @author Thomas Neidhart
 */
abstract class AbstractDecimalQuantity<P extends Q, Q extends Quantity<Q>>
    implements DecimalQuantity<P, Q>,
               TypedQuantity<P, Q> {

    protected final BigDecimal  value;
    protected final MathContext mc;
    protected final Unit<Q>     unit;

    protected AbstractDecimalQuantity(BigDecimal value, MathContext mc, Unit<Q> unit) {
        this.value = value;
        this.mc    = mc;
        this.unit  = unit;
    }

    @Override
    public MathContext getMathContext() {
        return mc;
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public BigDecimal decimalValue() {
        return value;
    }

    @Override
    public Unit<Q> getUnit() {
        return unit;
    }

    @Override
    public int compareTo(Quantity<Q> other) {
        if (this.unit.equals(other.getUnit())) {
            return this.value.compareTo(other.decimalValue());
        } else {
            UnitConverter converter = other.getUnit().getConverterTo(this.getUnit());
            return this.value.compareTo(converter.convert(other.decimalValue(), mc));
        }
    }

    @Override
    public boolean isEqual(Quantity<Q> other, double epsilon) {
        BigDecimal otherValue;

        if (this.unit.equals(other.getUnit())) {
            otherValue = other.decimalValue();
        } else {
            UnitConverter converter = other.getUnit().getConverterTo(this.getUnit());
            otherValue = converter.convert(other.decimalValue(), mc);
        }

        return otherValue.subtract(this.value, mc).abs(mc).doubleValue() <= epsilon;
    }

    @Override
    public boolean isZero(double epsilon) {
        return value.abs().doubleValue() <= epsilon;
    }

    @Override
    public boolean isZero(Unit<Q> inUnit, double epsilon) {
        BigDecimal thisValue;

        if (this.unit.equals(inUnit)) {
            thisValue = value;
        } else {
            UnitConverter converter = unit.getConverterTo(inUnit);
            thisValue = converter.convert(value, mc);
        }

        return thisValue.abs().doubleValue() <= epsilon;
    }

    @Override
    public boolean isStrictlyZero() {
        return BigDecimal.ZERO.compareTo(value) == 0;
    }

    @Override
    public P add(Quantity<Q> addend) {
        Quantity<Q> scaledQuantity = addend.to(unit);
        return with(value.add(scaledQuantity.decimalValue(), mc), unit);
    }

    @Override
    public P subtract(Quantity<Q> subtrahend) {
        Quantity<Q> scaledQuantity = subtrahend.to(unit);
        return with(value.subtract(scaledQuantity.decimalValue(), mc), unit);
    }

    @Override
    public P negate() {
        return with(value.negate(mc), unit);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        Unit<?> combinedSystemUnit = unit.multiply(multiplier.getUnit()).getSystemUnit();
        return genericDecimalQuantity(multiplyInternal(this, multiplier), combinedSystemUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Quantity<R>> R multiply(Quantity<?> multiplier, Class<R> quantityClass) {
        Unit<R> calculatedSystemUnit = (Unit<R>) unit.multiply(multiplier.getUnit()).getSystemUnit();

        Unit<R> systemUnit = (Unit<R>) Quantities.Type.systemUnitFor(quantityClass, calculatedSystemUnit);
        if (systemUnit != calculatedSystemUnit) {
            TypeUtil.requireCommensurable(calculatedSystemUnit, systemUnit);
            calculatedSystemUnit = systemUnit;
        }

        return Quantities.create(multiplyInternal(this, multiplier), mc, calculatedSystemUnit, quantityClass);
    }

    private BigDecimal multiplyInternal(Quantity<?> multiplicand, Quantity<?> multiplier) {
        BigDecimal multiplicandInSystemUnit = toSystemUnitValue(multiplicand);
        BigDecimal multiplierInSystemUnit   = toSystemUnitValue(multiplier);

        return multiplicandInSystemUnit.multiply(multiplierInSystemUnit, mc);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        Unit<?> combinedSystemUnit = unit.divide(divisor.getUnit()).getSystemUnit();
        return genericDecimalQuantity(divideInternal(this, divisor), combinedSystemUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Quantity<R>> R divide(Quantity<?> divisor, Class<R> quantityClass) {
        Unit<R> calculatedSystemUnit = (Unit<R>) unit.divide(divisor.getUnit()).getSystemUnit();

        Unit<R> systemUnit = (Unit<R>) Quantities.Type.systemUnitFor(quantityClass, calculatedSystemUnit);
        if (systemUnit != calculatedSystemUnit) {
            TypeUtil.requireCommensurable(calculatedSystemUnit, systemUnit);
            calculatedSystemUnit = systemUnit;
        }

        return Quantities.create(divideInternal(this, divisor), mc, calculatedSystemUnit, quantityClass);
    }

    private BigDecimal divideInternal(Quantity<?> dividend, Quantity<?> divisor) {
        BigDecimal dividendInSystemUnit = toSystemUnitValue(dividend);
        BigDecimal divisorInSystemUnit   = toSystemUnitValue(divisor);

        return dividendInSystemUnit.divide(divisorInSystemUnit, mc);
    }

    @Override
    public Quantity<?> reciprocal() {
        return genericDecimalQuantity(divideInternal(one(), this), unit.inverse().getSystemUnit());
    }

    @Override
    public Quantity<?> one() {
        // return a generic quantity of dimension ONE with the same mathContext as this instance.
        return genericDecimalQuantity(BigDecimal.ONE, Units.ONE);
    }

    @Override
    public Quantity<Q> zero() {
        return with(BigDecimal.ZERO, mc, unit);
    }

    @Override
    public P to(Unit<Q> toUnit) {
        return to(toUnit, mc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public P to(Unit<Q> toUnit, MathContext toMc) {
        if (getUnit().equals(toUnit)) {
            return (P) this;
        }
        TypeUtil.requireCommensurable(this, toUnit);
        UnitConverter converter = unit.getConverterTo(toUnit);
        return with(converter.convert(value, toMc), toUnit);
    }

    @Override
    public P toSystemUnit() {
        return toSystemUnit(mc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public P toSystemUnit(MathContext toMc) {
        if (unit.isSystemUnit()) {
            Unit<Q> namedUnit = Units.getNamedUnitIfPresent(unit);
            return namedUnit == unit ?
                    (P) this :
                    with(value, namedUnit);
        }

        UnitConverter converter = unit.getSystemConverter();
        return with(converter.convert(value, toMc), unit.getSystemUnit());
    }

    private BigDecimal toSystemUnitValue(Quantity<?> quantity) {
        return quantity.getUnit().isSystemUnit() ?
                quantity.decimalValue() :
                quantity.getUnit().getSystemConverter().convert(quantity.decimalValue(), mc);
    }

    protected Class<?> getQuantityClass() {
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <R extends S, S extends Quantity<S>> R asQuantity(Class<R> quantityType) {
        Objects.requireNonNull(quantityType);

        Class<?> quantityClass = getQuantityClass();
        if (quantityClass != null &&
            quantityType.isAssignableFrom(quantityClass)) {
            return (R) this;
        } else {
            try {
                Unit<Q> systemUnit          = getSystemUnit();
                Unit<S> requestedSystemUnit = (Unit<S>) Quantities.Type.systemUnitFor(quantityType, (Unit) unit);

                BigDecimal quantityValue = this.value;
                Unit<S>    quantityUnit  = (Unit<S>) this.unit;

                if (!systemUnit.equals(requestedSystemUnit)) {
                    TypeUtil.requireCommensurable(systemUnit, requestedSystemUnit);

                    quantityValue = quantityUnit.getConverterToAny(requestedSystemUnit).convert(quantityValue, mc);
                    quantityUnit  = requestedSystemUnit;
                }

                R quantity = Quantities.create(quantityValue, mc, quantityUnit, quantityType);
                return (R) quantity;
            } catch (UnsupportedOperationException ex) {
                throw new IncommensurableException("Incompatible quantity class: " + quantityType.getSimpleName() +
                                                   " has not overridden its getSystemUnit() method.");
            }
        }
    }

    protected Quantity<?> genericDecimalQuantity(BigDecimal value, Unit<?> unit) {
        Class<?> quantityClass = Quantities.getQuantityType(unit);
        return quantityClass == null ?
                new GenericDecimalQuantity(value, mc, unit) :
                Quantities.create(value, mc, unit);
    }

    @Override
    public String toString() {
        return Quantities.defaultFormatter().format(this);
    }
}
