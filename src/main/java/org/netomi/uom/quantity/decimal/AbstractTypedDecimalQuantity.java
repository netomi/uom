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

package org.netomi.uom.quantity.decimal;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.quantity.Quantities;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @param <P>
 * @param <Q>
 *
 * @author Thomas Neidhart
 */
public abstract class AbstractTypedDecimalQuantity<P extends DecimalQuantity<Q>, Q extends Quantity<Q>> implements DecimalQuantity<Q> {

    protected final BigDecimal  value;
    protected final MathContext mathContext;
    protected final Unit<Q>     unit;

    protected AbstractTypedDecimalQuantity(BigDecimal value, MathContext mathContext, Unit<Q> unit) {
        this.value       = value;
        this.mathContext = mathContext;
        this.unit        = unit;
    }

    @Override
    public MathContext getMathContext() {
        return mathContext;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public BigDecimal decimalValue() {
        return value;
    }

    public Unit<Q> getUnit() {
        return unit;
    }

    @Override
    public P add(Quantity<Q> addend) {
        Quantity<Q> scaledQuantity = addend.to(unit);
        return with(value.add(scaledQuantity.decimalValue()), unit);
    }

    @Override
    public P subtract(Quantity<Q> subtrahend) {
        Quantity<Q> scaledQuantity = subtrahend.to(unit);
        return with(value.subtract(scaledQuantity.decimalValue()), unit);
    }

    @Override
    public P negate() {
        return with(value.negate(mathContext), unit);
    }

    @Override
    public DecimalQuantity<?> multiply(Quantity<?> multiplicand) {
        Unit<?> combinedSystemUnit = unit.multiply(multiplicand.getUnit()).getSystemUnit();

        UnitConverter toSystemConverter = unit.getConverterTo(unit.getSystemUnit());
        BigDecimal valueInSystemUnit    = toSystemConverter.convert(value, mathContext);

        Quantity<?> multiplicandInSystemUnit =
                multiplicand.getUnit().isSystemUnit() ?
                        multiplicand :
                        multiplicand.to((Unit) multiplicand.getUnit().getSystemUnit());

        return genericDecimalQuantity(valueInSystemUnit.multiply(multiplicandInSystemUnit.decimalValue(), mathContext),
                                      combinedSystemUnit);
    }

    @Override
    public DecimalQuantity<?> divide(Quantity<?> divisor) {
        Unit<?> combinedSystemUnit = unit.divide(divisor.getUnit()).getSystemUnit();

        UnitConverter toSystemConverter = unit.getConverterTo(unit.getSystemUnit());
        BigDecimal valueInSystemUnit    = toSystemConverter.convert(value, mathContext);

        Quantity<?> multiplicandInSystemUnit =
                divisor.getUnit().isSystemUnit() ?
                        divisor :
                        divisor.to((Unit) divisor.getUnit().getSystemUnit());

        return genericDecimalQuantity(valueInSystemUnit.divide(multiplicandInSystemUnit.decimalValue(), mathContext),
                                      combinedSystemUnit);
    }

    @Override
    public DecimalQuantity<?> reciprocal() {
        return genericDecimalQuantity(BigDecimal.ONE.divide(value, mathContext), unit);
    }

    @Override
    public P to(Unit<Q> toUnit) {
        return to(toUnit, mathContext);
    }

    public P to(Unit<Q> toUnit, MathContext context) {
        UnitConverter converter = unit.getConverterTo(toUnit);
        return with(converter.convert(value, context), toUnit);
    }

    @Override
    public <T extends R, R extends Quantity<R>> T asTypedQuantity(Class<T> clazz) {
        return Quantities.getQuantityAsType(this, clazz);
    }

    @Override
    public P with(BigDecimal value, Unit<Q> unit) {
        return with(value, mathContext, unit);
    }

    @Override
    public abstract P with(BigDecimal value, MathContext mathContext, Unit<Q> unit);

    protected DecimalQuantity<?> genericDecimalQuantity(BigDecimal value, Unit<?> unit) {
        return new GenericImpl(value, mathContext, unit);
    }

    @Override
    public String toString() {
        return String.format("%f %s", value, unit.getSymbol());
    }

    public static class GenericImpl extends AbstractTypedDecimalQuantity {

        public static DecimalQuantityFactory factory() {
            return (value, context, unit) -> new GenericImpl(value, context, unit);
        }

        public GenericImpl(BigDecimal value, MathContext mathContext, Unit<?> unit) {
            super(value, mathContext, unit);
        }

        @Override
        public DecimalQuantity with(BigDecimal value, MathContext mathContext, Unit unit) {
            return new GenericImpl(value, mathContext, unit);
        }
    }

}
