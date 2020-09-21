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
package com.github.netomi.uom.unit.systems;

import org.junit.jupiter.api.Test;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.Quantities;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link US} system of units.
 */
public class USTest {

    @Test
    public void lengthUnits() {
        assertConversion(US.YARD, SI.METRE, 1.093611111111111,  0.9144018288036576);
        assertConversion(US.FOOT, SI.METRE, 3.2808333333333333, 0.3048006096);
        assertConversion(US.INCH, SI.METRE, 39.37,              0.0254000508);

        assertConversion(US.ROD,  SI.METRE, 0.19883838383838384, 5.029210058420117);
        assertConversion(US.LINK, SI.METRE, 4.970959595959596,   0.20116840233680466);

        assertConversion(US.CHAIN,   SI.METRE, 0.049709596,  20.116840233680467);
        assertConversion(US.FURLONG, SI.METRE, 0.0049709596, 201.16840233680466);
        assertConversion(US.MILE,    SI.METRE, 0.0006213699, 1609.3472186944373);
    }

    @Test
    public void areaUnits() {
        assertConversion(US.SQUARE_FOOT,  SI.SQUARE_METER, 10.763867361111112,    0.0929034116);
        assertConversion(US.SQUARE_ROD,   SI.SQUARE_METER, 0.039536702887460466,  25.292953811714074);
        assertConversion(US.SQUARE_CHAIN, SI.SQUARE_METER, 0.002471043930466279,  404.6872609874252);
        assertConversion(US.SQUARE_MILE,  SI.SQUARE_METER, 3.861006141353561E-7,  2589998.470319521);
        assertConversion(US.ACRE,         SI.SQUARE_METER, 2.471043930466279E-4,  4046.872609874252);
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
