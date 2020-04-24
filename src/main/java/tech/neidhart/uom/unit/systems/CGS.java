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

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<?> unit, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(unit, quantityClass);
    }

    // base units
    public static final Unit<Length> CENTIMETRE = addUnit(SI.METRE.multiply(1, 100).withSymbol("cm").withName("CENTIMETER"), Length.class);
    public static final Unit<Mass>   GRAM       = addUnit(SI.KILOGRAM.multiply(1, 1000).withSymbol("g") .withName("GRAM"), Mass.class);
    public static final Unit<Time>   SECOND     = addUnit(SI.SECOND, Time.class);

    public static final Unit<Force>  DYNE = addUnit(GRAM.multiply(CENTIMETRE).divide(SECOND.pow(2)).withSymbol("dyn").withName("DYNE"), Force.class);

    public  static final Unit<?>              COULOMB_CONSTANT = SI.VOLT.multiply(SI.METRE).divide(SI.AMPERE.multiply(SECOND)).multiply(8.987551787368176E9).withName("COULOMB CONSTANT");
    public  static final Unit<ElectricCharge> STATCOULOMB      = addUnit(DYNE.root(2).multiply(CENTIMETRE).divide(COULOMB_CONSTANT.root(2)).withSymbol("statC").withName("STATCOULOMB"), ElectricCharge.class);
    // alternative conversion:
    //public Unit<ElectricCharge> STATCOULOMB = SI.COULOMB.divide(SI.C).multiply(1, 10).asType(ElectricCharge.class).withSymbolAndName("statC", "STATCOULOMB");

    static {
        Units.register(INSTANCE);
    }
}
