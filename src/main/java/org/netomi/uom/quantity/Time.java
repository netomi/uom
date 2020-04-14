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
 * A {@link Quantity} representing a measure of time.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Time">Wikipedia: Time</a>
 *
 * @author Thomas Neidhart
 */
public interface Time extends Quantity<Time> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link Time}.
     * <p>
     * The registered {@link org.netomi.uom.QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link org.netomi.uom.quantity.impl.DoubleQuantity}
     * will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link Time} instance for the given value.
     */
    static Time of(double value, Unit<Time> unit) {
        return Quantities.createQuantity(value, unit, Time.class);
    }

    static Time ofSecond(double value) {
        return of(value, SI.SECOND);
    }

    @Override
    default Unit<Time> getSystemUnit() {
        return SI.SECOND;
    }

    @Override
    default Frequency reciprocal() {
        return one().divide(this, Frequency.class);
    }
}
