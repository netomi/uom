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
package org.netomi.uom.quantity.primitive;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.quantity.Quantities;

import java.math.BigDecimal;

/**
 *
 * @param <P>
 * @param <Q>
 *
 * @author Thomas Neidhart
 */
public abstract class AbstractTypedDoubleQuantity<P extends DoubleQuantity<Q>, Q extends Quantity<Q>>
    implements DoubleQuantity<Q> {

    protected final double  value;
    protected final Unit<Q> unit;

    protected AbstractTypedDoubleQuantity(double value, Unit<Q> unit) {
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
    public int compareTo(Quantity<Q> other) {
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
        double thisValue;

        if (this.unit.isSystemUnit()) {
            thisValue = value;
        } else {
            UnitConverter converter = unit.getSystemConverter();
            thisValue = converter.convert(value);
        }

        return Math.abs(thisValue) <= epsilon;
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
        return toSystemUnit().doubleValue() == 0;
    }

    @Override
    public P add(Quantity<Q> addend) {
        Quantity<Q> scaledQuantity = addend.to(unit);
        return with(value + scaledQuantity.doubleValue(), unit);
    }

    @Override
    public P subtract(Quantity<Q> subtrahend) {
        Quantity<Q> scaledQuantity = subtrahend.to(unit);
        return with(value - scaledQuantity.doubleValue(), unit);
    }

    @Override
    public P negate() {
        return with(-value, unit);
    }

    @Override
    public DoubleQuantity<?> multiply(Quantity<?> multiplicand) {
        Unit<?> combinedSystemUnit = unit.multiply(multiplicand.getUnit()).getSystemUnit();

        UnitConverter toSystemConverter = unit.getSystemConverter();
        double valueInSystemUnit        = toSystemConverter.convert(value);

        Quantity<?> multiplicandInSystemUnit =
                multiplicand.getUnit().isSystemUnit() ?
                        multiplicand :
                        multiplicand.toSystemUnit();

        return genericDoubleQuantity(valueInSystemUnit * multiplicandInSystemUnit.doubleValue(), combinedSystemUnit);
    }

    @Override
    public DoubleQuantity<?> divide(Quantity<?> divisor) {
        Unit<?> combinedSystemUnit = unit.divide(divisor.getUnit()).getSystemUnit();

        UnitConverter toSystemConverter = unit.getSystemConverter();
        double valueInSystemUnit        = toSystemConverter.convert(value);

        Quantity<?> divisorInSystemUnit =
                divisor.getUnit().isSystemUnit() ?
                        divisor :
                        divisor.toSystemUnit();

        return genericDoubleQuantity(valueInSystemUnit / divisorInSystemUnit.doubleValue(), combinedSystemUnit);
    }

    @Override
    public DoubleQuantity<?> reciprocal() {
        return genericDoubleQuantity(1. / value, unit.inverse());
    }

    @Override
    public P to(Unit<Q> toUnit) {
        UnitConverter converter = unit.getConverterTo(toUnit);
        return with(converter.convert(value), toUnit);
    }

    @Override
    public P toSystemUnit() {
        if (unit.isSystemUnit()) {
            return (P) this;
        }

        UnitConverter converter = unit.getSystemConverter();
        return with(converter.convert(value), unit.getSystemUnit());
    }

    @Override
    public <T extends R, R extends Quantity<R>> T asTypedQuantity(Class<T> clazz) {
        return Quantities.getQuantityAsType(this, clazz);
    }

    public abstract P with(double value, Unit<Q> unit);

    protected DoubleQuantity<?> genericDoubleQuantity(double value, Unit<?> unit) {
        return new GenericImpl(value, unit);
    }

    @Override
    public String toString() {
        return String.format("%e %s", doubleValue(), getUnit().getSymbol());
    }

    public static class GenericImpl extends AbstractTypedDoubleQuantity {

        public static DoubleQuantityFactory factory() {
            return (value, unit) -> new GenericImpl(value, unit);
        }

        private GenericImpl(double value, Unit<?> unit) {
            super(value, unit);
        }

        @Override
        public DoubleQuantity with(double value, Unit unit) {
            return new GenericImpl(value, unit);
        }
    }
}
