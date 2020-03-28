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

abstract class AbstractTypedDoubleQuantity<P extends DoubleQuantity<Q>, Q extends Quantity<Q>> implements DoubleQuantity<Q> {

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
    public P add(Quantity<Q> addend) {
        Quantity<Q> scaledQuantity = addend.to(getUnit());
        return with(doubleValue() + scaledQuantity.doubleValue(), getUnit());
    }

    @Override
    public DoubleQuantity<?> multiply(Quantity<?> multiplicand) {
        Unit<?> unit = this.getUnit().multiply(multiplicand.getUnit());
        return genericDoubleQuantity(doubleValue() * multiplicand.doubleValue(), unit);
    }

    @Override
    public DoubleQuantity<?> divide(Quantity<?> divisor) {
        Unit<?> unit = this.getUnit().divide(divisor.getUnit());
        return genericDoubleQuantity(doubleValue() / divisor.doubleValue(), unit);
    }

    @Override
    public P to(Unit<Q> unit) {
        UnitConverter converter = getUnit().getConverterTo(unit);
        return with(converter.convert(value), unit);
    }

    @Override
    public <T extends R, R extends Quantity<R>> T asType(Class<T> clazz) {
        return Quantities.getQuantityAsType(this, clazz);
    }

    protected abstract P with(double value, Unit<Q> unit);

    protected DoubleQuantity<?> genericDoubleQuantity(double value, Unit<?> unit) {
        return new GenericImpl(value, unit);
    }

    @Override
    public String toString() {
        return String.format("%e %s", doubleValue(), getUnit().getSymbol());
    }

    static class GenericImpl extends AbstractTypedDoubleQuantity {

        public GenericImpl(double value, Unit<?> unit) {
            super(value, unit);
        }

        @Override
        protected DoubleQuantity with(double value, Unit unit) {
            return new GenericImpl(value, unit);
        }
    }
}
