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
import org.netomi.uom.quantity.Area;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.quantity.Mass;
import org.netomi.uom.unit.Units;

import java.math.BigDecimal;

/**
 * The IMPERIAL system of units.
 */
public class Imperial extends AbstractSystemOfUnits {
    protected Imperial() {
        super("Imperial");
    }

    public static final Imperial INSTANCE = new Imperial();

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<?> unit, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(unit, quantityClass);
    }

    // length units
    public static final Unit<Length> YARD  = addUnit(SI.METRE.multiply(new BigDecimal("0.9144")).withSymbol("yd").withName("YARD"), Length.class);
    public static final Unit<Length> FOOT  = addUnit(YARD    .multiply(1, 3)                    .withSymbol("ft").withName("FOOT"), Length.class);
    public static final Unit<Length> INCH  = addUnit(FOOT    .multiply(1, 12)                   .withSymbol("in").withName("INCH"), Length.class);
    public static final Unit<Length> THOU  = addUnit(INCH    .multiply(1, 1000)                 .withSymbol("th").withName("THOU"), Length.class);

    public static final Unit<Length> CHAIN   = addUnit(YARD   .multiply(22, 1).withSymbol("ch") .withName("CHAIN")  , Length.class);
    public static final Unit<Length> FURLONG = addUnit(CHAIN  .multiply(10, 1).withSymbol("fur").withName("FURLONG"), Length.class);
    public static final Unit<Length> MILE    = addUnit(FURLONG.multiply(8, 1) .withSymbol("mi") .withName("MILE")   , Length.class);
    public static final Unit<Length> LEAGUE  = addUnit(MILE   .multiply(3, 1) .withSymbol("lea").withName("LEAGUE") , Length.class);

    // length units for nautical purposes
    public static final Unit<Length> NM     = addUnit(SI.METRE.multiply(1852, 1).withSymbol("nm")   .withName("NAUTICAL MILE"), Length.class);
    public static final Unit<Length> CABLE  = addUnit(NM      .multiply(1, 10)  .withSymbol("cable").withName("CABLE")        , Length.class);
    public static final Unit<Length> FATHOM = addUnit(CABLE   .multiply(1, 100) .withSymbol("ftm")  .withName("FATHOM")       , Length.class);

    // area units
    public static final Unit<Area> SQUARE_INCH  = addUnit(INCH.multiply(INCH).withSymbol("sq in").withName("SQUARE INCH"), Area.class);
    public static final Unit<Area> SQUARE_FOOT  = addUnit(FOOT.multiply(FOOT).withSymbol("sq ft").withName("SQUARE FOOT"), Area.class);
    public static final Unit<Area> SQUARE_YARD  = addUnit(YARD.multiply(YARD).withSymbol("sq yd").withName("SQUARE YARD"), Area.class);
    public static final Unit<Area> SQUARE_CHAIN = addUnit(CHAIN.multiply(CHAIN).withSymbol("sq ch").withName("SQUARE CHAIN"), Area.class);
    public static final Unit<Area> SQUARE_MILE  = addUnit(MILE.multiply(MILE).withSymbol("sq mi").withName("SQUARE MILE"), Area.class);
    public static final Unit<Area> ACRE         = addUnit(CHAIN.multiply(FURLONG).withSymbol("ac").withName("ACRE"), Area.class);

    // mass units
    public static final Unit<Mass> POUND = addUnit(SI.KILOGRAM.multiply(new BigDecimal("0.45359237")).withSymbol("pd").withName("POUND"), Mass.class);
    public static final Unit<Mass> GRAIN = addUnit(POUND      .multiply(7000, 1)                     .withSymbol("gr").withName("GRAIN"), Mass.class);

    static {
        Units.register(INSTANCE);
    }
}
