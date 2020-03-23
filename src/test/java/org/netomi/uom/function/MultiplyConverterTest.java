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
 * Unit tests for the {@code MultiplyConverter} class.
 */
public class MultiplyConverterTest {

    @Test
    public void identity() {
        // constructor is hidden, thus this should never happen.
        UnitConverter converter1 = new MultiplyConverter(1.0);
        assertEquals(1.0, converter1.convert(1.0), 1e-6);

        // ensure that the UnitConverters class returns an
        // identity converter when supplying a multiplier of 1.
        UnitConverter converter2 = UnitConverters.multiply(1.0);
        assertEquals(UnitConverters.identity(), converter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMultiplier() {
        UnitConverter converter = new MultiplyConverter(0.0);
        fail("expected IllegalArgumentException");
    }

    @Test
    public void getter() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        assertEquals(10.0, converter.getMultiplier(), 1e-6);
        assertTrue(BigDecimal.TEN.compareTo(converter.getDecimalMultiplier()) == 0);

        converter = new MultiplyConverter(100.0);

        assertEquals(100.0, converter.getMultiplier(), 1e-6);
        assertTrue(new BigDecimal("100.0").compareTo(converter.getDecimalMultiplier()) == 0);
    }

    @Test
    public void negate() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        assertEquals(10.0, converter.getMultiplier(), 1e-6);

        converter = converter.inverse();

        assertEquals(1.0 / 10.0, converter.getMultiplier(), 1e-6);
        assertEquals(1.0, converter.convert(10.0), 1e-6);
    }

    @Test
    public void concatenate() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        UnitConverter unitConverter = converter.concatenate(new MultiplyConverter(2.0));

        assertTrue(unitConverter instanceof MultiplyConverter);
        assertEquals(20.0, ((MultiplyConverter) unitConverter).getMultiplier(), 1e-6);

        MultiplyConverter first = new MultiplyConverter(10.0);
        UnitConverter concatenate = first.concatenate(new MultiplyConverter(1.0 / 10.0));

        assertTrue(concatenate == UnitConverters.identity());
        assertEquals(1.0, concatenate.convert(1.0), 1e-6);

        MultiplyConverter left  = new MultiplyConverter(10.0);
        MultiplyConverter right = new MultiplyConverter(2.0);
        concatenate = left.concatenate(right);

        double val1 = right.convert(left.convert(1.0));
        double val2 = concatenate.convert(1.0);

        assertEquals(val1, val2, 1e-6);
    }
}
