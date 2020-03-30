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

import org.netomi.uom.Dimension;
import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Fraction;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a base unit in a given {@link org.netomi.uom.SystemOfUnits}.
 *
 * @param <Q> the quantity type of this unit
 *
 * @author Thomas Neidhart
 */
class BaseUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> implements Unit<Q> {

    private final String    symbol;
    private final String    name;
    private final Dimension dimension;

    private final Map<Unit<Q>, Fraction> baseUnitMap;

    BaseUnit(String symbol, String name, Dimension dimension) {
        this.symbol    = symbol;
        this.name      = name;
        this.dimension = dimension;

        this.baseUnitMap = Collections.singletonMap(this, Fraction.ONE);
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
    public Dimension getDimension() {
        return dimension;
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
        return baseUnitMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUnit<?> baseUnit = (BaseUnit<?>) o;
        return Objects.equals(symbol, baseUnit.symbol) &&
               Objects.equals(dimension, baseUnit.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, dimension);
    }
}
