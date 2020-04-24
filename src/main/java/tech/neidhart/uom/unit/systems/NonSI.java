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
import tech.neidhart.uom.unit.Prefixes;
import tech.neidhart.uom.unit.Units;

/**
 * Any other units not directly linked to a specific system of units.
 */
public final class NonSI extends AbstractSystemOfUnits {
    // hide constructor.
    private NonSI() {
        super("Non-SI");
    }

    public static final NonSI INSTANCE = new NonSI();

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<?> unit, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(unit, quantityClass);
    }

    // length units
    public static final Unit<Length> NAUTICAL_MILE = addUnit(SI.METRE.multiply(1852, 1).withSymbol("nm").withName("NAUTICAL MILE"), Length.class);

    // area units
    public static final Unit<Area> ARE       = addUnit(SI.METRE.pow(2).multiply(100, 1).withSymbol("a").withName("ARE"), Area.class);
    public static final Unit<Area> HECTARE   = addUnit(SI.METRE.pow(2).multiply(10000, 1).withSymbol("ha").withName("HECTARE"), Area.class);
    public static final Unit<Area> SQUARE_KM = addUnit(SI.METRE.withPrefix(Prefixes.Metric.KILO).pow(2).withSymbol("km\u00B2").withName("SQUARE KILOMETER"), Area.class);

    // time units
    public static final Unit<Time>  MINUTE = addUnit(SI.SECOND.multiply(60, 1).withSymbol("m").withName("MINUTE"), Time.class);
    public static final Unit<Time>  HOUR   = addUnit(MINUTE   .multiply(60, 1).withSymbol("h").withName("HOUR")  , Time.class);

    public static final Unit<Angle> DEGREE = addUnit(SI.RADIAN.multiply(Units.PI).multiply(1, 180).withSymbol("deg").withName("DEGREE"), Angle.class);

    // speed units
    public static final Unit<Speed> KMH  = addUnit(SI.METRE.withPrefix(Prefixes.Metric.KILO).divide(HOUR).withSymbol("km/h").withName("KM PER HOUR"), Speed.class);
    public static final Unit<Speed> KNOT = addUnit(NAUTICAL_MILE.divide(HOUR).withSymbol("kt").withName("KNOT"), Speed.class);

    // temperature units
    public static final Unit<Temperature> FAHRENHEIT = addUnit(SI.KELVIN.shift(459.67).multiply(5, 9).withSymbol("°F").withName("FAHRENHEIT"), Temperature.class);

    static {
        Units.register(INSTANCE);
    }
}
