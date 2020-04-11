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
package org.netomi.uom;

import org.netomi.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

/**
 * An interface to support conversion of numbers expressed in different units.
 * 
 * @see Unit#getConverterTo(Unit)
 * @author Thomas Neidhart
 */
public interface UnitConverter {

    /**
     * Returns whether the given {@link UnitConverter} instance performs
     * an identity conversion.
     *
     * @return {@code true} if this is an identity conversion, {@code false} otherwise.
     */
    default boolean isIdentity() {
        return false;
    }

    /**
     * Returns whether this {@link UnitConverter} instance is linear.
     * <p>
     * Non-linear converters cannot be concatenated with root operations.
     *
     * @return {@code true} if this converter is linear, false otherwise.
     */
    boolean isLinear();

    /**
     * Returns the scale of this {@link UnitConverter} represented as a fraction
     * if this is a linear converter, otherwise {@link Optional#empty()} is returned.
     */
    Optional<BigFraction> scale();

    /**
     * Returns a {@link UnitConverter} instance that is the inverse of this
     * instance, such that {@code x == inverse().convert(convert(x))} holds true.
     *
     * @return an inverse converter of this instance
     */
    UnitConverter inverse();

    /**
     * Converts the given double value.
     *
     * @param value the double value to convert.
     * @return the converted value.
     */
    double convert(double value);

    /**
     * Converts the given decimal value using a
     * {@code MathContext#DECIMAL128} context.
     *
     * @param value the decimal value to convert.
     * @return the converted value.
     */
    BigDecimal convert(BigDecimal value);

    /**
     * Converts the given decimal value with using the
     * specified {@link MathContext}.
     *
     * @param value    the decimal value to convert.
     * @param context  the {@link MathContext} to use.
     * @return the converted value.
     */
    BigDecimal convert(BigDecimal value, MathContext context);

    /**
     * Returns a composed {@link UnitConverter} that first applies the @{code before}
     * converter to its input, and then applies this converter to the result.
     *
     * The returned converter produces the same output as calling
     * {@code this.convert(before.convert(value))}.
     *
     * @param before the converter to apply before this converter is applied.
     * @return a composed converter that first applies the before converter and then applies this converter.
     */
    UnitConverter compose(UnitConverter before);

    /**
     * Returns a composed {@link UnitConverter} that first applies this converter
     * to its input, and then applies the after converter to the result.
     *
     * The returned converter produces the same output as calling
     * {@code after.convert(this.convert(value))}.
     *
     * @param after the converter to apply after this converter is applied.
     * @return a composed converter that first applies this converter and then applies the after converter.
     */
    UnitConverter andThen(UnitConverter after);
}
