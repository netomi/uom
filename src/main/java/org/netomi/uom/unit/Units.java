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

import org.netomi.uom.Prefix;
import org.netomi.uom.Quantity;
import org.netomi.uom.SystemOfUnits;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Units {

    private static ConcurrentHashMap<Class<? extends Quantity<?>>, Set<Unit<?>>> unitsPerQuantity = new ConcurrentHashMap<>();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new DerivedUnit<>();
    public static final Unit<Dimensionless> PI  = ONE.multiply(StrictMath.PI);

    // System of Units.
    public static final SI       SI       = new Units.SI();
    public static final CGS      CGS      = new Units.CGS();
    public static final Imperial Imperial = new Units.Imperial();
    public static final Other    Other    = new Units.Other();

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<Q> unit, Class<Q> quantityClass) {
        Set<Unit<?>> knownUnits = unitsPerQuantity.get(quantityClass);

        if (knownUnits == null) {
            knownUnits = new LinkedHashSet<>();
            unitsPerQuantity.put(quantityClass, knownUnits);
        }

        knownUnits.add(unit);

        return unit;
    }

    public static <Q extends Quantity<Q>> Set<? extends Unit<Q>> unitsForQuantity(Class<Q> quantityClass) {
        Set<Unit<?>> knownUnits = unitsPerQuantity.get(quantityClass);

        if (knownUnits == null) {
            return Collections.emptySet();
        } else {
            return (Set) Collections.unmodifiableSet(knownUnits);
        }
    }

    public static <Q extends Quantity<Q>> UnitBuilder<Q> buildFrom(Unit<Q> unit) {
        return new UnitBuilder(unit);
    }

    /**
     * The SI system of units.
     */
    public static class SI implements SystemOfUnits {
        private SI() {}

        @Override
        public Type getType() {
            return Type.SI;
        }

        // Base units of the International System of Units (SI).
        public Unit<Length>          METRE    = addUnit(new BaseUnit<>("m", "METER", Dimensions.LENGTH), Length.class);
        public Unit<Time>            SECOND   = addUnit(new BaseUnit<>("s", "SECOND", Dimensions.TIME), Time.class);
        public Unit<Mass>            KILOGRAM = addUnit(new BaseUnit<>("kg", "KILOGRAM", Dimensions.MASS), Mass.class);
        public Unit<Temperature>     KELVIN   = addUnit(new BaseUnit<>("K", "KELVIN", Dimensions.TEMPERATURE), Temperature.class);
        public Unit<ElectricCurrent> AMPERE   = addUnit(new BaseUnit<>("A", "AMPERE", Dimensions.ELECTRIC_CURRENT), ElectricCurrent.class);

        public Unit<Speed>           M_PER_S = buildFrom(METRE.divide(SECOND)).withSymbol("m/s").withName("METER PER SECOND").forQuantity(Speed.class).build();

        public Unit<Force>           NEWTON   = buildFrom(KILOGRAM.multiply(METRE).divide(SECOND.pow(2))).withSymbol("N").withName("NEWTON").forQuantity(Force.class).build();

        public Unit<Energy>            JOULE     = buildFrom(NEWTON.multiply(METRE)) .withSymbol("J").withName("JOULE")  .forQuantity(Energy.class)           .build();
        public Unit<ElectricCharge>    COULOMB   = buildFrom(AMPERE.multiply(SECOND)).withSymbol("C").withName("COULOMB").forQuantity(ElectricCharge.class)   .build();
        public Unit<ElectricPotential> VOLT      = buildFrom(JOULE.divide(COULOMB)).withSymbol("V").withName("VOLT")     .forQuantity(ElectricPotential.class).build();

        // Constants expressed in SI units.
        public final Unit<Speed> C                = buildFrom(M_PER_S).multipliedBy(299792458, 1).withName("SPEED OF LIGHT").build();
        public final Unit<?>     COULOMB_CONSTANT = buildFrom(VOLT.multiply(METRE).divide(AMPERE.multiply(SECOND))).multipliedBy(8.987551787368176E9).withName("COULOMB CONSTANT").build();
    }

    /**
     * The CGS system of units.
     */
    public static class CGS implements SystemOfUnits {
        private CGS() {}

        @Override
        public Type getType() {
            return Type.CGS;
        }

        public Unit<Length> CENTIMETRE = buildFrom(SI.METRE)   .multipliedBy(1, 100) .withSymbol("cm").withName("CENTIMETER").build();
        public Unit<Mass>   GRAM       = buildFrom(SI.KILOGRAM).multipliedBy(1, 1000).withSymbol("g") .withName("GRAM")      .build();
        public Unit<Time>   SECOND     = Units.SI.SECOND;

        public Unit<Force>  DYN = buildFrom(GRAM.multiply(CENTIMETRE).divide(SECOND.pow(2))).withSymbol("dyn").withName("DYNE").forQuantity(Force.class).build();

        //public Unit<ElectricCharge> STATCOULOMB = SI.COULOMB.divide(SI.C).multiply(1, 10).asType(ElectricCharge.class).withSymbolAndName("statC", "STATCOULOMB");
        public Unit<ElectricCharge> STATCOULOMB = buildFrom(DYN.root(2).multiply(CENTIMETRE).divide(Units.SI.COULOMB_CONSTANT.root(2))).withSymbol("statC").withName("STATCOULOMB").forQuantity(ElectricCharge.class).build();
    }

    /**
     * The IMPERIAL system of units.
     */
    public static class Imperial implements SystemOfUnits {
        private Imperial() {}

        @Override
        public Type getType() {
            return Type.IMPERIAL;
        }

        // length units
        public Unit<Length> YARD  = buildFrom(SI.METRE).multipliedBy(new BigDecimal("0.9144")).withSymbol("yd").withName("YARD").build();
        public Unit<Length> FOOT  = buildFrom(YARD)    .multipliedBy(1, 3)                    .withSymbol("ft").withName("FOOT").build();
        public Unit<Length> INCH  = buildFrom(FOOT)    .multipliedBy(1, 12)                   .withSymbol("in").withName("INCH").build();
        public Unit<Length> THOU  = buildFrom(INCH)    .multipliedBy(1, 1000)                 .withSymbol("th").withName("THOU").build();

        public Unit<Length> CHAIN   = buildFrom(YARD)   .multipliedBy(22, 1).withSymbol("ch") .withName("CHAIN")  .build();
        public Unit<Length> FURLONG = buildFrom(CHAIN)  .multipliedBy(10, 1).withSymbol("fur").withName("FURLONG").build();
        public Unit<Length> MILE    = buildFrom(FURLONG).multipliedBy(8, 1) .withSymbol("mi") .withName("MILE")   .build();
        public Unit<Length> LEAGUE  = buildFrom(MILE)   .multipliedBy(3, 1) .withSymbol("lea").withName("LEAGUE") .build();

        // length units for nautical purposes
        public Unit<Length> NM     = buildFrom(SI.METRE).multipliedBy(1852, 1).withSymbol("nm")   .withName("NAUTICAL MILE").build();
        public Unit<Length> CABLE  = buildFrom(NM)      .multipliedBy(1, 10)  .withSymbol("cable").withName("CABLE")        .build();
        public Unit<Length> FATHOM = buildFrom(CABLE)   .multipliedBy(1, 100) .withSymbol("ftm")  .withName("FATHOM")       .build();

        // mass units
        public Unit<Mass> POUND = buildFrom(SI.KILOGRAM).multipliedBy(new BigDecimal("0.45359237")).withSymbol("pd").withName("POUND").build();
        public Unit<Mass> GRAIN = buildFrom(POUND)      .multipliedBy(7000, 1)                     .withSymbol("gr").withName("GRAIN").build();
    }

    /**
     * Any other units not directly linked to a specific system of units.
     */
    public static class Other implements SystemOfUnits {
        private Other() {}

        @Override
        public Type getType() {
            return Type.NONE;
        }

        // time units
        public Unit<Time>  MINUTE = buildFrom(SI.SECOND).multipliedBy(60, 1).withSymbol("m").withName("MINUTE").build();
        public Unit<Time>  HOUR   = buildFrom(MINUTE)   .multipliedBy(60, 1).withSymbol("h").withName("HOUR")  .build();

        // speed units
        public Unit<Speed> KMH = buildFrom(SI.METRE.withPrefix(Prefixes.Metric.KILO).divide(HOUR)).withSymbol("km/h").withName("KM PER HOUR").forQuantity(Speed.class).build();

        // temperature units
        public Unit<Temperature> CELSIUS    = buildFrom(SI.KELVIN).shiftedBy(273.15)                   .withSymbol("°C").withName("CELSIUS")   .build();
        public Unit<Temperature> FAHRENHEIT = buildFrom(SI.KELVIN).shiftedBy(459.67).multipliedBy(5, 9).withSymbol("°F").withName("FAHRENHEIT").build();
    }
}
