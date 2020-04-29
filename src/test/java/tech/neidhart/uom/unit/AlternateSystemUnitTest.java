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
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.function.UnitConverters;
import tech.neidhart.uom.math.Fraction;
import tech.neidhart.uom.quantity.Length;
import tech.neidhart.uom.unit.systems.SI;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AlternateSystemUnit} class.
 */
public class AlternateSystemUnitTest {

    @Test
    public void systemUnit() {
        Unit<Length> unit = AlternateSystemUnit.of(Units.ONE, "rad", "RADIAN");

        assertTrue(unit.isSystemUnit());
        assertSame(unit, unit.getSystemUnit());
    }

    @Test
    public void baseUnits() {
        // dimensionless alternate system units should be considered as base unit.
        assertEquals(Collections.singletonMap(SI.RADIAN, Fraction.ONE), SI.RADIAN.getBaseUnits());
        assertEquals(Collections.singletonMap(SI.STERADIAN, Fraction.ONE), SI.STERADIAN.getBaseUnits());

        // other alternate units should only return the normal base units.
        assertEquals(Collections.singletonMap(SI.SECOND, Fraction.of(-1)), SI.BECQUEREL.getBaseUnits());
        assertEquals(Collections.singletonMap(SI.SECOND, Fraction.of(-1)), SI.HERTZ.getBaseUnits());
    }

    @Test
    public void withSymbol() {
        Unit<Length> unit = AlternateSystemUnit.of(Units.ONE, "rad", "RADIAN");
        Unit<Length> customUnit = unit.withSymbol("custom");
        assertNotSame(unit, customUnit);
        assertEquals("custom", customUnit.getSymbol());
    }

    @Test
    public void withName() {
        Unit<Length> unit = AlternateSystemUnit.of(Units.ONE, "rad", "RADIAN");
        Unit<Length> customUnit = unit.withName("CUSTOM");
        assertNotSame(unit, customUnit);
        assertEquals("CUSTOM", customUnit.getName());
    }

    @Test
    public void withPrefix() {
        Unit<Length> unit = AlternateSystemUnit.of(Units.ONE, "rad", "RADIAN");
        Unit<Length> prefixedUnit = unit.withPrefix(Prefixes.Metric.KILO);
        assertNotSame(unit, prefixedUnit);
        assertEquals(UnitConverters.multiply(1000), prefixedUnit.getSystemConverter());
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(new AlternateSystemUnit<>(Units.ONE, "rad", null), new AlternateSystemUnit<>(Units.ONE, "rad", null))
                .addEqualityGroup(new BaseUnit<>("rad", null, Dimensions.NONE))
                .addEqualityGroup(Units.ONE, SI.METRE.divide(SI.METRE))
                .addEqualityGroup(new TransformedUnit<>(Units.ONE, UnitConverters.multiply(10)))
                .addEqualityGroup("blabla")
                .testEquals();
    }
}
