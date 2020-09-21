/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.netomi.uom.math;

/*
 * Note: this class has been extracted from the Apache Commons Numbers library.
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * Some useful, arithmetics related, additions to the built-in functions in {@link Math}.
 */
public final class ArithmeticUtils {

    /** Overflow gcd exception message for 2^63. */
    private static final String OVERFLOW_GCD_MESSAGE_2_POWER_63 = "overflow: gcd({0}, {1}) is 2^63";

    /** Negative exponent exception message part 1. */
    private static final String NEGATIVE_EXPONENT_1 = "negative exponent ({";
    /** Negative exponent exception message part 2. */
    private static final String NEGATIVE_EXPONENT_2 = "})";

    /** Private constructor. */
    private ArithmeticUtils() {
        // intentionally empty.
    }

    /**
     * Computes the greatest common divisor of the absolute value of two
     * numbers, using a modified version of the "binary gcd" method.
     * See Knuth 4.5.2 algorithm B.
     * The algorithm is due to Josef Stein (1961).
     * <br>
     * Special cases:
     * <ul>
     *  <li>The invocations
     *   {@code gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)},
     *   {@code gcd(Integer.MIN_VALUE, 0)} and
     *   {@code gcd(0, Integer.MIN_VALUE)} throw an
     *   {@code ArithmeticException}, because the result would be 2^31, which
     *   is too large for an int value.</li>
     *  <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and
     *   {@code gcd(x, 0)} is the absolute value of {@code x}, except
     *   for the special cases above.</li>
     *  <li>The invocation {@code gcd(0, 0)} is the only one which returns
     *   {@code 0}.</li>
     * </ul>
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor (never negative).
     * @throws ArithmeticException if the result cannot be represented as
     * a non-negative {@code int} value.
     */
    public static int gcd(int p, int q) {
        // Perform the gcd algorithm on negative numbers, so that -2^31 does not
        // need to be handled separately
        int a = p > 0 ? -p : p;
        int b = q > 0 ? -q : q;

        int negatedGcd;
        if (a == 0) {
            negatedGcd = b;
        } else if (b == 0) {
            negatedGcd = a;
        } else {
            // Make "a" and "b" odd, keeping track of common power of 2.
            final int aTwos = Integer.numberOfTrailingZeros(a);
            final int bTwos = Integer.numberOfTrailingZeros(b);
            a >>= aTwos;
            b >>= bTwos;
            final int shift = Math.min(aTwos, bTwos);

            // "a" and "b" are negative and odd.
            // If a < b then "gdc(a, b)" is equal to "gcd(a - b, b)".
            // If a > b then "gcd(a, b)" is equal to "gcd(b - a, a)".
            // Hence, in the successive iterations:
            //  "a" becomes the negative absolute difference of the current values,
            //  "b" becomes that value of the two that is closer to zero.
            while (a != b) {
                final int delta = a - b;
                b = Math.max(a, b);
                a = delta > 0 ? -delta : delta;

                // Remove any power of 2 in "a" ("b" is guaranteed to be odd).
                a >>= Integer.numberOfTrailingZeros(a);
            }

            // Recover the common power of 2.
            negatedGcd = a << shift;
        }
        if (negatedGcd == Integer.MIN_VALUE) {
            throw new NumbersArithmeticException("overflow: gcd({0}, {1}) is 2^31", p, q);
        } else {
            return -negatedGcd;
        }
    }

