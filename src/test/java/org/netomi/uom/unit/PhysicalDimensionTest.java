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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PhysicalDimension} class.
 */
public class PhysicalDimensionTest {

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
        assertThrows(IllegalArgumentException.class, () -> Dimensions.LENGTH.root(-2));

        assertThrows(IllegalArgumentException.class, () -> Dimensions.LENGTH.root(0));
    }

    @Test
    public void generalDimensions() {
        Dimension length = Dimensions.LENGTH;
        Dimension money  = Dimensions.ofName("MONEY");

        Dimension combined = length.multiply(money);

        assertTrue(combined instanceof ProductDimension);
        assertEquals("L,MONEY", combined.toString());

        assertSame(length, combined.divide(money));
        assertSame(Dimensions.NONE, combined.divide(money).divide(length));

        assertEquals(money, combined.divide(length));
    }

    @Test
    public void dimensionCache() {
        Dimension none   = Dimensions.NONE;
        Dimension length = Dimensions.LENGTH;
        Dimension time   = Dimensions.TIME;

        assertSame(time, length.multiply(time).divide(length));
        assertSame(none, length.divide(length));
        assertSame(length.multiply(time), length.multiply(time));
    }

    @Test
    public void cacheCleanup() throws InterruptedException {
        Dimension length = Dimensions.LENGTH;
        Dimension time   = Dimensions.TIME;

        // This test checks whether any dimension that does not have
        // a strong reference gets remove from the dimension cache.
        Dimension dimension = length.multiply(time);
        // remember the memory location of the object.
        int identityHashCode = System.identityHashCode(dimension);

        assertSame(dimension, length.multiply(time));
        assertEquals(identityHashCode, System.identityHashCode(length.multiply(time)));

        // remove the strong reference.
        dimension = null;

        // let the GC do its work.
        System.gc();
        TimeUnit.SECONDS.sleep(3);
        System.gc();

        // the same dimension should have a different memory location, it should not
        // be retrieved from the dimension cache.
        assertNotEquals(identityHashCode, System.identityHashCode(length.multiply(time)));
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(Dimensions.LENGTH, Dimensions.LENGTH)
                .addEqualityGroup(Dimensions.TIME)
                .addEqualityGroup(Dimensions.NONE)
                .addEqualityGroup(Dimensions.ofName("MONEY"))
                .addEqualityGroup(Dimensions.TIME.pow(2))
                .addEqualityGroup(Dimensions.LENGTH.multiply(Dimensions.LENGTH), Dimensions.LENGTH.pow(2), Dimensions.LENGTH.pow(4).root(2))
                .addEqualityGroup(Dimensions.LENGTH.multiply(Dimensions.TIME).multiply(Dimensions.MASS.pow(5).root(2)),
                                  Dimensions.LENGTH.divide(Dimensions.TIME).multiply(Dimensions.MASS.root(2)).divide(Dimensions.MASS.pow(-2)).multiply(Dimensions.TIME.pow(2)))
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
