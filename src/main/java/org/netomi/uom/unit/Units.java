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
package org.netomi.uom.unit;

import org.netomi.uom.Quantity;
import org.netomi.uom.SystemOfUnits;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Neidhart
 */
public class Units {

    private static Map<Unit<?>, Unit<?>> namedUnits = new ConcurrentHashMap<>();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new ProductUnit<>();
    public static final BigDecimal          PI  = BigDecimal.valueOf(StrictMath.PI);

    // System of Units.
    public static final SI       SI       = new Units.SI();
    public static final CGS      CGS      = new Units.CGS();
    public static final Imperial Imperial = new Units.Imperial();
    public static final Other    Other    = new Units.Other();

    static {
        for (SystemOfUnits system : Arrays.asList(SI, CGS, Imperial, Other)) {
            for (Unit<?> unit : system.getUnits()) {
                // do not put dimensionless units into the set of named units.
                // all dimensionless units are equal to each other.
                if (unit.getDimension() == Dimensions.NONE) {
                    continue;
                }

                // do not use putIfAbsent to be compatible with android
                // as much as possible.
                if (!namedUnits.containsKey(unit)) {
                    namedUnits.put(unit, unit);
                }
            }
        }
    }

    static <Q extends Quantity<Q>> Unit<Q> getNamedUnitIfPresent(Unit<Q> unit) {
        Unit<?> namedUnit = namedUnits.get(unit);
        return namedUnit != null ? (Unit<Q>) namedUnit : unit;
    }

    public static <Q extends Quantity<Q>> Unit<Q> baseUnitForDimension(String symbol, String name, Dimension dimension) {
        return new BaseUnit<>(symbol, name, dimension);
    }

    /**
     * The SI system of units.
     */
    public static class SI extends AbstractSystemOfUnits {
        private SI() {
            super("SI");
        }

        // Base units of the International System of Units (SI).
        public Unit<Length>            METRE    = addUnit(new BaseUnit<>("m",   "METER",    Dimensions.LENGTH),              Length.class);
        public Unit<Time>              SECOND   = addUnit(new BaseUnit<>("s",   "SECOND",   Dimensions.TIME),                Time.class);
        public Unit<Mass>              KILOGRAM = addUnit(new BaseUnit<>("kg",  "KILOGRAM", Dimensions.MASS),                Mass.class);
        public Unit<Temperature>       KELVIN   = addUnit(new BaseUnit<>("K",   "KELVIN",   Dimensions.TEMPERATURE),         Temperature.class);
        public Unit<ElectricCurrent>   AMPERE   = addUnit(new BaseUnit<>("A",   "AMPERE",   Dimensions.ELECTRIC_CURRENT),    ElectricCurrent.class);
        public Unit<LuminousIntensity> CANDELA  = addUnit(new BaseUnit<>("cd",  "CANDELA",  Dimensions.LUMINOUS_INTENSITY),  LuminousIntensity.class);
        public Unit<AmountOfSubstance> MOLE     = addUnit(new BaseUnit<>("mol", "MOLE",     Dimensions.AMOUNT_OF_SUBSTANCE), AmountOfSubstance.class);

        public Unit<Temperature>       CELSIUS  = addUnit(KELVIN.shift(273.15).withSymbol("°C").withName("DEGREE CELSIUS"), Temperature.class);

        public Unit<Angle>             RADIAN   = addUnit(ONE.withSymbol("rad").withName("RADIAN"), Angle.class);
        public Unit<Frequency>         HERTZ    = addUnit(ONE.divide(SECOND).withSymbol("Hz") .withName("HERTZ"), Frequency.class);

        public Unit<Speed>             METER_PER_SECOND = addUnit(METRE.divide(SECOND).withSymbol("m/s").withName("METER PER SECOND"), Speed.class);
        public Unit<Acceleration>      METER_PER_SECOND_SQUARED = addUnit(METER_PER_SECOND.divide(SECOND).withSymbol("m/s²").withName("METER PER SECOND SQUARED"), Acceleration.class);

        public Unit<Area>              SQUARE_METER = addUnit(METRE.multiply(METRE).withSymbol("m²").withName("SQUARE METER"), Area.class);
        public Unit<Volume>            CUBIC_METER  = addUnit(SQUARE_METER.multiply(METRE).withSymbol("m³").withName("CUBIC METER"), Volume.class);

        public Unit<Force>             NEWTON = addUnit(KILOGRAM.multiply(METRE).divide(SECOND.pow(2)).withSymbol("N").withName("NEWTON"), Force.class);
        public Unit<Pressure>          PASCAL = addUnit(NEWTON.divide(SQUARE_METER).withSymbol("Pa").withName("PASCAL"), Pressure.class);
        public Unit<Energy>            JOULE  = addUnit(NEWTON.multiply(METRE).withSymbol("J").withName("JOULE"), Energy.class);
        public Unit<Power>             WATT   = addUnit(JOULE.divide(SECOND).withSymbol("W").withName("WATT"), Power.class);

        public Unit<ElectricCharge>      COULOMB = addUnit(AMPERE.multiply(SECOND).withSymbol("C").withName("COULOMB"), ElectricCharge.class);
        public Unit<ElectricPotential>   VOLT    = addUnit(JOULE.divide(COULOMB).withSymbol("V").withName("VOLT"), ElectricPotential.class);
        public Unit<ElectricCapacitance> FARAD   = addUnit(COULOMB.divide(VOLT).withSymbol("F").withName("FARAD"), ElectricCapacitance.class);
        public Unit<ElectricResistance>  OHM     = addUnit(VOLT.divide(AMPERE).withSymbol("Ω").withName("OHM"), ElectricResistance.class);
        public Unit<ElectricConductance> SIEMENS = addUnit(ONE.divide(OHM).withSymbol("S").withName("SIEMENS"), ElectricConductance.class);

