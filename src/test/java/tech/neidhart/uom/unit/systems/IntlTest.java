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
package tech.neidhart.uom.unit.systems;

import org.junit.jupiter.api.Test;
import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.quantity.Quantities;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link Intl} system of units.
 */
public class IntlTest {

    @Test
    public void lengthUnits() {
        assertConversion(Intl.YARD, SI.METRE, 1.0936132983,      0.9144);
        assertConversion(Intl.FOOT, SI.METRE, 3.280839895,       0.3048);
        assertConversion(Intl.INCH, SI.METRE, 39.37007874,       0.0254);
        assertConversion(Intl.THOU, SI.METRE, 39370.07874015748, 0.0000254);

        assertConversion(Intl.CHAIN,   SI.METRE, 0.0497096954, 20.1168);
        assertConversion(Intl.FURLONG, SI.METRE, 0.0049709695, 201.168);
        assertConversion(Intl.MILE,    SI.METRE, 0.0006213712, 1609.344);
    }

    @Test
    public void areaUnits() {
        assertConversion(Intl.SQUARE_INCH,  SI.SQUARE_METER, 1550.0031000062,       6.4516E-4);
        assertConversion(Intl.SQUARE_FOOT,  SI.SQUARE_METER, 10.763910416709722,    0.09290304);
        assertConversion(Intl.SQUARE_YARD,  SI.SQUARE_METER, 1.1959900463010802,    0.83612736);
        assertConversion(Intl.SQUARE_CHAIN, SI.SQUARE_METER, 0.0024710538146716535, 404.68564224);
        assertConversion(Intl.SQUARE_MILE,  SI.SQUARE_METER, 3.8610215854244587E-7, 2589988.110336);
        assertConversion(Intl.ACRE,         SI.SQUARE_METER, 2.471053814671653E-4,  4046.8564224);
    }

    @Test
    public void weightUnits() {
        assertConversion(Intl.POUND, SI.KILOGRAM, 2.2046226218487757, 0.45359237);
        assertConversion(Intl.OUNCE, SI.KILOGRAM, 35.27396194958041, 0.028349523125);
        assertConversion(Intl.GRAIN, SI.KILOGRAM, 15432.35835294143, 6.479891e-5);
    }

    private <Q extends Quantity<Q>> void assertConversion(Unit<Q> testUnit,
                                                          Unit<Q> systemUnit,
                                                          double  expectedValueOfTestUnit,
                                                          double  expectedValueOfSystemUnit) {

        Q quantity = Quantities.create(1, systemUnit);
        assertEquals(expectedValueOfTestUnit, quantity.to(testUnit).doubleValue(), 1e-8);

        Q reverseQuantity = Quantities.create(1, testUnit);
        assertEquals(expectedValueOfSystemUnit, reverseQuantity.to(systemUnit).doubleValue(), 1e-8);
    }
}
