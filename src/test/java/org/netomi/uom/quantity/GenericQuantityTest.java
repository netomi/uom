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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.quantity.primitive.DoubleQuantity;
import org.netomi.uom.unit.*;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Generic test cases for quantities.
 *
 * @param <Q> the quantity type
 */
public abstract class GenericQuantityTest<T extends Q, Q extends Quantity<Q>> {

    private static final double eps = 1e-6;

    protected abstract Class<T> getQuantityClass();

    protected abstract Unit<Q> getSystemUnit();

    protected abstract BiFunction<Double, Unit<Q>, T> getFactoryMethod();

    protected abstract Function<Double, T> getFactoryMethodForSystemUnit();

    protected Q createQuantity(double value, Class<Number> numberClass) {
        if (Double.class.equals(numberClass)) {
            return Quantities.createQuantity(value, getSystemUnit(), getQuantityClass());
        } else if (BigDecimal.class.equals(numberClass)) {
            return Quantities.createQuantity(BigDecimal.valueOf(value), getSystemUnit(), getQuantityClass());
        }
        throw new AssertionError("unexpected number class " + numberClass);
    }

    protected Q createQuantity(double value) {
        return Quantities.createQuantity(value, getSystemUnit(), getQuantityClass());
    }

    protected Q createQuantity(double value, Unit<Q> unit, Class<Number> numberClass) {
        if (Double.class.equals(numberClass)) {
            return Quantities.createQuantity(value, unit, getQuantityClass());
        } else if (BigDecimal.class.equals(numberClass)) {
            return Quantities.createQuantity(BigDecimal.valueOf(value), unit, getQuantityClass());
        }
        throw new AssertionError("unexpected number class " + numberClass);
    }

    protected Q createQuantity(double value, Unit<Q> unit) {
        return Quantities.createQuantity(value, unit, getQuantityClass());
    }

    protected Unit<Q> getMilliUnit() {
        return getSystemUnit().withPrefix(Prefixes.Metric.MILLI);
    }

    protected Unit<Q> getKiloUnit() {
        return getSystemUnit().withPrefix(Prefixes.Metric.KILO);
    }

    @Test
    public void typedQuantityCreation() {
        Q quantity = createQuantity(10);

        assertEquals(10, quantity.doubleValue(), eps);
        assertEquals(BigDecimal.valueOf(10).doubleValue(), quantity.decimalValue().doubleValue(), eps);

        assertTrue(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());
    }

