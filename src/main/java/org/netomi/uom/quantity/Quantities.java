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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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

        registerInternalQuantityFactory(Length.class,        DoubleLength.factory(),        DecimalLength.factory());
        registerInternalQuantityFactory(Mass.class,          DoubleMass.factory(),          DecimalMass.factory());
        registerInternalQuantityFactory(Time.class,          DoubleTime.factory(),          DecimalTime.factory());
        registerInternalQuantityFactory(Temperature.class,   DoubleTemperature.factory(),   DecimalTemperature.factory());

        registerInternalQuantityFactory(Distance.class,      DoubleDistance.factory(),      DecimalDistance.factory());

        registerInternalQuantityFactory(Frequency.class,     DoubleFrequency.factory(),     DecimalFrequency.factory());

        registerInternalQuantityFactory(Speed.class,         DoubleSpeed.factory(),         DecimalSpeed.factory());
        registerInternalQuantityFactory(Acceleration.class,  DoubleAcceleration.factory(),  DecimalAcceleration.factory());
        registerInternalQuantityFactory(Area.class,          DoubleArea.factory(),          DecimalArea.factory());

        registerInternalQuantityFactory(Force.class,         DoubleForce.factory(),         DecimalForce.factory());
        registerInternalQuantityFactory(Energy.class,        DoubleEnergy.factory(),        DecimalEnergy.factory());
        registerInternalQuantityFactory(Power.class,         DoublePower.factory(),         DecimalPower.factory());

        registerInternalQuantityFactory(ElectricPotential.class, DoubleElectricPotential.factory(), DecimalElectricPotential.factory());
        registerInternalQuantityFactory(ElectricCharge.class,    DoubleElectricCharge.factory(),    DecimalElectricCharge.factory());
        registerInternalQuantityFactory(ElectricCurrent.class,   DoubleElectricCurrent.factory(),   DecimalElectricCurrent.factory());

        genericQuantityFactory =
                DelegateQuantityFactory.of(AbstractTypedDoubleQuantity.GenericImpl.factory(),
                                           AbstractTypedDecimalQuantity.GenericImpl.factory());
    }

    private static <T extends Q, Q extends Quantity<Q>>
        void registerInternalQuantityFactory(Class<T> quantityClass,
                                             DoubleQuantityFactory<?, ?>  doubleFactory,
                                             DecimalQuantityFactory<?, ?> decimalFactory) {
        factoryMap.put(quantityClass,
                       DelegateQuantityFactory.of(doubleFactory, decimalFactory));
    }

    public static <T extends Q, Q extends Quantity<Q>> QuantityFactory<Q> getQuantityFactory(Class<T> quantityClass) {
        QuantityFactory<?> factory = null;

        if (quantityClass != null) {
            factory = factoryMap.get(quantityClass);
        }

        return factory == null ?
                (QuantityFactory<Q>) genericQuantityFactory :
                (QuantityFactory<Q>) factory;
    }

    public static <T extends Q, Q extends Quantity<Q>> void registerQuantityFactory(Class<Q>           quantityClass,
                                                                                    QuantityFactory<Q> factory) {
        factoryMap.put(quantityClass, factory);
    }

    // hide constructor.
    private Quantities() {}

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(double value, Unit<Q> unit, Class<T> quantity) {
        return (T) getQuantityFactory(quantity).create(value, (Unit) unit);
    }

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(BigDecimal value, Unit<Q> unit, Class<Q> quantity) {
        return (T) getQuantityFactory(quantity).create(value, (Unit) unit);
    }

    public static <T extends Q, Q extends Quantity<Q>> T createQuantity(BigDecimal value, MathContext mathContext, Unit<Q> unit, Class<Q> quantity) {
        return (T) getQuantityFactory(quantity).create(value, mathContext, (Unit) unit);
    }

    public static <T extends Q, Q extends Quantity<Q>> T getQuantityAsType(Quantity<?> quantity, Class<T> clazz) {
        if (quantity instanceof DoubleQuantity<?>) {
            return (T) createQuantity(quantity.doubleValue(), quantity.getUnit(), (Class) clazz);
        } else {
            return (T) createQuantity(quantity.decimalValue(), ((DecimalQuantity) quantity).getMathContext(), quantity.getUnit(), (Class) clazz);
        }
    }

    static class DelegateQuantityFactory<Q extends Quantity<Q>> implements QuantityFactory<Q> {

        private final DoubleQuantityFactory<?, Q>  doubleQuantityFactory;
        private final DecimalQuantityFactory<?, Q> decimalQuantityFactory;

        static <Q extends Quantity<Q>> DelegateQuantityFactory<Q>
            of(DoubleQuantityFactory doubleFactory, DecimalQuantityFactory decimalFactory) {
            return new DelegateQuantityFactory<>(doubleFactory, decimalFactory);
        }

        private DelegateQuantityFactory(DoubleQuantityFactory<?, Q>  doubleQuantityFactory,
                                        DecimalQuantityFactory<?, Q> decimalQuantityFactory) {
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
