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
 * Unit test for the {@link Distance} quantity.
 */
public class DistanceTest extends GenericQuantityTest<Distance, Length> {

    @Override
    protected Class<Distance> getQuantityClass() {
        return Distance.class;
    }

    @Override
    protected Unit<Length> getSystemUnit() {
        return Units.SI.METRE;
    }

    @Override
    protected BiFunction<Double, Unit<Length>, Distance> getFactoryMethod() {
        return Distance::of;
    }

    @Override
    protected Function<Double, Distance> getFactoryMethodForSystemUnit() {
        return Distance::ofMeter;
    }
}