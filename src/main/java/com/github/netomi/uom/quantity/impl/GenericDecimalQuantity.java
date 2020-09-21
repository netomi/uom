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
import com.github.netomi.uom.Unit;

import java.math.BigDecimal;
import java.math.MathContext;

@SuppressWarnings("rawtypes")
class GenericDecimalQuantity extends AbstractDecimalQuantity {

    public static <Q extends Quantity<Q>> GenericDecimalQuantityFactory<Q> factory() {
        return GenericDecimalQuantity::new;
    }

    @SuppressWarnings("unchecked")
    GenericDecimalQuantity(BigDecimal value, MathContext mc, Unit unit) {
        super(value, mc, unit);
    }

    @Override
    public DecimalQuantity with(BigDecimal value, MathContext mc, Unit unit) {
        return new GenericDecimalQuantity(value, mc, unit);
    }
}
