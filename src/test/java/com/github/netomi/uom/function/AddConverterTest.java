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

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AddConverter} class.
 */
public class AddConverterTest {

    @Test
    public void identity() {
        // constructor is hidden, thus this should never happen.
        UnitConverter converter1 = new AddConverter(0.0);
        assertEquals(1.0, converter1.convert(1.0), 1e-6);

        // ensure that the UnitConverters class returns an
        // identity converter when supplying a zero shift.
        UnitConverter converter2 = UnitConverters.shift(0.0);
        Assertions.assertEquals(UnitConverters.identity(), converter2);
    }

    @Test
    public void getter() {
        AddConverter converter = new AddConverter(10.0);

        assertEquals(10.0, converter.getOffset().doubleValue(), 1e-6);
        assertEquals(0, BigDecimal.TEN.compareTo(converter.getOffset()));

        converter = new AddConverter(100.0);

        assertEquals(100.0, converter.getOffset().doubleValue(), 1e-6);
        assertEquals(0, new BigDecimal("100.0").compareTo(converter.getOffset()));

        assertFalse(converter.isLinear());
    }

    @Test
    public void convert() {
        AddConverter converter = new AddConverter(100);

        assertEquals(200.0, converter.convert(100.0), 1e-6);
        assertEquals(BigDecimal.ONE.add(converter.getOffset()).doubleValue(), converter.convert(BigDecimal.ONE).doubleValue(), 1e-6);
    }

    @Test
    public void scale() {
        AddConverter converter = new AddConverter(100);

        assertThrows(UnsupportedOperationException.class, () -> {
           converter.scale();
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            converter.scaleAsFraction();
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            converter.scale(MathContext.DECIMAL128);
        });

        AddConverter inverse = converter.inverse();

        assertThrows(UnsupportedOperationException.class, () -> {
            inverse.scale();
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            inverse.scaleAsFraction();
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            inverse.scale(MathContext.DECIMAL128);
        });
    }

    @Test
    public void negate() {
        AddConverter converter = new AddConverter(10.0);

        assertEquals(10.0, converter.getOffset().doubleValue(), 1e-6);

        converter = converter.inverse();

        assertEquals(-10.0, converter.getOffset().doubleValue(), 1e-6);
        assertEquals(-10.0, converter.convert(0), 1e-6);
    }

    @Test
    public void andThen() {
        AddConverter converter = new AddConverter(10.0);

        UnitConverter unitConverter = converter.andThen(new AddConverter(20.0));

        assertTrue(unitConverter instanceof AddConverter);
        assertEquals(30.0, ((AddConverter) unitConverter).getOffset().doubleValue(), 1e-6);

        AddConverter first = new AddConverter(10.0);
        UnitConverter concatenate = first.andThen(new AddConverter(-10.0));

        assertSame(concatenate, UnitConverters.identity());
        assertEquals(1.0, concatenate.convert(1.0), 1e-6);

        AddConverter left  = new AddConverter(10.0);
        AddConverter right = new AddConverter(20.0);
        concatenate = left.andThen(right);

        double val1 = right.convert(left.convert(1.0));
        double val2 = concatenate.convert(1.0);

        assertEquals(val1, val2, 1e-6);
    }

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(new AddConverter(100.0), new AddConverter(50).andThen(new AddConverter(50)))
                .addEqualityGroup(new AddConverter(200.0), new AddConverter(400).andThen(new AddConverter(-200)))
                .addEqualityGroup(new AddConverter(-0.1))
                .addEqualityGroup(new MultiplyConverter(2))
                .addEqualityGroup("blabla")
                .testEquals();
    }
}
