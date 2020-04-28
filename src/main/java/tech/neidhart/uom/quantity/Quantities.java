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
package tech.neidhart.uom.quantity;

import tech.neidhart.uom.*;
import tech.neidhart.uom.format.QuantityFormat;
import tech.neidhart.uom.format.QuantityFormatter;
import tech.neidhart.uom.quantity.impl.*;
import tech.neidhart.uom.util.Proxies;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class to create concrete instances for specific {@link Quantity} types.
 * <p>
 * You can also register a custom factory for a specific quantity type to fit your needs.
 * Some possible use-cases:
 * <ul>
 *     <li>a pool of quantity instances for commonly used values</li>
 *     <li>replace default quantity factories with one that always creates an instance with a fixed decimal precision</li>
 * </ul>
 *
 * @author Thomas Neidhart
 */
public final class Quantities {

    private static volatile QuantityFormatter DEFAULT_FORMATTER = QuantityFormat.defaultFormatter();

    private static final Map<Class<? extends Quantity<?>>, QuantityFactory<?>> quantityFactories;

    private static final Map<Unit<?>, Class<? extends Quantity<?>>> unitToQuantityMap;

    // quantity factories for generic quantities, i.e. quantities for which the quantity type
    // is not known.
    private static final GenericQuantityFactory<?> genericFactory =
            CombinedGenericQuantityFactory.of(DoubleQuantity.factory(),
                                              DecimalQuantity.factory());


    static {
        quantityFactories = new ConcurrentHashMap<>(Type.values().length * 2);
        // register default factories for all built-in quantities.
        registerDefaultFactories();

        unitToQuantityMap = new ConcurrentHashMap<>(Type.values().length * 2);
        // register system unit -> quantity type mapping.
        registerSystemUnits();
    }

    // hide constructor.
    private Quantities() {}

