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
import org.netomi.uom.function.UnitConverters;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link TransformedUnit} class.
 */
public class TransformedUnitTest {

    @Test
    public void shiftedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m + 100
        Unit<?> unit = TransformedUnit.of(parentUnit, UnitConverters.shift(100));

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(100).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m + 200.23
        unit = from(parentUnit).shiftedBy(BigDecimal.valueOf(200.23)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.shift(BigDecimal.valueOf(200.23)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }

    @Test
    public void multipliedBy() {
        Unit<?> parentUnit = Units.SI.METRE;

        // m * 2.5
        Unit<?> unit = TransformedUnit.of(parentUnit, UnitConverters.multiply(2.5));

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(2.5).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 2.5
        unit = from(parentUnit).multipliedBy(BigDecimal.valueOf(2.5)).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(BigDecimal.valueOf(2.5)).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));

        // m * 5/9
        unit = from(parentUnit).multipliedBy(5, 9).build();

        assertEquals(parentUnit.getSymbol(), unit.getSymbol());
        assertEquals(parentUnit.getName(), unit.getName());
        assertEquals(UnitConverters.multiply(5, 9).andThen(parentUnit.getConverterToAny(Units.Imperial.YARD)),
                     unit.getConverterToAny(Units.Imperial.YARD));
    }
}
