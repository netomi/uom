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

import org.junit.jupiter.api.Test;
import org.netomi.uom.Prefix;
import org.netomi.uom.Unit;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Precision;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for the {@link Prefixes.Binary} class.
 */
public class BinaryPrefixTest {

    @Test
    public void factoryMethods() {
        checkFactoryMethod(Prefixes.Binary::KIBI, Prefixes.Binary.KIBI, Math.pow(1024, 1));
        checkFactoryMethod(Prefixes.Binary::MEBI, Prefixes.Binary.MEBI, Math.pow(1024, 2));
        checkFactoryMethod(Prefixes.Binary::GIBI, Prefixes.Binary.GIBI, Math.pow(1024, 3));
        checkFactoryMethod(Prefixes.Binary::TEBI, Prefixes.Binary.TEBI, Math.pow(1024, 4));
        checkFactoryMethod(Prefixes.Binary::PEBI, Prefixes.Binary.PEBI, Math.pow(1024, 5));
        checkFactoryMethod(Prefixes.Binary::EXBI, Prefixes.Binary.EXBI, Math.pow(1024, 6));
        checkFactoryMethod(Prefixes.Binary::ZEBI, Prefixes.Binary.ZEBI, Math.pow(1024, 7));
        checkFactoryMethod(Prefixes.Binary::YOBI, Prefixes.Binary.YOBI, Math.pow(1024, 8));
    }

    @Test
    public void withExponent() {
        assertSame(Prefixes.Binary.GIBI, Prefixes.Binary.KIBI.withExponent(3));

        assertEquals(1024,    Prefixes.Binary.KIBI.withExponent(-1).getBase());
        assertEquals(-1,      Prefixes.Binary.KIBI.withExponent(-1).getExponent());
        assertEquals("1024⁻¹", Prefixes.Binary.KIBI.withExponent(-1).getSymbol());

        assertEquals(UnitConverters.pow(1024, -1), Prefixes.Binary.KIBI.withExponent(-1).getUnitConverter());
        assertSame(Prefixes.Binary.GIBI, Prefixes.Binary.KIBI.withExponent(-1).withExponent(3));
    }

    private static void checkFactoryMethod(UnaryOperator<Unit<?>> factoryMethod, Prefix prefix, double multiplier) {
        Unit<?> unit = Units.SI.METRE;

        Unit<?> prefixedUnit = factoryMethod.apply(unit);

        assertEquals(prefix.getSymbol() + unit.getSymbol(), prefixedUnit.getSymbol());
        assertEquals(prefix.getName() + unit.getName(), prefixedUnit.getName());
        assertEquals(UnitConverters.pow(prefix.getBase(), prefix.getExponent()), prefixedUnit.getSystemConverter());

        // double precision
        assertTrue(Precision.equals(42. * multiplier, prefixedUnit.getSystemConverter().convert(42.), 1e-12));
        assertTrue(Precision.equals(42. / multiplier, prefixedUnit.getSystemConverter().inverse().convert(42.), 1e-12));

        // DECIMAL128 precision
        MathContext context = MathContext.DECIMAL128;
        assertTrue(Precision.equals(BigDecimal.valueOf(42).multiply(BigDecimal.valueOf(multiplier), context).doubleValue(),
                                    prefixedUnit.getSystemConverter().convert(BigDecimal.valueOf(42), context).doubleValue(), 1e-24));
        assertTrue(Precision.equals(BigDecimal.valueOf(42).divide(BigDecimal.valueOf(multiplier), context).doubleValue(),
                                    prefixedUnit.getSystemConverter().inverse().convert(BigDecimal.valueOf(42), context).doubleValue(), 1e-24));
    }
}
