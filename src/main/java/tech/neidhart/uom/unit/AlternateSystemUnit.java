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

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.function.UnitConverters;
import tech.neidhart.uom.math.Fraction;
import tech.neidhart.uom.quantity.Dimensionless;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @param <Q> the quantity type of this unit
 *
 * @author Thomas Neidhart
 */
class AlternateSystemUnit<Q extends Quantity<Q>> extends DelegateUnit<Q> {

    private final String    symbol;
    private final String    name;

    static <Q extends Quantity<Q>> AlternateSystemUnit<Q> of(Unit<?> parentUnit, String symbol, String name) {
        return new AlternateSystemUnit(parentUnit, symbol, name);
    }

    AlternateSystemUnit(Unit<Dimensionless> parentUnit, String symbol, String name) {
        super((Unit) parentUnit);
        Objects.requireNonNull(symbol);

        this.symbol = symbol;
        this.name   = name;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSystemUnit() {
        return true;
    }

    @Override
    public Unit<Q> getSystemUnit() {
        return this;
    }

    @Override
    public UnitConverter getSystemConverter() {
        return UnitConverters.identity();
    }

    @Override
    public Map<? extends Unit<?>, Fraction> getBaseUnits() {
        return Collections.emptyMap();
    }

    @Override
    public UnitElement[] getUnitElements() {
        return new UnitElement[] { new UnitElement(this, Fraction.ONE) };
    }

    @Override
    public Unit<Q> withSymbol(String symbol) {
        return new AlternateSystemUnit<>((Unit<Dimensionless>) getDelegateUnit(), symbol, this.name);
    }

    @Override
    public Unit<Q> withName(String name) {
        return new AlternateSystemUnit<>((Unit<Dimensionless>) getDelegateUnit(), this.symbol, name);
    }
}
