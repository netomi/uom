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
 * Unit tests for the {@link PowConverter} class.
 */
public class PowConverterTest {

    @Test
    public void identity() {
        // constructor is hidden, thus this should never happen.
        UnitConverter converter1 = new PowConverter(UnitConverters.identity(), 1);
        assertEquals(1.0, converter1.convert(1.0), 1e-6);

        // ensure that the UnitConverters class returns an
        // identity converter when supplying an identity converter.
        UnitConverter converter2 = UnitConverters.pow(UnitConverters.identity(), 2);
        assertEquals(UnitConverters.identity(), converter2);

        // A pow converter with exponent = 1 can be reduced.
        UnitConverter addConverter = UnitConverters.shift(100);
        UnitConverter converter3 = UnitConverters.pow(addConverter, 1);
        assertEquals(addConverter, converter3);
    }

    @Test
    public void getter() {
        PowConverter converter = new PowConverter(UnitConverters.identity(), 1);

        assertEquals(1, converter.getExponent());
        assertEquals(UnitConverters.identity(), converter.getUnitConverter());

        converter = new PowConverter(UnitConverters.identity(), 100);

        assertEquals(100, converter.getExponent());
        assertEquals(UnitConverters.identity(), converter.getUnitConverter());
    }

    @Test
    public void negate() {
        AddConverter addConverter = new AddConverter(10.0);

        PowConverter converter = new PowConverter(addConverter, 1);

        assertEquals(addConverter, converter.getUnitConverter());

        converter = converter.inverse();

        // Ensure that the delegate converter got inverted.
        assertEquals(-10.0, converter.convert(0), 1e-6);
    }

    @Test
    public void andThen() {
        AddConverter addConverter = new AddConverter(10.0);

        PowConverter converter = new PowConverter(addConverter, 2);

        UnitConverter unitConverter = converter.andThen(new AddConverter(20.0));

        // x = 2 * 10 + 20
        assertEquals(40.0, unitConverter.convert(0), 1e-6);
    }
}
