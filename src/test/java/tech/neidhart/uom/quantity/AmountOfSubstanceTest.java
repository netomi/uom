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

import tech.neidhart.uom.Unit;
import tech.neidhart.uom.unit.systems.SI;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Unit test for the {@link AmountOfSubstance} quantity.
 */
public class AmountOfSubstanceTest extends GenericQuantityTest<AmountOfSubstance> {

    @Override
    protected Class<AmountOfSubstance> getQuantityClass() {
        return AmountOfSubstance.class;
    }

    @Override
    protected Unit<AmountOfSubstance> getSystemUnit() {
        return SI.MOLE;
    }

    @Override
    protected BiFunction<Double, Unit<AmountOfSubstance>, AmountOfSubstance> getFactoryMethod() {
        return AmountOfSubstance::of;
    }

    @Override
    protected Function<Double, AmountOfSubstance> getFactoryMethodForSystemUnit() {
        return AmountOfSubstance::ofMole;
    }
}