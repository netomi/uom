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
package tech.neidhart.uom.unit;

import tech.neidhart.uom.Prefix;
import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.math.Fraction;

import java.util.Objects;

/**
 * Represents a {@link Unit} with a specific {@link Prefix}.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class PrefixedUnit<Q extends Quantity<Q>> extends TransformedUnit<Q> {

    private final Prefix prefix;

    /**
     * Returns a new {@link PrefixedUnit} for the given {@link Unit} and {@link Prefix}.
     *
     * @param unit    the unit to add a prefix to.
     * @param prefix  the prefix to apply to the unit.
     * @param <Q>     the quantity type.
     * @return a new {@link PrefixedUnit}.
     * @throws IllegalArgumentException if the unit is already prefixed.
     */
    static <Q extends Quantity<Q>> PrefixedUnit<Q> of(Unit<Q> unit, Prefix prefix) {
        Objects.requireNonNull(prefix);

        if (unit instanceof PrefixedUnit<?>) {
            throw new IllegalArgumentException("trying to add a prefix to an already prefixed unit: " + unit);
        }

        return new PrefixedUnit<>(unit, prefix);
    }

    private PrefixedUnit(Unit<Q> delegateUnit, Prefix prefix) {
        super(delegateUnit, prefix.getUnitConverter());
        this.prefix = prefix;
    }

    private PrefixedUnit(Unit<Q> delegateUnit, Prefix prefix, String symbol, String name) {
        super(delegateUnit, symbol, name, prefix.getUnitConverter());
        this.prefix = prefix;
    }

    @Override
    public String getSymbol() {
        return symbol != null ? symbol : prefix.getSymbol() + getDelegateUnit().getSymbol();
    }

    @Override
    public String getName() {
        return name != null ? name : prefix.getName() + getDelegateUnit().getName();
    }

    @Override
    public UnitElement[] getUnitElements() {
        return new UnitElement[] { new UnitElement(this, Fraction.ONE) };
    }

    @Override
    public PrefixedUnit<Q> withSymbol(String symbol) {
        return new PrefixedUnit<>(getDelegateUnit(), prefix, symbol, this.name);
    }

    @Override
    public PrefixedUnit<Q> withName(String name) {
        return new PrefixedUnit<>(getDelegateUnit(), prefix, this.symbol, name);
    }
}
