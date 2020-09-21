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
package com.github.netomi.uom.unit.systems;

import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.SystemOfUnits;
import com.github.netomi.uom.Unit;

import java.util.*;

/**
 * A simple implementation of a container of units.
 *
 * @author Thomas Neidhart
 */
public abstract class AbstractSystemOfUnits implements SystemOfUnits {

    private final String              name;
    private final Collection<Unit<?>> units;

    protected AbstractSystemOfUnits(String name) {
        this.name  = name;
        this.units = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    protected <Q extends Quantity<Q>> Unit<Q> addUnitForQuantity(Unit<?> unit, Class<Q> quantityClass) {
        Objects.requireNonNull(unit);
        this.units.add(unit);
        return (Unit<Q>) unit;
    }

    @Override
    public Iterable<Unit<?>> getUnits() {
        return Collections.unmodifiableCollection(units);
    }

    @Override
    public String getName() {
        return name;
    }
}
