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
package com.github.netomi.uom.quantity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.quantity.kinematic.Speed;
import com.github.netomi.uom.unit.systems.SI;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit test for the {@link Length} quantity.
 */
public class LengthTest extends AbstractTypedQuantityTest<Length> {

    @Override
    protected Class<Length> getQuantityClass() {
        return Length.class;
    }

    @Override
    protected Unit<Length> getSystemUnit() {
        return SI.METRE.getSystemUnit();
    }

    @Override
    protected BiFunction<Double, Unit<Length>, Length> getFactoryMethod() {
        return Length::of;
    }

    @Override
    protected Function<Double, Length> getFactoryMethodForSystemUnit() {
        return Length::ofMeter;
    }

    @ParameterizedTest
    @ValueSource(classes = { Double.class, BigDecimal.class })
    public void asQuantity(Class<Number> numberClass) {
        Speed s = createQuantity(10, SI.METER_PER_SECOND, Speed.class, numberClass);
        Time  t = createQuantity(20, SI.SECOND, Time.class, numberClass);

        // the resulting quantity has a unit of m.
        Quantity<?> result = s.multiply(t);

        assertEquals(200, result.doubleValue(), 1e-6);
        assertEquals(SI.METRE, result.getUnit());

        Length l = result.asQuantity(Length.class);
        assertSame(result, l);
    }
}
