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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.math.ArithmeticUtils;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link RootConverter} class.
 */
public class RootConverterTest {

    @Test
    public void unsupportedRoot() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // constructor is hidden, thus this should never happen.
            new RootConverter(UnitConverters.identity(), 5);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // constructor is hidden, thus this should never happen.
            new RootConverter(UnitConverters.identity(), -2);
        });
    }

    @Test
    public void unsupportedComposition() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // constructor is hidden, thus this should never happen.
            new RootConverter(new AddConverter(100), 2);
        });
    }

    @Test
    public void factoryMethod() {
        // ensure that the UnitConverters class returns a
        // constant converter when supplying an n of 0.
        UnitConverter converter = UnitConverters.root(new AddConverter(100), 0);

        assertEquals(1, converter.convert(100));
    }

    @Test
    public void getter() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(10, 1);
        RootConverter     converter         = new RootConverter(multiplyConverter, 2);

        assertEquals(2, converter.getN());
        assertEquals(multiplyConverter, converter.getUnitConverter());

        assertTrue(converter.isLinear());
    }

    @Test
    public void convert() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(1000, 1);
        RootConverter     converter         = new RootConverter(multiplyConverter, 2);

        assertEquals(Math.sqrt(1000), converter.convert(1), 1e-6);

        assertEquals(ArithmeticUtils.sqrt(BigDecimal.valueOf(1000), MathContext.DECIMAL128).doubleValue(),
                     converter.convert(BigDecimal.ONE).doubleValue(), 1e-6);
    }

    @Test
    public void negate() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(1000, 1);
        RootConverter     converter         = new RootConverter(multiplyConverter, 2);

        assertEquals(multiplyConverter, converter.getUnitConverter());

        converter = converter.inverse();

        // Ensure that the delegate converter got inverted.
        assertEquals(Math.sqrt(1. / 1000.), converter.convert(1), 1e-6);
        assertEquals(multiplyConverter.inverse(), converter.getUnitConverter());
    }

    @Test
    public void andThen() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(1000, 1);
        RootConverter     converter         = new RootConverter(multiplyConverter, 2);

        UnitConverter unitConverter = converter.andThen(new AddConverter(10));

        // x = (x * sqrt(1000 / 1)) + 10
        assertEquals(Math.sqrt(1000) * 1 + 10, unitConverter.convert(1), 1e-6);
    }
}