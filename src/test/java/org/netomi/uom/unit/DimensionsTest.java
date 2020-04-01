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
import org.netomi.uom.math.Fraction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Dimensions} class and its inner classes.
 */
public class DimensionTest {

    @Test
    public void noneDimension() {
        Dimension none = Dimensions.NONE;

        assertEquals("", none.toString());
        assertTrue(none.getBaseDimensions().isEmpty());

        // NONE * something = something
        assertSame(Dimensions.LENGTH, none.multiply(Dimensions.LENGTH));

        assertSame(Dimensions.NONE, Dimensions.LENGTH.divide(Dimensions.LENGTH));

        assertSame(Dimensions.NONE, none.pow(2));
        assertSame(Dimensions.NONE, none.root(2));
    }

    @Test
    public void oneBaseDimension() {
        Dimension length = Dimensions.LENGTH;

        assertBaseDimensions(length.multiply(length), 1, length, Fraction.of(2));
        assertSame(Dimensions.NONE, length.divide(length));
        assertBaseDimensions(length.pow(3), 1, length, Fraction.of(3));
        assertBaseDimensions(length.pow(4).root(2), 1, length, Fraction.of(2));
        assertBaseDimensions(length.root(2), 1, length, Fraction.of(1, 2));
    }

    @Test
    public void multipleBaseDimensions() {
        Dimension length = Dimensions.LENGTH;
        Dimension time   = Dimensions.TIME;
        Dimension mass   = Dimensions.MASS;

        // L^2
        assertBaseDimensions(length.multiply(time), 2, length, Fraction.of(1));
        assertBaseDimensions(length.multiply(time), 2, time, Fraction.of(1));

        // LT
        assertBaseDimensions(length.divide(time), 2, length, Fraction.of(1));
        assertBaseDimensions(length.divide(time), 2, time, Fraction.of(-1));

        // LT^-1
        assertBaseDimensions(length.pow(2).divide(time), 2, length, Fraction.of(2));
        assertBaseDimensions(length.pow(2).divide(time), 2, time, Fraction.of(-1));

        // LT
        assertBaseDimensions(length.multiply(mass).multiply(time).divide(mass), 2, length, Fraction.of(1));
        assertBaseDimensions(length.multiply(mass).multiply(time).divide(mass), 2, time, Fraction.of(1));

        // L^2T^-2
        assertBaseDimensions(length.multiply(time).pow(2).divide(time.pow(4)), 2, length, Fraction.of(2));
        assertBaseDimensions(length.multiply(time).pow(2).divide(time.pow(4)), 2, time, Fraction.of(-2));

        // L^2T^6
        assertBaseDimensions(length.multiply(time).pow(2).divide(time.pow(-4)), 2, length, Fraction.of(2));
        assertBaseDimensions(length.multiply(time).pow(2).divide(time.pow(-4)), 2, time, Fraction.of(6));

        // LT^1/2
        assertBaseDimensions(length.multiply(time.root(2)), 2, length, Fraction.of(1));
        assertBaseDimensions(length.multiply(time.root(2)), 2, time, Fraction.of(1, 2));

    }

    @Test
    public void rootWithNonPositiveInteger() {
        assertThrows(IllegalArgumentException.class, () -> {
           Dimensions.LENGTH.root(-2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Dimensions.LENGTH.root(0);
        });
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(Dimensions.LENGTH, Dimensions.LENGTH)
                .addEqualityGroup(Dimensions.TIME)
                .addEqualityGroup(Dimensions.NONE)
                .addEqualityGroup(Dimensions.TIME.pow(2))
                .addEqualityGroup(Dimensions.LENGTH.multiply(Dimensions.LENGTH), Dimensions.LENGTH.pow(2), Dimensions.LENGTH.pow(4).root(2))
                .addEqualityGroup("blabla")
                .testEquals();

        assertSame(Dimensions.NONE, Dimensions.LENGTH.divide(Dimensions.LENGTH));
        assertSame(Dimensions.TIME, Dimensions.LENGTH.multiply(Dimensions.TIME.divide(Dimensions.LENGTH)));

        assertSame(Dimensions.LENGTH, Dimensions.LENGTH.pow(2).root(2));
        assertSame(Dimensions.LENGTH, Dimensions.LENGTH.root(2).pow(2));
    }

    private static void assertBaseDimensions(Dimension d, int numberOfDimensions, Dimension baseDimension, Fraction fraction) {
        Map<Dimension, Fraction> baseDimensions = d.getBaseDimensions();

        assertEquals(numberOfDimensions, baseDimensions.size());
        assertEquals(fraction, baseDimensions.get(baseDimension));
    }
}
