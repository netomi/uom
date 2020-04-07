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
import org.netomi.uom.quantity.decimal.*;
import org.netomi.uom.quantity.primitive.*;
import org.netomi.uom.util.TypeUtil;

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
        registerInternalQuantityFactory(Dimensionless.class, DoubleDimensionless.factory(), DecimalDimensionless.factory());

        // quantities for base units in SI.
        registerInternalQuantityFactory(Length.class,            DoubleLength.factory(),            DecimalLength.factory());
        registerInternalQuantityFactory(Mass.class,              DoubleMass.factory(),              DecimalMass.factory());
        registerInternalQuantityFactory(Time.class,              DoubleTime.factory(),              DecimalTime.factory());
        registerInternalQuantityFactory(Temperature.class,       DoubleTemperature.factory(),       DecimalTemperature.factory());
        registerInternalQuantityFactory(LuminousIntensity.class, DoubleLuminousIntensity.factory(), DecimalLuminousIntensity.factory());
        registerInternalQuantityFactory(ElectricCurrent.class,   DoubleElectricCurrent.factory(),   DecimalElectricCurrent.factory());
        registerInternalQuantityFactory(AmountOfSubstance.class, DoubleAmountOfSubstance.factory(), DecimalAmountOfSubstance.factory());

        // quantities for synonyms.
        registerInternalQuantityFactory(Distance.class,      DoubleDistance.factory(),      DecimalDistance.factory());
        registerInternalQuantityFactory(Duration.class,      DoubleDuration.factory(),      DecimalDuration.factory());

        // quantities for derives units.
        registerInternalQuantityFactory(Angle.class,         DoubleAngle.factory(),         DecimalAngle.factory());
        registerInternalQuantityFactory(Frequency.class,     DoubleFrequency.factory(),     DecimalFrequency.factory());
        registerInternalQuantityFactory(Speed.class,         DoubleSpeed.factory(),         DecimalSpeed.factory());
        registerInternalQuantityFactory(Acceleration.class,  DoubleAcceleration.factory(),  DecimalAcceleration.factory());
        registerInternalQuantityFactory(Area.class,          DoubleArea.factory(),          DecimalArea.factory());
        registerInternalQuantityFactory(Volume.class,        DoubleVolume.factory(),        DecimalVolume.factory());
        registerInternalQuantityFactory(Force.class,         DoubleForce.factory(),         DecimalForce.factory());
        registerInternalQuantityFactory(Energy.class,        DoubleEnergy.factory(),        DecimalEnergy.factory());
        registerInternalQuantityFactory(Power.class,         DoublePower.factory(),         DecimalPower.factory());
        registerInternalQuantityFactory(Pressure.class,      DoublePressure.factory(),      DecimalPressure.factory());

        registerInternalQuantityFactory(ElectricPotential.class,   DoubleElectricPotential.factory(),   DecimalElectricPotential.factory());
        registerInternalQuantityFactory(ElectricCharge.class,      DoubleElectricCharge.factory(),      DecimalElectricCharge.factory());
        registerInternalQuantityFactory(ElectricCapacitance.class, DoubleElectricCapacitance.factory(), DecimalElectricCapacitance.factory());

        genericQuantityFactory = DelegateQuantityFactory.of(DoubleQuantity.factory(), DecimalQuantity.factory());
    }

    private static <T extends Q, Q extends Quantity<Q>>
        void registerInternalQuantityFactory(Class<T>           quantityClass,
                                             QuantityFactory<Q> doubleFactory,
                                             QuantityFactory<Q> decimalFactory) {
        factoryMap.put(quantityClass,
                       DelegateQuantityFactory.of(doubleFactory, decimalFactory));
    }

    public static <T extends Q, Q extends Quantity<Q>> QuantityFactory<Q> getQuantityFactory(Class<T> quantityClass) {
        @SuppressWarnings("unchecked")
        QuantityFactory<Q> quantityFactory = (QuantityFactory<Q>) factoryMap.get(quantityClass);

        return quantityFactory != null ?
                quantityFactory :
                (QuantityFactory<Q>) genericQuantityFactory;
    }

    public static <T extends Q, Q extends Quantity<Q>> void registerQuantityFactory(Class<Q>           quantityClass,
                                                                                    QuantityFactory<Q> factory) {
        factoryMap.put(quantityClass, factory);
    }

    // hide constructor.
    private Quantities() {}

    // create methods for generic quantities.

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(double  value,
                                                                     Unit<Q> unit) {
        @SuppressWarnings("unchecked")
        Quantity<Q> quantity = ((QuantityFactory<Q>) genericQuantityFactory).create(value, unit);
        return quantity;
    }

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(BigDecimal value,
                                                                     Unit<Q>    unit) {
        @SuppressWarnings("unchecked")
        Quantity<Q> quantity = ((QuantityFactory<Q>) genericQuantityFactory).create(value, unit);
        return quantity;
    }

    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(BigDecimal  value,
                                                                     MathContext mathContext,
                                                                     Unit<Q>     unit) {
        @SuppressWarnings("unchecked")
        Quantity<Q> quantity = ((QuantityFactory<Q>) genericQuantityFactory).create(value, mathContext, unit);
        return quantity;
    }

    // create method for typed quantities.

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(double   value,
                                                                        Unit<Q>  unit,
                                                                        Class<T> quantityClass) {
        @SuppressWarnings("unchecked")
        T quantity = (T) getQuantityFactory(quantityClass).create(value, unit);
        TypeUtil.requireCommensurable(quantity, unit);
        return quantity;
    }

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(BigDecimal value,
                                                                        Unit<Q>    unit,
                                                                        Class<T>   quantityClass) {
        @SuppressWarnings("unchecked")
        T quantity = (T) getQuantityFactory(quantityClass).create(value, unit);
        TypeUtil.requireCommensurable(quantity, unit);
        return quantity;
    }

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(BigDecimal  value,
                                                                        MathContext mathContext,
                                                                        Unit<Q>     unit,
                                                                        Class<T>    quantityClass) {
        @SuppressWarnings("unchecked")
        T quantity = (T) getQuantityFactory(quantityClass).create(value, mathContext, unit);
        TypeUtil.requireCommensurable(quantity, unit);
        return quantity;
    }

    public static <T extends Q, Q extends Quantity<Q>> T getQuantityAsType(Quantity<?> quantity, Class<T> quantityClass) {
        if (quantity instanceof DoubleQuantity<?>) {
            @SuppressWarnings("unchecked")
            T typedQuantity = (T) createQuantity(quantity.doubleValue(), (Unit<Q>) quantity.getUnit(), quantityClass);

            TypeUtil.requireCommensurable(typedQuantity, quantity.getUnit());
            return typedQuantity;
        } else if (quantity instanceof DecimalQuantity<?>) {
            DecimalQuantity<?> decimalQuantity = (DecimalQuantity<?>) quantity;

            @SuppressWarnings("unchecked")
            T typedQuantity = (T) createQuantity(quantity.decimalValue(),
                                                 decimalQuantity.getMathContext(),
                                                 (Unit<Q>) quantity.getUnit(),
                                                 quantityClass);

            TypeUtil.requireCommensurable(typedQuantity, quantity.getUnit());
            return typedQuantity;
        }

        throw new AssertionError("unknown quantity class");
    }

    static class DelegateQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {

        private final QuantityFactory<Q> doubleQuantityFactory;
        private final QuantityFactory<Q> decimalQuantityFactory;

        static <Q extends Quantity<Q>> DelegateQuantityFactory<Q>
            of(QuantityFactory<?> doubleFactory, QuantityFactory<?> decimalFactory) {
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
}
