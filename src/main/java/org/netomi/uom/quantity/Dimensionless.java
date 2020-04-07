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
import org.netomi.uom.unit.Dimension;
import org.netomi.uom.unit.Units;

/**
 * A {@link Quantity} representing a measure without any phyiscal dimension.
 * <p>
 * Examples for this are constants like {@code PI}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Dimensionless">Wikipedia: Dimensionless</a>
 *
 * @author Thomas Neidhart
 */
public interface Dimensionless extends Quantity<Dimensionless> {

    /**
     * Convenience method to create a {@link Quantity} of type {@link Dimensionless}.
     * <p>
     * The registered {@link org.netomi.uom.QuantityFactory} in the class {@link Quantities}
     * is used to generate the concrete implementation, by default a quantity
     * with double precision ({@link org.netomi.uom.quantity.primitive.DoubleQuantity}
     * will be returned.
     *
     * @param value the quantity value, expressed in the given unit.
     * @param unit  the unit corresponding to the value.
     * @return a new {@link Dimensionless} instance for the given value.
     */
    static Dimensionless of(double value, Unit<Dimensionless> unit) {
        return Quantities.createQuantity(value, unit, Dimensionless.class);
    }

    static Dimensionless of(double value) {
        return of(value, Units.ONE);
    }

    @Override
    default Dimension getDimension() {
        return Units.ONE.getDimension();
    }

    @Override
    Dimensionless to(Unit<Dimensionless> unit);

    @Override
    Dimensionless add(Quantity<Dimensionless> addend);

    @Override
    Dimensionless subtract(Quantity<Dimensionless> subtrahend);

    @Override
    Dimensionless negate();
}