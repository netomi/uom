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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link PrefixedUnit} class.
 */
public class PrefixedUnitTest {

    @Test
    public void prefix() {
        Prefix  prefix = Prefixes.Metric.KILO;
        Unit<?> parentUnit = Units.SI.METRE;

        Unit<?> km = PrefixedUnit.withPrefix(parentUnit, prefix);

        assertEquals(prefix.getSymbol() + parentUnit.getSymbol(), km.getSymbol());
        assertEquals(prefix.getName() + parentUnit.getName(), km.getName());
        assertEquals(UnitConverters.multiply(1000, 1).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     km.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void prefixWithNonLinearConverter() {
        Unit<Temperature> mC = PrefixedUnit.withPrefix(Units.SI.CELSIUS, Prefixes.Metric.MILLI);

        UnitConverter withPrefix    = mC.getConverterTo(Units.SI.KELVIN);
        UnitConverter withoutPrefix = Units.SI.CELSIUS.getConverterTo(Units.SI.KELVIN);

        // 0.01 C converted to K should be equal to 10 mC.
        assertEquals(withoutPrefix.convert(10 * 1e-3), withPrefix.convert(10), 1e-6);
        // 10 K converted to C should be 1e3 in mC.
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-3, 1e-6);

        // apply milli prefix again to get microdegree celsius.
        Unit<Temperature> µC = mC.withPrefix(Prefixes.Metric.MILLI);

        withPrefix    = µC.getConverterTo(Units.SI.KELVIN);
        withoutPrefix = Units.SI.CELSIUS.getConverterTo(Units.SI.KELVIN);

        assertEquals(withoutPrefix.convert(10 * 1e-6), withPrefix.convert(10), 1e-6);
        assertEquals(withoutPrefix.inverse().convert(10), withPrefix.inverse().convert(10) * 1e-6, 1e-6);
    }

    @Test
    public void prefixWithLinearConverter() {
        Unit<Length> mm = PrefixedUnit.withPrefix(Units.SI.METRE, Prefixes.Metric.MILLI);

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

}
