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
import org.netomi.uom.unit.Units;

/**
 * A {@link Quantity} representing a measure of an electric current.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Electric_current">Wikipedia: Electric current</a>
 *
 * @author Thomas Neidhart
 */
public interface ElectricCurrent extends Quantity<ElectricCurrent> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link ElectricCurrent}.
     * <p>
     * The registered {@link org.netomi.uom.QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link org.netomi.uom.quantity.primitive.DoubleQuantity}
     * will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link ElectricCurrent} instance for the given value.
     */
    static ElectricCurrent of(double value, Unit<ElectricCurrent> unit) {
        return Quantities.createQuantity(value, unit, ElectricCurrent.class);
    }

    static ElectricCurrent ofAmpere(double value) {
        return of(value, Units.SI.AMPERE);
    }

    @Override
    ElectricCurrent to(Unit<ElectricCurrent> unit);

    @Override
    ElectricCurrent add(Quantity<ElectricCurrent> addend);

    @Override
    ElectricCurrent subtract(Quantity<ElectricCurrent> subtrahend);

    @Override
    ElectricCurrent negate();
}