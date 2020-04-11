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
import org.netomi.uom.Unit;
import org.netomi.uom.unit.systems.Imperial;
import org.netomi.uom.unit.systems.SI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link NamedUnit} class.
 */
public class NamedUnitTest {

    @Test
    public void symbol() {
        Unit<?> parentUnit = SI.METRE;

        Unit<?> unit = NamedUnit.withSymbol(parentUnit, "sym");

        assertEquals("sym", unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(parentUnit.getConverterToAny(Imperial.YARD), unit.getConverterToAny(Imperial.YARD));
    }

    @Test
    public void name() {
        Unit<?> parentUnit = SI.METRE;

        Unit<?> unit = NamedUnit.withName(parentUnit, "MY METER");

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals("MY METER", unit.getName());
        assertEquals(parentUnit.getConverterToAny(Imperial.YARD), unit.getConverterToAny(Imperial.YARD));
    }
}
