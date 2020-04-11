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
import org.netomi.uom.unit.systems.SI;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Unit test for the {@link Frequency} quantity.
 */
public class FrequencyTest extends GenericQuantityTest<Frequency> {

    @Override
    protected Class<Frequency> getQuantityClass() {
        return Frequency.class;
    }

    @Override
    protected Unit<Frequency> getSystemUnit() {
        return SI.HERTZ;
    }

    @Override
    protected BiFunction<Double, Unit<Frequency>, Frequency> getFactoryMethod() {
        return Frequency::of;
    }

    @Override
    protected Function<Double, Frequency> getFactoryMethodForSystemUnit() {
        return Frequency::ofHertz;
    }
}
