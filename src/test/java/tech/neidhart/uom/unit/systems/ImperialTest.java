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
 * Tests the {@link Imperial} system of units.
 */
public class ImperialTest {

    @Test
    public void lengthUnits() {
        assertConversion(Imperial.YARD, SI.METRE, 1.0936132983, 0.9144);
        assertConversion(Imperial.FOOT, SI.METRE, 3.280839895,  0.3048);
        assertConversion(Imperial.LINK, SI.METRE, 4.9709695379, 0.201168);
        assertConversion(Imperial.INCH, SI.METRE, 39.37007874,  0.0254);
        assertConversion(Imperial.THOU, SI.METRE, 39370.078740, 0.0000254);

        assertConversion(Imperial.CHAIN,   SI.METRE, 0.0497096954, 20.1168);
        assertConversion(Imperial.FURLONG, SI.METRE, 0.0049709695, 201.168);
        assertConversion(Imperial.MILE,    SI.METRE, 0.0006213712, 1609.344);
    }

    private <Q extends Quantity<Q>> void assertConversion(Unit<Q> testUnit,
                                                          Unit<Q> systemUnit,
                                                          double  expectedValueOfTestUnit,
                                                          double  expectedValueOfSystemUnit) {

        Q quantity = Quantities.create(1, systemUnit);
        assertEquals(expectedValueOfTestUnit, quantity.to(testUnit).doubleValue(), 1e-6);

        Q reverseQuantity = Quantities.create(1, testUnit);
        assertEquals(expectedValueOfSystemUnit, reverseQuantity.to(systemUnit).doubleValue(), 1e-6);
    }
}
