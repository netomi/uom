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
import org.netomi.uom.math.Fraction;

import java.math.BigDecimal;
import java.util.Map;

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
        if (unit instanceof UnitImpl) {
            UnitImpl impl = (UnitImpl) unit;

            delegateHasPrefix = impl.prefix != null;
            if (!delegateHasPrefix) {
                this.delegateUnit        = impl.delegateUnit;
                this.converterToDelegate = impl.converterToDelegate;

                this.symbol = impl.symbol;
                this.name   = impl.name;
            }
        } else if (unit instanceof DerivedUnitImpl) {
            DerivedUnitImpl impl = (DerivedUnitImpl) unit;

            delegateHasPrefix = impl.prefix != null;
            if (!delegateHasPrefix) {
                this.delegateUnit        = impl;
                this.converterToDelegate = impl.converterToDelegate;

                this.symbol = impl.symbol;
                this.name   = impl.name;
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
        if (delegateUnit instanceof DerivedUnit<?>) {
            DerivedUnitImpl impl = new DerivedUnitImpl((DerivedUnit<?>) delegateUnit, converterToDelegate);

            impl.symbol = symbol;
            impl.name   = name;
            impl.prefix = prefix;

            return impl;
        } else {
            UnitImpl impl = new UnitImpl(delegateUnit, converterToDelegate);

            impl.symbol = symbol;
            impl.name   = name;
            impl.prefix = prefix;

            return impl;
        }
    }

    static class DerivedUnitImpl<Q extends Quantity<Q>> extends DerivedUnit<Q> {

        private final UnitConverter converterToDelegate;

        String  symbol;
        String  name;
        Prefix  prefix;

        DerivedUnitImpl(DerivedUnit<?> derivedUnit, UnitConverter converterToDelegate) {
            super(derivedUnit.getUnitElements());

            this.converterToDelegate = converterToDelegate;
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
                sb.append(super.getSymbol());
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
                sb.append(super.getName());
            }

            return sb.toString();
        }

        @Override
        public UnitConverter getSystemConverter() {
            return converterToDelegate.isIdentity() ?
                    super.getSystemConverter() :
                    converterToDelegate.andThen(super.getSystemConverter());
        }
    }

    static class UnitImpl<Q extends Quantity<Q>> extends AbstractUnit<Q> {

        private final Unit<Q>       delegateUnit;
        private final UnitConverter converterToDelegate;

        String  symbol;
        String  name;
        Prefix  prefix;

        private UnitImpl(Unit<Q> delegateUnit, UnitConverter converterToDelegate) {
            this.delegateUnit        = delegateUnit;
            this.converterToDelegate = converterToDelegate;
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
                sb.append(delegateUnit.getSymbol());
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
                sb.append(delegateUnit.getName());
            }

            return sb.toString();
        }

        @Override
        public Dimension getDimension() {
            return delegateUnit.getDimension();
        }

        @Override
        public boolean isSystemUnit() {
            return delegateUnit.isSystemUnit();
        }

        @Override
        public Unit<Q> getSystemUnit() {
            return delegateUnit.getSystemUnit();
        }

        @Override
        public UnitConverter getSystemConverter() {
            return converterToDelegate.isIdentity() ?
                    delegateUnit.getSystemConverter() :
                    converterToDelegate.andThen(delegateUnit.getSystemConverter());
        }

        @Override
        public Map<? extends Unit<?>, Fraction> getBaseUnits() {
            return delegateUnit.getBaseUnits();
        }
    }
}