        // Constants expressed in SI units.
        public final Unit<Speed>       C                = buildUnit(METER_PER_SECOND.multiply(299792458, 1).withName("SPEED OF LIGHT"), Speed.class);
        public final Unit<?>           COULOMB_CONSTANT = VOLT.multiply(METRE).divide(AMPERE.multiply(SECOND)).multiply(8.987551787368176E9).withName("COULOMB CONSTANT");
    }

    /**
     * The CGS system of units.
     */
    public static class CGS extends AbstractSystemOfUnits {
        private CGS() {
            super("CGS");
        }

        public Unit<Length> CENTIMETRE = addUnit(SI.METRE.multiply(1, 100).withSymbol("cm").withName("CENTIMETER"), Length.class);
        public Unit<Mass>   GRAM       = addUnit(SI.KILOGRAM.multiply(1, 1000).withSymbol("g") .withName("GRAM"), Mass.class);
        public Unit<Time>   SECOND     = addUnit(Units.SI.SECOND, Time.class);

        public Unit<Force>  DYNE = addUnit(GRAM.multiply(CENTIMETRE).divide(SECOND.pow(2)).withSymbol("dyn").withName("DYNE"), Force.class);

        public Unit<ElectricCharge> STATCOULOMB = addUnit(DYNE.root(2).multiply(CENTIMETRE).divide(Units.SI.COULOMB_CONSTANT.root(2)).withSymbol("statC").withName("STATCOULOMB"), ElectricCharge.class);
        // alternative conversion:
        //public Unit<ElectricCharge> STATCOULOMB = SI.COULOMB.divide(SI.C).multiply(1, 10).asType(ElectricCharge.class).withSymbolAndName("statC", "STATCOULOMB");

    }

    /**
     * The IMPERIAL system of units.
     */
    public static class Imperial extends AbstractSystemOfUnits {
        private Imperial() {
            super("Imperial");
        }

        // length units
        public Unit<Length> YARD  = addUnit(SI.METRE.multiply(new BigDecimal("0.9144")).withSymbol("yd").withName("YARD"), Length.class);
        public Unit<Length> FOOT  = addUnit(YARD    .multiply(1, 3)                    .withSymbol("ft").withName("FOOT"), Length.class);
        public Unit<Length> INCH  = addUnit(FOOT    .multiply(1, 12)                   .withSymbol("in").withName("INCH"), Length.class);
        public Unit<Length> THOU  = addUnit(INCH    .multiply(1, 1000)                 .withSymbol("th").withName("THOU"), Length.class);

        public Unit<Length> CHAIN   = addUnit(YARD   .multiply(22, 1).withSymbol("ch") .withName("CHAIN")  , Length.class);
        public Unit<Length> FURLONG = addUnit(CHAIN  .multiply(10, 1).withSymbol("fur").withName("FURLONG"), Length.class);
        public Unit<Length> MILE    = addUnit(FURLONG.multiply(8, 1) .withSymbol("mi") .withName("MILE")   , Length.class);
        public Unit<Length> LEAGUE  = addUnit(MILE   .multiply(3, 1) .withSymbol("lea").withName("LEAGUE") , Length.class);

        // length units for nautical purposes
        public Unit<Length> NM     = addUnit(SI.METRE.multiply(1852, 1).withSymbol("nm")   .withName("NAUTICAL MILE"), Length.class);
        public Unit<Length> CABLE  = addUnit(NM      .multiply(1, 10)  .withSymbol("cable").withName("CABLE")        , Length.class);
        public Unit<Length> FATHOM = addUnit(CABLE   .multiply(1, 100) .withSymbol("ftm")  .withName("FATHOM")       , Length.class);

        // mass units
        public Unit<Mass> POUND = addUnit(SI.KILOGRAM.multiply(new BigDecimal("0.45359237")).withSymbol("pd").withName("POUND"), Mass.class);
        public Unit<Mass> GRAIN = addUnit(POUND      .multiply(7000, 1)                     .withSymbol("gr").withName("GRAIN"), Mass.class);
    }

    /**
     * Any other units not directly linked to a specific system of units.
     */
    public static class Other extends AbstractSystemOfUnits {
        private Other() {
            super("Other");
        }

        // time units
        public Unit<Time>  MINUTE = addUnit(SI.SECOND.multiply(60, 1).withSymbol("m").withName("MINUTE"), Time.class);
        public Unit<Time>  HOUR   = addUnit(MINUTE   .multiply(60, 1).withSymbol("h").withName("HOUR")  , Time.class);

        public Unit<Angle> DEGREE = addUnit(SI.RADIAN.multiply(PI).multiply(1, 180).withSymbol("deg").withName("DEGREE"), Angle.class);

        // speed units
        public Unit<Speed> KMH = addUnit(SI.METRE.withPrefix(Prefixes.Metric.KILO).divide(HOUR).withSymbol("km/h").withName("KM PER HOUR"), Speed.class);

        // temperature units
        public Unit<Temperature> FAHRENHEIT = addUnit(SI.KELVIN.shift(459.67).multiply(5, 9).withSymbol("°F").withName("FAHRENHEIT"), Temperature.class);
    }
}
