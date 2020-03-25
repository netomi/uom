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
import org.netomi.uom.util.BigFraction;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Utility class to access various unit converter implementations.
 *
 * @author Thomas Neidhart
 */
public class UnitConverters {

    private static final UnitConverter IDENTITY = new IdentityConverter();

    // hide constructor of a pure utility class.
    private UnitConverters() {}

    /**
     * Returns an identity converter.
     */
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
        return multiply(BigFraction.from(multiplicand));
    }

    public static UnitConverter multiply(BigDecimal multiplicand) {
        return multiply(BigFraction.from(multiplicand));
    }

    public static UnitConverter multiply(long numerator, long denominator) {
        return numerator == denominator ? identity() : multiply(BigFraction.of(numerator, denominator));
    }

    static UnitConverter multiply(BigFraction multiplicand) {
        return BigFraction.ONE.compareTo(multiplicand) == 0 ? identity() : new MultiplyConverter(multiplicand);
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
    private static class IdentityConverter implements UnitConverter {

        @Override
        public boolean isIdentity() {
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
        public String toString() {
            return String.format("ComposeConverter[before=%s, after=%s]", before, after);
        }
    }
}
