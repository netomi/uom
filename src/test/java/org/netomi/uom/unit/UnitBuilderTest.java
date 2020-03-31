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
import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.quantity.Temperature;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link UnitBuilder} class.
 */
public class UnitBuilderTest {

    private static <Q extends Quantity<Q>> UnitBuilder<Q> buildFrom(Unit<Q> unit) {
        return new UnitBuilder<>(unit);
    }

    @Test
    public void symbol() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = buildFrom(parentUnit).withSymbol("sym").build();

        assertEquals("sym", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void name() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = buildFrom(parentUnit).withName("MY METER").build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("MY METER", unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void prefix() {
        Prefix  prefix = Prefixes.Metric.KILO;
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> km = buildFrom(parentUnit).withPrefix(prefix).build();

        assertEquals(prefix.getSymbol() + parentUnit.getSymbol(), km.getSymbol());
        assertEquals(prefix.getName() + parentUnit.getName(), km.getName());
        assertEquals(UnitConverters.multiply(1000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     km.getConverterToAny(Units.Imperial.YARD));

        // MILLI(KILO(m)) = m
        Unit<?> unit = buildFrom(km).withPrefix(Prefixes.Metric.MILLI).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));

        // DECI(KILO(m)) = HECTO(m)
        unit = buildFrom(km).withPrefix(Prefixes.Metric.DECI).build();

        assertEquals(Prefixes.Metric.HECTO.getSymbol() + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(Prefixes.Metric.HECTO.getName() + parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(100, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // DEKA(KILO(m)) = 10^4(m)
        unit = buildFrom(km).withPrefix(Prefixes.Metric.DEKA).build();

        assertEquals("10^4*" + parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("10^4*" + parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(10000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void prefixWithNonLinearConverter() {
        Unit<Temperature> mC = Units.Other.CELSIUS.withPrefix(Prefixes.Metric.MILLI);

        UnitConverter withPrefix    = mC.getConverterTo(Units.SI.KELVIN);
        UnitConverter withoutPrefix = Units.Other.CELSIUS.getConverterTo(Units.SI.KELVIN);

        // 0.01 C converted to K should be equal to 10 mC.
        assertEquals(withoutPrefix.convert(10 * 1e-3), withPrefix.convert(10), 1e-6);
        // 10 K converted to C should be 1e3 in mC.
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-3, 1e-6);

        // apply milli prefix again to get microdegree celsius.
        Unit<Temperature> µC = mC.withPrefix(Prefixes.Metric.MILLI);

        withPrefix    = µC.getConverterTo(Units.SI.KELVIN);
        withoutPrefix = Units.Other.CELSIUS.getConverterTo(Units.SI.KELVIN);

        assertEquals(withoutPrefix.convert(10 * 1e-6), withPrefix.convert(10), 1e-6);
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-6, 1e-6);
    }

    @Test
    public void prefixWithLinearConverter() {
        Unit<Length> mm = Units.SI.METRE.withPrefix(Prefixes.Metric.MILLI);

        UnitConverter withPrefix    = mm.getConverterTo(Units.Imperial.YARD);
        UnitConverter withoutPrefix = Units.SI.METRE.getConverterTo(Units.Imperial.YARD);

        // 0.02 m converted to yd should be equal to 20 mm.
        assertEquals(withoutPrefix.convert(20 * 1e-3), withPrefix.convert(20), 1e-6);
        // 20 yd converted to m should be 1e3 in mm.
        assertEquals(withoutPrefix.inverse().convert(20), withPrefix.inverse().convert(20) * 1e-3, 1e-6);

        // apply milli prefix again to get micrometer.
        Unit<Length> µm = mm.withPrefix(Prefixes.Metric.MILLI);

        withPrefix    = µm.getConverterTo(Units.Imperial.YARD);
        withoutPrefix = Units.SI.METRE.getConverterTo(Units.Imperial.YARD);

        assertEquals(withoutPrefix.convert(10 * 1e-6), withPrefix.convert(10), 1e-6);
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-6, 1e-6);
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
        unit = buildFrom(parentUnit).shiftedBy(BigDecimal.valueOf(200.23)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(BigDecimal.valueOf(200.23)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void multipliedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m * 2.5
        Unit<?> unit = buildFrom(parentUnit).multipliedBy(2.5).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(2.5).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 2.5
        unit = buildFrom(parentUnit).multipliedBy(BigDecimal.valueOf(2.5)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(BigDecimal.valueOf(2.5)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 5/9
        unit = buildFrom(parentUnit).multipliedBy(5, 9).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(5, 9).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void forQuantity() {
        Unit<?> parentUnit = Units.SI.METRE;

        // not really a functional test, rather a compiler check.
        Unit<Length> unit = buildFrom(parentUnit).forQuantity(Length.class).build();

        assertEquals(parentUnit.getDimension(), unit.getDimension());
    }

}
