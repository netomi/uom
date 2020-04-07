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
import org.netomi.uom.quantity.Pressure;
import org.netomi.uom.quantity.Quantities;

/**
 * An extension of the {@link Quantity} interface for quantities with double precisions.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public interface DoubleQuantity<Q extends Quantity<Q>> extends Quantity<Q> {

    static <P extends Quantity<P>> DoubleQuantity<P> of(double value, Unit<P> unit) {
        return Quantities.createQuantity(value, unit, null);
    }

    static <Q extends Quantity<Q>> QuantityFactory<Q> factory() {
        return AbstractTypedDoubleQuantity.GenericImpl.factory();
    }

    DoubleQuantity<Q> with(double value, Unit<Q> unit);

    @Override
    DoubleQuantity<Q> to(Unit<Q> unit);

    @Override
    DoubleQuantity<Q> add(Quantity<Q> addend);

    @Override
    DoubleQuantity<Q> subtract(Quantity<Q> subtrahend);

    @Override
    DoubleQuantity<Q> negate();

    @Override
    DoubleQuantity<?> multiply(Quantity<?> multiplicand);

    @Override
    DoubleQuantity<?> divide(Quantity<?> divisor);

    @Override
    DoubleQuantity<?> reciprocal();
}
