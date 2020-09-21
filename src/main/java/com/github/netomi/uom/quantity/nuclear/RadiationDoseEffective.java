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
package com.github.netomi.uom.quantity.nuclear;

import com.github.netomi.uom.quantity.impl.DoubleQuantity;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.QuantityFactory;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.Quantities;
import com.github.netomi.uom.unit.systems.SI;

/**
 * A {@link Quantity} representing a measure of an effective dose of radioactivity.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Equivalent_dose">Wikipedia: Equivalent dose</a>
 *
 * @author Thomas Neidhart
 */
public interface RadiationDoseEffective extends RadiationDoseEquivalent {

    /**
     * Convenience method to create a {@link Quantity} of type {@link RadiationDoseEffective}.
     * <p>
     * The registered {@link QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link DoubleQuantity} will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link RadiationDoseEffective} instance for the given value.
     */
    static RadiationDoseEffective of(double value, Unit<RadiationDoseEquivalent> unit) {
        return Quantities.create(value, unit, RadiationDoseEffective.class);
    }

    static RadiationDoseEffective ofSievert(double value) {
        return of(value, SI.SIEVERT);
    }

}