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
import org.netomi.uom.math.ArithmeticUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * {@code UnitConverter} implementation that converts values by applying
 * the nth root of the provided {@link UnitConverter}.
 * <p>
 * This class should not be used directly, instead use
 * {@code UnitConverters#root} to create an instance of this class. Only square
 * roots are supported atm, special cases like n = 0 are handled by the
 * factory method.
 *
 * @author Thomas Neidhart
 */
class RootConverter extends AbstractConverter {

    private final UnitConverter unitConverter;
    private final double        multiplierRooted;

    RootConverter(UnitConverter unitConverter, int n) {
        if (n != 2) {
            throw new IllegalArgumentException(String.format("Unsupported nth root '%d', only n=2 is allowed.", n));
        }

        if (!unitConverter.isLinear()) {
            throw new IllegalArgumentException(String.format("Root converter applied to non-linear converter: '%s'",
                                                             unitConverter));
        }

        this.unitConverter = unitConverter;

        // get the multiplier from the delegate converter
        // and calculate its root as double for caching reasons.
        // do not cache the BigDecimal value as the MathContext
        // is not known in advance.
        double multiplier = unitConverter.convert(1);
        multiplierRooted = Math.sqrt(multiplier);
    }

    public UnitConverter getUnitConverter() {
        return unitConverter;
    }

    public int getN() {
        return 2;
    }

    @Override
    public boolean isLinear() {
        return unitConverter.isLinear();
    }

    @Override
    public RootConverter inverse() {
        // Use a fixed root of 2 as only this is supported.
        return new RootConverter(unitConverter.inverse(), 2);
    }

    @Override
    public double convert(double value) {
        return value * multiplierRooted;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        BigDecimal multiplier = unitConverter.convert(BigDecimal.ONE, context);
        multiplier = ArithmeticUtils.sqrt(multiplier, context);
        return value.multiply(multiplier, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RootConverter that = (RootConverter) o;
        return Objects.equals(unitConverter, that.unitConverter) &&
               // comparison for the exponent only for completeness.
               Objects.equals(getN(), that.getN());
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitConverter, getN());
    }

    @Override
    public String toString() {
        return String.format("(root %d '%s')", getN(), unitConverter);
    }
}
