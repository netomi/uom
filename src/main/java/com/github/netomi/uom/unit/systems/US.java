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

import com.github.netomi.uom.Unit;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.quantity.mechanical.Area;
import com.github.netomi.uom.quantity.Length;
import com.github.netomi.uom.unit.Units;

/**
 * The US customary system of units.
 */
public final class US extends AbstractSystemOfUnits {
    // hide constructor.
    private US() {
        super("US Customary");
    }

    public static final US INSTANCE = new US();

    // length units
    public static final Unit<Length> INCH    = addUnit("in us", Length.class);
    public static final Unit<Length> FOOT    = addUnit("ft us", Length.class);
    public static final Unit<Length> YARD    = addUnit("yd us", Length.class);
    public static final Unit<Length> ROD     = addUnit("rd us", Length.class);
    public static final Unit<Length> LINK    = addUnit("lk us", Length.class);
    public static final Unit<Length> CHAIN   = addUnit("ch us", Length.class);
    public static final Unit<Length> FURLONG = addUnit("fur us", Length.class);
    public static final Unit<Length> MILE    = addUnit("mi us", Length.class);

    public static final Unit<Length> FATHOM  = addUnit("fth us", Length.class);

    // area units
    public static final Unit<Area> SQUARE_FOOT  = addUnit("sq ft us", Area.class);
    public static final Unit<Area> SQUARE_ROD   = addUnit("sq rd us", Area.class);
    public static final Unit<Area> SQUARE_CHAIN = addUnit("sq ch us", Area.class);

    public static final Unit<Area> ACRE         = addUnit("ac us",    Area.class);
    public static final Unit<Area> SQUARE_MILE  = addUnit("sq mi us", Area.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
