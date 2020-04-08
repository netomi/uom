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
import org.netomi.uom.quantity.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Neidhart
 */
public class Units {

    private static Map<Unit<?>, Unit<?>> namedUnits = new ConcurrentHashMap<>();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new ProductUnit<>();
    public static final BigDecimal          PI  = BigDecimal.valueOf(StrictMath.PI);

    public static void register(SystemOfUnits systemOfUnits) {
        for (Unit<?> unit : systemOfUnits.getUnits()) {
            // do not put dimensionless units into the set of named units.
            // all dimensionless units are equal to each other.
            if (unit.getDimension() == Dimensions.NONE) {
                continue;
            }

            // do not use putIfAbsent to be compatible with android
            // as much as possible.
            if (!namedUnits.containsKey(unit)) {
                namedUnits.put(unit, unit);
                // refresh the ProductUnit cache with named units.
                ProductUnit.putNamedUnitIntoCache(unit);
            }
        }
    }

    public static <Q extends Quantity<Q>> Unit<Q> getNamedUnitIfPresent(Unit<Q> unit) {
        Unit<?> namedUnit = namedUnits.get(unit);
        return namedUnit != null ? (Unit<Q>) namedUnit : unit;
    }

    public static Iterable<Unit<?>> namedUnits() {
        return Collections.unmodifiableSet(namedUnits.keySet());
    }

    public static <Q extends Quantity<Q>> Unit<Q> baseUnitForDimension(String symbol, String name, Dimension dimension) {
        return new BaseUnit<>(symbol, name, dimension);
    }

    // Convenient access to base SI units.
    public static class SI extends org.netomi.uom.unit.systems.SI {}
}
