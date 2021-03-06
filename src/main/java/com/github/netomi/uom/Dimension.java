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
package com.github.netomi.uom;

import com.github.netomi.uom.math.Fraction;
import com.github.netomi.uom.unit.Dimensions;

import java.util.Map;

/**
 * Represents a dimension of a unit for the purpose of dimensional analysis.
 * <p>
 * Implementations exist for 2 different kind of dimensions:
 * <ul>
 *     <li>physical dimensions</li>
 *     <li>custom dimensions</li>
 * </ul>
 * The different dimensions can be combined as needed, all of the predefined {@link Quantity}s and {@link Unit}s
 * are based on physical dimensions which are highly optimized.
 * <p>
 * The pre-defined physical base dimensions can be accessed via the {@link Dimensions} class.
 * <p>
 * Note: in general it is not needed and advised to extend this class.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Dimensional_analysis">Wikipedia: Dimensional analysis</a>
 *
 * @author Thomas Neidhart
 */
public abstract class Dimension {

    /**
     * Returns a new dimension that represents the multiplication of this dimension with the
     * specified dimension.
     * <p>
     * The mathematical formulation of this operation is:
     * <code>X<sup>n</sup> × X<sup>m</sup> = X<sup>n+m</sup></code>
     * for each base dimension X contained in the two dimensions.
     *
     * @param multiplicand the dimension to multiply with his dimension.
     * @return a new dimension representing the multiplication of this dimension with the other.
     */
    public Dimension multiply(Dimension multiplicand) {
        return multiplicand == Dimensions.NONE ?
                this :
                Dimensions.productOf(this, Fraction.ONE, multiplicand, Fraction.ONE);
    }

    /**
     * Returns a new dimension that represents the division of this dimension with the
     * specified dimension.
     * <p>
     * The mathematical formulation of this operation is:
     * <code>X<sup>n</sup> / X<sup>m</sup> = X<sup>n-m</sup></code>
     * for each base dimension X contained in the two dimensions.
     *
     * @param divisor the dimension to multiply with his dimension.
     * @return a new dimension representing the division of this dimension with the other.
     */
    public Dimension divide(Dimension divisor) {
        return divisor == Dimensions.NONE ?
                this :
                Dimensions.productOf(this, Fraction.ONE, divisor, Fraction.of(-1));
    }

    /**
     * Returns a new dimension that represents the nth power of this dimension.
     * <p>
     * The mathematical formulation of this operation is:
     * <code>pow(X<sup>n</sup>, m) = X<sup>n*m</sup></code>
     * for each base dimension X contained in this dimension.
     *
     * @param n the exponent for the power operation.
     * @return a new dimension representing the nth power of this dimension.
     */
    public Dimension pow(int n) {
        if (n == 1) {
            return this;
        }

        return Dimensions.powOf(this, Fraction.of(n));
    }

    /**
     * Returns a new dimension that represents the nth root of this dimension.
     * <p>
     * The mathematical formulation of this operation is:
     * <code>root(X<sup>n</sup>, m) = X<sup>n/m</sup></code>
     * for each base dimension X contained in this dimension.
     *
     * @param n the exponent for the root operation (must be a positive integer).
     * @return a new dimension representing the nth root of this dimension.
     * @throws IllegalArgumentException if n is not a positive integer.
     */
    public Dimension root(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be a positive integer.");
        }

        if (n == 1) {
            return this;
        }

        return Dimensions.powOf(this, Fraction.of(1, n));
    }

    /**
     * Returns a new {@link Map} containing mappings for each base dimension
     * of this dimension with their respective dimensional exponent represented
     * as fraction.
     * <p>
     * Mappings for base dimensions with a zero exponent are not containing in
     * the returned map.
     *
     * @return a new {@link Map} containing the base dimensions of this dimension.
     */
    public abstract Map<Dimension, Fraction> getBaseDimensions();

    /**
     * Any concrete implementation of a {@link Dimension} must properly implement
     * {@link Object#hashCode()}.
     */
    @Override
    public abstract int hashCode();

    /**
     * Any concrete implementation of a {@link Dimension} must properly implement
     * {@link Object#equals(Object)}.
     */
    @Override
    public abstract boolean equals(Object o);
}
