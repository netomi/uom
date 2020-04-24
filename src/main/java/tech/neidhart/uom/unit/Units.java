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

import tech.neidhart.uom.*;
import tech.neidhart.uom.format.UnitFormat;
import tech.neidhart.uom.format.UnitFormatter;
import tech.neidhart.uom.math.Fraction;
import tech.neidhart.uom.quantity.Dimensionless;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Neidhart
 */
public final class Units {

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

    @SuppressWarnings("unchecked")
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

    // Internal methods with public scope.

    /**
     * Returns a {@link Unit} which is the product of the 2 given units and
     * their associated fractions.
     * <p>
     * Note: this method is only used for internal purposes and should not be called
     * otherwise.
     */
    public static Unit<?> productOf(Unit<?> left, Fraction leftFraction, Unit<?> right, Fraction rightFraction) {
        return ProductUnit.ofProduct(left, leftFraction, right, rightFraction);
    }

    /**
     * Returns a new {@link Unit} that represents the nth power of this unit.
     * <p>
     * Note: this method is only used for internal purposes and should not be called
     * otherwise.
     */
    public static Unit<?> powerOf(Unit<?> unit, Fraction fraction) {
        return ProductUnit.ofProduct(unit, fraction);
    }
}
