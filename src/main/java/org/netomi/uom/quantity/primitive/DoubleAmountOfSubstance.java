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

import org.netomi.uom.Unit;
import org.netomi.uom.quantity.AmountOfSubstance;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link AmountOfSubstance} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleAmountOfSubstance
        extends    AbstractTypedDoubleQuantity<DoubleAmountOfSubstance, AmountOfSubstance>
        implements AmountOfSubstance {

    public static DoubleAmountOfSubstance of(double value, Unit<AmountOfSubstance> unit) {
        return new DoubleAmountOfSubstance(value, unit);
    }

    public static DoubleAmountOfSubstance ofMole(double value) {
        return of(value, Units.SI.MOLE);
    }

    public static DoubleQuantityFactory<DoubleAmountOfSubstance, AmountOfSubstance> factory() {
        return DoubleAmountOfSubstance::of;
    }

    private DoubleAmountOfSubstance(double value, Unit<AmountOfSubstance> unit) {
        super(value, unit);
    }

    @Override
    public DoubleAmountOfSubstance with(double value, Unit<AmountOfSubstance> unit) {
        return of(value, unit);
    }
}