    /**
     * <p>
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     * </p>
     * Special cases:
     * <ul>
     * <li>The invocations
     * {@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},
     * {@code gcd(Long.MIN_VALUE, 0L)} and
     * {@code gcd(0L, Long.MIN_VALUE)} throw an
     * {@code ArithmeticException}, because the result would be 2^63, which
     * is too large for a long value.</li>
     * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and
     * {@code gcd(x, 0L)} is the absolute value of {@code x}, except
     * for the special cases above.
     * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns
     * {@code 0L}.</li>
     * </ul>
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws ArithmeticException if the result cannot be represented as
     * a non-negative {@code long} value.
     */
    public static long gcd(final long p, final long q) {
        long u = p;
        long v = q;
        if ((u == 0) || (v == 0)) {
            if ((u == Long.MIN_VALUE) || (v == Long.MIN_VALUE)) {
                throw new NumbersArithmeticException(OVERFLOW_GCD_MESSAGE_2_POWER_63,
                                                  p, q);
            }
            return Math.abs(u) + Math.abs(v);
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */
        if (u > 0) {
            u = -u;
        } // make u negative
        if (v > 0) {
            v = -v;
        } // make v negative
        // B1. [Find power of 2]
        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 63) { // while u and v are
                                                            // both even...
            u /= 2;
            v /= 2;
            k++; // cast out twos.
        }
        if (k == 63) {
            throw new NumbersArithmeticException(OVERFLOW_GCD_MESSAGE_2_POWER_63,
                                              p, q);
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        long t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while ((t & 1) == 0) { // while t is even..
                t /= 2; // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2;
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0);
        return -u * (1L << k); // gcd is u*2^k
    }

    /**
     * Raise an int to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws IllegalArgumentException if {@code e < 0}.
     * @throws ArithmeticException if the result would overflow.
     */
    public static int pow(final int k,
                          final int e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        int exp = e;
        int result = 1;
        int k2p    = k;
        while (true) {
            if ((exp & 0x1) != 0) {
                result = Math.multiplyExact(result, k2p);
            }

            exp >>= 1;
            if (exp == 0) {
                break;
            }

            k2p = Math.multiplyExact(k2p, k2p);
        }

        return result;
    }

    /**
     * Raise a long to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws IllegalArgumentException if {@code e < 0}.
     * @throws ArithmeticException if the result would overflow.
     */
    public static long pow(final long k,
                           final int  e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        int exp = e;
        long result = 1;
        long k2p    = k;
        while (true) {
            if ((exp & 0x1) != 0) {
                result = Math.multiplyExact(result, k2p);
            }

            exp >>= 1;
            if (exp == 0) {
                break;
            }

            k2p = Math.multiplyExact(k2p, k2p);
        }

        return result;
    }

    /**
     * Raise a BigInteger to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, int e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        return k.pow(e);
    }

    /**
     * Raise a BigInteger to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, final long e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        long exp = e;
        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (exp != 0) {
            if ((exp & 0x1) != 0) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            exp >>= 1;
        }

        return result;

    }

    /**
     * Raise a BigInteger to a BigInteger power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if {@code e < 0}.
     */
    public static BigInteger pow(final BigInteger k, final BigInteger e) {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        BigInteger exp = e;
        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (!BigInteger.ZERO.equals(exp)) {
            if (exp.testBit(0)) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            exp = exp.shiftRight(1);
        }

        return result;
    }

    /**
     * Returns an approximation to the nth root of the provided {@code bigDecimal}
     * with rounding according to the context settings.
     * <p>
     * The code is derived from Richard J. Mathar as described in his paper
     * "A Java Math.BigDecimal Implementation of Core Mathematical Functions".
     *
     * @see <a href="https://arxiv.org/abs/0908.3030v2">A Java Math.BigDecimal Implementation of Core Mathematical Functions</a>
     * @see <a href="https://arxiv.org/src/0908.3030v2/anc">Ancillary files</a>
     *
     * @param n the requested integral root.
     * @param bigDecimal the decimal number to
     * @param mc the context to use.
     * @return the nth root of the provided {@link BigDecimal}.
     * @throws NullPointerException if {@code bigDecimal} is null.
     * @throws ArithmeticException if {@code bigDecimal} is less than zero.
     */
    public static BigDecimal root(int n, BigDecimal bigDecimal, MathContext mc) {
        Objects.requireNonNull(bigDecimal);

        if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Attempted square root of negative BigDecimal: " + bigDecimal);
        }

        if (n <= 0) {
            throw new ArithmeticException("Attempted negative power for root: " + n);
        }

        if (n == 1) {
            return bigDecimal;
        }

        // start the computation from a double precision estimate.
        BigDecimal guess = new BigDecimal(Math.pow(bigDecimal.doubleValue(), 1.0 / n), mc);

        // this creates n as decimal number with a nominal precision of 1 digit.
        final BigDecimal nDecimal = new BigDecimal(n);

        // Relative accuracy of the result based on the requested precision.
        final BigDecimal eps = BigDecimal.valueOf(1, mc.getPrecision());

        // for temporary results, use a mathContext with a slightly larger
        // precision as the requested one.
        MathContext tmpMc = new MathContext(2 + mc.getPrecision(), RoundingMode.HALF_EVEN);

        // start with the initial guess with double precision.
        BigDecimal s = guess;

        for (;;) {
            // s = s - (s/n - x/n / s^(n-1)) = s - (s - x/s^(n-1))/n;
            BigDecimal correction = s.subtract(bigDecimal.divide(s.pow(n - 1), tmpMc), tmpMc).divide(nDecimal, tmpMc);
            s = s.subtract(correction, tmpMc);

            // test correction s/n - x/n/s^(n-1) for being smaller than the precision requested.
            // The relative correction is (1 - x/s^n)/n,
            BigDecimal relativeCorrection = correction.divide(s, tmpMc);
            if (relativeCorrection.abs().compareTo(eps) < 0) {
                break;
            }
        }

        return s.round(mc).stripTrailingZeros();
    }

    /**
     * Exception.
     */
    private static class NumbersArithmeticException extends ArithmeticException {
        /** Serializable version Id. */
        private static final long serialVersionUID = 20180130L;

        /**
         * Constructor with a specific message.
         *
         * @param message Message pattern providing the specific context of
         * the error.
         * @param args Arguments.
         */
        NumbersArithmeticException(String message, Object... args) {
            super(MessageFormat.format(message, args));
        }
    }
}
