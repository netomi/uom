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

import org.netomi.uom.Unit;
import org.netomi.uom.unit.Units;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Unit test for the {@link ElectricCurrent} quantity.
 */
public class ElectricCurrentTest extends GenericQuantityTest<ElectricCurrent, ElectricCurrent> {

    @Override
    protected Class<ElectricCurrent> getQuantityClass() {
        return ElectricCurrent.class;
    }

    @Override
    protected Unit<ElectricCurrent> getSystemUnit() {
        return Units.SI.AMPERE;
    }

    @Override
    protected BiFunction<Double, Unit<ElectricCurrent>, ElectricCurrent> getFactoryMethod() {
        return ElectricCurrent::of;
    }

    @Override
    protected Function<Double, ElectricCurrent> getFactoryMethodForSystemUnit() {
        return ElectricCurrent::ofAmpere;
    }
}
