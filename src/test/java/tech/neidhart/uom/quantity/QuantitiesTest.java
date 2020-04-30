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
package tech.neidhart.uom.quantity;

import org.junit.jupiter.api.Test;
import tech.neidhart.uom.Unit;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuantitiesTest {

    @Test
    public void quantityTypes() {
        // Ensure that all systemUnits for all registered quantities are distinct.
        Set<Unit<?>> systemUnits = new HashSet<>();

        for (Quantities.Type type : Quantities.Type.values()) {
            systemUnits.add(type.getSystemUnit());
        }

        assertEquals(Quantities.Type.values().length, systemUnits.size());
    }
}
