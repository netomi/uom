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

import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.QuantityFactory;
import com.github.netomi.uom.Unit;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A {@link QuantityFactory} that generates quantity instance for a specific quantity type
 * with decimal precision.
 *
 * @param <Q> the quantity type parameter
 *
 * @author Thomas Neidhart
 */
public interface DecimalQuantityFactory<Q extends Quantity<Q>> extends QuantityFactory<Q> {

    MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128;

    @Override
    default Q create(double value, Unit<Q> unit) {
        return create(BigDecimal.valueOf(value), unit);
    }

    @Override
    default Q create(BigDecimal value, Unit<Q> unit) {
        return create(value, DEFAULT_MATH_CONTEXT, unit);
    }

    Q create(BigDecimal value, MathContext mc, Unit<Q> unit);
}
