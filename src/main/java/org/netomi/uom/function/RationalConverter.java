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
import java.math.BigInteger;
import java.math.MathContext;

/**
 * A {@code UnitConverter} implementation that converts values by multiplying
 * them with a rational number.
 *
 * @author Thomas Neidhart
 */
class RationalConverter implements UnitConverter {

    private final long dividend;
    private final long divisor;

    private final BigDecimal decimalDividend;
    private final BigDecimal decimalDivisor;

    RationalConverter(long dividend, long divisor) {
        if (dividend <= 0 || divisor <= 0) {
            throw new IllegalArgumentException("dividend and divisor must be a positive integer.");
        }

        this.dividend = dividend;
        this.divisor  = divisor;

        decimalDividend = BigDecimal.valueOf(dividend);
        decimalDivisor  = BigDecimal.valueOf(divisor);
    }

    /**
     * Returns the dividend.
     */
    public long getDividend() {
        return dividend;
    }

    /**
     * Returns the divisor.
     */
    public long getDivisor() {
        return divisor;
    }

    @Override
    public RationalConverter inverse() {
        return new RationalConverter(divisor, dividend);
    }

    @Override
    public UnitConverter concatenate(UnitConverter that) {
        if (that instanceof RationalConverter) {
            BigInteger newDividend =
                    BigInteger.valueOf(dividend).multiply(BigInteger.valueOf(((RationalConverter) that).dividend));
            BigInteger newDivisor  =
                    BigInteger.valueOf(divisor).multiply(BigInteger.valueOf(((RationalConverter) that).divisor));

            BigInteger gcd = newDividend.gcd(newDivisor);

            newDividend = newDividend.divide(gcd);
            newDivisor  = newDivisor.divide(gcd);

            return UnitConverters.multiply(newDividend.longValue(), newDivisor.longValue());
        }

        return UnitConverter.super.concatenate(that);
    }

    @Override
    public double convert(double value) {
        return value * dividend / divisor;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {

        return value.multiply(decimalDividend, context).divide(decimalDivisor, context);
    }

    @Override
    public String toString() {
        return String.format("RationalConverter[dividend=%d,divisor=%d]", dividend, divisor);
    }
}
