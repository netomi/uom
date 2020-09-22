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
package com.github.netomi.uom.unit.systems;

import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.electromagnetic.*;
import com.github.netomi.uom.unit.Units;

/**
 * The ESU-CGS system of units.
 */
public final class EMU extends CGS {
    // hide constructor.
    private EMU() {
        super("EMU-CGS");
    }

    // TODO: add units from CGS parent.
    public static final EMU INSTANCE = new EMU();

    // electrical units.

    public static final Unit<ElectricCharge>      ABCOULOMB = addUnit("abC", ElectricCharge.class);
    public static final Unit<ElectricCurrent>     ABAMPERE  = addUnit("abA", ElectricCurrent.class);
    public static final Unit<ElectricPotential>   ABVOLT    = addUnit("abV", ElectricPotential.class);
    public static final Unit<ElectricResistance>  ABOHM     = addUnit("abΩ", ElectricResistance.class);
    public static final Unit<ElectricCapacitance> ABFARAD   = addUnit("abF", ElectricCapacitance.class);

    public static final Unit<MagneticFlux>        MAXWELL   = addUnit("Mx",  MagneticFlux.class);
    public static final Unit<MagneticInductance>  GAUSS     = addUnit("Gs",  MagneticInductance.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
