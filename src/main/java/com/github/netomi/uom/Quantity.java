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

import com.github.netomi.uom.unit.Units;

import java.math.BigDecimal;

/**
 * Represents a physical quantity expressed in a specific unit of measurement.
 *
 * @param <Q> the quantity type.
 *
 * @author Thomas Neidhart
 */
public interface Quantity<Q extends Quantity<Q>> extends Comparable<Quantity<Q>> {

    /**
     * Returns the raw value of this quantity expressed in its {@link Unit}.
     *
     * @return the value of this quantity expressed in its {@link Unit}.
     */
    double doubleValue();

    /**
     * Returns the raw value of this quantity with as {@link BigDecimal} expressed
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
     * @throws IncommensurableException if the specified {@link Unit} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Quantity<Q> to(Unit<Q> unit);

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
    default Quantity<Q> toAny(Unit unit) {
        return (Quantity<Q>) to(unit);
    }

    /**
     * Returns a new {@link Quantity} with its value expressed in the
     * corresponding system unit. If this quantity is already expressed
     * in a system unit, the same quantity will be returned.
     *
     * @return a new {@link Quantity} with this value expressed in the corresponding system unit.
     */
    Quantity<Q> toSystemUnit();

    /**
     * Returns the system unit of this quantity.
     * <p>
     * The default implementation returns the system unit of the unit
     * in which this quantity is expressed. For full type-safety when using custom
     * quantities, overwrite this method to return the correct system unit of
     * this quantity.
     * <p>
     * All built-in quantities return a proper system unit.
     *
     * @return the system {@link Unit} for this quantity.
     */
    default Unit<Q> getSystemUnit() {
        return getUnit().getSystemUnit();
    }

    /**
     * Returns if the given unit is compatible with this quantity, i.e.
     * if {@code this.getSystemUnit().equals(unit.getSystemUnit())}.
     *
     * @param unit  the unit to check for compatibility.
     * @return {@code true} if the unit is compatible, {@code false} otherwise.
     */
    default boolean isCompatible(Unit<?> unit) {
        return getSystemUnit().equals(unit.getSystemUnit());
    }

    /**
     * Compares this {@link Quantity} to another quantity of the same quantity type. If the two
     * quantities are expressed in the same unit, their raw values are compared, otherwise the
     * other quantity is converted to the unit of this quantity before comparison.
     * <p>
     * Note: when comparing a large number of quantities it is advised to convert them first to
     * the same unit.
     *
     * @param other the other quantity to compare to.
     * @return a negative integer, zero, or a positive integer as this quantity is less than, equal to,
     *         or greater than the specified quantity.
     * @throws IncommensurableException if the dimensions of the two quantities are not compatible.
     */
    @Override
    int compareTo(Quantity<Q> other) throws IncommensurableException;

    /**
     * Returns whether this {@link Quantity} is strictly greater in value than the other quantity.
     * If the two quantities are expressed in the same unit, their raw values are used, otherwise the
     * other quantity is converted to the unit of this quantity before comparison.
     *
     * @param other the other quantity to compare to.
     * @return {@code true} if this quantity is strictly greater than the other quantity.
     * @throws IncommensurableException if the dimensions of the two quantities are not compatible.
     */
    default boolean isGreaterThan(Quantity<Q> other) throws IncommensurableException {
        return this.compareTo(other) > 0;
    }

    /**
     * Returns whether this {@link Quantity} is strictly smaller in value than the other quantity.
     * If the two quantities are expressed in the same unit, their raw values are used, otherwise the
     * other quantity is converted to the unit of this quantity before comparison.
     *
     * @param other the other quantity to compare to.
     * @return {@code true} if this quantity is strictly less than the other quantity.
     * @throws IncommensurableException if the dimensions of the two quantities are not compatible.
     */
    default boolean isLessThan(Quantity<Q> other) throws IncommensurableException {
        return this.compareTo(other) < 0;
    }

