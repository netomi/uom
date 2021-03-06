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
package com.github.netomi.uom.function;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.netomi.uom.UnitConverter;
import com.github.netomi.uom.math.BigFraction;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link MultiplyConverter} class.
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

    @Test
    public void invalidMultiplier() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MultiplyConverter(0.0));
    }

    @Test
    public void getter() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        assertEquals(10.0, converter.getMultiplier().doubleValue(), 1e-6);
        assertEquals(0, BigFraction.of(10).compareTo(converter.getMultiplier()));

        converter = new MultiplyConverter(100.0);

        assertEquals(100.0, converter.getMultiplier().doubleValue(), 1e-6);
        assertEquals(0, BigFraction.of(100).compareTo(converter.getMultiplier()));

        assertTrue(converter.isLinear());
    }

    @Test
    public void convert() {
        MultiplyConverter converter = new MultiplyConverter(10, 1);

        assertEquals(200.0, converter.convert(20), 1e-6);
        assertEquals(BigDecimal.valueOf(20).multiply(converter.getMultiplier().bigDecimalValue()).doubleValue(),
                     converter.convert(BigDecimal.valueOf(20)).doubleValue(), 1e-6);
    }

    @Test
    public void scale() {
        MultiplyConverter converter = new MultiplyConverter(100, 1);
        assertEquals(100., converter.scale(), 1e-6);
        assertEquals(BigFraction.of(100, 1), converter.scaleAsFraction());

        MultiplyConverter inverse = converter.inverse();
        assertEquals(1e-2, inverse.scale(), 1e-6);
        assertEquals(BigFraction.of(1, 100), inverse.scaleAsFraction());
    }

    @Test
    public void negate() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        assertEquals(10.0, converter.getMultiplier().doubleValue(), 1e-6);

        converter = converter.inverse();

        assertEquals(1.0 / 10.0, converter.getMultiplier().doubleValue(), 1e-6);
        assertEquals(BigFraction.of(1, 10).doubleValue(), converter.getMultiplier().doubleValue(), 1e-6);
        assertEquals(1.0, converter.convert(10.0), 1e-6);
    }

    @Test
    public void andThen() {
        MultiplyConverter converter = new MultiplyConverter(10.0);

        UnitConverter unitConverter = converter.andThen(new MultiplyConverter(2.0));

        assertTrue(unitConverter instanceof MultiplyConverter);
        assertEquals(20.0, ((MultiplyConverter) unitConverter).getMultiplier().doubleValue(), 1e-6);

        MultiplyConverter first = new MultiplyConverter(10.0);
        UnitConverter concatenate = first.andThen(new MultiplyConverter(1, 10));

        assertSame(concatenate, UnitConverters.identity());
        assertEquals(1.0, concatenate.convert(1.0), 1e-6);

        MultiplyConverter left  = new MultiplyConverter(10.0);
        MultiplyConverter right = new MultiplyConverter(2.0);
        concatenate = left.andThen(right);

        double val1 = right.convert(left.convert(1.0));
        double val2 = concatenate.convert(1.0);

        assertEquals(val1, val2, 1e-6);
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(new MultiplyConverter(2.0), new MultiplyConverter(2, 1), new MultiplyConverter(4.0).andThen(new MultiplyConverter(0.5)))
                .addEqualityGroup(new MultiplyConverter(5.0), new MultiplyConverter(2.5).andThen(new MultiplyConverter(2.0)))
                .addEqualityGroup(new MultiplyConverter(4))
                .addEqualityGroup(new AddConverter(100))
                .addEqualityGroup("blabla")
                .testEquals();
    }
}
