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
package tech.neidhart.uom.quantity.molar;

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.QuantityFactory;
import tech.neidhart.uom.TypedQuantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.quantity.Quantities;
import tech.neidhart.uom.quantity.impl.DoubleQuantity;
import tech.neidhart.uom.unit.systems.SI;

/**
 * A {@link Quantity} representing a measure of an amount of substance.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Amount_of_substance">Wikipedia: Amount of substance</a>
 *
 * @author Thomas Neidhart
 */
public interface AmountOfSubstance extends TypedQuantity<AmountOfSubstance, AmountOfSubstance> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link AmountOfSubstance}.
     * <p>
     * The registered {@link QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link DoubleQuantity} will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link AmountOfSubstance} instance for the given value.
     */
    static AmountOfSubstance of(double value, Unit<AmountOfSubstance> unit) {
        return Quantities.create(value, unit, AmountOfSubstance.class);
    }

    static AmountOfSubstance ofMole(double value) {
        return of(value, SI.MOLE);
    }

    @Override
    default Unit<AmountOfSubstance> getSystemUnit() {
        return SI.MOLE.getSystemUnit();
    }
}