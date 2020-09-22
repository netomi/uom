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
package com.github.netomi.uom.quantity.electromagnetic;

import com.github.netomi.uom.quantity.AbstractTypedQuantityTest;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.unit.systems.SI;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Unit test for the {@link ElectricResistance} quantity.
 */
public class ElectricResistanceTest extends AbstractTypedQuantityTest<ElectricResistance> {

    @Override
    protected Class<ElectricResistance> getQuantityClass() {
        return ElectricResistance.class;
    }

    @Override
    protected Unit<ElectricResistance> getSystemUnit() {
        return SI.OHM.getSystemUnit();
    }

    @Override
    protected BiFunction<Double, Unit<ElectricResistance>, ElectricResistance> getFactoryMethod() {
        return ElectricResistance::of;
    }

    @Override
    protected Function<Double, ElectricResistance> getFactoryMethodForSystemUnit() {
        return ElectricResistance::ofOhm;
    }
}
