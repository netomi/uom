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
import org.netomi.uom.quantity.decimal.DecimalLength;
import org.netomi.uom.quantity.primitive.DoubleLength;

import java.math.BigDecimal;
import java.math.MathContext;

public interface Length extends Quantity<Length> {

    static Length of(Quantity<?> quantity) {
        return of(quantity.doubleValue(), (Unit<Length>) quantity.getUnit());
    }

    static DoubleLength of(double value, Unit<Length> unit) {
        return new DoubleLength(value, unit);
    }

    static DecimalLength decimalOf(BigDecimal value, Unit<Length> unit) {
        return decimalOf(value, MathContext.DECIMAL128, unit);
    }

    static DecimalLength decimalOf(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        return new DecimalLength(value, mathContext, unit);
    }

    @Override
    Length to(Unit<Length> unit);

    @Override
    Length add(Quantity<Length> addend);

    default Area multiplyByLength(Quantity<Length> multiplicand) {
        return multiply(multiplicand).asType(Area.class);
    }

    default Speed divideByTime(Quantity<Time> divisor) {
        return Speed.of(divide(divisor));
    }
}
