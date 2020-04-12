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
package org.netomi.uom.quantity;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.unit.systems.SI;

/**
 * A {@link Quantity} representing a measure of an area.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Area">Wikipedia: Area</a>
 *
 * @author Thomas Neidhart
 */
public interface Area extends Quantity<Area> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link Area}.
     * <p>
     * The registered {@link org.netomi.uom.QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link org.netomi.uom.quantity.impl.DoubleQuantity}
     * will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link Area} instance for the given value.
     */
    static Area of(double value, Unit<Area> unit) {
        return Quantities.createQuantity(value, unit, Area.class);
    }

    static Area ofSquareMeter(double value) {
        return of(value, SI.SQUARE_METER);
    }

    @Override
    default Unit<Area> getSystemUnit() {
        return SI.SQUARE_METER;
    }
}