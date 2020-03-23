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

import org.netomi.uom.function.UnitConverters;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * An interface to support conversion of numbers expressed in different units.
 * 
 * @see Unit#getConverterTo(Unit)
 * @author Thomas Neidhart
 */
public interface UnitConverter {

    /**
     * Returns whether the given {@code UnitConverter} instance performs
     * an identity conversion.
     *
     * @return {@code true} if this is an identity conversion, {@code false} otherwise.
     */
    default boolean isIdentity() {
        return false;
    }

    /**
     * Returns a {@code UnitConverter} instance that is the inverse of this
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
    default BigDecimal convert(BigDecimal value) {
        return convert(value, MathContext.DECIMAL128);
    }

    /**
     * Converts the given decimal value with using the
     * specified {@code MathContext}.
     *
     * @param value    the decimal value to convert.
     * @param context  the {@code MathContext} to use.
     * @return the converted value.
     */
    BigDecimal convert(BigDecimal value, MathContext context);

    /**
     * Returns a {@code UnitConverter} that concatenates this
     * converter with another one.
     *
     * The resulting converter first uses the right converter
     * and applies the result on the left converter such that:
     * {@code right.convert(left.convert(value))}.
     *
     * @param that the converter to concatente with this converter.
     * @return the concatenation of this converter with the other converter.
     */
    default UnitConverter concatenate(UnitConverter that) {
        return UnitConverters.concatenate(this, that);
    }
}
