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
import org.netomi.uom.quantity.ElectricConductance;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link ElectricConductance} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleElectricConductance
        extends    AbstractTypedDoubleQuantity<DoubleElectricConductance, ElectricConductance>
        implements ElectricConductance {

    public static DoubleElectricConductance of(double value, Unit<ElectricConductance> unit) {
        return new DoubleElectricConductance(value, unit);
    }

    public static DoubleElectricConductance ofOhm(double value) {
        return of(value, Units.SI.SIEMENS);
    }

    public static DoubleQuantityFactory<DoubleElectricConductance, ElectricConductance> factory() {
        return DoubleElectricConductance::of;
    }

    private DoubleElectricConductance(double value, Unit<ElectricConductance> unit) {
        super(value, unit);
    }

    @Override
    public DoubleElectricConductance with(double value, Unit<ElectricConductance> unit) {
        return of(value, unit);
    }
}
