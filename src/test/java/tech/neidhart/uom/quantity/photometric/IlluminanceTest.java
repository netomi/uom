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
package tech.neidhart.uom.quantity.photometric;

import tech.neidhart.uom.Unit;
import tech.neidhart.uom.quantity.AbstractTypedQuantityTest;
import tech.neidhart.uom.quantity.photometric.Illuminance;
import tech.neidhart.uom.unit.systems.SI;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Unit test for the {@link Illuminance} quantity.
 */
public class IlluminanceTest extends AbstractTypedQuantityTest<Illuminance, Illuminance> {

    @Override
    protected Class<Illuminance> getQuantityClass() {
        return Illuminance.class;
    }

    @Override
    protected Unit<Illuminance> getSystemUnit() {
        return SI.LUX.getSystemUnit();
    }

    @Override
    protected BiFunction<Double, Unit<Illuminance>, Illuminance> getFactoryMethod() {
        return Illuminance::of;
    }

    @Override
    protected Function<Double, Illuminance> getFactoryMethodForSystemUnit() {
        return Illuminance::ofLux;
    }
}
