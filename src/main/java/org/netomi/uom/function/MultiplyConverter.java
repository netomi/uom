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
package org.netomi.uom.function;

import org.netomi.uom.UnitConverter;
import org.netomi.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * A {@code UnitConverter} implementation that converts values by multiplying
 * them with a constant factor represented as decimal fraction.
 *
 * @author Thomas Neidhart
 */
class MultiplyConverter extends AbstractConverter {

    // the multiplier represented as decimal fraction.
    private final BigFraction multiplier;
    // the multiplier as double value, for caching reasons.
    private final double      multiplierAsDouble;

    public MultiplyConverter(double value) {
        this(BigFraction.from(value));
    }

    public MultiplyConverter(long numerator, long denominator) {
        this(BigFraction.of(numerator, denominator));
    }

    MultiplyConverter(BigFraction multiplier) {
        if (BigFraction.ZERO.compareTo(multiplier) == 0) {
            throw new IllegalArgumentException("Multiplier must be different from 0.");
        }

        this.multiplier         = multiplier;
        this.multiplierAsDouble = multiplier.doubleValue();
    }

    /**
     * Returns the multiplier as decimal fraction.
     */
    public BigFraction getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean isLinear() {
        return true;
    }

    @Override
    public MultiplyConverter inverse() {
        return new MultiplyConverter(multiplier.reciprocal());
    }

    @Override
    public UnitConverter compose(UnitConverter that) {
        if (that instanceof MultiplyConverter) {
            BigFraction multiplicand = multiplier.multiply(((MultiplyConverter) that).multiplier);
            return UnitConverters.multiply(multiplicand);
        }

        return super.compose(that);
    }

    @Override
    public UnitConverter andThen(UnitConverter that) {
        if (that instanceof MultiplyConverter) {
            BigFraction multiplicand = multiplier.multiply(((MultiplyConverter) that).multiplier);
            return UnitConverters.multiply(multiplicand);
        }

        return super.andThen(that);
    }

    @Override
    public double convert(double value) {
        return value * multiplierAsDouble;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        return value.multiply(multiplier.bigDecimalValue(context), context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiplyConverter that = (MultiplyConverter) o;
        return Objects.equals(multiplier, that.multiplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multiplier);
    }

    @Override
    public String toString() {
        return String.format("(* x '%s')", multiplier);
    }
}
