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
package org.netomi.uom.unit.systems;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.*;
import org.netomi.uom.unit.Dimensions;
import org.netomi.uom.unit.Units;

/**
 * The SI system of units.
 */
public final class SI extends AbstractSystemOfUnits {
    // hide constructor.
    private SI() {
        super("SI");
    }

    public static final SI INSTANCE = new SI();

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<?> unit, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(unit, quantityClass);
    }

    public static final Unit<Dimensionless>     ONE = Units.ONE;

    // Base units of the International System of Units (SI).
    public static final Unit<Length>            METRE    = addUnit(Units.baseUnitForDimension("m",   "METER",    Dimensions.LENGTH),              Length.class);
    public static final Unit<Time>              SECOND   = addUnit(Units.baseUnitForDimension("s",   "SECOND",   Dimensions.TIME),                Time.class);
    public static final Unit<Mass>              KILOGRAM = addUnit(Units.baseUnitForDimension("kg",  "KILOGRAM", Dimensions.MASS),                Mass.class);
    public static final Unit<Temperature>       KELVIN   = addUnit(Units.baseUnitForDimension("K",   "KELVIN",   Dimensions.TEMPERATURE),         Temperature.class);
    public static final Unit<ElectricCurrent>   AMPERE   = addUnit(Units.baseUnitForDimension("A",   "AMPERE",   Dimensions.ELECTRIC_CURRENT),    ElectricCurrent.class);
    public static final Unit<LuminousIntensity> CANDELA  = addUnit(Units.baseUnitForDimension("cd",  "CANDELA",  Dimensions.LUMINOUS_INTENSITY),  LuminousIntensity.class);
    public static final Unit<AmountOfSubstance> MOLE     = addUnit(Units.baseUnitForDimension("mol", "MOLE",     Dimensions.AMOUNT_OF_SUBSTANCE), AmountOfSubstance.class);

    public static final Unit<Temperature>       CELSIUS  = addUnit(KELVIN.shift(273.15).withSymbol("°C").withName("DEGREE CELSIUS"), Temperature.class);

    public static final Unit<Angle>             RADIAN   = addUnit(Units.ONE.withSymbol("rad").withName("RADIAN"), Angle.class);
    public static final Unit<Frequency>         HERTZ    = addUnit(Units.ONE.divide(SECOND).withSymbol("Hz") .withName("HERTZ"), Frequency.class);

    public static final Unit<Speed>             METER_PER_SECOND = addUnit(METRE.divide(SECOND).withSymbol("m/s").withName("METER PER SECOND"), Speed.class);
    public static final Unit<Acceleration>      METER_PER_SECOND_SQUARED = addUnit(METER_PER_SECOND.divide(SECOND).withSymbol("m/s²").withName("METER PER SECOND SQUARED"), Acceleration.class);

    public static final Unit<Area>              SQUARE_METER = addUnit(METRE.multiply(METRE).withSymbol("m\u00B2").withName("SQUARE METER"), Area.class);
    public static final Unit<Volume>            CUBIC_METER  = addUnit(SQUARE_METER.multiply(METRE).withSymbol("m\u00B3").withName("CUBIC METER"), Volume.class);

    public static final Unit<Force>             NEWTON = addUnit(KILOGRAM.multiply(METRE).divide(SECOND.pow(2)).withSymbol("N").withName("NEWTON"), Force.class);
    public static final Unit<Pressure>          PASCAL = addUnit(NEWTON.divide(SQUARE_METER).withSymbol("Pa").withName("PASCAL"), Pressure.class);
    public static final Unit<Energy>            JOULE  = addUnit(NEWTON.multiply(METRE).withSymbol("J").withName("JOULE"), Energy.class);
    public static final Unit<Power>             WATT   = addUnit(JOULE.divide(SECOND).withSymbol("W").withName("WATT"), Power.class);

    public static final Unit<ElectricCharge>      COULOMB = addUnit(AMPERE.multiply(SECOND).withSymbol("C").withName("COULOMB"), ElectricCharge.class);
    public static final Unit<ElectricPotential>   VOLT    = addUnit(JOULE.divide(COULOMB).withSymbol("V").withName("VOLT"), ElectricPotential.class);
    public static final Unit<ElectricCapacitance> FARAD   = addUnit(COULOMB.divide(VOLT).withSymbol("F").withName("FARAD"), ElectricCapacitance.class);
    public static final Unit<ElectricResistance>  OHM     = addUnit(VOLT.divide(AMPERE).withSymbol("Ω").withName("OHM"), ElectricResistance.class);
    public static final Unit<ElectricConductance> SIEMENS = addUnit(Units.ONE.divide(OHM).withSymbol("S").withName("SIEMENS"), ElectricConductance.class);

    // Constants expressed in SI units.
    //public static final Unit<Speed>       C                = INSTANCE.buildUnit(METER_PER_SECOND.multiply(299792458, 1).withName("SPEED OF LIGHT"), Speed.class);

    static {
        Units.register(INSTANCE);
    }
}
