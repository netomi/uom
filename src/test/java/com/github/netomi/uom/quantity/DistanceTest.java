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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the {@link Distance} quantity.
 */
public class DistanceTest extends AbstractTypedQuantityTest<Distance, Length> {

    @Override
    protected Class<Distance> getQuantityClass() {
        return Distance.class;
    }

    @Override
    protected Unit<Length> getSystemUnit() {
        return SI.METRE.getSystemUnit();
    }

    @Override
    protected BiFunction<Double, Unit<Length>, Distance> getFactoryMethod() {
        return Distance::of;
    }

    @Override
    protected Function<Double, Distance> getFactoryMethodForSystemUnit() {
        return Distance::ofMeter;
    }

    @Override
    protected boolean isExtendedQuantity() {
        return true;
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

        Distance l = result.asQuantity(Distance.class);
        assertNotSame(result, l);
        assertEquals(200, l.doubleValue(), 1e-6);
        assertEquals(SI.METRE, l.getUnit());
    }
}
