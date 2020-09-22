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
package com.github.netomi.uom.quantity.nuclear;

import com.github.netomi.uom.quantity.AbstractTypedQuantityTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.Length;
import com.github.netomi.uom.quantity.kinematic.Speed;
import com.github.netomi.uom.unit.systems.SI;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the {@link RadioActivity} quantity.
 */
public class RadioActivityTest extends AbstractTypedQuantityTest<RadioActivity> {

    @Override
    protected Class<RadioActivity> getQuantityClass() {
        return RadioActivity.class;
    }

    @Override
    protected Unit<RadioActivity> getSystemUnit() {
        return SI.BECQUEREL.getSystemUnit();
    }

    @Override
    protected BiFunction<Double, Unit<RadioActivity>, RadioActivity> getFactoryMethod() {
        return RadioActivity::of;
    }

    @Override
    protected Function<Double, RadioActivity> getFactoryMethodForSystemUnit() {
        return RadioActivity::ofBecquerel;
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void asQuantity(Class<Number> numberClass) {
        Speed s  = createQuantity(10, SI.METER_PER_SECOND, Speed.class, numberClass);
        Length l = createQuantity(100, SI.METRE, Length.class, numberClass);

        // the resulting quantity has a unit of 1 / s.
        Quantity<?> result = s.divide(l);

        assertEquals(0.1, result.doubleValue(), 1e-6);
        assertEquals(SI.HERTZ, result.getUnit());

        // RadioActivity has the same dimension, so we can cast it
        // to this quantity type if needed.
        RadioActivity r = result.asQuantity(RadioActivity.class);
        assertEquals(0.1, r.doubleValue(), 1e-6);
        assertEquals(SI.BECQUEREL, r.getUnit());
    }
}
