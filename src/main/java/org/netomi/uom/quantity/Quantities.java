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
package org.netomi.uom.quantity;

import org.netomi.uom.Quantity;
import org.netomi.uom.QuantityFactory;
import org.netomi.uom.Unit;
import org.netomi.uom.quantity.impl.DecimalQuantity;
import org.netomi.uom.quantity.impl.DoubleQuantity;
import org.netomi.uom.unit.Dimension;
import org.netomi.uom.util.Proxies;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Thomas Neidhart
 */
public class Quantities {

    static Map<Class<? extends Quantity<?>>, QuantityFactory<?>> factoryMap;

    static QuantityFactory<?> genericQuantityFactory;

    static {
        factoryMap = new HashMap<>();

        // register built-in factories.
        //registerInternalQuantityFactory(Dimensionless.class, DoubleDimensionless.factory(), DecimalDimensionless.factory());

        //genericQuantityFactory = DelegateQuantityFactory.of(DoubleQuantity::factory, DecimalQuantity.factory());
    }

    public static <T extends Q, Q extends Quantity<Q>> void registerQuantityFactory(Class<Q>           quantityClass,
                                                                                    QuantityFactory factory) {
        factoryMap.put(quantityClass, factory);
    }

    // hide constructor.
    private Quantities() {}

    // create methods for generic quantities.

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(double value, Unit<Q> unit) {
        return (Quantity<Q>) DoubleQuantity.factory().create(value, unit);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(BigDecimal value, Unit<Q> unit) {
        return (Quantity<Q>) DecimalQuantity.factory().create(value, unit);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(BigDecimal  value,
                                                                     MathContext mathContext,
                                                                     Unit<Q>     unit) {
        return (Quantity<Q>) DecimalQuantity.factory().create(value, mathContext, unit);
    }

    // create method for typed quantities.

    public static <Q extends Quantity<Q>> Q createQuantity(double   value,
                                                           Unit<Q>  unit,
                                                           Class<Q> quantityClass) {
        return DoubleQuantity.factory(quantityClass).create(value, unit);
    }

    public static <Q extends Quantity<Q>> Q createQuantity(BigDecimal value,
                                                           Unit<Q>    unit,
                                                           Class<Q>   quantityClass) {
        return DecimalQuantity.factory(quantityClass).create(value, unit);
    }

    public static <Q extends Quantity<Q>> Q createQuantity(BigDecimal  value,
                                                           MathContext mathContext,
                                                           Unit<Q>     unit,
                                                           Class<Q>    quantityClass) {
        return DecimalQuantity.factory(quantityClass).create(value, mathContext, unit);
    }

    static class DelegateQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {

        private final QuantityFactory<Q> doubleQuantityFactory;
        private final QuantityFactory<Q> decimalQuantityFactory;

        static <Q extends Quantity<Q>> DelegateQuantityFactory<Q>
            of(QuantityFactory doubleFactory, QuantityFactory decimalFactory) {
            return new DelegateQuantityFactory(doubleFactory, decimalFactory);
        }

        private DelegateQuantityFactory(QuantityFactory<Q> doubleQuantityFactory,
                                        QuantityFactory<Q> decimalQuantityFactory) {
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
        public Q create(BigDecimal value, MathContext mathContext, Unit<Q> unit) {
                return decimalQuantityFactory.create(value, mathContext, unit);
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
        ELECTRIC_POTENTIAL(ElectricPotential.class),
        ELECTRIC_RESISTANCE(ElectricResistance.class),
        ENERGY(Energy.class),
        FORCE(Force.class),
        FREQUENCY(Frequency.class),
        LENGTH(Length.class),
        LUMINOUS_FLUX(LuminousFlux.class),
        LUMINOUS_INTENSITY(LuminousIntensity.class),
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
    }
}
