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
public final class UnitConverters {

    // hide constructor of a pure utility class.
    private UnitConverters() {}

    /**
     * Returns an identity converter.
     * <p>
     * This method always returns the same instance.
     */
    public static UnitConverter identity() {
        return IdentityConverter.INSTANCE;
    }

    /**
     * Returns a {@link UnitConverter} that applies a shift by a constant factor.
     * <p>
     * If an offset of {@code 0} is provided, {@link #identity()} is returned.
     * <p>
     * Note: prefer using {@link #shift(BigDecimal)}.
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
     * If an offset of {@code 0} is provided, {@link #identity()} is returned.
     *
     * @param offset the constant factor by which the value will be shifted.
     * @return a {@link UnitConverter} applying a constant shift operation.
     */
    public static UnitConverter shift(BigDecimal offset) {
        return BigDecimal.ZERO.compareTo(offset) == 0 ? identity() : new AddConverter(offset);
    }

    /**
     * Returns a {@link UnitConverter} that multiplies the input with a constant factor.
     * <p>
     * If a factor of {@code 1} is provided, {@link #identity()} is returned.
     * <p>
     * Note: prefer using {@link #multiply(long, long)}.
     *
     * @param multiplicand the constant multiplication factor.
     * @return a {@link UnitConverter} multiplying by a constant factor.
     */
    public static UnitConverter multiply(double multiplicand) {
        return multiply(BigFraction.from(multiplicand));
    }

    /**
     * Returns a {@link UnitConverter} that multiplies the input with a constant factor.
     * <p>
     * If a factor of {@code 1} is provided, {@link #identity()} is returned.
     * <p>
     * Note: prefer using {@link #multiply(long, long)}.
     *
     * @param multiplicand the constant multiplication factor.
     * @return a {@link UnitConverter} multiplying by a constant factor.
     */
    public static UnitConverter multiply(BigDecimal multiplicand) {
        return multiply(BigFraction.from(multiplicand));
    }

    /**
     * Returns a {@link UnitConverter} that multiplies the input with a constant factor provided as
     * fraction {@code numerator / denominator}.
     * <p>
     * If a factor of {@code 1} is provided, {@link #identity()} is returned.
     *
     * @param numerator    the numerator of the fraction.
     * @param denominator  the denominator of the fraction.
     * @return a {@link UnitConverter} multiplying by a constant factor.
     */
    public static UnitConverter multiply(long numerator, long denominator) {
        return numerator == denominator ? identity() : multiply(BigFraction.of(numerator, denominator));
    }

    /**
     * Returns a {@link UnitConverter} that multiplies the input with a constant factor provided as
     * fraction {@code numerator / denominator}.
     * <p>
     * If a factor of {@code 1} is provided, {@link #identity()} is returned.
     *
     * @param numerator    the numerator of the fraction.
     * @param denominator  the denominator of the fraction.
     * @return a {@link UnitConverter} multiplying by a constant factor.
     */
    public static UnitConverter multiply(BigInteger numerator, BigInteger denominator) {
        return numerator.compareTo(denominator) == 0 ? identity() : multiply(BigFraction.of(numerator, denominator));
    }

    static UnitConverter multiply(BigFraction multiplicand) {
        return BigFraction.ONE.compareTo(multiplicand) == 0 ? identity() : new MultiplyConverter(multiplicand);
    }

    /**
     * Returns a {@link UnitConverter} that
     */
    public static UnitConverter pow(int base, int exponent) {
        if (exponent == 0) {
            return identity();
        }

        if (exponent > 0) {
            try {
                long value = ArithmeticUtils.pow((long) base, exponent);
                return multiply(value, 1);
            } catch (ArithmeticException ex) {
                // long overflow.
                BigInteger numerator = BigInteger.valueOf(base).pow(exponent);
                return multiply(numerator, BigInteger.ONE);
            }
        } else {
            try {
                long value = ArithmeticUtils.pow((long) base, -exponent);
                return multiply(1, value);
            } catch (ArithmeticException ex) {
                // long overflow.
                BigInteger denominator = BigInteger.valueOf(base).pow(-exponent);
                return multiply(BigInteger.ONE, denominator);
            }
        }
    }

    /**
     * Returns a {@link UnitConverter} that applies the given converter n times
     * to the input.
     * <p>
     * Special cases:
     * <ul>
     *   <li>if an identity converter is provided, return the converter as is
     *   <li>if an exponent of 1 is provided, return the converter as is
     *   <li>if an exponent of 0 is provided, return {@link #identity()}
     * </ul>
     *
     * @param converter  the converter to use for the power operation.
     * @param exponent   the exponent of the power operation.
     * @return a {@link UnitConverter} applying the converter n times.
     */
    public static UnitConverter pow(UnitConverter converter, int exponent) {
        if (converter.isIdentity() || exponent == 1) {
            return converter;
        } else if (exponent == 0) {
            return identity();
        } else if (converter.isLinear()) {
            // unroll the power operation for linear converters to be able
            // to reduce the unit converters.

            if (exponent < 0) {
                exponent  = -exponent;
                converter = converter.inverse();
            }

            UnitConverter poweredConverter = UnitConverters.identity();
            for (int j = 0; j < exponent; j++) {
                poweredConverter = poweredConverter.andThen(converter);
            }
            return poweredConverter;
        } else {
            // do not unroll the power operation for non-linear converters.
            // its possible but does not bring much value.
            return exponent > 1 ?
                    new PowConverter(converter, exponent) :
                    new PowConverter(converter.inverse(), -exponent);
        }
    }

    /**
     * Returns a {@link UnitConverter} that computes the root of the given
     * converter. Only square roots are supported.
     * <p>
     * Special cases:
     * <ul>
     *   <li>if an identity converter is provided, return the converter as is
     *   <li>if an exponent of 1 is provided, return the converter as is
     * </ul>
     *
     * @param converter  the converter to use for the power operation.
     * @param n          specifies the number of the root operation.
     * @return a {@link UnitConverter} applying the converter n times.
     * @throws IllegalArgumentException if n is negative.
     */
    public static UnitConverter root(UnitConverter converter, int n) {
        return converter.isIdentity() ? converter :
               n == 1                 ? converter :
                                        new RootConverter(converter, n);
    }

    /**
     * Internal method, use {@link UnitConverter#compose(UnitConverter)} or
     * {@link UnitConverter#andThen(UnitConverter)} instead.
     */
    static UnitConverter compose(UnitConverter before, UnitConverter after) {
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
        public double scale() {
            return 1.;
        }

        @Override
        public BigFraction scaleAsFraction() {
            return BigFraction.ONE;
        }

        @Override
        public BigDecimal scale(MathContext mc) {
            return BigDecimal.ONE;
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
        public BigDecimal convert(BigDecimal value) {
            return value;
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext mc) {
            return value;
        }

        @Override
        public String toString() {
            return "(identity x)";
        }
    }

    /**
     * A converter that composes 2 {@link UnitConverter} instances.
     */
    private static class ComposeConverter extends AbstractConverter {
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
        public UnitConverter compose(UnitConverter that) {
            return UnitConverters.compose(before.compose(that), after);
        }

        @Override
        public UnitConverter andThen(UnitConverter that) {
            return UnitConverters.compose(before, after.andThen(that));
        }

        @Override
        public double convert(double value) {
            return after.convert(before.convert(value));
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext mc) {
            return after.convert(before.convert(value, mc), mc);
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
