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

    protected abstract Class<T> getQuantityClass();

    protected abstract Unit<Q> getSystemUnit();

    protected abstract BiFunction<Double, Unit<Q>, T> getFactoryMethod();

    protected abstract Function<Double, T> getFactoryMethodForSystemUnit();

    protected Q createQuantity(double value) {
        return Quantities.createQuantity(value, getSystemUnit(), getQuantityClass());
    }

    protected Q createQuantity(double value, Unit<Q> unit) {
        return Quantities.createQuantity(value, unit, getQuantityClass());
    }

    @Test
    public void typedQuantityCreation() {
        Q quantity = createQuantity(10);

        assertEquals(10, quantity.doubleValue(), 1e-6);
        assertEquals(BigDecimal.valueOf(10).doubleValue(), quantity.decimalValue().doubleValue(), 1e-12);

        assertTrue(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());
    }

    @Test
    public void genericQuantityCreation() {
        Quantity<Q> quantity = Quantities.createQuantity(100, getSystemUnit());

        assertEquals(100, quantity.doubleValue(), 1e-6);
        assertEquals(BigDecimal.valueOf(100).doubleValue(), quantity.decimalValue().doubleValue(), 1e-12);

        // generic quantity do not implement the specific quantity interface.
        assertFalse(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());

        quantity = DoubleQuantity.<Q>of(200, getSystemUnit());

        assertEquals(200, quantity.doubleValue(), 1e-6);
        assertEquals(BigDecimal.valueOf(200).doubleValue(), quantity.decimalValue().doubleValue(), 1e-12);

        // generic quantity do not implement the specific quantity interface.
        assertFalse(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());
    }

    @Test
    public void add() {
        Q q1 = createQuantity(10);
        Q q2 = createQuantity(20);

        Quantity<Q> sum = q1.add(q2);

        assertEquals(30, sum.doubleValue(), 1e-6);
        assertSame(q1.getUnit(), sum.getUnit());
    }

    @Test
    public void subtract() {
        Q q1 = createQuantity(10);
        Q q2 = createQuantity(20);

        Quantity<Q> diff = q1.subtract(q2);

        assertEquals(-10, diff.doubleValue(), 1e-6);
        assertSame(q1.getUnit(), diff.getUnit());
    }

    @Test
    public void negate() {
        Q quantity = createQuantity(10);

        assertEquals(-10, quantity.negate().doubleValue(), 1e-6);
        assertSame(quantity.getUnit(), quantity.negate().getUnit());
    }

    @Test
    public void multiply() {
        Q q1 = createQuantity(25);
        Q q2 = createQuantity(0.005);

        // convert to milli unit.
        q2 = (Q) q2.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(5, q2.doubleValue(), 1e-6);

        Q result = (Q) q1.multiply(q2);

        assertEquals(25. * 0.005, result.doubleValue(), 1e-6);

        if (getSystemUnit().getDimension() != Dimensions.NONE) {
            assertSame(getSystemUnit().multiply(getSystemUnit()), result.getUnit());
        }
    }

    @Test
    public void divide() {
        Q q1 = createQuantity(100);
        Q q2 = createQuantity(2);

        // convert to milli unit.
        q2 = (Q) q2.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(2000, q2.doubleValue(), 1e-6);

        Q result = (Q) q1.divide(q2);

        assertEquals(100. / 2., result.doubleValue(), 1e-6);

        if (getSystemUnit().getDimension() != Dimensions.NONE) {
            assertSame(getSystemUnit().divide(getSystemUnit()), result.getUnit());
        }
    }

    @Test
    public void reciprocal() {
        Q quantity = createQuantity(100);

        Q result = (Q) quantity.reciprocal();

        assertEquals(1. / 100., result.doubleValue(), 1e-6);
        assertSame(Units.ONE.divide(getSystemUnit()), result.getUnit());

        // convert to milli unit.
        Q quantityInMilli = (Q) quantity.to(getSystemUnit().withPrefix(Prefixes.Metric.MILLI));

        // make sure the q2 value is correctly expressed in milli unit.
        assertEquals(100000, quantityInMilli.doubleValue(), 1e-6);

        Q resultInMilli = (Q) quantityInMilli.reciprocal();

        assertEquals(1. / 100000., resultInMilli.doubleValue(), 1e-6);
        assertSame(Units.ONE.divide(getSystemUnit().withPrefix(Prefixes.Metric.MILLI)), resultInMilli.getUnit());
    }

    @Test
    public void to() {
        Q quantity = createQuantity(123);

        Unit<Q> milliUnit = getSystemUnit().withPrefix(Prefixes.Metric.MILLI);
        UnitConverter converter = Prefixes.Metric.MILLI.getUnitConverter().inverse();
        assertEquals(converter.convert(123), quantity.to(milliUnit).doubleValue(), 1e-6);
    }

    @Test
    public void toSystemUnit() {
        Unit<Q> kiloUnit = getSystemUnit().withPrefix(Prefixes.Metric.KILO);
        Q quantity = createQuantity(123, kiloUnit);

        Quantity<Q> result = quantity.toSystemUnit();

        assertEquals(Prefixes.Metric.KILO.getUnitConverter().convert(123), result.doubleValue(), 1e-6);
        assertSame(getSystemUnit(), result.getUnit());
    }

    @Test
    public void factoryMethod() {
        T quantity = getFactoryMethod().apply(10.0, getSystemUnit());

        assertSame(getSystemUnit(), quantity.getUnit());
        assertEquals(10, quantity.doubleValue(), 1e-6);

        quantity = getFactoryMethodForSystemUnit().apply(20.0);

        assertSame(getSystemUnit(), quantity.getUnit());
        assertEquals(20, quantity.doubleValue(), 1e-6);
    }
}
