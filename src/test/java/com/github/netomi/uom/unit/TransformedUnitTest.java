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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.function.UnitConverters;
import com.github.netomi.uom.quantity.Length;
import com.github.netomi.uom.unit.systems.Intl;
import com.github.netomi.uom.unit.systems.SI;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TransformedUnit} class.
 */
public class TransformedUnitTest {

    @Test
    public void factoryMethod() {
        Unit<Length> unit = TransformedUnit.of(SI.METRE, UnitConverters.multiply(100, 1));

        assertTrue(unit instanceof TransformedUnit<?>);

        // resulting unit should have an identity converter and thus the system unit should
        // be returned by the factory method.
        unit = TransformedUnit.of(unit, UnitConverters.multiply(1, 100));
        assertFalse(unit instanceof TransformedUnit<?>);

        assertSame(SI.METRE, unit);
    }

    @Test
    public void systemUnit() {
        Unit<Length> unit = TransformedUnit.of(SI.METRE, UnitConverters.multiply(100));

        assertFalse(unit.isSystemUnit());
        assertEquals(SI.METRE, unit.getSystemUnit());
    }

    @Test
    public void shiftedBy() {
        Unit<?> parentUnit = SI.METRE;

        // m + 100
        Unit<?> unit = TransformedUnit.of(parentUnit, UnitConverters.shift(100));

        assertEquals("(transform m (+ x '100.0'))", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        Assertions.assertEquals(UnitConverters.shift(100).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     unit.getConverterToAny(Intl.YARD));

        // m + 200.23
        unit = TransformedUnit.of(parentUnit, UnitConverters.shift(BigDecimal.valueOf(200.23)));

        assertEquals("(transform m (+ x '200.23'))", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        Assertions.assertEquals(UnitConverters.shift(BigDecimal.valueOf(200.23)).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     unit.getConverterToAny(Intl.YARD));
    }

    @Test
    public void multipliedBy() {
        Unit<?> parentUnit = SI.METRE;

        // m * 2.5
        Unit<?> unit = TransformedUnit.of(parentUnit, UnitConverters.multiply(2.5));

        assertEquals("5|2" + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        Assertions.assertEquals(UnitConverters.multiply(2.5).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     unit.getConverterToAny(Intl.YARD));

        // m * 2.5
        unit = TransformedUnit.of(parentUnit, UnitConverters.multiply(BigDecimal.valueOf(2.5)));

        assertEquals("5|2" + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        Assertions.assertEquals(UnitConverters.multiply(BigDecimal.valueOf(2.5)).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     unit.getConverterToAny(Intl.YARD));

        // m * 5/9
        unit = TransformedUnit.of(parentUnit, UnitConverters.multiply(5, 9));

        assertEquals("5|9" + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        Assertions.assertEquals(UnitConverters.multiply(5, 9).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     unit.getConverterToAny(Intl.YARD));
    }

    @Test
    public void withSymbol() {
        Unit<Length> unit = TransformedUnit.of(SI.METRE, UnitConverters.multiply(100));
        Unit<Length> customUnit = unit.withSymbol("custom");
        assertNotSame(unit, customUnit);
        assertEquals("custom", customUnit.getSymbol());
    }

    @Test
    public void withName() {
        Unit<Length> unit = TransformedUnit.of(SI.METRE, UnitConverters.multiply(100));
        Unit<Length> customUnit = unit.withName("CUSTOM");
        assertNotSame(unit, customUnit);
        assertEquals("CUSTOM", customUnit.getName());
    }

    @Test
    public void withPrefix() {
        Unit<Length> unit = TransformedUnit.of(SI.METRE, UnitConverters.multiply(100));
        Unit<Length> prefixedUnit = unit.withPrefix(Prefixes.Metric.KILO);
        assertNotSame(unit, prefixedUnit);
        Assertions.assertEquals(UnitConverters.multiply(100000), prefixedUnit.getSystemConverter());
    }
}
