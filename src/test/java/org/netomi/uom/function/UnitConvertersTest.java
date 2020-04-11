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

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link UnitConverters} class.
 */
public class UnitConvertersTest {

    @Test
    public void identity() {
        UnitConverter identity = UnitConverters.identity();

        // always the same instance is returned
        assertSame(UnitConverters.identity(), identity);

        assertEquals(5, identity.convert(5), 1e-12);
        BigDecimal value = BigDecimal.valueOf(5);
        assertSame(value, identity.convert(value));
        assertSame(value, identity.convert(value, MathContext.DECIMAL128));

        assertEquals(BigFraction.ONE, identity.scale().get());
    }

    @Test
    public void shift() {
        // double parameter
        UnitConverter shift = UnitConverters.shift(100);

        assertEquals(110, shift.convert(10), 1e-12);
        assertEquals(90, shift.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(110).doubleValue(), shift.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(90).doubleValue(), shift.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // BigDecimal parameter
        shift = UnitConverters.shift(BigDecimal.valueOf(100));

        assertEquals(110, shift.convert(10), 1e-12);
        assertEquals(90, shift.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(110).doubleValue(), shift.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(90).doubleValue(), shift.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);
    }

    @Test
    public void multiply() {
        // double parameter
        UnitConverter multiply = UnitConverters.multiply(2.5);

        assertEquals(25, multiply.convert(10), 1e-12);
        assertEquals(-25, multiply.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(25).doubleValue(), multiply.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(-25).doubleValue(), multiply.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // BigDecimal parameter
        multiply = UnitConverters.multiply(BigDecimal.valueOf(2.5));

        assertEquals(25, multiply.convert(10), 1e-12);
        assertEquals(-25, multiply.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(25).doubleValue(), multiply.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(-25).doubleValue(), multiply.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // fraction as long parameter
        multiply = UnitConverters.multiply(5, 2);

        assertEquals(25, multiply.convert(10), 1e-12);
        assertEquals(-25, multiply.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(25).doubleValue(), multiply.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(-25).doubleValue(), multiply.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // fraction as BigInteger parameter
        multiply = UnitConverters.multiply(BigInteger.valueOf(5), BigInteger.valueOf(2));

        assertEquals(25, multiply.convert(10), 1e-12);
        assertEquals(-25, multiply.convert(-10), 1e-12);

        assertEquals(BigDecimal.valueOf(25).doubleValue(), multiply.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(BigDecimal.valueOf(-25).doubleValue(), multiply.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);
    }

    @Test
    public void pow() {
        // I^x = I
        assertSame(UnitConverters.identity(), UnitConverters.pow(UnitConverters.identity(), 2));

        UnitConverter multiplyConverter = new MultiplyConverter(2);
        assertSame(multiplyConverter, UnitConverters.pow(multiplyConverter, 1));

        // C^0 = I
        assertSame(UnitConverters.identity(), UnitConverters.pow(multiplyConverter, 0));

        // positive exponent
        UnitConverter pow = UnitConverters.pow(multiplyConverter, 2);

        assertEquals(multiplyConverter.convert(multiplyConverter.convert(10)), pow.convert(10), 1e-12);
        assertEquals(multiplyConverter.convert(multiplyConverter.convert(-10)), pow.convert(-10), 1e-12);

        assertEquals(multiplyConverter.convert(multiplyConverter.convert(BigDecimal.TEN)).doubleValue(), pow.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(multiplyConverter.convert(multiplyConverter.convert(BigDecimal.TEN.negate())).doubleValue(), pow.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // negative exponent
        pow = UnitConverters.pow(multiplyConverter, -2);

        UnitConverter inverseMultiply = multiplyConverter.inverse();
        assertEquals(inverseMultiply.convert(inverseMultiply.convert(10)), pow.convert(10), 1e-12);
        assertEquals(inverseMultiply.convert(inverseMultiply.convert(-10)), pow.convert(-10), 1e-12);

        assertEquals(inverseMultiply.convert(inverseMultiply.convert(BigDecimal.TEN)).doubleValue(), pow.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(inverseMultiply.convert(inverseMultiply.convert(BigDecimal.TEN.negate())).doubleValue(), pow.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);
    }

    @Test
    public void root() {
        // I^1/x = I
        assertSame(UnitConverters.identity(), UnitConverters.root(UnitConverters.identity(), 2));

        MultiplyConverter multiplyConverter = new MultiplyConverter(2);
        assertSame(multiplyConverter, UnitConverters.root(multiplyConverter, 1));

        // positive exponent
        UnitConverter root = UnitConverters.root(multiplyConverter, 2);

        double rootMultiplier = Math.sqrt(multiplyConverter.getMultiplier().doubleValue());
        assertEquals(rootMultiplier * 10, root.convert(10), 1e-12);
        assertEquals(rootMultiplier * -10, root.convert(-10), 1e-12);

        assertEquals(rootMultiplier * 10, root.convert(BigDecimal.TEN).doubleValue(), 1e-12);
        assertEquals(rootMultiplier * -10, root.convert(BigDecimal.TEN.negate()).doubleValue(), 1e-12);

        // non-positive exponent
        assertThrows(IllegalArgumentException.class, () -> UnitConverters.root(multiplyConverter, 0));

        assertThrows(IllegalArgumentException.class, () -> UnitConverters.root(multiplyConverter, -2));

        // exponent > 2
        assertThrows(IllegalArgumentException.class, () -> UnitConverters.root(multiplyConverter, 3));
    }

    @Test
    public void compose() {
        UnitConverter m1 = new MultiplyConverter(2);
        UnitConverter m2 = new MultiplyConverter(3);
        UnitConverter m3 = new MultiplyConverter(4);
        UnitConverter a1 = new AddConverter(100);

        assertTrue(UnitConverters.compose(m1, m2).isLinear());
        assertFalse(UnitConverters.compose(m1, m2).isIdentity());
        assertFalse(UnitConverters.compose(m1, a1).isLinear());

        assertSame(m1, UnitConverters.compose(UnitConverters.identity(), m1));
        assertSame(m1, UnitConverters.compose(m1, UnitConverters.identity()));

        assertEquals(m2.convert(m1.convert(10)), UnitConverters.compose(m1, m2).convert(10), 1e-6);
        assertEquals(m1.convert(a1.convert(10)), UnitConverters.compose(a1, m1).convert(10), 1e-6);
        assertNotEquals(a1.convert(m1.convert(10)), UnitConverters.compose(a1, m1).convert(10), 1e-6);
        assertEquals(a1.convert(m1.convert(10)), UnitConverters.compose(m1, a1).convert(10), 1e-6);

        UnitConverter m12 = UnitConverters.compose(m1, m2);
        assertEquals(m3.convert(m2.convert(m1.convert(10))), UnitConverters.compose(m12, m3).convert(10), 1e-6);
    }

    @Test
    public void equality() {
        // all unit converters shall be different.
        new EqualsTester()
                .addEqualityGroup(UnitConverters.identity())
                .addEqualityGroup(UnitConverters.shift(100))
                .addEqualityGroup(UnitConverters.multiply(2))
                .addEqualityGroup(UnitConverters.pow(10, 6))
                .addEqualityGroup(UnitConverters.pow(UnitConverters.multiply(5), 2))
                .addEqualityGroup(UnitConverters.root(UnitConverters.multiply(5), 2))
                .addEqualityGroup(UnitConverters.compose(UnitConverters.shift(100), UnitConverters.multiply(2)))
                .testEquals();
    }
}
