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

package org.netomi.uom.quantity;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.decimal.DecimalTime;
import org.netomi.uom.quantity.primitive.DoubleTime;

import java.math.BigDecimal;
import java.math.MathContext;

public interface Time extends Quantity<Time> {

    static DoubleTime of(double value, Unit<Time> unit) {
        return new DoubleTime(value, unit);
    }

    static DecimalTime decimalOf(BigDecimal value, Unit<Time> unit) {
        return new DecimalTime(value, MathContext.DECIMAL128, unit);
    }

    static DecimalTime decimalOf(BigDecimal value, MathContext mathContext, Unit<Time> unit) {
        return new DecimalTime(value, mathContext, unit);
    }

    @Override
    Time to(Unit<Time> unit);

    @Override
    Time add(Quantity<Time> addend);
}
