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
import org.netomi.uom.unit.Units;

/**
 * The US survey system of units.
 */
public final class US extends AbstractSystemOfUnits {
    // hide constructor.
    private US() {
        super("US Survey");
    }

    public static final US INSTANCE = new US();

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<?> unit, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(unit, quantityClass);
    }

    // length units
    public static final Unit<Length> FOOT  = addUnit(SI.METRE.multiply(1200, 3937).withSymbol("ft (US)").withName("FOOT (US)"), Length.class);
    public static final Unit<Length> LINK  = addUnit(FOOT.multiply(66, 100).withSymbol("lnk (US)").withName("LINK (US)"), Length.class);
    public static final Unit<Length> CHAIN = addUnit(FOOT.multiply(66, 1).withSymbol("ch (US)") .withName("CHAIN (US)")  , Length.class);
    public static final Unit<Length> MILE  = addUnit(FOOT.multiply(5280, 1) .withSymbol("mi (US)") .withName("MILE (US)"), Length.class);

    // area units
    public static final Unit<Area> SQUARE_FOOT  = addUnit(FOOT.multiply(FOOT).withSymbol("sq ft").withName("SQUARE FOOT (US)"), Area.class);
    public static final Unit<Area> SQUARE_LINK  = addUnit(LINK.multiply(LINK).withSymbol("sq lnk").withName("SQUARE LINK (US)"), Area.class);
    public static final Unit<Area> SQUARE_CHAIN = addUnit(CHAIN.multiply(CHAIN).withSymbol("sq ch").withName("SQUARE CHAIN (US)"), Area.class);
    public static final Unit<Area> ACRE         = addUnit(SQUARE_CHAIN.multiply(10, 1).withSymbol("ac").withName("ACRE (US)"), Area.class);
    public static final Unit<Area> SQUARE_MILE  = addUnit(MILE.multiply(MILE).withSymbol("sq mi").withName("SQUARE MILE (US)"), Area.class);

    static {
        Units.register(INSTANCE);
    }
}
