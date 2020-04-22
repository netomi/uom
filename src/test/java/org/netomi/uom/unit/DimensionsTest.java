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

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.netomi.uom.Dimension;
import org.netomi.uom.math.Fraction;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Dimensions} class and its inner classes.
 */
public class DimensionsTest {

    @Test
    public void noneDimension() {
        Dimension none   = Dimensions.NONE;
        Dimension length = Dimensions.LENGTH;

        assertSame(Dimensions.NONE, none.multiply(none));
        assertSame(Dimensions.NONE, none.divide(none));
        assertSame(Dimensions.NONE, none.pow(2));
        assertSame(Dimensions.NONE, none.root(2));

        assertEquals("", none.toString());

        assertSame(length, none.multiply(length));
        assertSame(length, length.multiply(none));
    }

    @Test
    public void physicalBaseDimensions() {
        Collection<Dimension> baseDimensions = Dimensions.getPhysicalBaseDimensions();

        assertTrue(baseDimensions.contains(Dimensions.LENGTH));
        assertEquals("L", Dimensions.LENGTH.toString());
        assertTrue(baseDimensions.contains(Dimensions.MASS));
        assertEquals("M", Dimensions.MASS.toString());
        assertTrue(baseDimensions.contains(Dimensions.TIME));
        assertEquals("T", Dimensions.TIME.toString());
        assertTrue(baseDimensions.contains(Dimensions.TEMPERATURE));
        assertEquals("\u0398", Dimensions.TEMPERATURE.toString());
        assertTrue(baseDimensions.contains(Dimensions.ELECTRIC_CURRENT));
        assertEquals("I", Dimensions.ELECTRIC_CURRENT.toString());
        assertTrue(baseDimensions.contains(Dimensions.AMOUNT_OF_SUBSTANCE));
        assertEquals("N", Dimensions.AMOUNT_OF_SUBSTANCE.toString());
        assertTrue(baseDimensions.contains(Dimensions.LUMINOUS_INTENSITY));
        assertEquals("J", Dimensions.LUMINOUS_INTENSITY.toString());
    }

    @Test
    public void namedDimension() {
        Dimension none = Dimensions.NONE;
        Dimension d    = Dimensions.ofName("CUSTOM");

        assertEquals("CUSTOM", d.toString());

        assertSame(d, d.multiply(none));
        assertSame(d, d.divide(none));

        assertEquals(d, d.multiply(Dimensions.TIME).divide(Dimensions.TIME));

        assertEquals(d, d.pow(2).root(2));
        assertEquals(d, d.root(2).pow(2));

        assertBaseDimensions(d, 1, d, Fraction.ONE);

        assertBaseDimensions(d.multiply(Dimensions.LENGTH), 2, d, Fraction.ONE);
        assertBaseDimensions(d.multiply(Dimensions.LENGTH), 2, Dimensions.LENGTH, Fraction.ONE);

        assertBaseDimensions(d.multiply(Dimensions.LENGTH).divide(Dimensions.LENGTH), 1, d, Fraction.ONE);

        new EqualsTester()
                .addEqualityGroup(d)
                .addEqualityGroup(Dimensions.ofName("OTHER"))
                .addEqualityGroup(Dimensions.NONE)
                .addEqualityGroup(Dimensions.TIME.pow(2))
                .addEqualityGroup(Dimensions.LENGTH.multiply(Dimensions.LENGTH), Dimensions.LENGTH.pow(2), Dimensions.LENGTH.pow(4).root(2))
                .addEqualityGroup("blabla")
                .testEquals();
    }

    private static void assertBaseDimensions(Dimension d, int numberOfDimensions, Dimension baseDimension, Fraction fraction) {
        Map<Dimension, Fraction> baseDimensions = d.getBaseDimensions();

        assertEquals(numberOfDimensions, baseDimensions.size());
        assertEquals(fraction, baseDimensions.get(baseDimension));
    }
}
