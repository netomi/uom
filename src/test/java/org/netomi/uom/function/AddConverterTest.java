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

import org.junit.jupiter.api.Test;
import org.netomi.uom.UnitConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@code AddConverter} class.
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
        assertEquals(UnitConverters.identity(), converter2);
    }

    @Test
    public void getter() {
        AddConverter converter = new AddConverter(10.0);

        assertEquals(10.0, converter.getOffset().doubleValue(), 1e-6);
        assertTrue(BigDecimal.TEN.compareTo(converter.getOffset()) == 0);

        converter = new AddConverter(100.0);

        assertEquals(100.0, converter.getOffset().doubleValue(), 1e-6);
        assertTrue(new BigDecimal("100.0").compareTo(converter.getOffset()) == 0);
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

        assertTrue(concatenate == UnitConverters.identity());
        assertEquals(1.0, concatenate.convert(1.0), 1e-6);

        AddConverter left  = new AddConverter(10.0);
        AddConverter right = new AddConverter(20.0);
        concatenate = left.andThen(right);

        double val1 = right.convert(left.convert(1.0));
        double val2 = concatenate.convert(1.0);

        assertEquals(val1, val2, 1e-6);
    }
}
