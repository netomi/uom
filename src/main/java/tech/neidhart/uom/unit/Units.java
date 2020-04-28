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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Thomas Neidhart
 */
public final class Units {

    private static final String CONFIG_FILE            = "uom.properties";
    private static final String CONFIG_UNIT_SYSTEM_KEY = "units.system";

    public enum UnitSystem {
        SI      ("si.system"),
        ESU     ("esu.system"),
        EMU     ("emu.system"),
        GAUSSIAN("gaussian.system");

        private final String definitionFile;

        UnitSystem(String definitionFile) {
            this.definitionFile = definitionFile;
        }

        String getDefinitionFile() {
            return definitionFile;
        }
    }

    private static volatile UnitFormatter DEFAULT_FORMATTER = UnitFormat.symbolAndDimension();

    private static final UnitSystem   unitSystem;
    private static final UnitRegistry unitReqistry = new UnitRegistry();

    // Some globally unique units / constants.
    public static final Unit<Dimensionless> ONE = new ProductUnit<>();

    static {
        Properties properties = loadProperties();
        unitSystem = UnitSystem.valueOf(properties.getProperty(CONFIG_UNIT_SYSTEM_KEY));

        Map<String, Unit<?>> units = UnitDefinitionParser.parse(unitSystem.getDefinitionFile(), unitReqistry);
        unitReqistry.addUnits(units);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        properties.setProperty(CONFIG_UNIT_SYSTEM_KEY, "SI");

        try (InputStream is = Units.class.getResourceAsStream("/" + CONFIG_FILE)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException ignored) {}

        return properties;
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

    static <Q extends Quantity<Q>> Unit<Q> alternateDimensionlessSystemUnit(String symbol, String name) {
        return AlternateSystemUnit.of(Units.ONE, symbol, name);
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
