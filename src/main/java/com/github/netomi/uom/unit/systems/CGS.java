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
import com.github.netomi.uom.quantity.Energy;
import com.github.netomi.uom.quantity.Length;
import com.github.netomi.uom.quantity.Mass;
import com.github.netomi.uom.quantity.Time;
import com.github.netomi.uom.quantity.mechanical.Force;
import com.github.netomi.uom.unit.Units;

/**
 * The CGS system of units.
 */
public class CGS extends AbstractSystemOfUnits {
    // hide constructor.
    private CGS() {
        super("CGS");
    }

    protected CGS(String name) {
        super(name);
    }

    public static final CGS INSTANCE = new CGS();

    // base units
    public static final Unit<Length> CENTIMETRE = addUnit("cm", Length.class);
    public static final Unit<Mass>   GRAM       = addUnit("g",  Mass.class);
    public static final Unit<Time>   SECOND     = addUnit("s",  Time.class);

    public static final Unit<Force>  DYNE = addUnit("dyn", Force.class);
    public static final Unit<Energy> ERG  = addUnit("erg", Energy.class);

    // private helper methods.

    private static <Q extends Quantity<Q>> Unit<Q> addUnit(String symbol, Class<Q> quantityClass) {
        return INSTANCE.addUnitForQuantity(Units.get(symbol, quantityClass), quantityClass);
    }
}
