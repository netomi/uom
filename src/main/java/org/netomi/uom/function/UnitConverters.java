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
import org.netomi.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

/**
 * Utility class to access various unit converter implementations.
 *
 * @author Thomas Neidhart
 */
public class UnitConverters {

    // hide constructor of a pure utility class.
    private UnitConverters() {}

    /**
     * Returns an identity converter.
     */
    public static UnitConverter identity() {
        return IdentityConverter.INSTANCE;
    }

    /**
     * Returns a {@link UnitConverter} that applies a shift by a constant factor.
     * <p>
     * If an offset of {@code 0} is supplied, {@link #identity()} is returned.
     * <p>
     * Note: prefer using {@link #shift(BigDecimal)}
     *
     * @param offset the constant factor by which the value will be shifted.
     * @return a {@link UnitConverter} applying a constant shift operation.
     */
    public static UnitConverter shift(double offset) {
        return shift(BigDecimal.valueOf(offset));
    }

    /**
     * Returns a {@link UnitConverter} that applies a shift by a constant factor.
     * <p>
     * If an offset of {@code 0} is supplied, {@link #identity()} is returned.
     *
     * @param offset the constant factor by which the value will be shifted.
     * @return a {@link UnitConverter} applying a constant shift operation.
     */
    public static UnitConverter shift(BigDecimal offset) {
        return BigDecimal.ZERO.compareTo(offset) == 0 ? identity() : new AddConverter(offset);
    }

    public static UnitConverter multiply(double multiplicand) {
        return multiply(BigFraction.from(multiplicand));
    }

    public static UnitConverter multiply(BigDecimal multiplicand) {
        return multiply(BigFraction.from(multiplicand));
    }

    public static UnitConverter multiply(long numerator, long denominator) {
        return numerator == denominator ? identity() : multiply(BigFraction.of(numerator, denominator));
    }

    public static UnitConverter multiply(BigInteger numerator, BigInteger denominator) {
        return numerator.compareTo(denominator) == 0 ? identity() : multiply(BigFraction.of(numerator, denominator));
    }

    static UnitConverter multiply(BigFraction multiplicand) {
        return BigFraction.ONE.compareTo(multiplicand) == 0 ? identity() : new MultiplyConverter(multiplicand);
    }

    public static UnitConverter pow(int base, int exponent) {
        if (exponent == 0) {
            return identity();
        }

        if (exponent > 0) {
            try {
                long value = ArithmeticUtils.pow((long) base, exponent);
                return multiply(value, 1l);
            } catch (ArithmeticException ex) {
                // long overflow.
                BigInteger numerator = BigInteger.valueOf(base).pow(exponent);
                return multiply(numerator, BigInteger.ONE);
            }
        } else {
            try {
                long value = ArithmeticUtils.pow((long) base, -exponent);
                return multiply(1l, value);
            } catch (ArithmeticException ex) {
                // long overflow.
                BigInteger denominator = BigInteger.valueOf(base).pow(-exponent);
                return multiply(BigInteger.ONE, denominator);
            }
        }
    }

    public static UnitConverter pow(UnitConverter converter, int exponent) {
        return converter.isIdentity() ? converter :
               exponent == 1          ? converter :
               exponent == 0          ? new MultiplyConverter(1, 1) :
               exponent > 1           ? new PowConverter(converter, exponent) :
                                        new PowConverter(converter.inverse(), -exponent);
    }

    public static UnitConverter root(UnitConverter converter, int n) {
        return converter.isIdentity() ? converter :
               n == 1                 ? converter :
               n == 0                 ? new ConstantConverter(BigFraction.ONE) :
                                        new RootConverter(converter, n);
    }

    public static UnitConverter compose(UnitConverter before, UnitConverter after) {
        if (before.isIdentity()) {
            return after;
        } else if (after.isIdentity()) {
            return before;
        }

        return new ComposeConverter(before, after);
    }

    // Inner helper classes

    /**
     * An identity converter that just returns the value passed as argument.
     */
    private enum IdentityConverter implements UnitConverter {
        INSTANCE;

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public boolean isLinear() {
            return true;
        }

        @Override
        public UnitConverter compose(UnitConverter before) {
            return before;
        }

        @Override
        public UnitConverter andThen(UnitConverter that) {
            return that;
        }


        @Override
        public UnitConverter inverse() {
            return INSTANCE;
        }

        @Override
        public double convert(double value) {
            return value;
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext context) {
            return value;
        }

        @Override
        public String toString() {
            return "(identity x)";
        }
    }

    /**
     * A converter that returns a constant value.
     * Not really needed but added for completeness (used for root(x, 0)).
     */
    private static class ConstantConverter implements UnitConverter {
        private final BigFraction constant;
        private final double      constantAsDouble;

        ConstantConverter(BigFraction value) {
            this.constant         = value;
            this.constantAsDouble = value.doubleValue();
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public boolean isLinear() {
            return false;
        }

        @Override
        public UnitConverter compose(UnitConverter before) {
            return before;
        }

        @Override
        public UnitConverter andThen(UnitConverter that) {
            return that;
        }


        @Override
        public UnitConverter inverse() {
            return new ConstantConverter(constant.reciprocal());
        }

        @Override
        public double convert(double value) {
            return constantAsDouble;
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext context) {
            return constant.bigDecimalValue(context);
        }

        @Override
        public String toString() {
            return String.format("(constant '%s')", constant.toString());
        }
    }

    /**
     * A converter that composes 2 {@link UnitConverter} instances.
     */
    private static class ComposeConverter implements UnitConverter {
        private final UnitConverter before;
        private final UnitConverter after;

        ComposeConverter(UnitConverter before, UnitConverter after) {
            this.before = before;
            this.after  = after;
        }

        @Override
        public boolean isIdentity() {
            return before.isIdentity() && after.isIdentity();
        }

        @Override
        public boolean isLinear() {
            return before.isLinear() && after.isLinear();
        }

        @Override
        public UnitConverter inverse() {
            return new ComposeConverter(after.inverse(), before.inverse());
        }

        @Override
        public double convert(double value) {
            return after.convert(before.convert(value));
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext context) {
            return after.convert(before.convert(value, context), context);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComposeConverter that = (ComposeConverter) o;
            return Objects.equals(before, that.before) &&
                   Objects.equals(after, that.after);
        }

        @Override
        public int hashCode() {
            return Objects.hash(before, after);
        }

        @Override
        public String toString() {
            return String.format("(compose '%s' '%s')", after, before);
        }
    }
}
