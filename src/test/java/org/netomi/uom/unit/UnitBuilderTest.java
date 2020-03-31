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

package org.netomi.uom.unit;

import org.junit.jupiter.api.Test;
import org.netomi.uom.Prefix;
import org.netomi.uom.Unit;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.quantity.Length;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitBuilderTest {

    @Test
    public void symbol() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = Units.buildFrom(parentUnit).withSymbol("sym").build();

        assertEquals("sym", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void name() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = Units.buildFrom(parentUnit).withName("MY METER").build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("MY METER", unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void prefix() {
        Prefix  prefix = Prefixes.Metric.KILO;
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> km = Units.buildFrom(parentUnit).withPrefix(prefix).build();

        assertEquals(prefix.getSymbol() + parentUnit.getSymbol(), km.getSymbol());
        assertEquals(prefix.getName() + parentUnit.getName(), km.getName());
        assertEquals(UnitConverters.multiply(1000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     km.getConverterToAny(Units.Imperial.YARD));

        // MILLI(KILO(m)) = m
        Unit<?> unit = Units.buildFrom(km).withPrefix(Prefixes.Metric.MILLI).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));

        // DECI(KILO(m)) = HECTO(m)
        unit = Units.buildFrom(km).withPrefix(Prefixes.Metric.DECI).build();

        assertEquals(Prefixes.Metric.HECTO.getSymbol() + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(Prefixes.Metric.HECTO.getName() + parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(100, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // DEKA(KILO(m)) = 10^4(m)
        unit = Units.buildFrom(km).withPrefix(Prefixes.Metric.DEKA).build();

        assertEquals("10^4*" + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("10^4*" + parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(10000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void shiftedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m + 100
        Unit<?> unit = Units.buildFrom(parentUnit).shiftedBy(100).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(100).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m + 200.23
        unit = Units.buildFrom(parentUnit).shiftedBy(BigDecimal.valueOf(200.23)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(BigDecimal.valueOf(200.23)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void multipliedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m * 2.5
        Unit<?> unit = Units.buildFrom(parentUnit).multipliedBy(2.5).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(2.5).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 2.5
        unit = Units.buildFrom(parentUnit).multipliedBy(BigDecimal.valueOf(2.5)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(BigDecimal.valueOf(2.5)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 5/9
        unit = Units.buildFrom(parentUnit).multipliedBy(5, 9).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(5, 9).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void forQuantity() {
        Unit<?> parentUnit = Units.SI.METRE;

        // not really a functional test, rather a compiler check.
        Unit<Length> unit = Units.buildFrom(parentUnit).forQuantity(Length.class).build();

        assertEquals(parentUnit.getDimension(), unit.getDimension());
    }

}
