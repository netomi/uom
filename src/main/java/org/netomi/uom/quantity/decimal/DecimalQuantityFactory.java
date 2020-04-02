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

import java.math.BigDecimal;
import java.math.MathContext;

public interface DecimalQuantityFactory<T extends Q, Q extends Quantity<Q>> extends QuantityFactory<Q> {

    MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128;

    @Override
    default T create(double value, Unit<Q> unit) {
        return create(BigDecimal.valueOf(value), unit);
    }

    @Override
    default T create(BigDecimal value, Unit<Q> unit) {
        return create(value, DEFAULT_MATH_CONTEXT, unit);
    }

    T create(BigDecimal value, MathContext mathContext, Unit<Q> unit);
}