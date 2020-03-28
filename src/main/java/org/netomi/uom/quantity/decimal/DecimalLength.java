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

import org.netomi.uom.Unit;
import org.netomi.uom.quantity.Length;

import java.math.BigDecimal;
import java.math.MathContext;

public class DecimalLength extends AbstractTypedDecimalQuantity<DecimalLength, Length> implements Length {

    public DecimalLength(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        super(value, mathContext, unit);
    }

    protected DecimalLength with(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        return new DecimalLength(value, mathContext, unit);
    }
}
