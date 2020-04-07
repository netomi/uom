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
import org.netomi.uom.QuantityFactory;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.Quantities;
import org.netomi.uom.quantity.primitive.AbstractTypedDoubleQuantity;
import org.netomi.uom.quantity.primitive.DoubleQuantityFactory;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * An extension of the {@link Quantity} interface for quantities with decimal precisions.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public interface DecimalQuantity<Q extends Quantity<Q>> extends Quantity<Q> {

    static <P extends Quantity<P>> DecimalQuantity<P> of(BigDecimal value, Unit<P> unit) {
        return Quantities.createQuantity(value, unit, null);
    }

    static <P extends Quantity<P>> DecimalQuantity<P> of(BigDecimal value, MathContext mathContext, Unit<P> unit) {
        return Quantities.createQuantity(value, mathContext, unit, null);
    }

    static <Q extends Quantity<Q>> QuantityFactory<Q> factory() {
        return AbstractTypedDecimalQuantity.GenericImpl.factory();
    }

    MathContext getMathContext();

    default DecimalQuantity<Q> with(BigDecimal value, Unit<Q> unit) {
        return with(value, getMathContext(), unit);
    }

    DecimalQuantity<Q> with(BigDecimal value, MathContext mathContext, Unit<Q> unit);

    @Override
    default DecimalQuantity<Q> to(Unit<Q> unit) {
        return to(unit, getMathContext());
    }

    DecimalQuantity<Q> to(Unit<Q> unit, MathContext context);

    @Override
    default DecimalQuantity<Q> toSystemUnit() {
        return toSystemUnit(getMathContext());
    }

    DecimalQuantity<Q> toSystemUnit(MathContext context);

    @Override
    DecimalQuantity<Q> add(Quantity<Q> addend);

    @Override
    DecimalQuantity<Q> subtract(Quantity<Q> addend);

    @Override
    DecimalQuantity<Q> negate();

    @Override
    DecimalQuantity<?> multiply(Quantity<?> multiplicand);

    @Override
    DecimalQuantity<?> divide(Quantity<?> divisor);

    @Override
    DecimalQuantity<?> reciprocal();
}
