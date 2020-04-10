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
 * Unit test for the {@link Time} quantity.
 */
public class TimeTest extends GenericQuantityTest<Time> {

    @Override
    protected Class<Time> getQuantityClass() {
        return Time.class;
    }

    @Override
    protected Unit<Time> getSystemUnit() {
        return Units.SI.SECOND;
    }

    @Override
    protected BiFunction<Double, Unit<Time>, Time> getFactoryMethod() {
        return Time::of;
    }

    @Override
    protected Function<Double, Time> getFactoryMethodForSystemUnit() {
        return Time::ofSecond;
    }
}
