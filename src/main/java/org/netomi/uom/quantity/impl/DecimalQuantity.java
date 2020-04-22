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
package org.netomi.uom.quantity.impl;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;

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

    static <Q extends Quantity<Q>> GenericDecimalQuantityFactory<Q> factory() {
        return GenericDecimalQuantity.factory();
    }

    static <Q extends Quantity<Q>> DecimalQuantityFactory<Q> factory(Class<Q> quantityClass) {
        return ProxyDecimalQuantity.factory(quantityClass);
    }

    MathContext getMathContext();

    Q to(Unit<Q> toUnit, MathContext mc);

    default Q toSystemUnit() {
        return toSystemUnit(getMathContext());
    }

    Q toSystemUnit(MathContext mc);

    default Q with(BigDecimal value, Unit<Q> unit) {
        return with(value, getMathContext(), unit);
    }

    Q with(BigDecimal value, MathContext mc, Unit<Q> unit);
}
