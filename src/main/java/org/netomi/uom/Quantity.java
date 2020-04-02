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
package org.netomi.uom;

import java.math.BigDecimal;

/**
 * Represents a physical quantity expressed in a specific unit of measurement.
 *
 * @param <Q> the quantity type.
 *
 * @author Thomas Neidhart
 */
public interface Quantity<Q extends Quantity<Q>> {

    /**
     * Returns the value of this quantity expressed in its {@link Unit}.
     *
     * @return the value of this quantity expressed in its {@link Unit}.
     */
    double doubleValue();

    /**
     * Returns the value of this quantity with as {@link BigDecimal} expressed
     * in its {@link Unit}.
     *
     * @return the decimal value of this quantity expressed in its {@link Unit}.
     */
    BigDecimal decimalValue();

    /**
     * Returns the {@link Unit} of this {@link Quantity}.
     *
     * @return the {@link Unit} of this {@link Quantity}.
     */
    Unit<Q> getUnit();

    /**
     * Returns a new {@link Quantity} with its value expressed in the
     * given {@link Unit}.
     *
     * @param unit the {@link Unit} to convert to.
     * @return a new {@link Quantity} with this value expressed in the given {@link Unit}.
     */
    Quantity<Q> to(Unit<Q> unit);

    /**
     * Returns a new {@link Quantity} that is the result of adding the given quantity
     * to this quantity. The resulting quantity will be expressed in the
     * same {@link Unit} as this quantity.
     *
     * @param addend the {@link Quantity} to add to this quantity.
     * @return a new {@link Quantity} representing the sum of this and the other quantity.
     */
    Quantity<Q> add(Quantity<Q> addend);

    /**
     * Returns a new {@link Quantity} that is the result of adding the given quantity
     * to this quantity expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * added.
     *
     * @param addend the {@link Quantity} to add to this quantity.
     * @param unit   the {@link Unit} of the resulting quantity.
     * @return a new {@link Quantity} representing the sum of this and the other quantity
     */
    default Quantity<Q> add(Quantity<Q> addend, Unit<Q> unit) {
        return this.to(unit).add(addend.to(unit));
    }

    /**
     * Returns a new {@link Quantity} that is the result of subtracting the given quantity
     * from this quantity. The resulting quantity will be expressed in the
     * same {@link Unit} as this quantity.
     *
     * @param subtrahend the {@link Quantity} to subtract from this quantity.
     * @return a new {@link Quantity} which is the result of subtracting the other quantity from this.
     */
    Quantity<Q> subtract(Quantity<Q> subtrahend);

    /**
     * Returns a new {@link Quantity} that is the result of subtracting the given quantity
     * from this quantity expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * subtracted.
     *
     * @param subtrahend the {@link Quantity} to subtract from this quantity.
     * @param unit       the {@link Unit} of the resulting quantity.
     * @return a new {@link Quantity} which is the result of subtracting the other quantity from this.
     */
    default Quantity<Q> subtract(Quantity<Q> subtrahend, Unit<Q> unit) {
        return this.to(unit).subtract(subtrahend.to(unit));
    }

    /**
     * Returns a new {@link Quantity} whose value is {@code -this}.
     *
     * @return a new {@link Quantity} whose value is {@code -this}.
     */
    Quantity<Q> negate();

    Quantity<?> multiply(Quantity<?> multiplicand);

    Quantity<?> divide(Quantity<?> divisor);

    Quantity<?> reciprocal();

    default <R extends Quantity<R>> Quantity<R> asQuantity(Class<R> quantityClass) {
        // TODO: implement dimension check
        return (Quantity<R>) this;
    }

    <T extends R, R extends Quantity<R>> T asTypedQuantity(Class<T> quantityClass);
}
