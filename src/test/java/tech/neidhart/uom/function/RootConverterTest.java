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
package tech.neidhart.uom.function;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.math.ArithmeticUtils;
import tech.neidhart.uom.math.BigFraction;

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

        assertEquals(ArithmeticUtils.root(2, BigDecimal.valueOf(1000), MathContext.DECIMAL128).doubleValue(),
                     converter.convert(BigDecimal.ONE).doubleValue(), 1e-6);
    }

    @Test
    public void convertCubicRoot() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(1000, 1);
        RootConverter     converter         = new RootConverter(multiplyConverter, 3);

        assertEquals(Math.pow(1000, 1. / 3.), converter.convert(1), 1e-6);

        assertEquals(ArithmeticUtils.root(3, BigDecimal.valueOf(1000), MathContext.DECIMAL128).doubleValue(),
                     converter.convert(BigDecimal.ONE).doubleValue(), 1e-6);
    }

    @Test
    public void scale() {
        MultiplyConverter multiplyConverter = new MultiplyConverter(1000, 1);
        RootConverter converter = new RootConverter(multiplyConverter, 2);

        assertEquals(Math.sqrt(1000), converter.scale(), 1e-6);
        assertEquals(BigFraction.from(Math.sqrt(1000)).doubleValue(), converter.scaleAsFraction().doubleValue(), 1e-6);

        RootConverter inverse = converter.inverse();

        assertEquals(1. / Math.sqrt(1000), inverse.scale(), 1e-6);
        assertEquals(BigFraction.from(1. / Math.sqrt(1000)).doubleValue(), inverse.scaleAsFraction().doubleValue(), 1e-6);
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

    @Test
    public void equality() {
        new EqualsTester()
                .addEqualityGroup(new RootConverter(new MultiplyConverter(10), 2), new RootConverter(new MultiplyConverter(10), 2))
                .addEqualityGroup(new RootConverter(new MultiplyConverter(20), 2), new RootConverter(new MultiplyConverter(5).andThen(new MultiplyConverter(4)), 2))
                .addEqualityGroup(new RootConverter(new PowConverter(new MultiplyConverter(5), 2), 2))
                .addEqualityGroup(new MultiplyConverter(4))
                .addEqualityGroup("blabla")
                .testEquals();
    }
}
