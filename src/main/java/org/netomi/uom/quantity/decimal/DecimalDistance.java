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
import org.netomi.uom.quantity.Distance;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.unit.Units;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.netomi.uom.quantity.decimal.DecimalQuantityFactory.DEFAULT_MATH_CONTEXT;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link Distance} with decimal precision.
 *
 * @author Thomas Neidhart
 */
public final class DecimalDistance
        extends    AbstractTypedDecimalQuantity<DecimalDistance, Length>
        implements Distance {

    public static DecimalDistance of(BigDecimal value, Unit<Length> unit) {
        return of(value, DEFAULT_MATH_CONTEXT, unit);
    }

    public static DecimalDistance of(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        return new DecimalDistance(value, mathContext, unit);
    }

    public static DecimalDistance ofMeter(BigDecimal value) {
        return ofMeter(value, DEFAULT_MATH_CONTEXT);
    }

    public static DecimalDistance ofMeter(BigDecimal value, MathContext mathContext) {
        return of(value, mathContext, Units.SI.METRE);
    }

    public static DecimalQuantityFactory<DecimalDistance, Length> factory() {
        return (value, context, unit) -> of(value, context, unit);
    }

    public static DecimalQuantityFactory<DecimalDistance, Length> factory(MathContext mathContext) {
        return (value, context, unit) -> of(value, mathContext, unit);
    }

    private DecimalDistance(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        super(value, mathContext, unit);
    }

    @Override
    public DecimalDistance with(BigDecimal value, MathContext mathContext, Unit<Length> unit) {
        return new DecimalDistance(value, mathContext, unit);
    }
}
