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
package com.github.netomi.uom;

/**
 * An extension of the {@link Quantity} interface with specialization of return
 * types for fully typed quantities.
 *
 * @param <Q> the quantity type.
 *
 * @author Thomas Neidhart
 */
public interface TypedQuantity<Q extends Quantity<Q>> extends Quantity<Q> {

    /**
     * Returns a new {@link TypedQuantity} with its value expressed in the
     * given {@link Unit}.
     *
     * @param unit the {@link Unit} to convert to.
     * @return a new {@link TypedQuantity} with this value expressed in the given {@link Unit}.
     * @throws IncommensurableException if the specified {@link Unit} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Q to(Unit<Q> unit);

    /**
     * Returns a new {@link Quantity} with its value expressed in the
     * given {@link Unit}.
     *
     * @param unit the {@link Unit} to convert to.
     * @return a new {@link Quantity} with this value expressed in the given {@link Unit}.
     * @throws IncommensurableException if the specified {@link Unit} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default Q toAny(Unit unit) {
        return (Q) to(unit);
    }

    /**
     * Returns a new {@link TypedQuantity} with its value expressed in the
     * corresponding system unit. If this quantity is already expressed
     * in a system unit, the same quantity will be returned.
     *
     * @return a new {@link TypedQuantity} with this value expressed in the corresponding system unit.
     */
    Q toSystemUnit();

    /**
     * Returns a new {@link TypedQuantity} that is the result of adding the given quantity
     * to this quantity. The resulting quantity will be expressed in the
     * same {@link Unit} as this quantity.
     *
     * @param addend the {@link TypedQuantity} to add to this quantity.
     * @return a new {@link TypedQuantity} representing the sum of this and the other quantity.
     * @throws IncommensurableException if the specified {@link TypedQuantity} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Q add(Quantity<Q> addend);

    /**
     * Returns a new {@link TypedQuantity} that is the result of adding the given quantity
     * to this quantity, expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * added.
     *
     * @param addend the {@link TypedQuantity} to add to this quantity.
     * @param unit   the {@link Unit} of the resulting quantity.
     * @return a new {@link TypedQuantity} representing the sum of this and the other quantity
     * @throws IncommensurableException if the specified {@link TypedQuantity} or {@link Unit} is
     * not compatible with this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    @SuppressWarnings("unchecked")
    default Q add(Quantity<Q> addend, Unit<Q> unit) {
        return (Q) this.to(unit).add(addend.to(unit));
    }

    /**
     * Returns a new {@link TypedQuantity} that is the result of subtracting the given quantity
     * from this quantity. The resulting quantity will be expressed in the
     * same {@link Unit} as this quantity.
     *
     * @param subtrahend the {@link TypedQuantity} to subtract from this quantity.
     * @return a new {@link TypedQuantity} which is the result of subtracting the other quantity from this.
     * @throws IncommensurableException if the specified {@link TypedQuantity} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Q subtract(Quantity<Q> subtrahend);

    /**
     * Returns a new {@link TypedQuantity} that is the result of subtracting the given quantity
     * from this quantity, expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * subtracted.
     *
     * @param subtrahend the {@link TypedQuantity} to subtract from this quantity.
     * @param unit       the {@link Unit} of the resulting quantity.
     * @return a new {@link TypedQuantity} which is the result of subtracting the other quantity from this.
     * @throws IncommensurableException if the specified {@link TypedQuantity} or {@link Unit} is
     * not compatible with this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    @SuppressWarnings("unchecked")
    default Q subtract(Quantity<Q> subtrahend, Unit<Q> unit) {
        return (Q) this.to(unit).subtract(subtrahend.to(unit));
    }

    /**
     * Returns a new {@link TypedQuantity} whose value is {@code -this}.
     *
     * @return a new {@link TypedQuantity} whose value is {@code -this}.
     */
    Q negate();
}
