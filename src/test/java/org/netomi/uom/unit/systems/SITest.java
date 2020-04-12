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
package org.netomi.uom.unit.systems;

import org.junit.jupiter.api.Test;
import org.netomi.uom.unit.Units;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link SI} system of units.
 */
public class SITest {

    @Test
    public void angularUnits() {
        assertTrue(SI.RADIAN.isCompatible(Units.ONE));
        assertTrue(SI.STERADIAN.isCompatible(Units.ONE));

        assertSame(SI.RADIAN, SI.RADIAN.getSystemUnit());
        assertSame(SI.STERADIAN, SI.STERADIAN.getSystemUnit());

        assertTrue(SI.RADIAN.isCompatible(SI.STERADIAN));
        assertTrue(SI.STERADIAN.isCompatible(SI.RADIAN));

        assertNotEquals(SI.RADIAN.getSystemUnit(), SI.STERADIAN.getSystemUnit());
        assertNotEquals(SI.STERADIAN.getSystemUnit(), SI.RADIAN.getSystemUnit());
    }

    @Test
    public void photometricUnits() {
        // Candela has the same dimension as Lumen.
        assertTrue(SI.CANDELA.isCompatible(SI.LUMEN));
        assertTrue(SI.LUMEN.isCompatible(SI.CANDELA));

        // but they have different system units.
        assertSame(SI.CANDELA, SI.CANDELA.getSystemUnit());
        assertSame(SI.LUMEN, SI.LUMEN.getSystemUnit());

        assertNotEquals(SI.CANDELA.getSystemUnit(), SI.LUMEN.getSystemUnit());
    }
}
