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
public final class Units {

    private static Map<Unit<?>, Unit<?>> namedUnits = new ConcurrentHashMap<>();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new DerivedUnit<>();
    public static final BigDecimal          PI  = BigDecimal.valueOf(StrictMath.PI);

    // System of Units.
    public static final SI       SI       = new Units.SI();
    public static final CGS      CGS      = new Units.CGS();
    public static final Imperial Imperial = new Units.Imperial();
    public static final Other    Other    = new Units.Other();

    static {
        for (SystemOfUnits system : Arrays.asList(SI, CGS, Imperial, Other)) {
            for (Unit<?> unit : system.getUnits()) {
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

    public static <Q extends Quantity<Q>> UnitBuilder<Q> buildFromAny(Unit<?> unit) {
        return UnitBuilder.fromAny(unit);
    }

    public static <Q extends Quantity<Q>> UnitBuilder<Q> buildFrom(Unit<Q> unit) {
        return UnitBuilder.from(unit);
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

        public Unit<Temperature>       CELSIUS  = addUnit(buildFrom(KELVIN).shiftedBy(273.15).withSymbol("°C").withName("DEGREE CELSIUS").build(), Temperature.class);

        public Unit<Angle>             RADIAN   = addUnit(UnitBuilder.<Angle>fromAny(ONE).withSymbol("rad").withName("RADIAN").build(), Angle.class);
        public Unit<Frequency>         HERTZ    = addUnit(UnitBuilder.<Frequency>fromAny(ONE.divide(SECOND)).withSymbol("Hz") .withName("HERTZ") .build(), Frequency.class);

        public Unit<Speed>             METER_PER_SECOND =
                addUnit(UnitBuilder.<Speed>fromAny(METRE.divide(SECOND)).withSymbol("m/s").withName("METER PER SECOND").build(), Speed.class);

        public Unit<Acceleration>      METER_PER_SECOND_SQUARED =
                addUnit(UnitBuilder.<Acceleration>fromAny(METER_PER_SECOND.divide(SECOND))
                                   .withSymbol("m/s²")
                                   .withName("METER PER SECOND SQUARED")
                                   .build(), Acceleration.class);

        public Unit<Area>              SQUARE_METER =
                addUnit(UnitBuilder.<Area>fromAny(METRE.multiply(METRE))
                                   .withSymbol("m²")
                                   .withName("SQUARE METER")
                                   .build(), Area.class);

        public Unit<Volume>            CUBIC_METER  =
                addUnit(UnitBuilder.<Volume>fromAny(SQUARE_METER.multiply(METRE))
                                  .withSymbol("m³")
                                  .withName("CUBIC METER")
                                  .build(), Volume.class);

        public Unit<Force>             NEWTON   =
                addUnit(UnitBuilder.<Force>fromAny(KILOGRAM.multiply(METRE).divide(SECOND.pow(2)))
                                   .withSymbol("N")
                                   .withName("NEWTON")
                                   .build(), Force.class);

        public Unit<Pressure>          PASCAL   =
                addUnit(UnitBuilder.<Pressure>fromAny(NEWTON.divide(SQUARE_METER))
                                   .withSymbol("Pa")
                                   .withName("PASCAL")
                                   .build(), Pressure.class);

        public Unit<Energy>            JOULE  = addUnit(UnitBuilder.<Energy>fromAny(NEWTON.multiply(METRE)) .withSymbol("J").withName("JOULE").build(), Energy.class);
        public Unit<Power>             WATT   = addUnit(UnitBuilder.<Power>fromAny (JOULE.divide(SECOND))   .withSymbol("W").withName("WATT") .build(), Power.class);

        public Unit<ElectricCharge>    COULOMB =
                addUnit(UnitBuilder.<ElectricCharge>fromAny   (AMPERE.multiply(SECOND))
                                   .withSymbol("C")
                                   .withName("COULOMB")
                                   .build(), ElectricCharge.class);

        public Unit<ElectricPotential> VOLT    =
                addUnit(UnitBuilder.<ElectricPotential>fromAny(JOULE.divide(COULOMB))
                                   .withSymbol("V")
                                   .withName("VOLT")
                                   .build(), ElectricPotential.class);

        public Unit<ElectricCapacitance> FARAD =
                addUnit(UnitBuilder.<ElectricCapacitance>fromAny(COULOMB.divide(VOLT))
                                   .withSymbol("F")
                                   .withName("FARAD")
                                   .build(), ElectricCapacitance.class);

        public Unit<ElectricResistance> OHM =
                addUnit(UnitBuilder.<ElectricResistance>fromAny(VOLT.divide(AMPERE))
                                   .withSymbol("Ω")
                                   .withName("OHM")
                                   .build(), ElectricResistance.class);

        // Constants expressed in SI units.
        public final Unit<Speed>       C                = buildFrom(METER_PER_SECOND).multipliedBy(299792458, 1).withName("SPEED OF LIGHT").build();
        public final Unit<?>           COULOMB_CONSTANT = buildFrom(VOLT.multiply(METRE).divide(AMPERE.multiply(SECOND))).multipliedBy(8.987551787368176E9).withName("COULOMB CONSTANT").build();
    }

    /**
     * The CGS system of units.
     */
    public static class CGS extends AbstractSystemOfUnits {
        private CGS() {
            super("CGS");
        }

        public Unit<Length> CENTIMETRE = addUnit(buildFrom(SI.METRE)   .multipliedBy(1, 100) .withSymbol("cm").withName("CENTIMETER").build(), Length.class);
        public Unit<Mass>   GRAM       = addUnit(buildFrom(SI.KILOGRAM).multipliedBy(1, 1000).withSymbol("g") .withName("GRAM")      .build(), Mass.class);
        public Unit<Time>   SECOND     = addUnit(Units.SI.SECOND, Time.class);

        public Unit<Force>  DYNE = addUnit(UnitBuilder.<Force>fromAny(GRAM.multiply(CENTIMETRE).divide(SECOND.pow(2)))
                                                      .withSymbol("dyn")
                                                      .withName("DYNE")
                                                      .build(), Force.class);

        //public Unit<ElectricCharge> STATCOULOMB = SI.COULOMB.divide(SI.C).multiply(1, 10).asType(ElectricCharge.class).withSymbolAndName("statC", "STATCOULOMB");
        public Unit<ElectricCharge> STATCOULOMB =
                addUnit(UnitBuilder.<ElectricCharge>fromAny(DYNE.root(2).multiply(CENTIMETRE).divide(Units.SI.COULOMB_CONSTANT.root(2)))
                                   .withSymbol("statC")
                                   .withName("STATCOULOMB")
                                   .build(), ElectricCharge.class);
    }

    /**
     * The IMPERIAL system of units.
     */
    public static class Imperial extends AbstractSystemOfUnits {
        private Imperial() {
            super("Imperial");
        }

        // length units
        public Unit<Length> YARD  = addUnit(buildFrom(SI.METRE).multipliedBy(new BigDecimal("0.9144")).withSymbol("yd").withName("YARD").build(), Length.class);
        public Unit<Length> FOOT  = addUnit(buildFrom(YARD)    .multipliedBy(1, 3)                    .withSymbol("ft").withName("FOOT").build(), Length.class);
        public Unit<Length> INCH  = addUnit(buildFrom(FOOT)    .multipliedBy(1, 12)                   .withSymbol("in").withName("INCH").build(), Length.class);
        public Unit<Length> THOU  = addUnit(buildFrom(INCH)    .multipliedBy(1, 1000)                 .withSymbol("th").withName("THOU").build(), Length.class);

        public Unit<Length> CHAIN   = addUnit(buildFrom(YARD)   .multipliedBy(22, 1).withSymbol("ch") .withName("CHAIN")  .build(), Length.class);
        public Unit<Length> FURLONG = addUnit(buildFrom(CHAIN)  .multipliedBy(10, 1).withSymbol("fur").withName("FURLONG").build(), Length.class);
        public Unit<Length> MILE    = addUnit(buildFrom(FURLONG).multipliedBy(8, 1) .withSymbol("mi") .withName("MILE")   .build(), Length.class);
        public Unit<Length> LEAGUE  = addUnit(buildFrom(MILE)   .multipliedBy(3, 1) .withSymbol("lea").withName("LEAGUE") .build(), Length.class);

        // length units for nautical purposes
        public Unit<Length> NM     = addUnit(buildFrom(SI.METRE).multipliedBy(1852, 1).withSymbol("nm")   .withName("NAUTICAL MILE").build(), Length.class);
        public Unit<Length> CABLE  = addUnit(buildFrom(NM)      .multipliedBy(1, 10)  .withSymbol("cable").withName("CABLE")        .build(), Length.class);
        public Unit<Length> FATHOM = addUnit(buildFrom(CABLE)   .multipliedBy(1, 100) .withSymbol("ftm")  .withName("FATHOM")       .build(), Length.class);

        // mass units
        public Unit<Mass> POUND = addUnit(buildFrom(SI.KILOGRAM).multipliedBy(new BigDecimal("0.45359237")).withSymbol("pd").withName("POUND").build(), Mass.class);
        public Unit<Mass> GRAIN = addUnit(buildFrom(POUND)      .multipliedBy(7000, 1)                     .withSymbol("gr").withName("GRAIN").build(), Mass.class);
    }

    /**
     * Any other units not directly linked to a specific system of units.
     */
    public static class Other extends AbstractSystemOfUnits {
        private Other() {
            super("Other");
        }

        // time units
        public Unit<Time>  MINUTE = addUnit(buildFrom(SI.SECOND).multipliedBy(60, 1).withSymbol("m").withName("MINUTE").build(), Time.class);
        public Unit<Time>  HOUR   = addUnit(buildFrom(MINUTE)   .multipliedBy(60, 1).withSymbol("h").withName("HOUR")  .build(), Time.class);

        public Unit<Angle> DEGREE = addUnit(buildFrom(SI.RADIAN).multipliedBy(PI).multipliedBy(1, 180).withSymbol("deg").withName("DEGREE").build(), Angle.class);

        // speed units
        public Unit<Speed> KMH = addUnit(UnitBuilder.<Speed>fromAny(SI.METRE.withPrefix(Prefixes.Metric.KILO).divide(HOUR)).withSymbol("km/h").withName("KM PER HOUR").build(), Speed.class);

        // temperature units
        public Unit<Temperature> FAHRENHEIT = addUnit(buildFrom(SI.KELVIN).shiftedBy(459.67).multipliedBy(5, 9).withSymbol("°F").withName("FAHRENHEIT").build(), Temperature.class);
    }
}
