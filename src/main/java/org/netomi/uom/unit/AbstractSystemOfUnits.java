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
import org.netomi.uom.SystemOfUnits;
import org.netomi.uom.Unit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of a container of units.
 *
 * @author Thomas Neidhart
 */
abstract class AbstractSystemOfUnits implements SystemOfUnits {

    private final String       name;
    private final Set<Unit<?>> units;

    protected AbstractSystemOfUnits(String name) {
        this.name  = name;
        this.units = new HashSet<>();
    }

    protected <Q extends Quantity<Q>> Unit<Q> addUnit(Unit<Q> unit, Class<Q> quantityClass) {
        this.units.add(unit);
        return unit;
    }

    @Override
    public Iterable<Unit<?>> getUnits() {
        return Collections.unmodifiableSet(units);
    }

    @Override
    public String getName() {
        return name;
    }
}
