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
package org.netomi.uom.quantity;

import org.junit.jupiter.api.Test;
import org.netomi.uom.Unit;
import org.netomi.uom.unit.Units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Unit test for the {@link Length} quantity.
 */
public class LengthTest extends GenericQuantityTest<Length> {

    @Override
    protected Class<Length> getQuantityClass() {
        return Length.class;
    }

    @Override
    protected Unit<Length> getSystemUnit() {
        return Units.SI.METRE;
    }

    @Test
    public void factoryMethods() {
        Length l = Length.of(10, Units.SI.METRE);

        assertSame(Units.SI.METRE, l.getUnit());
        assertEquals(10, l.doubleValue(), 1e-6);

        l = Length.ofMeter(20);
        assertSame(Units.SI.METRE, l.getUnit());
        assertEquals(20, l.doubleValue(), 1e-6);
    }
}