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
import org.netomi.uom.unit.Prefixes;

import static org.junit.Assert.*;

/**
 * Generic test cases for quantities.
 *
 * @param <Q> the quantity type
 */
public abstract class GenericQuantityTest<T extends Q, Q extends Quantity<Q>> {

    protected abstract Class<T> getQuantityClass();

    protected abstract Unit<Q> getSystemUnit();

    protected Q createQuantity(double value) {
        return Quantities.createQuantity(value, getSystemUnit(), getQuantityClass());
    }

    @Test
    public void typedQuantityCreation() {
        Q quantity = createQuantity(10);

        assertTrue(getQuantityClass().isAssignableFrom(quantity.getClass()));
        assertSame(getSystemUnit(), quantity.getUnit());
    }

    @Test
    public void genericQuantityCreation() {
        Quantity<Q> quantity = Quantities.createQuantity(0, getSystemUnit());

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
    public void to() {
        Q quantity = createQuantity(123);

        Unit<Q> milliUnit = getSystemUnit().withPrefix(Prefixes.Metric.MILLI);
        UnitConverter converter = Prefixes.Metric.MILLI.getUnitConverter().inverse();
        assertEquals(converter.convert(123), quantity.to(milliUnit).doubleValue(), 1e-6);
    }
}
