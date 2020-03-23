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
 * Utility class to access various unit converter implementations.
 *
 * @author Thomas Neidhart
 */
public class UnitConverters {

    private static final UnitConverter IDENTITY = new IdentityConverter();

    private UnitConverters() {}

    public static UnitConverter identity() {
        return IDENTITY;
    }

    public static UnitConverter shift(double offset) {
        return shift(BigDecimal.valueOf(offset));
    }

    public static UnitConverter shift(BigDecimal offset) {
        return BigDecimal.ZERO.compareTo(offset) == 0 ? identity() : new AddConverter(offset);
    }

    public static UnitConverter multiply(double multiplicand) {
        return multiply(BigDecimal.valueOf(multiplicand));
    }

    public static UnitConverter multiply(BigDecimal multiplicand) {
        return BigDecimal.ONE.compareTo(multiplicand) == 0 ? identity() : new MultiplyConverter(multiplicand);
    }

    public static UnitConverter multiply(long dividend, long divisor) {
        return dividend == divisor ? identity() : new RationalConverter(dividend, divisor);
    }

    public static UnitConverter concatenate(UnitConverter left, UnitConverter right) {
        if (left.isIdentity()) {
            return right;
        } else if (right.isIdentity()) {
            return left;
        }

        return new PairConverter(left, right);
    }

    // helper methods

//    long gcd(long n1, long n2) {
//        if (n1 == 0) {
//            return n2;
//        }
//
//        if (n2 == 0) {
//            return n1;
//        }
//
//        long n;
//        for (n = 0; ((n1 | n2) & 1) == 0; n++) {
//            n1 >>= 1;
//            n2 >>= 1;
//        }
//
//        while ((n1 & 1) == 0) {
//            n1 >>= 1;
//        }
//
//        do {
//            while ((n2 & 1) == 0) {
//                n2 >>= 1;
//            }
//
//            if (n1 > n2) {
//                long temp = n1;
//                n1 = n2;
//                n2 = temp;
//            }
//            n2 = (n2 - n1);
//        } while (n2 != 0);
//        return n1 << n;
//    }

    // Inner helper classes

    private static class IdentityConverter implements UnitConverter {

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public UnitConverter concatenate(UnitConverter that) {
            return that;
        }

        @Override
        public UnitConverter inverse() {
            return IDENTITY;
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
            return "IdentityConverter";
        }
    }

    private static class PairConverter implements UnitConverter {
        private final UnitConverter left;
        private final UnitConverter right;

        PairConverter(UnitConverter left, UnitConverter right) {
            this.left  = left;
            this.right = right;
        }

        @Override
        public boolean isIdentity() {
            return left.isIdentity() && right.isIdentity();
        }

        @Override
        public UnitConverter inverse() {
            return new PairConverter(right.inverse(), left.inverse());
        }

        @Override
        public double convert(double value) {
            return right.convert(left.convert(value));
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext context) {
            return left.convert(right.convert(value, context), context);
        }

        @Override
        public String toString() {
            return String.format("PairConverter[left=%s, right=%s]", left, right);
        }
    }
}
