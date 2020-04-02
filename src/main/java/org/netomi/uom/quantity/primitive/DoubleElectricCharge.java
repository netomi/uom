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
import org.netomi.uom.quantity.ElectricCharge;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link ElectricCharge} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleElectricCharge
        extends    AbstractTypedDoubleQuantity<DoubleElectricCharge, ElectricCharge>
        implements ElectricCharge {

    public static DoubleElectricCharge of(double value, Unit<ElectricCharge> unit) {
        return new DoubleElectricCharge(value, unit);
    }

    public static DoubleElectricCharge ofCoulomb(double value) {
        return of(value, Units.SI.COULOMB);
    }

    public static DoubleQuantityFactory<DoubleElectricCharge, ElectricCharge> factory() {
        return (value, unit) -> of(value, unit);
    }

    private DoubleElectricCharge(double value, Unit<ElectricCharge> unit) {
        super(value, unit);
    }

    @Override
    public DoubleElectricCharge with(double value, Unit<ElectricCharge> unit) {
        return of(value, unit);
    }
}
