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
package org.netomi.uom.unit;

import org.netomi.uom.*;

/**
 * A utility class to provide access to different {@link Prefix} implementations.
 *
 * @author Thomas Neidhart
 */
public final class Prefixes {

    // Hide utility class.
    private Prefixes() {}

    /**
     * Provides support for the 20 prefixes used in the metric system.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Metric_prefix">Wikipedia: Metric Prefix</a>
     */
    public enum Metric implements Prefix {
        /** Prefix for 10<sup>24</sup>. */
        YOTTA("Y", 24),
        /** Prefix for 10<sup>21</sup>. */
        ZETTA("Z", 21),
        /** Prefix for 10<sup>18</sup>. */
        EXA("E", 18),
        /** Prefix for 10<sup>15</sup>. */
        PETA("P", 15),
        /** Prefix for 10<sup>12</sup>. */
        TERA("T", 12),
        /** Prefix for 10<sup>9</sup>. */
        GIGA("G", 9),
        /** Prefix for 10<sup>6</sup>. */
        MEGA("M", 6),
        /** Prefix for 10<sup>3</sup>. */
        KILO("k", 3),
        /** Prefix for 10<sup>2</sup>. */
        HECTO("h", 2),
        /** Prefix for 10<sup>1</sup>. */
        DEKA("da", 1),
        /** Prefix for 10<sup>-1</sup>. */
        DECI("d", -1),
        /** Prefix for 10<sup>-2</sup>. */
        CENTI("c", -2),
        /** Prefix for 10<sup>-3</sup>. */
        MILLI("m", -3),
        /** Prefix for 10<sup>-6</sup>. */
        MICRO("\u00b5", -6),
        /** Prefix for 10<sup>-9</sup>. */
        NANO("n", -9),
        /** Prefix for 10<sup>-12</sup>. */
        PICO("p", -12),
        /** Prefix for 10<sup>-15</sup>. */
        FEMTO("f", -15),
        /** Prefix for 10<sup>-18</sup>. */
        ATTO("a", -18),
        /** Prefix for 10<sup>-21</sup>. */
        ZEPTO("z", -21),
        /** Prefix for 10<sup>-24</sup>. */
        YOCTO("y", -24);

        private final String symbol;
        private final int    exponent;

