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
import org.netomi.uom.QuantityFactory;
import org.netomi.uom.Unit;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A {@link QuantityFactory} that generates quantity instance for a specific quantity type
 * with double precision.
 *
 * @param <T> the specific quantity type (e.g. Length)
 * @param <Q> the quantity type parameter
 *
 * @author Thomas Neidhart
 */
public interface DoubleQuantityFactory<T extends Q, Q extends Quantity<Q>> extends QuantityFactory<Q> {
    @Override
    T create(double value, Unit<Q> unit);

    @Override
    default T create(BigDecimal value, Unit<Q> unit) {
        return create(value.doubleValue(), unit);
    }

    @Override
    default T create(BigDecimal value, MathContext mathContext, Unit<Q> unit) {
        return create(value.doubleValue(), unit);
    }
}