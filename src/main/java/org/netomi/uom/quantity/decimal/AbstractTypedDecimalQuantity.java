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

import java.math.BigDecimal;
import java.math.MathContext;

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
        Quantity<Q> scaledQuantity = addend.to(getUnit());
        return with(decimalValue().add(scaledQuantity.decimalValue()), getUnit());
    }

    @Override
    public DecimalQuantity<?> multiply(Quantity<?> multiplicand) {
        Unit<?> unit = this.getUnit().multiply(multiplicand.getUnit());
        return genericDoubleQuantity(decimalValue().multiply(multiplicand.decimalValue()), unit);
    }

    @Override
    public DecimalQuantity<?> divide(Quantity<?> divisor) {
        Unit<?> unit = this.getUnit().divide(divisor.getUnit());
        return genericDoubleQuantity(decimalValue().divide(divisor.decimalValue()), unit);
    }

    @Override
    public P to(Unit<Q> unit) {
        return to(unit, mathContext);
    }

    public P to(Unit<Q> unit, MathContext context) {
        UnitConverter converter = getUnit().getConverterTo(unit);
        return with(converter.convert(value, context), unit);
    }

    @Override
    public <T extends R, R extends Quantity<R>> T asType(Class<T> clazz) {
        return (T) this;
    }

    protected P with(BigDecimal value, Unit<Q> unit) {
        return with(value, mathContext, unit);
    }

    protected abstract P with(BigDecimal value, MathContext mathContext, Unit<Q> unit);

    protected DecimalQuantity<?> genericDoubleQuantity(BigDecimal value, Unit<?> unit) {
        return new GenericImpl(value, mathContext, unit);
    }

    @Override
    public String toString() {
        return String.format("%s %s", decimalValue().toString(), getUnit().getSymbol());
    }

    static class GenericImpl extends AbstractTypedDecimalQuantity {

        public GenericImpl(BigDecimal value, MathContext mathContext, Unit<?> unit) {
            super(value, mathContext, unit);
        }

        @Override
        protected DecimalQuantity with(BigDecimal value, MathContext mathContext, Unit unit) {
            return new GenericImpl(value, mathContext, unit);
        }
    }

}
