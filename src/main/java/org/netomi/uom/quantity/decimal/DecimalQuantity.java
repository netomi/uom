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

import java.math.BigDecimal;
import java.math.MathContext;

public interface DecimalQuantity<Q extends Quantity<Q>> extends Quantity<Q> {

    static <P extends Quantity<P>> DecimalQuantity<P> of(BigDecimal value, Unit<P> unit) {
        return new AbstractTypedDecimalQuantity.GenericImpl(value, MathContext.DECIMAL128, unit);
    }

    static <P extends Quantity<P>> DecimalQuantity<P> of(BigDecimal value, MathContext mathContext, Unit<P> unit) {
        return new AbstractTypedDecimalQuantity.GenericImpl(value, mathContext, unit);
    }

    MathContext getMathContext();

    @Override
    DecimalQuantity<Q> add(Quantity<Q> addend);

    @Override
    DecimalQuantity<?> multiply(Quantity<?> multiplicand);

    @Override
    DecimalQuantity<?> divide(Quantity<?> divisor);

    @Override
    default DecimalQuantity<Q> to(Unit<Q> unit) {
        return to(unit, getMathContext());
    }

    DecimalQuantity<Q> to(Unit<Q> unit, MathContext context);
}
