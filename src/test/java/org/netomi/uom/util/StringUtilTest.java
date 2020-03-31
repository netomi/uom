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
package org.netomi.uom.util;

import org.junit.jupiter.api.Test;
import org.netomi.uom.math.Fraction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link StringUtil} class.
 */
public class StringUtilTest {

    @Test
    public void fractionToString() {
        // 0
        assertEquals("⁰", StringUtil.toUnicodeString(Fraction.ZERO));
        // 1
        assertEquals("¹", StringUtil.toUnicodeString(Fraction.ONE));
        // -1
        assertEquals("⁻¹", StringUtil.toUnicodeString(Fraction.ONE.negate()));
        // 42
        assertEquals("⁴²", StringUtil.toUnicodeString(Fraction.of(42)));
        // 1/2
        assertEquals("¹⁄₂", StringUtil.toUnicodeString(Fraction.ONE.divide(Fraction.of(2))));
        // 23/45
        assertEquals("²³⁄₄₅", StringUtil.toUnicodeString(Fraction.of(23).divide(Fraction.of(45))));
    }
}