    /**
     * Returns whether this {@link Quantity} is equal compared to the other quantity taking some
     * error into account such that two quantities are considered equal when their value expressed
     * in the same unit satisfies the condition: {@code Math.abs(thisValue, thatValue) <= epsilon}.
     * <p>
     * If the two quantities are expressed in the same unit, their raw values are used, otherwise the
     * other quantity is converted to the unit of this quantity before comparison.
     *
     * @param other   the other quantity to compare to.
     * @param epsilon the maximum allowed relative error when checking for equality.
     * @return {@code true} if this quantity is equal to the other quantity taking some error into account.
     * @throws IncommensurableException if the dimensions of the two quantities are not compatible.
     */
    boolean isEqual(Quantity<Q> other, double epsilon) throws IncommensurableException;

    /**
     * Returns whether the raw value of this {@link Quantity} is equal to zero such that the
     * following condition is satisfied: {@code Math.abs(thisValue, 0) <= epsilon}.
     *
     * @param epsilon the maximum allowed relative error when checking for equality.
     * @return {@code true} if this quantity is equal to zero taking some error into account.
     */
    boolean isZero(double epsilon);

    /**
     * Returns whether the raw value of this {@link Quantity} converted to the specified unit is equal
     * to zero such that the following condition is satisfied: {@code Math.abs(thisValue, 0) <= epsilon}.
     *
     * @param inUnit  the unit to which this quantity should be converted before comparison.
     * @param epsilon the maximum allowed relative error when checking for equality.
     * @return {@code true} if this quantity is equal to zero taking some error into account.
     * @throws IncommensurableException if the dimension of the specified unit is not compatible to this quantity.
     */
    boolean isZero(Unit<Q> inUnit, double epsilon) throws IncommensurableException;

    /**
     * Returns whether the raw value of this {@link Quantity} is strictly equal to zero.
     *
     * @return {@code true} if this quantity is equal to zero taking some error into account.
     */
    boolean isStrictlyZero();

    /**
     * Returns a new {@link Quantity} that is the result of adding the given quantity
     * to this quantity. The resulting quantity will be expressed in the
     * same {@link Unit} as this quantity.
     *
     * @param addend the {@link Quantity} to add to this quantity.
     * @return a new {@link Quantity} representing the sum of this and the other quantity.
     * @throws IncommensurableException if the specified {@link Quantity} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Quantity<Q> add(Quantity<Q> addend);

    /**
     * Returns a new {@link Quantity} that is the result of adding the given quantity
     * to this quantity, expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * added.
     *
     * @param addend the {@link Quantity} to add to this quantity.
     * @param unit   the {@link Unit} of the resulting quantity.
     * @return a new {@link Quantity} representing the sum of this and the other quantity
     * @throws IncommensurableException if the specified {@link Quantity} or {@link Unit} is
     * not compatible with this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
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
     * @throws IncommensurableException if the specified {@link Quantity} is not compatible with
     * this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
     */
    Quantity<Q> subtract(Quantity<Q> subtrahend);

    /**
     * Returns a new {@link Quantity} that is the result of subtracting the given quantity
     * from this quantity, expressed in the specified {@link Unit}.
     * <p>
     * Both quantities will be first converted to the specified {@link Unit} and then
     * subtracted.
     *
     * @param subtrahend the {@link Quantity} to subtract from this quantity.
     * @param unit       the {@link Unit} of the resulting quantity.
     * @return a new {@link Quantity} which is the result of subtracting the other quantity from this.
     * @throws IncommensurableException if the specified {@link Quantity} or {@link Unit} is
     * not compatible with this quantity, i.e. their {@link #getSystemUnit()}'s do not match.
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

    /**
     * Returns a quantity that is the result of multiplying this quantity with the
     * provided quantity. Both quantities are first converted into their respective
     * system unit and their raw values are then multiplied.
     * <p>
     * The returned quantity has the following properties:
     * <ul>
     *     <li>value = this.toSystemUnit() * other.toSystemUnit()
     *     <li>unit  = this.getSystemUnit().multiply(other.getSystemUnit())
     * </ul>
     *
     * @param multiplier the quantity to multiply with.
     * @return a new quantity which is the result of multiplying this quantity with the other.
     */
    Quantity<?> multiply(Quantity<?> multiplier);

