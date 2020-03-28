/*
 * Units of Measurement API
 * Copyright (c) 2014-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385 nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.netomi.uom;

/**
 * Provides support for common binary prefixes to be used by units.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Binary_prefix">Wikipedia: Binary Prefix</a>
 */
public enum BinaryPrefix implements Prefix {
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

    /**
     * The symbol of this prefix, as returned by {@link #getSymbol}.
     *
     * @serial
     * @see #getSymbol()
     */
    private final String symbol;

    /**
     * Exponent part of the associated factor in base^exponent representation.
     */
    private final int exponent;

    /**
     * Creates a new prefix.
     *
     * @param symbol
     *          the symbol of this prefix.
     * @param exponent
     *          part of the associated factor in base^exponent representation.
     */
    private BinaryPrefix(String symbol, int exponent) {
        this.symbol = symbol;
        this.exponent = exponent;
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024</code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1024)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> KIBI(Unit<Q> unit) {
        return unit.withPrefix(KIBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>2</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1048576)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> MEBI(Unit<Q> unit) {
        return unit.withPrefix(MEBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>3</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1073741824)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> GIBI(Unit<Q> unit) {
        return unit.withPrefix(GIBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>4</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1099511627776L)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> TEBI(Unit<Q> unit) {
        return unit.withPrefix(TEBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>5</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1125899906842624L)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> PEBI(Unit<Q> unit) {
        return unit.withPrefix(PEBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>6</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1152921504606846976L)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> EXBI(Unit<Q> unit) {
        return unit.withPrefix(EXBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>7</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1152921504606846976d)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> ZEBI(Unit<Q> unit) {
        return unit.withPrefix(ZEBI);
    }

    /**
     * Returns the specified unit multiplied by the factor <code>1024<sup>8</sup></code> (binary prefix).
     *
     * @param <Q>
     *          type of the quantity measured by the unit.
     * @param unit
     *          any unit.
     * @return <code>unit.multiply(1208925819614629174706176d)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> YOBI(Unit<Q> unit) {
        return unit.withPrefix(YOBI);
    }

    /**
     * Returns the symbol of this prefix.
     *
     * @return this prefix symbol, not {@code null}.
     */
    @Override
    public String getSymbol() {
        return symbol;
    }

    /**
     * Base part of the associated factor in {@code base^exponent} representation. For binary prefix, this is always 1024.
     */
    @Override
    public Integer getValue() {
        return 1024;
    }

    /**
     * Exponent part of the associated factor in {@code base^exponent} representation.
     */
    @Override
    public int getExponent() {
        return exponent;
    }

    /**
     * Returns the name of this prefix.
     *
     * @return this prefix name, not {@code null}.
     */
    @Override
    public String getName() {
        return name();
    }

    @Override
    public BinaryPrefix fromExponent(int exponent) {
        for (BinaryPrefix prefix : values()) {
            if (prefix.getExponent() == exponent) {
                return prefix;
            }
        }

        return null;
    }
}
