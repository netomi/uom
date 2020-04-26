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

import tech.neidhart.uom.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class UnitRegistry {

    private final Map<String, Unit<?>>  units;
    private final Map<Unit<?>, Unit<?>> unitMapping;

    public UnitRegistry() {
        this.units       = new ConcurrentHashMap<>();
        this.unitMapping = new ConcurrentHashMap<>();
    }

    public void addUnits(Map<String, Unit<?>> units) {
        for (Map.Entry<String, Unit<?>> entry : units.entrySet()) {
            Unit<?> unit = entry.getValue();

            this.units.put(entry.getKey(), unit);
            this.unitMapping.put(unit, unit);

            // refresh the ProductUnit cache with named system units.
            if (unit.isSystemUnit()) {
                ProductUnit.putProductUnitIntoCache(unit);
            }
        }
    }

    public Unit<?> get(String symbol) {
        return units.get(symbol);
    }

    public Unit<?> getOrDefault(Unit<?> unit, Unit<?> defaultUnit) {
        return unitMapping.getOrDefault(unit, defaultUnit);
    }

    public void putIfAbsent(Unit<?> unit) {
        this.units.putIfAbsent(unit.getSymbol(), unit);
        this.unitMapping.putIfAbsent(unit, unit);
    }

    Map<String, Unit<?>> getUnits() {
        return new HashMap<>(units);
    }
}