        Metric(String symbol, int exponent) {
            this.symbol   = symbol;
            this.exponent = exponent;
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>24</sup></code>.
         * @return {@code unit.multiply(1e24)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> YOTTA(Unit<Q> unit) {
            return unit.withPrefix(YOTTA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>21</sup></code>
         * @return {@code unit.multiply(1e21)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> ZETTA(Unit<Q> unit) {
            return unit.withPrefix(ZETTA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>18</sup></code>
         * @return {@code unit.multiply(1e18)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> EXA(Unit<Q> unit) {
            return unit.withPrefix(EXA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>15</sup></code>
         * @return {@code unit.multiply(1e15)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> PETA(Unit<Q> unit) {
            return unit.withPrefix(PETA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>12</sup></code>
         * @return {@code unit.multiply(1e12)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> TERA(Unit<Q> unit) {
            return unit.withPrefix(TERA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>9</sup></code>
         * @return {@code unit.multiply(1e9)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> GIGA(Unit<Q> unit) {
            return unit.withPrefix(GIGA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>6</sup></code>
         * @return {@code unit.multiply(1e6)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> MEGA(Unit<Q> unit) {
            return unit.withPrefix(MEGA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>3</sup></code>
         * @return {@code unit.multiply(1e3)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> KILO(Unit<Q> unit) {
            return unit.withPrefix(KILO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>2</sup></code>
         * @return {@code unit.multiply(1e2)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> HECTO(Unit<Q> unit) {
            return unit.withPrefix(HECTO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>1</sup></code>
         * @return {@code unit.multiply(1e1)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> DEKA(Unit<Q> unit) {
            return unit.withPrefix(DEKA);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-1</sup></code>
         * @return {@code unit.multiply(1e-1)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> DECI(Unit<Q> unit) {
            return unit.withPrefix(DECI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-2</sup></code>
         * @return {@code unit.multiply(1e-2)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> CENTI(Unit<Q> unit) {
            return unit.withPrefix(CENTI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-3</sup></code>
         * @return {@code unit.multiply(1e-3)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> MILLI(Unit<Q> unit) {
            return unit.withPrefix(MILLI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-6</sup></code>
         * @return {@code unit.multiply(1e-6)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> MICRO(Unit<Q> unit) {
            return unit.withPrefix(MICRO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-9</sup></code>
         * @return {@code unit.multiply(1e-9)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> NANO(Unit<Q> unit) {
            return unit.withPrefix(NANO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-12</sup></code>
         * @return {@code unit.multiply(1e-12)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> PICO(Unit<Q> unit) {
            return unit.withPrefix(PICO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-15</sup></code>
         * @return {@code unit.multiply(1e-15)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> FEMTO(Unit<Q> unit) {
            return unit.withPrefix(FEMTO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-18</sup></code>
         * @return {@code unit.multiply(1e-18)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> ATTO(Unit<Q> unit) {
            return unit.withPrefix(ATTO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-21</sup></code>
         * @return {@code unit.multiply(1e-21)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> ZEPTO(Unit<Q> unit) {
            return unit.withPrefix(ZEPTO);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>10<sup>-24</sup></code>
         * @return {@code unit.multiply(1e-24)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> YOCTO(Unit<Q> unit) {
            return unit.withPrefix(YOCTO);
        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public int getBase() {
            return 10;
        }

        @Override
        public int getExponent() {
            return exponent;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public Prefix withExponent(int exponent) {
            for (Metric prefix : values()) {
                if (prefix.getExponent() == exponent) {
                    return prefix;
                }
            }

            return new GenericPrefixImpl(this, getBase(), exponent);
        }
    }

    /**
     * Provides support for common binary prefixes to be used by units.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Binary_prefix">Wikipedia: Binary Prefix</a>
     */
    public enum Binary implements Prefix {
        /** Prefix for 1024. */
        KIBI("Ki", 1),
        /** Prefix for 1024<sup>2</sup>. */
        MEBI("Mi", 2),
        /** Prefix for 1024<sup>3</sup>. */
        GIBI("Gi", 3),
        /** Prefix for 1024<sup>4</sup>. */
        TEBI("Ti", 4),
        /** Prefix for 1024<sup>5</sup>. */
        PEBI("Pi", 5),
        /** Prefix for 1024<sup>6</sup>. */
        EXBI("Ei", 6),
        /** Prefix for 1024<sup>7</sup>. */
        ZEBI("Zi", 7),
        /** Prefix for 1024<sup>8</sup>. */
        YOBI("Yi", 8);

        private final String symbol;
        private final int    exponent;

        Binary(String symbol, int exponent) {
            this.symbol   = symbol;
            this.exponent = exponent;
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024</code> (binary prefix).
         * @return {@code unit.multiply(1024)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> KIBI(Unit<Q> unit) {
            return unit.withPrefix(KIBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>2</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^2)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> MEBI(Unit<Q> unit) {
            return unit.withPrefix(MEBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>3</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^3)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> GIBI(Unit<Q> unit) {
            return unit.withPrefix(GIBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>4</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^4)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> TEBI(Unit<Q> unit) {
            return unit.withPrefix(TEBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>5</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^5)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> PEBI(Unit<Q> unit) {
            return unit.withPrefix(PEBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>6</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^6)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> EXBI(Unit<Q> unit) {
            return unit.withPrefix(EXBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>7</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^7)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> ZEBI(Unit<Q> unit) {
            return unit.withPrefix(ZEBI);
        }

        /**
         * Returns the specified unit multiplied by the factor <code>1024<sup>8</sup></code> (binary prefix).
         * @return {@code unit.multiply(1024^8)}.
         */
        public static <Q extends Quantity<Q>> Unit<Q> YOBI(Unit<Q> unit) {
            return unit.withPrefix(YOBI);
        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public int getBase() {
            return 1024;
        }

        @Override
        public int getExponent() {
            return exponent;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public Prefix withExponent(int exponent) {
            for (Binary prefix : values()) {
                if (prefix.getExponent() == exponent) {
                    return prefix;
                }
            }

            return new GenericPrefixImpl(this, getBase(), exponent);
        }
    }

    /**
     * Private helper class to represent a generic non-named prefix.
     */
    private static class GenericPrefixImpl implements Prefix {

        private final Prefix reference;
        private final int    base;
        private final int    exponent;

        private GenericPrefixImpl(Prefix reference, int base, int exponent) {
            this.reference = reference;
            this.base      = base;
            this.exponent  = exponent;
        }

        @Override
        public String getName() {
            return String.format("%d^%d*", base, exponent);
        }

        @Override
        public String getSymbol() {
            return getName();
        }

        @Override
        public int getBase() {
            return base;
        }

        @Override
        public int getExponent() {
            return exponent;
        }

        @Override
        public Prefix withExponent(int exponent) {
            return reference.withExponent(exponent);
        }
    }
}
