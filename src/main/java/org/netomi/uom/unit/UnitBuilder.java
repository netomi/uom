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
 * units without as efficient as possible.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public final class UnitBuilder<Q extends Quantity<Q>> {

    private Unit<Q> delegateUnit;

    private String symbol;
    private String name;
    private Prefix prefix;
    private Prefix previousPrefix;

    private UnitConverter converterToParent;

    UnitBuilder(Unit<Q> unit) {
        this.delegateUnit      = unit;
        this.converterToParent = UnitConverters.identity();

        if (unit instanceof UnitImpl) {
            UnitImpl impl = (UnitImpl) unit;

            delegateUnit      = impl.delegateUnit;
            converterToParent = impl.converterToDelegate;

            symbol         = impl.symbol;
            name           = impl.name;
            prefix         = impl.prefix;
            previousPrefix = impl.prefix;
        }
    }

    public UnitBuilder<Q> withSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public UnitBuilder<Q> withName(String name) {
        this.name = name;
        return this;
    }

    public UnitBuilder<Q> withPrefix(Prefix prefix) {
        // If the delegate unit already contains a prefix,
        // try to combine it with this one.
        if (previousPrefix != null) {
            int combinedExponent = previousPrefix.getExponent() + prefix.getExponent();

            if (combinedExponent == 0) {
                this.prefix = null;
                return this;
            }

            Prefix combinedPrefix = prefix.withExponent(combinedExponent);
            if (combinedPrefix != null) {
                prefix = combinedPrefix;
            }
        }

        this.prefix         = prefix;
        this.previousPrefix = prefix;
        transformedBy(UnitConverters.pow(prefix.getBase(), prefix.getExponent()));
        return this;
    }

    public UnitBuilder<Q> shiftedBy(double offset) {
        converterToParent = converterToParent.andThen(UnitConverters.shift(offset));
        return this;
    }

    public UnitBuilder<Q> shiftedBy(BigDecimal offset) {
        converterToParent = converterToParent.andThen(UnitConverters.shift(offset));
        return this;
    }

    public UnitBuilder<Q> multipliedBy(double multiplicand) {
        converterToParent = converterToParent.andThen(UnitConverters.multiply(multiplicand));
        return this;
    }

    public UnitBuilder<Q> multipliedBy(BigDecimal multiplicand) {
        converterToParent = converterToParent.andThen(UnitConverters.multiply(multiplicand));
        return this;
    }

    public UnitBuilder<Q> multipliedBy(long numerator, long denominator) {
        converterToParent = converterToParent.andThen(UnitConverters.multiply(numerator, denominator));
        return this;
    }

    public UnitBuilder<Q> transformedBy(UnitConverter unitConverter) {
        converterToParent = converterToParent.andThen(unitConverter);
        return this;
    }

    public <T extends Quantity<T>> UnitBuilder<T> forQuantity(Class<T> quantityClass) {
        return (UnitBuilder<T>) this;
    }

    public Unit<Q> build() {
        UnitImpl<Q> impl = new UnitImpl<>(delegateUnit, converterToParent);

        impl.symbol = symbol;
        impl.name   = name;
        impl.prefix = prefix;

        return impl;
    }

    private static class UnitImpl<Q extends Quantity<Q>> extends AbstractUnit<Q> {

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
            return symbol != null ? symbol :
                   prefix != null ? prefix.getSymbol() + delegateUnit.getSymbol() :
                                    delegateUnit.getSymbol();
        }

        @Override
        public String getName() {
            return name   != null ? name :
                   prefix != null ? prefix.getName() + delegateUnit.getName() :
                                    delegateUnit.getName();
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
