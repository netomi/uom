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
import org.netomi.uom.quantity.decimal.DecimalMass;
import org.netomi.uom.quantity.primitive.DoubleMass;

import java.math.BigDecimal;
import java.math.MathContext;

public interface Mass extends Quantity<Mass> {

    static Mass of(Quantity<?> quantity) {
        return of(quantity.doubleValue(), (Unit<Mass>) quantity.getUnit());
    }

    static DoubleMass of(double value, Unit<Mass> unit) {
        return new DoubleMass(value, unit);
    }

    static DecimalMass decimalOf(BigDecimal value, Unit<Mass> unit) {
        return decimalOf(value, MathContext.DECIMAL128, unit);
    }

    static DecimalMass decimalOf(BigDecimal value, MathContext mathContext, Unit<Mass> unit) {
        return new DecimalMass(value, mathContext, unit);
    }

    @Override
    Mass to(Unit<Mass> unit);

    @Override
    Mass add(Quantity<Mass> addend);
}
