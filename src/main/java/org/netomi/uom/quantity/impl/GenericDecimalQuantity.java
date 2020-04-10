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

import org.netomi.uom.Unit;

import java.math.BigDecimal;
import java.math.MathContext;

class GenericDecimalQuantity extends AbstractDecimalQuantity {

    public static DecimalQuantityFactory factory() {
        return GenericDecimalQuantity::new;
    }

    GenericDecimalQuantity(BigDecimal value, MathContext mathContext, Unit unit) {
        super(value, mathContext, unit);
    }

    @Override
    public DecimalQuantity with(BigDecimal value, MathContext mathContext, Unit unit) {
        return new GenericDecimalQuantity(value, mathContext, unit);
    }
}
