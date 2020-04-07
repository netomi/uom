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
import org.netomi.uom.quantity.ElectricConductance;
import org.netomi.uom.unit.Units;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.netomi.uom.quantity.decimal.DecimalQuantityFactory.DEFAULT_MATH_CONTEXT;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link ElectricConductance} with decimal precision.
 *
 * @author Thomas Neidhart
 */
public final class DecimalElectricConductance
        extends    AbstractTypedDecimalQuantity<DecimalElectricConductance, ElectricConductance>
        implements ElectricConductance {

    public static DecimalElectricConductance of(BigDecimal value, Unit<ElectricConductance> unit) {
        return of(value, DEFAULT_MATH_CONTEXT, unit);
    }

    public static DecimalElectricConductance of(BigDecimal value, MathContext mathContext, Unit<ElectricConductance> unit) {
        return new DecimalElectricConductance(value, mathContext, unit);
    }

    public static DecimalElectricConductance ofSiemens(BigDecimal value) {
        return ofSiemens(value, DEFAULT_MATH_CONTEXT);
    }

    public static DecimalElectricConductance ofSiemens(BigDecimal value, MathContext mathContext) {
        return of(value, mathContext, Units.SI.SIEMENS);
    }

    public static DecimalQuantityFactory<DecimalElectricConductance, ElectricConductance> factory() {
        return DecimalElectricConductance::of;
    }

    public static DecimalQuantityFactory<DecimalElectricConductance, ElectricConductance> factory(MathContext mathContext) {
        return (value, context, unit) -> of(value, mathContext, unit);
    }

    private DecimalElectricConductance(BigDecimal value, MathContext mathContext, Unit<ElectricConductance> unit) {
        super(value, mathContext, unit);
    }

    @Override
    public DecimalElectricConductance with(BigDecimal value, MathContext mathContext, Unit<ElectricConductance> unit) {
        return new DecimalElectricConductance(value, mathContext, unit);
    }
}
