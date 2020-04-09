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

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.math.Fraction;

import java.util.Objects;

/**
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class NamedUnit<Q extends Quantity<Q>> extends DelegateUnit<Q> {

    private final String symbol;
    private final String name;

    static <Q extends Quantity<Q>> Unit<Q> withSymbol(Unit<Q> unit, String symbol) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(symbol);

        Unit<Q> delegateUnit = unit;
        String  name         = null;

        if (unit instanceof NamedUnit<?>) {
            NamedUnit<Q> namedUnit = (NamedUnit<Q>) unit;

            name         = namedUnit.name;
            delegateUnit = namedUnit.getDelegateUnit();
        }
        return new NamedUnit<>(delegateUnit, symbol, name);
    }

    static <Q extends Quantity<Q>> Unit<Q> withName(Unit<Q> unit, String name) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(name);

        Unit<Q> delegateUnit = unit;
        String  symbol       = null;

        if (unit instanceof NamedUnit<?>) {
            NamedUnit<Q> namedUnit = (NamedUnit<Q>) unit;

            symbol       = namedUnit.symbol;
            delegateUnit = namedUnit.getDelegateUnit();
        }
        return new NamedUnit<>(delegateUnit, symbol, name);
    }

    private NamedUnit(Unit<Q> delegateUnit, String symbol, String name) {
        super(delegateUnit);
        this.symbol = symbol;
        this.name   = name;
    }

    @Override
    public String getSymbol() {
        return symbol != null ? symbol : super.getSymbol();
    }

    @Override
    public String getName() {
        return name != null ? name : super.getName();
    }

    @Override
    public UnitElement[] getUnitElements() {
        // decompose any product unit, for others return this named instance.
        if (getDelegateUnit() instanceof ProductUnit<?>) {
            return getDelegateUnit().getUnitElements();
        } else {
            return new UnitElement[]{ new UnitElement(this, Fraction.ONE) };
        }
    }
}
