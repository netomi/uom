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
import org.netomi.uom.UnitConverter;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.quantity.Temperature;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.netomi.uom.unit.UnitBuilder.from;

/**
 * Unit tests for the {@link UnitBuilder} class.
 */
public class UnitBuilderTest {

    @Test
    public void symbol() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = from(parentUnit).withSymbol("sym").build();

        assertEquals("sym", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void name() {
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> unit = from(parentUnit).withName("MY METER").build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("MY METER", unit.getName());
        assertEquals(parentUnit.getConverterToAny(Units.Imperial.YARD), unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void prefix() {
        Prefix  prefix = Prefixes.Metric.KILO;
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> km = from(parentUnit).withPrefix(prefix).build();

        assertEquals(prefix.getSymbol() + parentUnit.getSymbol(), km.getSymbol());
        assertEquals(prefix.getName() + parentUnit.getName(), km.getName());
        assertEquals(UnitConverters.multiply(1000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     km.getConverterToAny(Units.Imperial.YARD));
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
    public void complexNaming() {
        Unit<Length> yard = Units.Imperial.YARD;

        Unit<Length> milliYard = Units.buildFrom(yard).withPrefix(Prefixes.Metric.MILLI).build();
        assertEquals("myd", milliYard.getSymbol());
        assertEquals("MILLIYARD", milliYard.getName());

        Unit<Length> customName = Units.buildFrom(yard).withPrefix(Prefixes.Metric.MILLI).withName("CUSTOM").build();
        assertEquals("myd", customName.getSymbol());
        assertEquals("MILLICUSTOM", customName.getName());

        Unit<Length> customSymbol = Units.buildFrom(yard).withPrefix(Prefixes.Metric.MILLI).withSymbol("custom").build();
        assertEquals("mcustom", customSymbol.getSymbol());
        assertEquals("MILLIYARD", customSymbol.getName());

        Unit<Length> customUnit = Units.buildFrom(yard).withPrefix(Prefixes.Metric.MILLI).withSymbol("custom").withName("CUSTOM").build();
        assertEquals("mcustom", customUnit.getSymbol());
        assertEquals("MILLICUSTOM", customUnit.getName());

        // Example does not make sense, just to illustrate how the UnitBuilder works atm.
        // If the delegate unit already has a prefix, wrap any newly added prefix around it.
        Unit<Length> customMilliYard = from(milliYard).multipliedBy(100).withSymbol("abc").withPrefix(Prefixes.Metric.KILO).build();
        assertEquals("kabc", customMilliYard.getSymbol());
        assertEquals("KILOMILLIYARD", customMilliYard.getName());
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
        unit = from(parentUnit).shiftedBy(BigDecimal.valueOf(200.23)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(BigDecimal.valueOf(200.23)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void multipliedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m * 2.5
        Unit<?> unit = from(parentUnit).multipliedBy(2.5).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(2.5).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 2.5
        unit = from(parentUnit).multipliedBy(BigDecimal.valueOf(2.5)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(BigDecimal.valueOf(2.5)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 5/9
        unit = from(parentUnit).multipliedBy(5, 9).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(5, 9).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void forQuantity() {
        Unit<?> parentUnit = Units.SI.METRE;

        // not really a functional test, rather a compiler check.
        Unit<Length> unit = UnitBuilder.<Length>fromAny(parentUnit).build();

        assertEquals(parentUnit.getDimension(), unit.getDimension());
    }

}
