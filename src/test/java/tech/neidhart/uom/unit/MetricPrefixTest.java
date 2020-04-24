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
package tech.neidhart.uom.unit;

import org.junit.jupiter.api.Test;
import tech.neidhart.uom.Prefix;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.function.UnitConverters;
import tech.neidhart.uom.math.BigFraction;
import tech.neidhart.uom.unit.systems.SI;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Prefixes.Metric} class.
 */
public class MetricPrefixTest {

    @Test
    public void factoryMethods() {
        checkFactoryMethod(Prefixes.Metric::YOTTA, Prefixes.Metric.YOTTA,  24);
        checkFactoryMethod(Prefixes.Metric::ZETTA, Prefixes.Metric.ZETTA,  21);
        checkFactoryMethod(Prefixes.Metric::EXA  , Prefixes.Metric.EXA  ,  18);
        checkFactoryMethod(Prefixes.Metric::PETA , Prefixes.Metric.PETA ,  15);
        checkFactoryMethod(Prefixes.Metric::TERA , Prefixes.Metric.TERA ,  12);
        checkFactoryMethod(Prefixes.Metric::GIGA , Prefixes.Metric.GIGA ,   9);
        checkFactoryMethod(Prefixes.Metric::MEGA , Prefixes.Metric.MEGA ,   6);
        checkFactoryMethod(Prefixes.Metric::KILO , Prefixes.Metric.KILO ,   3);
        checkFactoryMethod(Prefixes.Metric::HECTO, Prefixes.Metric.HECTO,   2);
        checkFactoryMethod(Prefixes.Metric::DEKA , Prefixes.Metric.DEKA ,   1);
        checkFactoryMethod(Prefixes.Metric::DECI , Prefixes.Metric.DECI ,  -1);
        checkFactoryMethod(Prefixes.Metric::CENTI, Prefixes.Metric.CENTI,  -2);
        checkFactoryMethod(Prefixes.Metric::MILLI, Prefixes.Metric.MILLI,  -3);
        checkFactoryMethod(Prefixes.Metric::MICRO, Prefixes.Metric.MICRO,  -6);
        checkFactoryMethod(Prefixes.Metric::NANO , Prefixes.Metric.NANO ,  -9);
        checkFactoryMethod(Prefixes.Metric::PICO , Prefixes.Metric.PICO , -12);
        checkFactoryMethod(Prefixes.Metric::FEMTO, Prefixes.Metric.FEMTO, -15);
        checkFactoryMethod(Prefixes.Metric::ATTO , Prefixes.Metric.ATTO , -18);
        checkFactoryMethod(Prefixes.Metric::ZEPTO, Prefixes.Metric.ZEPTO, -21);
        checkFactoryMethod(Prefixes.Metric::YOCTO, Prefixes.Metric.YOCTO, -24);
    }

    @Test
    public void withExponent() {
        assertSame(Prefixes.Metric.KILO, Prefixes.Metric.MILLI.withExponent(3));

        assertEquals(10,    Prefixes.Metric.MILLI.withExponent(4).getBase());
        assertEquals(4,     Prefixes.Metric.MILLI.withExponent(4).getExponent());
        assertEquals("10⁴", Prefixes.Metric.MILLI.withExponent(4).getSymbol());

        assertEquals(UnitConverters.pow(10, 4), Prefixes.Metric.MILLI.withExponent(4).getUnitConverter());
        assertSame(Prefixes.Metric.KILO, Prefixes.Metric.MILLI.withExponent(4).withExponent(3));
    }

    private static void checkFactoryMethod(UnaryOperator<Unit<?>> factoryMethod, Prefix prefix, int exponent) {
        Unit<?> unit = SI.METRE;

        UnitConverter unitConverter = UnitConverters.pow(10, exponent);
        BigFraction fraction = unitConverter.scaleAsFraction();

        Unit<?> prefixedUnit = factoryMethod.apply(unit);

        assertEquals(prefix.getSymbol() + unit.getSymbol(), prefixedUnit.getSymbol());
        assertEquals(prefix.getName() + unit.getName(), prefixedUnit.getName());
        assertEquals(UnitConverters.pow(prefix.getBase(), prefix.getExponent()), prefixedUnit.getSystemConverter());

        // double precision
        double multiplier        = fraction.doubleValue();
        double inverseMultiplier = fraction.reciprocal().doubleValue();

        assertEquals(42. * multiplier, prefixedUnit.getSystemConverter().convert(42.), 1e-12);
        assertEquals(42. * inverseMultiplier, prefixedUnit.getSystemConverter().inverse().convert(42.), 1e-12);

        // DECIMAL128 precision
        BigDecimal value    = BigDecimal.valueOf(42);
        MathContext context = MathContext.DECIMAL128;
        BigDecimal decimalMultiplier = fraction.bigDecimalValue(context);

        assertEquals(value.multiply(decimalMultiplier, context).doubleValue(),
                     prefixedUnit.getSystemConverter().convert(value, context).doubleValue(), 1e-24);
        assertEquals(value.divide(decimalMultiplier, context).doubleValue(),
                     prefixedUnit.getSystemConverter().inverse().convert(value, context).doubleValue(), 1e-24);
    }
}
