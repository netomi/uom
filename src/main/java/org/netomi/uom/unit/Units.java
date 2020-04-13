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

import org.netomi.uom.*;
import org.netomi.uom.format.UnitFormat;
import org.netomi.uom.format.UnitFormatter;
import org.netomi.uom.math.Fraction;
import org.netomi.uom.quantity.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Neidhart
 */
public class Units {

    private static UnitFormatter DEFAULT_FORMATTER = UnitFormat.symbolAndDimension();

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
                // refresh the ProductUnit cache with named system units.
                if (unit.isSystemUnit()) {
                    ProductUnit.putProductUnitIntoCache(unit);
                }
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

    public static <Q extends Quantity<Q>> Unit<Q> alternateDimensionlessSystemUnit(String symbol, String name) {
        return AlternateSystemUnit.of(Units.ONE, symbol, name);
    }

    public static UnitFormatter defaultFormatter() {
        return DEFAULT_FORMATTER;
    }

    public static void setDefaultFormatter(UnitFormatter unitFormatter) {
        Objects.requireNonNull(unitFormatter);
        DEFAULT_FORMATTER = unitFormatter;
    }

    // Builder methods to augment an existing unit, i.e. with a name or unit converter.

    public static <Q extends Quantity<Q>> Unit<Q> withPrefix(Unit<Q> unit, Prefix prefix) {
        return PrefixedUnit.of(unit, prefix);
    }

    public static <Q extends Quantity<Q>> Unit<Q> transformedWith(Unit<Q> unit, UnitConverter unitConverter) {
        return TransformedUnit.of(unit, unitConverter);
    }

    public static Unit<?> productOf(Unit<?> left, Fraction leftFraction, Unit<?> right, Fraction rightFraction) {
        return ProductUnit.ofProduct(left, leftFraction, right, rightFraction);
    }

    public static Unit<?> powerOf(Unit<?> unit, Fraction fraction) {
        return ProductUnit.ofProduct(unit, fraction);
    }
}