    @Test
    public void genericQuantityCreation() {
        Quantity<Q> quantity = Quantities.createQuantity(100, getSystemUnit());

        assertEquals(100, quantity.doubleValue(), eps);
        assertEquals(BigDecimal.valueOf(100).doubleValue(), quantity.decimalValue().doubleValue(), eps);

        // generic quantity do not implement the specific quantity interface.
        assertFalse(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());

        quantity = DoubleQuantity.<Q>of(200, getSystemUnit());

        assertEquals(200, quantity.doubleValue(), eps);
        assertEquals(BigDecimal.valueOf(200).doubleValue(), quantity.decimalValue().doubleValue(), eps);

        // generic quantity do not implement the specific quantity interface.
        assertFalse(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void add(Class<Number> numberClass) {
        Q q1 = createQuantity(10, numberClass);
        Q q2 = createQuantity(20, numberClass);

        Quantity<Q> sum = q1.add(q2);

        assertEquals(30, sum.doubleValue(), eps);
        assertEquals(30, sum.decimalValue().doubleValue(), eps);

        assertSame(q1.getUnit(), sum.getUnit());

        // adding quantities in different units.
        q1 = createQuantity(10);
        q2 = createQuantity(20, getMilliUnit());

        sum = q1.add(q2);

        assertEquals(10.020, sum.doubleValue(), eps);
        assertEquals(10.020, sum.decimalValue().doubleValue(), eps);

        assertSame(q1.getUnit(), sum.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void addWithUnit(Class<Number> numberClass) {
        Q q1 = createQuantity(10, numberClass);
        Q q2 = createQuantity(20, getMilliUnit(), numberClass);

        Quantity<Q> sum = q1.add(q2, getKiloUnit());

        assertEquals(0.01002, sum.doubleValue(), eps);
        assertEquals(0.01002, sum.decimalValue().doubleValue(), eps);

        assertEquals(getKiloUnit(), sum.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void subtract(Class<Number> numberClass) {
        Q q1 = createQuantity(10, numberClass);
        Q q2 = createQuantity(20, numberClass);

        Quantity<Q> diff = q1.subtract(q2);

        assertEquals(-10, diff.doubleValue(), eps);
        assertEquals(-10, diff.decimalValue().doubleValue(), eps);

        assertSame(q1.getUnit(), diff.getUnit());

        // subtracting quantities in different units.
        q1 = createQuantity(10);
        q2 = createQuantity(20, getMilliUnit());

        diff = q1.subtract(q2);

        assertEquals(9.98, diff.doubleValue(), eps);
        assertEquals(9.98, diff.decimalValue().doubleValue(), eps);

        assertSame(q1.getUnit(), diff.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void subtractWithUnit(Class<Number> numberClass) {
        Q q1 = createQuantity(10, numberClass);
        Q q2 = createQuantity(20, getMilliUnit(), numberClass);

        Quantity<Q> diff = q1.subtract(q2, getKiloUnit());

        assertEquals(0.00998, diff.doubleValue(), eps);
        assertEquals(0.00998, diff.decimalValue().doubleValue(), eps);

        assertEquals(getKiloUnit(), diff.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void negate(Class<Number> numberClass) {
        Q quantity = createQuantity(10, numberClass);

        assertEquals(-10, quantity.negate().doubleValue(), eps);
        assertEquals(-10, quantity.negate().decimalValue().doubleValue(), eps);

        assertSame(quantity.getUnit(), quantity.negate().getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void multiply(Class<Number> numberClass) {
        Q q1 = createQuantity(25, numberClass);
        Q q2 = createQuantity(0.005, numberClass);

        // convert to milli unit.
        q2 = (Q) q2.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(5, q2.doubleValue(), eps);
        assertEquals(5, q2.decimalValue().doubleValue(), eps);


        Q result = (Q) q1.multiply(q2);

        assertEquals(25. * 0.005, result.doubleValue(), eps);
        assertEquals(25. * 0.005, result.decimalValue().doubleValue(), eps);

        if (getSystemUnit().getDimension() != Dimensions.NONE) {
            assertSame(getSystemUnit().multiply(getSystemUnit()), result.getUnit());
        }
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void divide(Class<Number> numberClass) {
        Q q1 = createQuantity(100, numberClass);
        Q q2 = createQuantity(2, numberClass);

        // convert to milli unit.
        q2 = (Q) q2.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(2000, q2.doubleValue(), eps);
        assertEquals(2000, q2.decimalValue().doubleValue(), eps);

        Q result = (Q) q1.divide(q2);

        assertEquals(100. / 2., result.doubleValue(), eps);
        assertEquals(100. / 2., result.decimalValue().doubleValue(), eps);

        if (getSystemUnit().getDimension() != Dimensions.NONE) {
            assertSame(getSystemUnit().divide(getSystemUnit()), result.getUnit());
        }
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void reciprocal(Class<Number> numberClass) {
        Q quantity = createQuantity(100, numberClass);

        Q result = (Q) quantity.reciprocal();

        assertEquals(1. / 100., result.doubleValue(), eps);
        assertEquals(1. / 100., result.decimalValue().doubleValue(), eps);

        assertSame(Units.ONE.divide(getSystemUnit()), result.getUnit());

        // convert to milli unit.
        Q quantityInMilli = (Q) quantity.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(100000, quantityInMilli.doubleValue(), eps);
        assertEquals(100000, quantityInMilli.decimalValue().doubleValue(), eps);

        Q resultInMilli = (Q) quantityInMilli.reciprocal();

        assertEquals(1. / 100000., resultInMilli.doubleValue(), eps);
        assertEquals(1. / 100000., resultInMilli.decimalValue().doubleValue(), eps);

        assertSame(Units.ONE.divide(getSystemUnit().withPrefix(Prefixes.Metric.MILLI)), resultInMilli.getUnit());
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void to(Class<Number> numberClass) {
        Q quantity = createQuantity(123, numberClass);

        Unit<Q> milliUnit = getSystemUnit().withPrefix(Prefixes.Metric.MILLI);
        UnitConverter converter = Prefixes.Metric.MILLI.getUnitConverter().inverse();
        assertEquals(converter.convert(123), quantity.to(milliUnit).doubleValue(), eps);
        assertEquals(converter.convert(123), quantity.to(milliUnit).decimalValue().doubleValue(), eps);
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void toSystemUnit(Class<Number> numberClass) {
        Unit<Q> kiloUnit = getSystemUnit().withPrefix(Prefixes.Metric.KILO);
        Q quantity = createQuantity(123, kiloUnit, numberClass);

        Quantity<Q> result = quantity.toSystemUnit();

        assertEquals(Prefixes.Metric.KILO.getUnitConverter().convert(123), result.doubleValue(), eps);
        assertEquals(Prefixes.Metric.KILO.getUnitConverter().convert(BigDecimal.valueOf(123)).doubleValue(),
                     result.decimalValue().doubleValue(), eps);

        assertSame(getSystemUnit(), result.getUnit());
    }

    @Test
    public void factoryMethod() {
        T quantity = getFactoryMethod().apply(10.0, getSystemUnit());

        assertSame(getSystemUnit(), quantity.getUnit());
        assertEquals(10, quantity.doubleValue(), eps);

        quantity = getFactoryMethodForSystemUnit().apply(20.0);

        assertSame(getSystemUnit(), quantity.getUnit());
        assertEquals(20, quantity.doubleValue(), eps);
    }
}