    /**
     * Register another {@link QuantityFactory} for a specified quantity type.
     */
    public static <Q extends Quantity<Q>> void registerQuantityFactory(Class<Q>           quantityClass,
                                                                       QuantityFactory<Q> factory) {
        quantityFactories.put(quantityClass, factory);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerDefaultFactories() {
        for (Type type : Type.values()) {
            Class quantityClass = type.getQuantityType();

            quantityFactories.putIfAbsent(quantityClass,
                                          CombinedQuantityFactory.of(DoubleQuantity.factory(quantityClass),
                                                                     DecimalQuantity.factory(quantityClass)));
        }

    }

    private static void registerSystemUnits() {
        for (Type type : Type.values()) {
            unitToQuantityMap.put(type.getSystemUnit(), type.getQuantityType());
        }
    }

    @SuppressWarnings("unchecked")
    private static <Q extends Quantity<Q>> QuantityFactory<Q> getQuantityFactory(Class<Q> quantityType) {
        return (QuantityFactory<Q>)
                quantityFactories.computeIfAbsent(quantityType, key -> {
                    if (!Quantity.class.isAssignableFrom(quantityType)) {
                        throw new IllegalArgumentException(quantityType + " is not a Quantity.");
                    }

                    try {
                        // Check if the specified quantity has overridden its getSystemUnit() method.
                        Quantity<?> testQuantity = Proxies.delegatingProxy(new Object(), quantityType);
                        testQuantity.getSystemUnit();
                    } catch (Exception ex) {
                        throw new UnsupportedOperationException(quantityType +
                                                                " has not overridden its getSystemUnit() method.");
                    }

                    return CombinedQuantityFactory.of(DoubleQuantity.factory(quantityType),
                                                      DecimalQuantity.factory(quantityType));
                });
    }

    @SuppressWarnings("unchecked")
    public static <Q extends Quantity<Q>> Class<Q> getQuantityType(Unit<?> unit) {
        return (Class<Q>) unitToQuantityMap.get(unit.getSystemUnit());
    }

    // create methods for quantities.

    public static <Q extends Quantity<Q>> Q create(double value, Unit<Q> unit) {
        Class<Q> quantityType = getQuantityType(unit);

        if (quantityType != null) {
            return create(value, unit, quantityType);
        } else {
            throw new UnsupportedOperationException("unknown quantity type for unit " + unit);
        }
    }

    /**
     * Creates a new quantity for the specified quantity type.
     *
     * @throws IllegalArgumentException if the specified class does not implement the {@link Quantity} interface.
     */
    public static <Q extends Quantity<Q>> Q create(double   value,
                                                   Unit<Q>  unit,
                                                   Class<Q> quantityClass) {
        return getQuantityFactory(quantityClass).create(value, unit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <Q extends Quantity<Q>> Quantity<Q> createGeneric(double value, Unit<Q> unit) {
        return genericFactory.create(value, (Unit) unit);
    }

    public static <Q extends Quantity<Q>> Q create(BigDecimal value, Unit<Q> unit) {
        Class<Q> quantityType = getQuantityType(unit);

        if (quantityType != null) {
            return create(value, unit, quantityType);
        } else {
            throw new UnsupportedOperationException("unknown quantity type for unit " + unit);
        }
    }

    public static <Q extends Quantity<Q>> Q create(BigDecimal value,
                                                   Unit<Q>    unit,
                                                   Class<Q>   quantityClass) {
        return getQuantityFactory(quantityClass).create(value, unit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <Q extends Quantity<Q>> Quantity<Q> createGeneric(BigDecimal value, Unit<Q> unit) {
        return genericFactory.create(value, (Unit) unit);
    }

    public static <Q extends Quantity<Q>> Q create(BigDecimal  value,
                                                   MathContext mc,
                                                   Unit<Q>     unit) {
        Class<Q> quantityType = getQuantityType(unit);

        if (quantityType != null) {
            return create(value, mc, unit, quantityType);
        } else {
            throw new UnsupportedOperationException("unknown quantity type for unit " + unit);
        }
    }

    public static <Q extends Quantity<Q>> Q create(BigDecimal  value,
                                                   MathContext mc,
                                                   Unit<Q>     unit,
                                                   Class<Q>    quantityClass) {
        return getQuantityFactory(quantityClass).create(value, mc, unit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <Q extends Quantity<Q>> Quantity<Q> createGeneric(BigDecimal  value,
                                                                    MathContext mc,
                                                                    Unit<Q>     unit) {
        return genericFactory.create(value, mc, (Unit) unit);
    }

    // format related methods.

    public static QuantityFormatter defaultFormatter() {
        return DEFAULT_FORMATTER;
    }

    public static void setDefaultFormatter(QuantityFormatter quantityFormatter) {
        Objects.requireNonNull(quantityFormatter);
        DEFAULT_FORMATTER = quantityFormatter;
    }

    // inner helper classes.

    static class CombinedGenericQuantityFactory<Q extends Quantity<Q>> implements GenericQuantityFactory<Q> {

        private final GenericDoubleQuantityFactory<Q>  doubleQuantityFactory;
        private final GenericDecimalQuantityFactory<Q> decimalQuantityFactory;

        @SuppressWarnings({"unchecked", "rawtypes"})
        static CombinedGenericQuantityFactory of(GenericDoubleQuantityFactory<?>  doubleFactory,
                                                 GenericDecimalQuantityFactory<?> decimalFactory) {
            return new CombinedGenericQuantityFactory(doubleFactory, decimalFactory);
        }

        private CombinedGenericQuantityFactory(GenericDoubleQuantityFactory<Q>  doubleQuantityFactory,
                                               GenericDecimalQuantityFactory<Q> decimalQuantityFactory) {
            Objects.requireNonNull(doubleQuantityFactory);
            Objects.requireNonNull(decimalQuantityFactory);

            this.doubleQuantityFactory  = doubleQuantityFactory;
            this.decimalQuantityFactory = decimalQuantityFactory;
        }

        @Override
        public Quantity<Q> create(double value, Unit<Q> unit) {
            return doubleQuantityFactory.create(value, unit);
        }

        @Override
        public Quantity<Q> create(BigDecimal value, Unit<Q> unit) {
            return decimalQuantityFactory.create(value, unit);
        }

        @Override
        public Quantity<Q> create(BigDecimal value, MathContext mc, Unit<Q> unit) {
            return decimalQuantityFactory.create(value, mc, unit);
        }
    }

    static class CombinedQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {

        private final DoubleQuantityFactory<Q> doubleQuantityFactory;
        private final DecimalQuantityFactory<Q> decimalQuantityFactory;

        @SuppressWarnings({"unchecked", "rawtypes"})
        static <Q extends Quantity<Q>> CombinedQuantityFactory of(DoubleQuantityFactory<Q>  doubleFactory,
                                                                  DecimalQuantityFactory<Q> decimalFactory) {
            return new CombinedQuantityFactory(doubleFactory, decimalFactory);
        }

        private CombinedQuantityFactory(DoubleQuantityFactory<Q>  doubleQuantityFactory,
                                        DecimalQuantityFactory<Q> decimalQuantityFactory) {
            Objects.requireNonNull(doubleQuantityFactory);
            Objects.requireNonNull(decimalQuantityFactory);

            this.doubleQuantityFactory  = doubleQuantityFactory;
            this.decimalQuantityFactory = decimalQuantityFactory;
        }

        @Override
        public Q create(double value, Unit<Q> unit) {
            return doubleQuantityFactory.create(value, unit);
        }

        @Override
        public Q create(BigDecimal value, Unit<Q> unit) {
            return decimalQuantityFactory.create(value, unit);
        }

        @Override
        public Q create(BigDecimal value, MathContext mc, Unit<Q> unit) {
            return decimalQuantityFactory.create(value, mc, unit);
        }
    }

    /**
     * Builtin quantity types.
     */
    public enum Type {
        ACCELERATION(Acceleration.class),
        AMOUNT_OF_SUBSTANCE(AmountOfSubstance.class),
        ANGLE(Angle.class),
        AREA(Area.class),
        DIMENSIONLESS(Dimensionless.class),
        ELECTRIC_CAPACITANCE(ElectricCapacitance.class),
        ELECTRIC_CHARGE(ElectricCharge.class),
        ELECTRIC_CONDUCTANCE(ElectricConductance.class),
        ELECTRIC_CURRENT(ElectricCurrent.class),
        ELECTRIC_INDUCTANCE(ElectricInductance.class),
        ELECTRIC_POTENTIAL(ElectricPotential.class),
        ELECTRIC_RESISTANCE(ElectricResistance.class),
        ENERGY(Energy.class),
        FORCE(Force.class),
        FREQUENCY(Frequency.class),
        LENGTH(Length.class),
        LUMINOUS_FLUX(LuminousFlux.class),
        LUMINOUS_INTENSITY(LuminousIntensity.class),
        MAGNETIC_FLUX(MagneticFlux.class),
        MAGNETIC_INDUCTANCE(MagneticInductance.class),
        MASS(Mass.class),
        POWER(Power.class),
        PRESSURE(Pressure.class),
        SOLID_ANGLE(SolidAngle.class),
        SPEED(Speed.class),
        TEMPERATURE(Temperature.class),
        TIME(Time.class),
        VOLUME(Volume.class);

        private final Class<? extends Quantity<?>> quantityType;
        private final Unit<?>                      systemUnit;

        private static final Map<Class<?>, Unit<?>> quantityToSystemUnitMap = new HashMap<>();

        static {
            for (Type type : values()) {
                quantityToSystemUnitMap.put(type.getQuantityType(), type.getSystemUnit());
            }
        }

        Type(Class<? extends Quantity<?>> quantityType) {
            this.quantityType = quantityType;

            Quantity<?> quantity = Proxies.delegatingProxy(this, quantityType);
            systemUnit = quantity.getSystemUnit();
        }

        public Class<? extends Quantity<?>> getQuantityType() {
            return quantityType;
        }

        public Unit<?> getSystemUnit() {
            return systemUnit;
        }

        public Dimension getDimension() {
            return systemUnit.getDimension();
        }

        public static <Q extends Quantity<Q>> Unit<?> systemUnitFor(Class<Q> quantityType, Unit<Q> defaultUnit) {
            if (!Quantity.class.isAssignableFrom(quantityType)) {
                throw new IllegalArgumentException(quantityType + " is not a Quantity.");
            }

            return quantityToSystemUnitMap.getOrDefault(quantityType, defaultUnit);
        }
    }
}
