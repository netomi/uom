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
 * Unit tests for the {@link ObjectPrinter} class.
 */
public class ObjectPrinterTest {

    @Test
    public void printFraction() {

        ObjectPrinter objectPrinter = ObjectPrinter.forUnicode();

        // 0
        assertEquals("⁰", objectPrinter.print(Fraction.ZERO));
        // 1
        assertEquals("¹", objectPrinter.print(Fraction.ONE));
        // -1
        assertEquals("⁻¹", objectPrinter.print(Fraction.ONE.negate()));
        // 42
        assertEquals("⁴²", objectPrinter.print(Fraction.of(42)));
        // 1/2
        assertEquals("¹⁄₂", objectPrinter.print(Fraction.ONE.divide(Fraction.of(2))));
        // 23/45
        assertEquals("²³⁄₄₅", objectPrinter.print(Fraction.of(23).divide(Fraction.of(45))));
    }
}
