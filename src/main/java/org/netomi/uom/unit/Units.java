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

import org.netomi.uom.MetricPrefix;
import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.netomi.uom.function.UnitConverters.multiply;
import static org.netomi.uom.function.UnitConverters.shift;

public class Units {

    private static ConcurrentHashMap<Class<? extends Quantity<?>>, Set<Unit<?>>> unitsPerQuantity = new ConcurrentHashMap<>();

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

    public static Unit<Dimensionless> ONE = new DerivedUnit<>();

    public static final Unit<Dimensionless> PI = ONE.multiply(StrictMath.PI);

    public static class SI {
        private SI() {}

        // Base units of the International System of Units (SI).
        public Unit<Length>          METRE    = addUnit(new BaseUnit<>("m", "METER", Dimensions.LENGTH), Length.class);
        public Unit<Time>            SECOND   = addUnit(new BaseUnit<>("s", "SECOND", Dimensions.TIME), Time.class);
        public Unit<Mass>            KILOGRAM = addUnit(new BaseUnit<>("kg", "KILOGRAM", Dimensions.MASS), Mass.class);
        public Unit<Temperature>     KELVIN   = addUnit(new BaseUnit<>("K", "KELVIN", Dimensions.TEMPERATURE), Temperature.class);
        public Unit<ElectricCurrent> AMPERE   = addUnit(new BaseUnit<>("A", "AMPERE", Dimensions.ELECTRIC_CURRENT), ElectricCurrent.class);

        public Unit<Speed>           METER_PER_SECOND = addUnit(METRE.divide(SECOND).asType(Speed.class).withSymbolAndName("m/s", "METER PER SECOND"), Speed.class);
        public Unit<Force>           NEWTON   = KILOGRAM.multiply(METRE).divide(SECOND.pow(2)).withSymbolAndName("N", "NEWTON").asType(Force.class);

        public Unit<ElectricCharge>  COULOMB   = AMPERE.multiply(SECOND).asType(ElectricCharge.class).withSymbolAndName("C", "COULOMB");

        // Constants expressed in SI units.
        public final Unit<Speed> C = METER_PER_SECOND.multiply(299792458, 1);
    }

    public static class CGS {
        private CGS() {}

        public Unit<Length> CENTIMETRE = MetricPrefix.CENTI(SI.METRE);
        public Unit<Mass>   GRAM       = SI.KILOGRAM.multiply(1, 1000).withSymbolAndName("g", "GRAM");
        public Unit<Time>   SECOND     = Units.SI.SECOND;

        public Unit<Force>  DYN = GRAM.multiply(CENTIMETRE).divide(SECOND.pow(2)).asType(Force.class).withSymbolAndName("dyn", "DYNE");

        public Unit<ElectricCharge> STATCOULOMB = SI.COULOMB.divide(SI.C).multiply(1, 10).asType(ElectricCharge.class).withSymbolAndName("statC", "STATCOULOMB");
        //public static Unit<ElectricCharge> STATCOULOMB = DYN.root(2).multiply(CENTIMETRE).asType(ElectricCharge.class);

    }

    public static class Imperial {
        private Imperial() {}

        public Unit<Length> YARD  = SI.METRE.multiply(new BigDecimal("0.9144")).withSymbolAndName("yd", "YARD");
        public Unit<Length> FOOT  = YARD.multiply(1, 3).withSymbolAndName("ft", "FOOT");
        public Unit<Length> INCH  = FOOT.multiply(1, 12).withSymbolAndName("in", "INCH");
        public Unit<Length> THOU  = INCH.multiply(1, 1000).withSymbolAndName("th", "THOU");

        public Unit<Length> CHAIN   = YARD.multiply(22, 1).withSymbolAndName("ch", "CHAIN");
        public Unit<Length> FURLONG = CHAIN.multiply(10, 1).withSymbolAndName("fur", "FURLONG");
        public Unit<Length> MILE    = FURLONG.multiply(8, 1).withSymbolAndName("mi", "MILE");
        public Unit<Length> LEAGUE  = MILE.multiply(3, 1).withSymbolAndName("lea", "LEAGUE");

        public Unit<Length> NM     = SI.METRE.multiply(1852, 1).withSymbolAndName("nm", "NAUTICAL MILE");
        public Unit<Length> CABLE  = NM.multiply(1, 10).withSymbolAndName("cable", "CABLE");
        public Unit<Length> FATHOM = CABLE.multiply(1, 100).withSymbolAndName("ftm", "FATHOM");

        public Unit<Mass> POUND = SI.KILOGRAM.multiply(new BigDecimal("0.45359237")).withSymbolAndName("pd", "POUND");
        public Unit<Mass> GRAIN = POUND.multiply(7000, 1).withSymbolAndName("gr", "GRAIN");
    }

    public static class Other {
        private Other() {}

        public Unit<Time>  MINUTE = SI.SECOND.multiply(60, 1).withSymbolAndName("m", "MINUTE");
        public Unit<Time>  HOUR   = MINUTE.multiply(60, 1).withSymbolAndName("h", "HOUR");

        public Unit<Speed> KMH = addUnit(SI.METRE.withPrefix(MetricPrefix.KILO).divide(HOUR).asType(Speed.class).withSymbolAndName("km/h", "KM PER HOUR"), Speed.class);

        public Unit<Temperature> CELSIUS    = SI.KELVIN.shift(273.15).withSymbolAndName("°C", "CELSIUS");
        public Unit<Temperature> FAHRENHEIT = SI.KELVIN.transform(shift(459.67).andThen(multiply(5, 9))).withSymbolAndName("°F", "FAHRENHEIT");
    }

    public static SI       SI       = new Units.SI();
    public static CGS      CGS      = new Units.CGS();
    public static Imperial Imperial = new Units.Imperial();
    public static Other    Other    = new Units.Other();

}
