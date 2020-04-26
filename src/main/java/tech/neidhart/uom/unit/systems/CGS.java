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
package tech.neidhart.uom.unit.systems;

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.quantity.*;
import tech.neidhart.uom.unit.Units;

/**
 * The CGS system of units.
 */
public final class CGS extends AbstractSystemOfUnits {
    // hide constructor.
    private CGS() {
        super("CGS");
    }

    public static final CGS INSTANCE = new CGS();

    // base units
    public static final Unit<Length> CENTIMETRE = addUnit("cm", Length.class);
    public static final Unit<Mass>   GRAM       = addUnit("g",  Mass.class);
    public static final Unit<Time>   SECOND     = addUnit("s",  Time.class);

    public static final Unit<Force>  DYNE = addUnit("dyn", Force.class);

    //public  static final Unit<?>              COULOMB_CONSTANT = SI.VOLT.multiply(SI.METRE).divide(SI.AMPERE.multiply(SECOND)).multiply(8.987551787368176E9).withName("COULOMB CONSTANT");
    public static final Unit<ElectricCharge>  STATCOULOMB = addUnit("statC", ElectricCharge.class);
    public static final Unit<ElectricCurrent> STATAMPERE  = addUnit("statA", ElectricCurrent.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
