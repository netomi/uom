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
package com.github.netomi.uom.util;

import org.junit.jupiter.api.Test;
import com.github.netomi.uom.math.Fraction;
import com.github.netomi.uom.unit.Dimensions;
import com.github.netomi.uom.unit.systems.SI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link ObjectPrinter} class.
 */
public class ObjectPrinterTest {

    @Test
    public void printFractionUnicode() {
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

    @Test
    public void printFractionAscii() {
        ObjectPrinter objectPrinter = ObjectPrinter.forAscii();

        // 0
        assertEquals("^0", objectPrinter.print(Fraction.ZERO));
        // 1
        assertEquals("^1", objectPrinter.print(Fraction.ONE));
        // -1
        assertEquals("^-1", objectPrinter.print(Fraction.ONE.negate()));
        // 42
        assertEquals("^42", objectPrinter.print(Fraction.of(42)));
        // 1/2
        assertEquals("^1/2", objectPrinter.print(Fraction.ONE.divide(Fraction.of(2))));
        // 23/45
        assertEquals("^23/45", objectPrinter.print(Fraction.of(23).divide(Fraction.of(45))));
    }

    @Test
    public void printFractionMapUnicode() {
        ObjectPrinter objectPrinter = ObjectPrinter.forUnicode();

        assertEquals("LT⁻¹",  objectPrinter.print(SI.METER_PER_SECOND.getDimension().getBaseDimensions(), Object::toString));
        assertEquals("LMT⁻²", objectPrinter.print(SI.NEWTON.getDimension().getBaseDimensions(), Object::toString));
        assertEquals("",      objectPrinter.print(Dimensions.NONE.getBaseDimensions(), Object::toString));
    }

    @Test
    public void printFractionMapAscii() {
        ObjectPrinter objectPrinter = ObjectPrinter.forAscii();

        assertEquals("LT^-1",  objectPrinter.print(SI.METER_PER_SECOND.getDimension().getBaseDimensions(), Object::toString));
        assertEquals("LMT^-2", objectPrinter.print(SI.NEWTON.getDimension().getBaseDimensions(), Object::toString));
        assertEquals("",       objectPrinter.print(Dimensions.NONE.getBaseDimensions(), Object::toString));
    }
}
