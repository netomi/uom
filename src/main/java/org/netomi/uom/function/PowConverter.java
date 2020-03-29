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
 * {@code UnitConverter} implementation that converts values by applying
 * the provided {@link UnitConverter} n times.
 * <p>
 * This class should not be used directly, instead use
 * {@code UnitConverters#pow} to create an instance of this class.
 *
 * @author Thomas Neidhart
 */
class PowConverter implements UnitConverter {

    private final UnitConverter unitConverter;
    private final int           exponent;

    PowConverter(UnitConverter unitConverter, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("exponent must be non-negative.");
        }

        this.unitConverter = unitConverter;
        this.exponent      = exponent;
    }

    public UnitConverter getUnitConverter() {
        return unitConverter;
    }

    public int getExponent() {
        return exponent;
    }

    @Override
    public boolean isLinear() {
        return unitConverter.isLinear();
    }

    @Override
    public PowConverter inverse() {
        return new PowConverter(unitConverter.inverse(), exponent);
    }

    @Override
    public double convert(double value) {
        double result = value;
        for (int i = 0; i < exponent; i++) {
            result = unitConverter.convert(result);
        }
        return result;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        BigDecimal result = value;
        for (int i = 0; i < exponent; i++) {
            result = unitConverter.convert(result, context);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("(pow '%s' %d))", unitConverter, exponent);
    }
}