    /**
     * Returns a quantity that is the result of multiplying this quantity with the
     * provided quantity. Both quantities are first converted into their respective
     * system unit and their raw values are then multiplied.
     * <p>
     * The returned quantity has the following properties:
     * <ul>
     *     <li>value = this.toSystemUnit() * other.toSystemUnit()
     *     <li>unit  = this.getSystemUnit().multiply(other.getSystemUnit())
     * </ul>
     * <p>
     * The resulting quantity is cast to the requested quantity class. If the result
     * is not compatible with the requested quantity, an exception is thrown.
     *
     * @param multiplier    the quantity to multiply with.
     * @param quantityClass the requested quantity type.
     * @return a new quantity which is the result of multiplying this quantity with the other.
     * @throws IncommensurableException if the resulting unit does not match the system
     * unit of the specified quantity.
     */
    <R extends Quantity<R>> R multiply(Quantity<?> multiplier, Class<R> quantityClass);

    /**
     * Returns a quantity that is the result of dividing this quantity by the
     * provided quantity. Both quantities are first converted into their respective
     * system unit and their raw values are then divided.
     * <p>
     * The returned quantity has the following properties:
     * <ul>
     *     <li>value = this.toSystemUnit() / other.toSystemUnit()
     *     <li>unit  = this.getSystemUnit().divide(other.getSystemUnit())
     * </ul>
     *
     * @param divisor the quantity to divide by.
     * @return a new quantity which is the result of dividing this quantity by the other.
     */
    Quantity<?> divide(Quantity<?> divisor);

    /**
     * Returns a quantity that is the result of dividing this quantity by the
     * provided quantity. Both quantities are first converted into their respective
     * system unit and their raw values are then divided.
     * <p>
     * The returned quantity has the following properties:
     * <ul>
     *     <li>value = this.toSystemUnit() / other.toSystemUnit()
     *     <li>unit  = this.getSystemUnit().divide(other.getSystemUnit())
     * </ul>
     * <p>
     * The resulting quantity is cast to the requested quantity class. If the result
     * is not compatible with the requested quantity, an exception is thrown.
     *
     * @param divisor       the quantity to divide by.
     * @param quantityClass the requested quantity type.
     * @return a new quantity which is the result of dividing this quantity by the other.
     * @throws IncommensurableException if the resulting unit does not match the system
     * unit of the specified quantity.
     */
    <R extends Quantity<R>> R divide(Quantity<?> divisor, Class<R> quantityClass);

    /**
     * Returns the reciprocal of this quantity.
     * <p>
     * The returned quantity has the following properties:
     * <ul>
     *     <li>value = one() / this
     *     <li>unit  = this.getUnit().inverse().getSystemUnit()
     * </ul>
     *
     * @return a new quantity which is the reciprocal of this quantity.
     */
    Quantity<?> reciprocal();

    /**
     * Returns the multiplicative identity of this {@link Quantity}.
     *
     * @return a quantity with value "1" and unit {@link Units#ONE}.
     */
    Quantity<?> one();

    /**
     * Returns the additive identity of this {@link Quantity}.
     *
     * @return a quantity with value "0" and the same unit as this quantity.
     */
    Quantity<Q> zero();

    /**
     * Returns this {@link Quantity} as the specified typed quantity class.
     * <p>
     * It is verified if the quantity expressed in its unit is compatible
     * with the specified quantity type. If they are incompatible, an
     * {@link IncommensurableException} is thrown.
     *
     * @param quantityType the quantity type to which this quantity should be cast.
     * @param <R> the quantity type
     * @return this quantity cast as the requested quantity type.
     * @throws IncommensurableException if this quantity is not compatible with the requested quantity type.
     */
    <R extends Quantity<R>> R asQuantity(Class<R> quantityType);
}
