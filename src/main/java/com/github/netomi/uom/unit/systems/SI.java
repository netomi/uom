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
import com.github.netomi.uom.quantity.*;
import com.github.netomi.uom.quantity.electromagnetic.*;
import com.github.netomi.uom.quantity.kinematic.Acceleration;
import com.github.netomi.uom.quantity.kinematic.Speed;
import com.github.netomi.uom.quantity.mechanical.Area;
import com.github.netomi.uom.quantity.mechanical.Force;
import com.github.netomi.uom.quantity.mechanical.Power;
import com.github.netomi.uom.quantity.mechanical.Volume;
import com.github.netomi.uom.quantity.molar.AmountOfSubstance;
import com.github.netomi.uom.quantity.molar.CatalyticActivity;
import com.github.netomi.uom.quantity.nuclear.RadiationDoseAbsorbed;
import com.github.netomi.uom.quantity.nuclear.RadiationDoseEquivalent;
import com.github.netomi.uom.quantity.nuclear.RadioActivity;
import com.github.netomi.uom.quantity.photometric.Illuminance;
import com.github.netomi.uom.quantity.photometric.LuminousFlux;
import com.github.netomi.uom.quantity.photometric.LuminousIntensity;
import com.github.netomi.uom.quantity.thermodynamic.Pressure;
import com.github.netomi.uom.quantity.thermodynamic.Temperature;
import com.github.netomi.uom.unit.Units;

/**
 * The SI system of units.
 */
public final class SI extends AbstractSystemOfUnits {
    // hide constructor.
    private SI() {
        super("International System of Units");
    }

    public static final SI INSTANCE = new SI();

    public static final Unit<Dimensionless> ONE = Units.ONE;

    // base units of the International System of Units (SI).
    public static final Unit<Length>            METRE    = addUnit("m",   Length.class);
    public static final Unit<Time>              SECOND   = addUnit("s",   Time.class);
    public static final Unit<Mass>              KILOGRAM = addUnit("kg",  Mass.class);
    public static final Unit<Temperature>       KELVIN   = addUnit("K",   Temperature.class);
    public static final Unit<ElectricCurrent>   AMPERE   = addUnit("A",   ElectricCurrent.class);
    public static final Unit<LuminousIntensity> CANDELA  = addUnit("cd",  LuminousIntensity.class);
    public static final Unit<AmountOfSubstance> MOLE     = addUnit("mol", AmountOfSubstance.class);

    public static final Unit<Temperature>       CELSIUS  = addUnit("°C", Temperature.class);

    // System units for dimensionless quantities Angle and SolidAngle.
    public static final Unit<Angle>        RADIAN    = addUnit("rad", Angle.class);
    public static final Unit<SolidAngle>   STERADIAN = addUnit("sr", SolidAngle.class);

    public static final Unit<Frequency>    HERTZ     = addUnit("Hz", Frequency.class);

    public static final Unit<Speed>        METER_PER_SECOND         = addUnit("m/s",  Speed.class);
    public static final Unit<Acceleration> METER_PER_SECOND_SQUARED = addUnit("m/s\u00B2", Acceleration.class);

    public static final Unit<Area>         SQUARE_METER = addUnit("m\u00B2", Area.class);
    public static final Unit<Volume>       CUBIC_METER  = addUnit("m\u00B3", Volume.class);

    public static final Unit<Force>        NEWTON = addUnit("N",  Force.class);
    public static final Unit<Pressure>     PASCAL = addUnit("Pa", Pressure.class);
    public static final Unit<Energy>       JOULE  = addUnit("J",  Energy.class);
    public static final Unit<Power>        WATT   = addUnit("W",  Power.class);

    // nuclear units.
    public static final Unit<RadioActivity>           BECQUEREL = addUnit("Bq", RadioActivity.class);
    public static final Unit<RadiationDoseAbsorbed>   GRAY      = addUnit("Gy", RadiationDoseAbsorbed.class);
    public static final Unit<RadiationDoseEquivalent> SIEVERT   = addUnit("Sv", RadiationDoseEquivalent.class);

    // photometric units.
    public static final Unit<LuminousFlux> LUMEN  = addUnit("lm", LuminousFlux.class);
    public static final Unit<Illuminance>  LUX    = addUnit("lux", Illuminance.class);

    // electric units.
    public static final Unit<ElectricCharge>      COULOMB = addUnit("C", ElectricCharge.class);
    public static final Unit<ElectricPotential>   VOLT    = addUnit("V", ElectricPotential.class);
    public static final Unit<ElectricCapacitance> FARAD   = addUnit("F", ElectricCapacitance.class);
    public static final Unit<ElectricResistance>  OHM     = addUnit("Ω", ElectricResistance.class);
    public static final Unit<ElectricConductance> SIEMENS = addUnit("S", ElectricConductance.class);
    public static final Unit<ElectricInductance>  HENRY   = addUnit("H", ElectricInductance.class);

    // units related to the magnetic field.
    public static final Unit<MagneticFlux>       WEBER = addUnit("Wb", MagneticFlux.class);
    public static final Unit<MagneticInductance> TESLA = addUnit("T",  MagneticInductance.class);

    public static final Unit<CatalyticActivity> KATAL = addUnit("kat", CatalyticActivity.class);

    // derived units
    public static final Unit<Length> KM        = addUnit("km", Length.class);
    public static final Unit<Area>   SQUARE_KM = addUnit("km\u00B2", Area.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
