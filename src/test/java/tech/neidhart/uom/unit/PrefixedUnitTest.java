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
package tech.neidhart.uom.unit;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import tech.neidhart.uom.Prefix;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.function.UnitConverters;
import tech.neidhart.uom.quantity.Frequency;
import tech.neidhart.uom.quantity.Length;
import tech.neidhart.uom.quantity.thermodynamic.Temperature;
import tech.neidhart.uom.unit.systems.Intl;
import tech.neidhart.uom.unit.systems.SI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PrefixedUnit} class.
 */
public class PrefixedUnitTest {

    @Test
    public void systemUnit() {
        PrefixedUnit<Frequency> unit = PrefixedUnit.of(SI.HERTZ, Prefixes.Metric.KILO);

        assertFalse(unit.isSystemUnit());
        assertSame(SI.HERTZ, unit.getSystemUnit());
    }

    @Test
    public void prefix() {
        Prefix prefix = Prefixes.Metric.KILO;
        Unit<?> parentUnit = SI.METRE;

        PrefixedUnit<?> km = PrefixedUnit.of(parentUnit, prefix);

        assertEquals(prefix.getSymbol() + parentUnit.getSymbol(), km.getSymbol());
        assertEquals(prefix.getName() + parentUnit.getName(), km.getName());
        assertEquals(UnitConverters.multiply(1000, 1).andThen(parentUnit.getConverterToAny(Intl.YARD)),
                     km.getConverterToAny(Intl.YARD));
    }

    @Test
    public void prefixWithNonLinearConverter() {
        PrefixedUnit<Temperature> mC = PrefixedUnit.of(SI.CELSIUS, Prefixes.Metric.MILLI);

        UnitConverter withPrefix = mC.getConverterTo(SI.KELVIN);
        UnitConverter withoutPrefix = SI.CELSIUS.getConverterTo(SI.KELVIN);

        // 0.01 C converted to K should be equal to 10 mC.
        assertEquals(withoutPrefix.convert(10 * 1e-3), withPrefix.convert(10), 1e-6);
        // 10 K converted to C should be 1e3 in mC.
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-3, 1e-6);

        // microdegree Celsius.
        Unit<Temperature> µC = SI.CELSIUS.withPrefix(Prefixes.Metric.MICRO);

        withPrefix = µC.getConverterTo(SI.KELVIN);
        withoutPrefix = SI.CELSIUS.getConverterTo(SI.KELVIN);

        assertEquals(withoutPrefix.convert(10 * 1e-6), withPrefix.convert(10), 1e-6);
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-6, 1e-6);
    }

    @Test
    public void prefixWithLinearConverter() {
        PrefixedUnit<Length> mm = PrefixedUnit.of(SI.METRE, Prefixes.Metric.MILLI);

        UnitConverter withPrefix = mm.getConverterTo(Intl.YARD);
        UnitConverter withoutPrefix = SI.METRE.getConverterTo(Intl.YARD);

        // 0.02 m converted to yd should be equal to 20 mm.
        assertEquals(withoutPrefix.convert(20 * 1e-3), withPrefix.convert(20), 1e-6);
        // 20 yd converted to m should be 1e3 in mm.
        assertEquals(withoutPrefix.inverse().convert(20), withPrefix.inverse().convert(20) * 1e-3, 1e-6);

        // microdegree Celsius.
        Unit<Length> µm = SI.METRE.withPrefix(Prefixes.Metric.MICRO);

        withPrefix = µm.getConverterTo(Intl.YARD);
        withoutPrefix = SI.METRE.getConverterTo(Intl.YARD);

        assertEquals(withoutPrefix.convert(10 * 1e-6), withPrefix.convert(10), 1e-6);
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-6, 1e-6);
    }

    @Test
    public void withSymbol() {
        PrefixedUnit<Length> unit = PrefixedUnit.of(SI.METRE, Prefixes.Metric.KILO);

        PrefixedUnit<Length> customUnit = unit.withSymbol("custom");
        assertNotSame(unit, customUnit);
        assertEquals("custom", customUnit.getSymbol());
    }

    @Test
    public void withName() {
        PrefixedUnit<Length> unit = PrefixedUnit.of(SI.METRE, Prefixes.Metric.KILO);

        PrefixedUnit<Length> customUnit = unit.withName("CUSTOM");
        assertNotSame(unit, customUnit);
        assertEquals("CUSTOM", customUnit.getName());
    }

    @Test
    public void withPrefix() {
        PrefixedUnit<Length> unit = PrefixedUnit.of(SI.METRE, Prefixes.Metric.KILO);

        assertThrows(IllegalArgumentException.class, () -> {
            unit.withPrefix(Prefixes.Metric.KILO);
        });
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(PrefixedUnit.of(SI.CANDELA, Prefixes.Metric.CENTI))
                .addEqualityGroup(PrefixedUnit.of(SI.LUMEN, Prefixes.Metric.CENTI))
                .addEqualityGroup(Units.ONE)
                .addEqualityGroup(SI.METRE)
                .addEqualityGroup(new TransformedUnit<>(SI.CANDELA, UnitConverters.multiply(10)))
                .addEqualityGroup("blabla")
                .testEquals();
    }
}
