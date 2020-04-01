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
package org.netomi.uom.unit;

import org.junit.jupiter.api.Test;
import org.netomi.uom.Prefix;
import org.netomi.uom.Unit;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Precision;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Prefixes.Metric} class.
 */
public class MetricPrefixTest {

    @Test
    public void factoryMethods() {
        checkFactoryMethod(Prefixes.Metric::YOTTA, Prefixes.Metric.YOTTA, 1e24);
        checkFactoryMethod(Prefixes.Metric::ZETTA, Prefixes.Metric.ZETTA, 1e21);
        checkFactoryMethod(Prefixes.Metric::EXA  , Prefixes.Metric.EXA  , 1e18);
        checkFactoryMethod(Prefixes.Metric::PETA , Prefixes.Metric.PETA , 1e15);
        checkFactoryMethod(Prefixes.Metric::TERA , Prefixes.Metric.TERA , 1e12);
        checkFactoryMethod(Prefixes.Metric::GIGA , Prefixes.Metric.GIGA , 1e9);
        checkFactoryMethod(Prefixes.Metric::MEGA , Prefixes.Metric.MEGA , 1e6);
        checkFactoryMethod(Prefixes.Metric::KILO , Prefixes.Metric.KILO , 1e3);
        checkFactoryMethod(Prefixes.Metric::HECTO, Prefixes.Metric.HECTO, 1e2);
        checkFactoryMethod(Prefixes.Metric::DEKA , Prefixes.Metric.DEKA , 1e1);
        checkFactoryMethod(Prefixes.Metric::DECI , Prefixes.Metric.DECI , 1e-1);
        checkFactoryMethod(Prefixes.Metric::CENTI, Prefixes.Metric.CENTI, 1e-2);
        checkFactoryMethod(Prefixes.Metric::MILLI, Prefixes.Metric.MILLI, 1e-3);
        checkFactoryMethod(Prefixes.Metric::MICRO, Prefixes.Metric.MICRO, 1e-6);
        checkFactoryMethod(Prefixes.Metric::NANO , Prefixes.Metric.NANO , 1e-9);
        checkFactoryMethod(Prefixes.Metric::PICO , Prefixes.Metric.PICO , 1e-12);
        checkFactoryMethod(Prefixes.Metric::FEMTO, Prefixes.Metric.FEMTO, 1e-15);
        checkFactoryMethod(Prefixes.Metric::ATTO , Prefixes.Metric.ATTO , 1e-18);
        checkFactoryMethod(Prefixes.Metric::ZEPTO, Prefixes.Metric.ZEPTO, 1e-21);
        checkFactoryMethod(Prefixes.Metric::YOCTO, Prefixes.Metric.YOCTO, 1e-24);
    }

    private static void checkFactoryMethod(UnaryOperator<Unit<?>> factoryMethod, Prefix prefix, double multiplier) {
        Unit<?> unit = Units.SI.METRE;

        Unit<?> prefixedUnit = factoryMethod.apply(unit);

        assertEquals(prefix.getSymbol() + unit.getSymbol(), prefixedUnit.getSymbol());
        assertEquals(prefix.getName() + unit.getName(), prefixedUnit.getName());
        assertEquals(UnitConverters.pow(prefix.getBase(), prefix.getExponent()), prefixedUnit.getSystemConverter());

        // double precision
        assertTrue(Precision.equals(42. * multiplier, prefixedUnit.getSystemConverter().convert(42.), 1e-12));
        assertTrue(Precision.equals(42. / multiplier, prefixedUnit.getSystemConverter().inverse().convert(42.), 1e-12));

        // DECIMAL128 precision
        MathContext context = MathContext.DECIMAL128;
        assertTrue(Precision.equals(BigDecimal.valueOf(42).multiply(BigDecimal.valueOf(multiplier), context).doubleValue(),
                                    prefixedUnit.getSystemConverter().convert(BigDecimal.valueOf(42), context).doubleValue(), 1e-24));
        assertTrue(Precision.equals(BigDecimal.valueOf(42).divide(BigDecimal.valueOf(multiplier), context).doubleValue(),
                                    prefixedUnit.getSystemConverter().inverse().convert(BigDecimal.valueOf(42), context).doubleValue(), 1e-24));
    }
}
