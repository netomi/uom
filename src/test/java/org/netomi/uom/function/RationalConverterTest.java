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
package org.netomi.uom.function;

import org.junit.Test;
import org.netomi.uom.UnitConverter;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@code RationalConverter} class.
 */
public class RationalConverterTest {

    @Test
    public void identity() {
        // constructor is hidden, thus this should never happen.
        UnitConverter converter1 = new RationalConverter(10, 10);
        assertEquals(1.0, converter1.convert(1.0), 1e-6);

        // ensure that the UnitConverters class returns an
        // identity converter when supplying a multiplier of 1.
        UnitConverter converter2 = UnitConverters.multiply(10, 10);
        assertEquals(UnitConverters.identity(), converter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRational() {
        UnitConverter converter = new RationalConverter(-10, 100);
        fail("expected IllegalArgumentException");
    }

    @Test
    public void getter() {
        RationalConverter converter = new RationalConverter(3, 412);

        assertEquals(3, converter.getDividend());
        assertEquals(412, converter.getDivisor());
    }

    @Test
    public void negate() {
        RationalConverter converter = new RationalConverter(1, 60);

        assertEquals(1, converter.getDividend());
        assertEquals(60, converter.getDivisor());

        assertEquals(1, converter.convert(60), 1e-6);

        converter = converter.inverse();

        assertEquals(60, converter.getDividend());
        assertEquals(1, converter.getDivisor());

        assertEquals(60, converter.convert(1), 1e-6);
    }

    @Test
    public void concatenate() {
        RationalConverter converter = new RationalConverter(3, 4);

        UnitConverter unitConverter = converter.concatenate(new RationalConverter(6, 8));

        assertTrue(unitConverter instanceof RationalConverter);
        assertEquals(9, ((RationalConverter) unitConverter).getDividend());
        assertEquals(16, ((RationalConverter) unitConverter).getDivisor());

        RationalConverter first = new RationalConverter(1, 60);
        UnitConverter concatenate = first.concatenate(new RationalConverter(60, 1));

        assertTrue(concatenate == UnitConverters.identity());
        assertEquals(1.0, concatenate.convert(1.0), 1e-6);

        RationalConverter left  = new RationalConverter(1, 60);
        RationalConverter right = new RationalConverter(1, 60);
        concatenate = left.concatenate(right);

        double val1 = right.convert(left.convert(1.0));
        double val2 = concatenate.convert(1.0);

        assertEquals(1. / 3600., val1, 1e-6);
        assertEquals(val1, val2, 1e-6);
    }
}
