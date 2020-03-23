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

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A {@code UnitConverter} implementation that converts values by multiplying
 * them with a constant factor.
 *
 * @author Thomas Neidhart
 */
public class MultiplyConverter implements UnitConverter {

    // the constant multiplier factor as decimal value.
    private final BigDecimal decimalMultiplier;
    // the multiplier factor with double precision, cached.
    private final double     doubleMultiplier;

    public MultiplyConverter(double multiplier) {
        this(BigDecimal.valueOf(multiplier));
    }

    public MultiplyConverter(BigDecimal multiplier) {
        if (BigDecimal.ZERO.compareTo(multiplier) == 0) {
            throw new IllegalArgumentException("a multiplier of 0 is not supported.");
        }

        this.decimalMultiplier = multiplier;
        this.doubleMultiplier  = decimalMultiplier.doubleValue();
    }

    /**
     * Returns the multiplier as decimal value.
     */
    public BigDecimal getDecimalMultiplier() {
        return decimalMultiplier;
    }

    /**
     * Returns the multiplier with double precision.
     */
    public double getMultiplier() {
        return doubleMultiplier;
    }

    @Override
    public MultiplyConverter inverse() {
        return new MultiplyConverter(BigDecimal.ONE.divide(decimalMultiplier, MathContext.DECIMAL128));
    }

    @Override
    public UnitConverter concatenate(UnitConverter that) {
        if (that instanceof MultiplyConverter) {
            BigDecimal multiplicand = decimalMultiplier.multiply(((MultiplyConverter) that).decimalMultiplier, MathContext.DECIMAL128);
            return UnitConverters.multiply(multiplicand);
        }

        return UnitConverter.super.concatenate(that);
    }

    @Override
    public double convert(double value) {
        return value * doubleMultiplier;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        return value.multiply(decimalMultiplier, context);
    }

    @Override
    public String toString() {
        return String.format("MultiplyConverter[multiplier=%s]", decimalMultiplier.toPlainString());
    }
}
