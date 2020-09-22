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

import com.github.netomi.uom.function.UnitConverters;
import com.github.netomi.uom.math.Fraction;
import com.github.netomi.uom.quantity.Quantities;
import com.github.netomi.uom.unit.UnitElement;
import com.github.netomi.uom.unit.Units;
import com.github.netomi.uom.util.ConcurrentReferenceHashMap;
import com.github.netomi.uom.util.Preconditions;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static com.github.netomi.uom.util.ConcurrentReferenceHashMap.ReferenceType;

/**
 * Represents a unit of measurement to express the magnitude of a quantity.
 *
 * @param <Q> the quantity type
 *
 * @see <a href="https://en.wikipedia.org/wiki/Unit_of_measurement">Wikipedia: Unit of measurement</a>
 *
 * @author Thomas Neidhart
 */
public abstract class Unit<Q extends Quantity<Q>> {

    /**
     * A lazy initialized cache for {@link UnitConverter}s.
     * It uses WEAK references for its keys, and SOFT references for its values.
     */
    private volatile Map<Unit<Q>, UnitConverter> converterCache;

    /**
     * Returns the symbol associated with this unit.
     *
     * @return the symbol of the unit.
     */
    public abstract String getSymbol();

    /**
     * Returns the name associated with this unit, optional.
     *
     * @return the name of the unit.
     */
    public abstract String getName();

    /**
     * Returns the {@link Dimension} of this unit.
     * <p>
     * Conversion of quantities between different units requires that
     * the dimensions of the involved units is equal. Furthermore, units
     * can only be used for quantities whose dimensions match. Any operation
     * involving entities expressed in different dimensions results in
     * an {@link IncommensurableException} thrown at runtime.
     *
     * @return the dimension associated with this unit.
     */
    public abstract Dimension getDimension();

    /**
     * Returns whether this unit is compatible with the provided unit.
     * <p>
     * Two units are considered to be compatible if they have the same
     * dimension.
     * <p>
     * If two units are compatible with each other, values can be technically
     * converted between quantities expressed in them, although it does not
     * make sense in some cases, for example
     * <ul>
     *     <li>CANDELA and LUMEN
     *     <li>RADIAN and STERADIAN
     * </ul>
     * have the same dimension but inherently different physical nature.
     * To account for this, the {@link Quantity#isCompatible(Unit)} also
     * takes the system unit into account to see if a unit can be used
     * for a certain quantity.
     *
     * @param that the unit to check for compatibility.
     * @return {@code true} if the two units are compatible, {@code false} otherwise.
     */
    public boolean isCompatible(Unit<?> that) {
        return Objects.equals(this.getDimension(),  that.getDimension());
    }

    /**
     * Returns if this {@link Unit} is considered to be a system unit.
     * <p>
     * A system unit is defined as an unscaled unit of the reference system (fixed: SI).
     * @return {@code true} if this unit is system unit, {@code false} otherwise.
     */
    public boolean isSystemUnit() {
        return getSystemConverter().isIdentity();
    }

    public abstract Unit<Q> getSystemUnit();

    public abstract UnitConverter getSystemConverter();

    private Map<Unit<Q>, UnitConverter> getConverterCache() {
        // Lazy initialize the converter cache using a double checked locking pattern.
        // It is fine to use as we target JDK 8+.
        if (converterCache == null) {
            synchronized (this) {
                if (converterCache == null) {
                    // use WEAK references for the keys
                    // and SOFT references for the values
                    // rationale:
                    //   * the entries can be removed as soon as the unit is not strongly referenced anymore.
                    //   * unit converters are usually not strongly referenced, using also a WEAK reference would
                    //     remove them too soon from the cache, use SOFT instead which only removed them if the
                    //     VM needs memory.
                    converterCache = new ConcurrentReferenceHashMap<>(10, ReferenceType.WEAK, ReferenceType.SOFT);
                }
            }
        }
        return converterCache;
    }

    /**
     * Returns a {@link UnitConverter} to convert quantities expressed in this {@link Unit}
     * to the other unit.
     *
     * @param unit the unit to get a converter to.
     * @return a {@link UnitConverter} to convert quantities from this unit to the other.
     * @throws IncommensurableException if the specified {@link Unit} is not compatible with
     * this unit, i.e. their {@link Dimension} does not match.
     */
    public UnitConverter getConverterTo(Unit<Q> unit) throws IncommensurableException {
        return getConverterCache().computeIfAbsent(unit, u -> {
            Preconditions.requireCommensurable(this, u);

            UnitConverter thisConverter = getSystemConverter();
            UnitConverter thatConverter = u.getSystemConverter().inverse();

            return thisConverter.andThen(thatConverter);
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public UnitConverter getConverterToAny(Unit unit) {
        return getConverterTo(unit);
    }

    public abstract Map<? extends Unit<?>, Fraction> getBaseUnits();

    // builder methods which return a new unit.

    public Unit<Q> shift(double offset) {
        return transform(UnitConverters.shift(offset));
    }

    public Unit<Q> multiply(double multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    public Unit<Q> multiply(BigDecimal multiplier) {
        return transform(UnitConverters.multiply(multiplier));
    }

    public Unit<Q> multiply(long numerator, long denominator) {
        return transform(UnitConverters.multiply(numerator, denominator));
    }

    public Unit<Q> transform(UnitConverter converter) {
        return Units.transformedWith(this, converter);
    }

    public Unit<?> multiply(Unit<?> that) {
        return this == Units.ONE ? that :
               that == Units.ONE ? this :
                       Units.productOf(this, Fraction.ONE, that, Fraction.ONE);
    }

    public Unit<?> divide(Unit<?> that) {
        return that == Units.ONE ? this :
               Units.productOf(this, Fraction.ONE, that, Fraction.of(-1));
    }

    public Unit<?> pow(int n) {
        return n == 0 ? Units.ONE :
               n == 1 ? this      :
                        Units.powerOf(this, Fraction.of(n));
    }

    public Unit<?> root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer.");
        }

        return n == 1 ? this :
               Units.powerOf(this, Fraction.of(1, n));
    }

    public Unit<?> inverse() {
        return this == Units.ONE ? this :
               Units.productOf(Units.ONE, Fraction.ONE, this, Fraction.of(-1));
    }

    public abstract Unit<Q> withSymbol(String symbol);

    public abstract Unit<Q> withName(String name);

    public Unit<Q> withPrefix(Prefix prefix) {
        return Units.withPrefix(this, prefix);
    }

    /**
     * Casts this unit to the specified quantity type if it is compatible.
     *
     * @param quantityClass the class of the quantity type to cast to.
     * @param <T> the quantity type.
     * @return a unit of the specified quantity type.
     * @throws IncommensurableException if the dimension of this unit does
     * not match the dimension of the quantity.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Quantity<T>> Unit<T> forQuantity(Class<T> quantityClass) {
        // TODO: make this clean using TypeUtil.requireCommensurable
        // try to create a quantity of the requested type with this unit.
        // if it works, we can safely cast.
        Quantities.create(0, (Unit) this, quantityClass);
        return (Unit<T>) this;
    }

    // for internal use only.

    public abstract UnitElement[] getUnitElements();

    @Override
    public int hashCode() {
        return Objects.hash(getDimension(), getSystemConverter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;

        Unit<?> otherUnit = (Unit<?>) o;
        return Objects.equals(getDimension(),       otherUnit.getDimension()) &&
               Objects.equals(getSystemConverter(), otherUnit.getSystemConverter());
    }

    @Override
    public String toString() {
        return Units.defaultFormatter().format(this);
    }
}
