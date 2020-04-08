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
import org.netomi.uom.UnitConverter;
import org.netomi.uom.math.Fraction;

import java.util.Map;
import java.util.Objects;

/**
 * A {@link Unit} implementation that delegates relevant calls to a delegate unit.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
abstract class DelegateUnit<Q extends Quantity<Q>> extends Unit<Q> {

    private final Unit<Q> delegateUnit;

    protected DelegateUnit(Unit<Q> delegateUnit) {
        Objects.requireNonNull(delegateUnit);
        this.delegateUnit = delegateUnit;
    }

    protected Unit<Q> getDelegateUnit() {
        return delegateUnit;
    }

    @Override
    public String getSymbol() {
        return delegateUnit.getSymbol();
    }

    @Override
    public String getName() {
        return delegateUnit.getName();
    }

    @Override
    public Dimension getDimension() {
        return delegateUnit.getDimension();
    }

    @Override
    public Unit<Q> getSystemUnit() {
        return isSystemUnit() ?
                this :
                delegateUnit.getSystemUnit();
    }

    @Override
    public UnitConverter getSystemConverter() {
        return delegateUnit.getSystemConverter();
    }

    @Override
    public Map<? extends Unit<?>, Fraction> getBaseUnits() {
        return delegateUnit.getBaseUnits();
    }

    @Override
    public UnitElement[] getUnitElements() {
        return delegateUnit.getUnitElements();
    }
}
