/*
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
     * Returns a prefix with the same base but given exponent.
     *
     * @param exponent the exponent of the prefix.
     * @return a {@link Prefix} with the specified exponent.
     */
    Prefix withExponent(int exponent);

    /**
     * Returns a {@link UnitConverter} instance representing this prefix.
     *
     * @return a unit converter representing this prefix.
     */
    UnitConverter getUnitConverter();
}