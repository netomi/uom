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
import tech.neidhart.uom.Dimension;
import tech.neidhart.uom.math.Fraction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Dimensions} class and its inner classes.
 */
public class ProductDimensionTest {

    @Test
    public void noneDimension() {
        Dimension d = Dimensions.ofName("CUSTOM");

        Dimension product = d.multiply(Dimensions.TIME);

        assertTrue(product instanceof ProductDimension);
        assertSame(Dimensions.NONE, product.divide(d).divide(Dimensions.TIME));
    }

    @Test
    public void oneElement() {
        Dimension d      = Dimensions.ofName("CUSTOM");
        Dimension length = Dimensions.LENGTH;

        assertTrue(length.multiply(d.pow(2)).divide(length) instanceof ProductDimension);
        assertBaseDimensions(length.multiply(d.pow(2)).divide(length), 1, d, Fraction.of(2));

        assertBaseDimensions(d.pow(3), 1, d, Fraction.of(3));
        assertBaseDimensions(d.pow(4).root(2), 1, d, Fraction.of(2));
        assertBaseDimensions(d.root(2), 1, d, Fraction.of(1, 2));
    }

    @Test
    public void multipleElements() {
        Dimension d1     = Dimensions.ofName("CUSTOM1");
        Dimension d2     = Dimensions.ofName("CUSTOM2");

        Dimension length = Dimensions.LENGTH;
        Dimension time   = Dimensions.TIME;
        Dimension mass   = Dimensions.MASS;

        // CUSTOM1,CUSTOM2^2
        assertBaseDimensions(d1.multiply(d2.pow(2)), 2, d1, Fraction.of(1));
        assertBaseDimensions(d1.multiply(d2.pow(2)), 2, d2, Fraction.of(2));

        // LT^2,CUSTOM1,CUSTOM2^1/2
        assertBaseDimensions(Dimensions.LENGTH.multiply(d1).multiply(Dimensions.TIME.pow(2)).multiply(d2.root(2)), 4, d1, Fraction.of(1));
        assertBaseDimensions(Dimensions.LENGTH.multiply(d1).multiply(Dimensions.TIME.pow(2)).multiply(d2.root(2)), 4, Dimensions.LENGTH, Fraction.of(1));
        assertBaseDimensions(Dimensions.LENGTH.multiply(d1).multiply(Dimensions.TIME.pow(2)).multiply(d2.root(2)), 4, Dimensions.TIME, Fraction.of(2));
        assertBaseDimensions(Dimensions.LENGTH.multiply(d1).multiply(Dimensions.TIME.pow(2)).multiply(d2.root(2)), 4, d2, Fraction.of(1, 2));
    }

    @Test
    public void equality() {
        Dimension d1 = Dimensions.ofName("CUSTOM1");
        Dimension d2 = Dimensions.ofName("CUSTOM2");

        new EqualsTester()
                .addEqualityGroup(Dimensions.TIME.multiply(d1), d1.multiply(Dimensions.TIME), d1.pow(2).multiply(Dimensions.TIME).divide(d1))
                .addEqualityGroup(d1, Dimensions.ofName("CUSTOM1"))
                .addEqualityGroup(d2)
                .addEqualityGroup(d1.multiply(d2))
                .addEqualityGroup(Dimensions.NONE)
                .addEqualityGroup(Dimensions.TIME.pow(2))
                .addEqualityGroup("blabla")
                .testEquals();
    }

    private static void assertBaseDimensions(Dimension d, int numberOfDimensions, Dimension baseDimension, Fraction fraction) {
        Map<Dimension, Fraction> baseDimensions = d.getBaseDimensions();

        assertEquals(numberOfDimensions, baseDimensions.size());
        assertEquals(fraction, baseDimensions.get(baseDimension));
    }
}
