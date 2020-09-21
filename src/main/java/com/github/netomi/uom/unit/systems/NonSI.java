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
import com.github.netomi.uom.quantity.Angle;
import com.github.netomi.uom.quantity.Time;
import com.github.netomi.uom.quantity.kinematic.Speed;
import com.github.netomi.uom.quantity.mechanical.Area;
import com.github.netomi.uom.quantity.thermodynamic.Temperature;
import com.github.netomi.uom.unit.Units;

/**
 * Any other units not directly linked to a specific system of units.
 */
public final class NonSI extends AbstractSystemOfUnits {
    // hide constructor.
    private NonSI() {
        super("Non-SI");
    }

    public static final NonSI INSTANCE = new NonSI();

    // area units
    public static final Unit<Area> ARE       = addUnit("a",        Area.class);
    public static final Unit<Area> HECTARE   = addUnit("ha",       Area.class);

    // time units
    public static final Unit<Time>  MINUTE = addUnit("min", Time.class);
    public static final Unit<Time>  HOUR   = addUnit("h",   Time.class);
    public static final Unit<Time>  DAY    = addUnit("d",   Time.class);

    public static final Unit<Angle> DEGREE = addUnit("deg", Angle.class);

    // speed units
    public static final Unit<Speed> KMH  = addUnit("km/h", Speed.class);

    // temperature units
    public static final Unit<Temperature> FAHRENHEIT = addUnit("°F", Temperature.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
