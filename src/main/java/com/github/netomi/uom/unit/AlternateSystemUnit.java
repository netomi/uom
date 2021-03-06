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
package com.github.netomi.uom.unit;

import com.github.netomi.uom.function.UnitConverters;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.UnitConverter;
import com.github.netomi.uom.math.Fraction;

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

    private final Map<Unit<Q>, Fraction> baseUnitMap;

    static <Q extends Quantity<Q>> AlternateSystemUnit<Q> of(Unit<?> parentUnit, String symbol, String name) {
        return new AlternateSystemUnit<>(parentUnit, symbol, name);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    AlternateSystemUnit(Unit<?> parentUnit, String symbol, String name) {
        super((Unit) parentUnit);
        Objects.requireNonNull(symbol);

        this.symbol = symbol;
        this.name   = name;

        this.baseUnitMap = parentUnit.getDimension() == Dimensions.NONE ?
                Collections.singletonMap(this, Fraction.ONE) :
                null;
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
        return baseUnitMap != null ?
                baseUnitMap :
                super.getBaseUnits();
    }

    @Override
    public UnitElement[] getUnitElements() {
        return getDimension() == Dimensions.NONE ?
                new UnitElement[] { new UnitElement(this, Fraction.ONE) } :
                getDelegateUnit().getUnitElements();
    }

    @Override
    public Unit<Q> withSymbol(String symbol) {
        return new AlternateSystemUnit<>(getDelegateUnit(), symbol, this.name);
    }

    @Override
    public Unit<Q> withName(String name) {
        return new AlternateSystemUnit<>(getDelegateUnit(), this.symbol, name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDimension(), getSymbol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlternateSystemUnit)) return false;

        AlternateSystemUnit<?> otherUnit = (AlternateSystemUnit<?>) o;
        return Objects.equals(getSymbol(),    otherUnit.getSymbol()) &&
               Objects.equals(getDimension(), otherUnit.getDimension());
    }
}
