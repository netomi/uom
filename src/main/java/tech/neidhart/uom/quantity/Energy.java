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
package tech.neidhart.uom.quantity;

import tech.neidhart.uom.*;
import tech.neidhart.uom.unit.systems.SI;
import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.quantity.impl.DoubleQuantity;

/**
 * A {@link Quantity} representing a measure of energy.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Energy">Wikipedia: Energy</a>
 *
 * @author Thomas Neidhart
 */
public interface Energy extends TypedQuantity<Energy> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link Energy}.
     * <p>
     * The registered {@link QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link DoubleQuantity} will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link Energy} instance for the given value.
     */
    static Energy of(double value, Unit<Energy> unit) {
        return Quantities.create(value, unit, Energy.class);
    }

    static Energy ofJoule(double value) {
        return of(value, SI.JOULE);
    }

    @Override
    default Unit<Energy> getSystemUnit() {
        return SI.JOULE.getSystemUnit();
    }
}