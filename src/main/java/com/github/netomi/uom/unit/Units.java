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

import com.github.netomi.uom.*;
import com.github.netomi.uom.format.UnitFormat;
import com.github.netomi.uom.format.UnitFormatter;
import com.github.netomi.uom.math.Fraction;
import com.github.netomi.uom.quantity.Dimensionless;

import java.util.Map;
import java.util.Objects;

/**
 * @author Thomas Neidhart
 */
public final class Units {

    /**
     * The reference unit system to be used.
     * For now, only SI is supported and used by default.
     */
    public enum UnitSystem {
        SI("si.system");

        private final String definitionFile;

        UnitSystem(String definitionFile) {
            this.definitionFile = definitionFile;
        }

        String getDefinitionFile() {
            return definitionFile;
        }
    }

    private static volatile UnitFormatter DEFAULT_FORMATTER = UnitFormat.symbolAndDimension();

    private static final UnitSystem   unitSystem   = UnitSystem.SI;
    private static final UnitRegistry unitReqistry = new UnitRegistry();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new ProductUnit<>();

    static {
        Map<String, Unit<?>> units = UnitDefinitionParser.parse(unitSystem.getDefinitionFile(), unitReqistry);
        unitReqistry.addUnits(units);
    }

    public static UnitSystem getUnitSystem() {
        return unitSystem;
    }

    @SuppressWarnings("unchecked")
    public static <Q extends Quantity<Q>> Unit<Q> get(String symbol, Class<Q> quantityClass) {
        return (Unit<Q>) unitReqistry.get(symbol);
    }

    public static void register(SystemOfUnits systemOfUnits) {
        for (Unit<?> unit : systemOfUnits.getUnits()) {
            unitReqistry.putIfAbsent(unit);
        }
    }

    @SuppressWarnings("unchecked")
    public static <Q extends Quantity<Q>> Unit<Q> getNamedUnitIfPresent(Unit<Q> unit) {
        return (Unit<Q>) unitReqistry.getOrDefault(unit, unit);
    }

    public static <Q extends Quantity<Q>> Unit<Q> baseUnitForDimension(String symbol, String name, Dimension dimension) {
        return new BaseUnit<>(symbol, name, dimension);
    }

    public static <Q extends Quantity<Q>> Unit<Q> alternateSystemUnit(Unit<?> parentUnit, String symbol, String name) {
        return AlternateSystemUnit.of(parentUnit, symbol, name);
    }

    // format related methods.

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
