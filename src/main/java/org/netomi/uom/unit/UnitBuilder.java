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
package org.netomi.uom.unit;

import org.netomi.uom.*;
import org.netomi.uom.function.UnitConverters;

import java.math.BigDecimal;

/**
 * A builder class to construct new {@link Unit} instances from existing
 * units without as easy as possible.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public final class UnitBuilder<Q extends Quantity<Q>> {

    private Unit<?>        delegateUnit;
    private UnitConverter  converterToDelegate;
    private boolean        delegateHasPrefix;

    private String symbol;
    private String name;
    private Prefix prefix;

    /**
     * Create a new {@link UnitBuilder} instance to create a new unit with the
     * given unit as starting point.

     * @return a new {@link UnitBuilder} instance to build a new unit with the
     * specified unit as reference.
     */
    public static <Q extends Quantity<Q>> UnitBuilder<Q> fromAny(Unit<?> unit) {
        return new UnitBuilder<>(unit);
    }

    public static <Q extends Quantity<Q>> UnitBuilder<Q> from(Unit<Q> unit) {
        return new UnitBuilder<>(unit);
    }

    UnitBuilder(Unit<? extends Quantity<?>> unit) {
        this.delegateUnit        = unit;
        this.converterToDelegate = UnitConverters.identity();

        // if the unit to build from does not have a prefix yet,
        // we can more efficiently build the new unit instead of
        // creating a delegate to a delegate.
        if (unit instanceof UnitWrapper<?> &&
            // always wrap dimensionless units, otherwise getSystemUnit() returns NONE.
            unit.getDimension() != Dimensions.NONE) {
            UnitWrapper<?> wrapper = (UnitWrapper<?>) unit;

            delegateHasPrefix = wrapper.prefix != null;
            if (!delegateHasPrefix) {
                this.delegateUnit        = wrapper.getDelegateUnit();
                this.converterToDelegate = wrapper.converterToDelegate;

                this.symbol = wrapper.symbol;
                this.name   = wrapper.name;
            }
        }
    }

    /**
     * Set the symbol of the new unit to the given string.
     *
     * @param symbol the symbol of the unit.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> withSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * Set the name of the new unit to the given string.
     *
     * @param name the name of the unit.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add the given prefix to the new unit.
     * <p>
     * Note: there is no check if the reference
     * unit already has a prefix. Adding another
     * prefix to a unit which already has a prefix
     * usually doesn't make sense.
     *
     * @param prefix the prefix to add.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> withPrefix(Prefix prefix) {
        this.prefix = prefix;
        compose(prefix.getUnitConverter());
        return this;
    }

    /**
     * Concatenates a constant offset converter
     * to the existing converter to the reference unit.
     *
     * @param offset the constant offset.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> shiftedBy(double offset) {
        transformedBy(UnitConverters.shift(offset));
        return this;
    }

    /**
     * Concatenates a constant offset converter
     * to the existing converter to the reference unit.
     *
     * @param offset the constant offset.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> shiftedBy(BigDecimal offset) {
        transformedBy(UnitConverters.shift(offset));
        return this;
    }

    /**
     * Concatenates a constant factor converter
     * to the existing converter to the reference unit.
     *
     * @param multiplicand the constant factor.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> multipliedBy(double multiplicand) {
        transformedBy(UnitConverters.multiply(multiplicand));
        return this;
    }

    /**
     * Concatenates a constant factor converter
     * to the existing converter to the reference unit.
     *
     * @param multiplicand the constant factor.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> multipliedBy(BigDecimal multiplicand) {
        transformedBy(UnitConverters.multiply(multiplicand));
        return this;
    }

    /**
     * Concatenates a constant factor converter
     * to the existing converter to the reference unit.
     *
     * @param numerator   the numerator part of the factor in fractional form.
     * @param denominator the denominator part of the factor in fractional form.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> multipliedBy(long numerator, long denominator) {
        transformedBy(UnitConverters.multiply(numerator, denominator));
        return this;
    }

    /**
     * Concatenates the given converter to the existing converter
     * to the reference unit.
     *
     * @param unitConverter  the converter to concatenate.
     * @return this {@link UnitBuilder} instance.
     */
    public UnitBuilder<Q> transformedBy(UnitConverter unitConverter) {
        converterToDelegate = converterToDelegate.andThen(unitConverter);
        return this;
    }

    private UnitBuilder<Q> compose(UnitConverter unitConverter) {
        converterToDelegate = converterToDelegate.compose(unitConverter);
        return this;
    }

    /**
     * Create the unit based on the provided information and casts it to the
     * given quantity type.
     */
    public <T extends Quantity<T>> Unit<T> build(Class<T> quantityClass) {
        return (Unit<T>) build();
    }

    /**
     * Create the unit based on information provided through the builder methods.
     *
     * @return a new {@link Unit} instance.
     */
    public Unit<Q> build() {
        UnitWrapper<Q> wrapper;

        if (delegateUnit instanceof DerivedUnit<?>) {
            wrapper = new CompositeUnitWrapper((DerivedUnit<?>) delegateUnit, converterToDelegate);
        } else {
            wrapper = new SimpleUnitWrapper(delegateUnit, converterToDelegate);
        }

        wrapper.symbol = symbol;
        wrapper.name   = name;
        wrapper.prefix = prefix;

        return wrapper;
    }

    /**
     * Wrapper implementation for composite units.
     *
     * @param <Q> the quantity type
     */
    static class CompositeUnitWrapper<Q extends Quantity<Q>> extends UnitWrapper<Q> implements CompositeUnit {

        private final DerivedUnit<Q> derivedUnit;

        CompositeUnitWrapper(DerivedUnit<Q> derivedUnit, UnitConverter converterToDelegate) {
            super(derivedUnit, converterToDelegate);
            this.derivedUnit = derivedUnit;
        }

        @Override
        public UnitElementWrapper getUnitElements() {
            return derivedUnit.getUnitElements();
        }

        @Override
        public String getSymbol() {
            StringBuilder sb = new StringBuilder();

            if (prefix != null) {
                sb.append(prefix.getSymbol());
                if (symbol == null) {
                    sb.append('(');
                }
            }

            if (symbol != null) {
                sb.append(symbol);
            } else {
                sb.append(getDelegateUnit().getSymbol());
            }

            if (prefix != null && symbol == null) {
                sb.append(')');
            }

            return sb.toString();
        }

        @Override
        public String getName() {
            StringBuilder sb = new StringBuilder();

            if (prefix != null) {
                sb.append(prefix.getName());
                if (name == null) {
                    sb.append('(');
                }
            }

            if (name != null) {
                sb.append(name);
            } else {
                sb.append(getDelegateUnit().getName());
            }

            if (prefix != null && name == null) {
                sb.append(')');
            }

            return sb.toString();
        }
    }

    /**
     * Wrapper implementation for simple units, i.e. units that are not composed of multiple units.
     *
     * @param <Q> the quantity type
     */
    static class SimpleUnitWrapper<Q extends Quantity<Q>> extends UnitWrapper<Q> {

        private SimpleUnitWrapper(Unit<Q> delegateUnit, UnitConverter converterToDelegate) {
            super(delegateUnit, converterToDelegate);
        }

        @Override
        public String getSymbol() {
            StringBuilder sb = new StringBuilder();

            if (prefix != null) {
                sb.append(prefix.getSymbol());
            }

            if (symbol != null) {
                sb.append(symbol);
            } else {
                sb.append(getDelegateUnit().getSymbol());
            }

            return sb.toString();
        }

        @Override
        public String getName() {
            StringBuilder sb = new StringBuilder();

            if (prefix != null) {
                sb.append(prefix.getName());
            }

            if (name != null) {
                sb.append(name);
            } else {
                sb.append(getDelegateUnit().getName());
            }

            return sb.toString();
        }
    }

    /**
     * Base class for unit wrapper implementations.
     *
     * @param <Q> the quantity type
     */
    static abstract class UnitWrapper<Q extends Quantity<Q>> extends DelegateUnit<Q> {

        private final UnitConverter converterToDelegate;

        String  symbol;
        String  name;
        Prefix  prefix;

        UnitWrapper(Unit<Q> delegateUnit, UnitConverter converterToDelegate) {
            super(delegateUnit);
            this.converterToDelegate = converterToDelegate;
        }

        @Override
        public UnitConverter getSystemConverter() {
            return converterToDelegate.isIdentity() ?
                    getDelegateUnit().getSystemConverter() :
                    converterToDelegate.andThen(getDelegateUnit().getSystemConverter());
        }

        @Override
        public Unit<Q> getSystemUnit() {
            return isSystemUnit() ? this : getDelegateUnit().getSystemUnit();
        }
    }
}
