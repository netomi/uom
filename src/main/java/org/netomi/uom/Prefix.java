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

/**
 * A unit prefix (in {@code base^exponent} form) that can be prepended to a {@link Unit} to indicate
 * multiples or fractions of the units.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Unit_prefix">Wikipedia: Unit Prefix</a>
 */
public interface Prefix {
    /**
     * Returns the name of this prefix.
     *
     * @return this prefix name, not {@code null}.
     */
    String getName();

    /**
     * Returns the symbol of this prefix.
     *
     * @return this prefix symbol, not {@code null}.
     */
    String getSymbol();

    /**
     * Returns the base of this prefix.
     * 
     * @return the base component of this prefix.
     */
    int getBase();

    /**
     * Returns the exponent of this prefix.
     *
     * @return the exponent component of this prefix.
     */
    int getExponent();

    /**
     * Returns a prefix instance with the same base but specified exponent.
     *
     * @param exponent the exponent to use.
     * @return a {@link Prefix} instance with the specified exponent value.
     */
    Prefix withExponent(int exponent);
}