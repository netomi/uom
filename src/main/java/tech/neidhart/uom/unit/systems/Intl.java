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
import tech.neidhart.uom.quantity.Area;
import tech.neidhart.uom.quantity.Length;
import tech.neidhart.uom.quantity.Mass;
import tech.neidhart.uom.quantity.Speed;
import tech.neidhart.uom.unit.Units;

/**
 * The international customary (or Imperial) system of units which uses units as defined by the
 * <a href="https://en.wikipedia.org/wiki/International_yard_and_pound">international
 * yard and pound agreement of 1959</a>.
 */
public final class Intl extends AbstractSystemOfUnits {
    // hide constructor.
    private Intl() {
        super("International customary");
    }

    public static final Intl INSTANCE = new Intl();

    // length units
    public static final Unit<Length> THOU  = addUnit("th", Length.class);
    public static final Unit<Length> INCH  = addUnit("in", Length.class);
    public static final Unit<Length> FOOT  = addUnit("ft", Length.class);
    public static final Unit<Length> YARD  = addUnit("yd", Length.class);
    public static final Unit<Length> CHAIN = addUnit("ch", Length.class);

    public static final Unit<Length> FURLONG = addUnit("fur", Length.class);
    public static final Unit<Length> MILE    = addUnit("mi",  Length.class);
    public static final Unit<Length> LEAGUE  = addUnit("lea", Length.class);

    // length units for nautical purposes
    public static final Unit<Length> FATHOM        = addUnit("fth",   Length.class);
    public static final Unit<Length> CABLE         = addUnit("cable", Length.class);
    public static final Unit<Length> NAUTICAL_MILE = addUnit("nmi",   Length.class);

    // speed units
    public static final Unit<Speed> KNOT = addUnit("kt", Speed.class);

    // area units
    public static final Unit<Area> SQUARE_INCH  = addUnit("sq in", Area.class);
    public static final Unit<Area> SQUARE_FOOT  = addUnit("sq ft", Area.class);
    public static final Unit<Area> SQUARE_YARD  = addUnit("sq yd", Area.class);
    public static final Unit<Area> SQUARE_CHAIN = addUnit("sq ch", Area.class);
    public static final Unit<Area> SQUARE_MILE  = addUnit("sq mi", Area.class);
    public static final Unit<Area> ACRE         = addUnit("ac",    Area.class);

    // mass units
    public static final Unit<Mass> GRAIN = addUnit("gr", Mass.class);
    public static final Unit<Mass> OUNCE = addUnit("oz", Mass.class);
    public static final Unit<Mass> POUND = addUnit("lb", Mass.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
