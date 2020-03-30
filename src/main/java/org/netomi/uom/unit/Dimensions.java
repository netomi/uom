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

import org.netomi.uom.Dimension;
import org.netomi.uom.math.Fraction;
import org.netomi.uom.util.StringUtil;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * A utility class to access the supported set of {@link Dimension} instances.
 * <p>
 * {@link org.netomi.uom.SystemOfUnits} implementations may use a distinct subset of
 * these dimensions, but may not define their own set of dimensions. If a new dimension
 * is needed, this class should be extended accordingly.
 *
 * @author Thomas Neidhart
 */
public class Dimensions {

    private static final Map<Base, Dimension> baseDimensions = new EnumMap<>(Base.class);

    // Hide utility class constructor.
    private Dimensions() {}

    private static Dimension addDimension(Base baseDimension) {
        Dimension dimension = EnumDimension.of(baseDimension);
        baseDimensions.put(baseDimension, dimension);
        return dimension;
    }

    private static Dimension getDimension(Base baseDimension) {
        return baseDimensions.get(baseDimension);
    }

    /**
     * Returns an unmodifiable {@link Collection} containing all supported base dimensions.
     */
    public static Collection<Dimension> getBaseDimensions() {
        return Collections.unmodifiableCollection(baseDimensions.values());
    }

    /**
     * A {@link Dimension} to represent dimensionless quantities / units.
     */
    public static final Dimension NONE = EnumDimension.none();

    /**
     * The {@link Dimension} to represent quantities of type length.
     */
    public static final Dimension LENGTH = addDimension(Base.LENGTH);

    /**
     * The {@link Dimension} to represent quantities of type time.
     */
    public static final Dimension TIME = addDimension(Base.TIME);

    /**
     * The {@link Dimension} to represent quantities of type temperature.
     */
    public static final Dimension TEMPERATURE = addDimension(Base.TEMPERATURE);

    /**
     * The {@link Dimension} to represent quantities of type electric current.
     */
    public static final Dimension ELECTRIC_CURRENT = addDimension(Base.ELECTRIC_CURRENT);

    /**
     * The {@link Dimension} to represent quantities of type mass.
     */
    public static final Dimension MASS = addDimension(Base.MASS);

    /**
     * The {@link Dimension} to represent quantities of type amount of substance.
     */
    public static final Dimension AMOUNT_OF_SUBSTANCE = addDimension(Base.AMOUNT_OF_SUBSTANCE);

    /**
     * The {@link Dimension} to represent quantities of type luminous intensity.
     */
    public static final Dimension LUMINOUS_INTENSITY = addDimension(Base.LUMINOUS_INTENSITY);

    /**
     * An enum containing supported base dimensions.
     */
    private enum Base {

        LENGTH('L'),
        MASS('M'),
        TIME('T'),
        ELECTRIC_CURRENT('I'),
        TEMPERATURE('\u0398'),
        AMOUNT_OF_SUBSTANCE('N'),
        LUMINOUS_INTENSITY('J');

        private final char symbol;

        Base(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    /**
     * An immutable implementation of a {@link Dimension} using an {@link EnumMap} to
     * keep track the base dimensions and their corresponding exponent which comprises
     * this dimension.
     * <p>
     * A dimension is represented in the form:
     *
     * <pre>
     *     dim Q = L<sup>a</sup>M<sup>b</sup>T<sup>c</sup>I<sup>d</sup>Θ<sup>e</sup>N<sup>f</sup>J<sup>g</sup>
     * </pre>
     *
     * whereas each exponent is represented as a fraction.
     */
    private static class EnumDimension implements Dimension {

        private final Map<Base, Fraction> dimensionMap;

        static Dimension none() {
            return new EnumDimension();
        }

        static Dimension of(Base dimension) {
            return new EnumDimension(dimension);
        }

        static Dimension of(EnumMap<Base, Fraction> map) {
            return map.isEmpty() ?
                    Dimensions.NONE :
                    new EnumDimension(map);
        }

        private EnumDimension() {
            dimensionMap = new EnumMap<>(Base.class);
        }

        private EnumDimension(Base dimension) {
            this();
            dimensionMap.put(dimension, Fraction.ONE);
        }

        private EnumDimension(EnumDimension other) {
            dimensionMap = new EnumMap<>(other.dimensionMap);
        }

        private EnumDimension(Map<Base, Fraction> map) {
            dimensionMap = map;
        }

        private EnumDimension copy() {
            return new EnumDimension(this);
        }

        @Override
        public Dimension multiply(Dimension multiplicand) {
            Objects.requireNonNull(multiplicand);

            if (!(multiplicand instanceof EnumDimension)) {
                throw new UnsupportedDimensionException(UnsupportedDimensionException.ERROR_UNSUPPORTED_DIMENSION,
                                                        multiplicand.getClass());
            }

            // Optimization: NONE * anything = anything
            if (this == NONE) {
                return multiplicand;
            }

            EnumDimension other = (EnumDimension) multiplicand;
            return of(combine(this.dimensionMap, other.dimensionMap, fraction -> fraction, Fraction::add));
        }

        @Override
        public Dimension divide(Dimension divisor) {
            Objects.requireNonNull(divisor);

            if (!(divisor instanceof EnumDimension)) {
                throw new UnsupportedDimensionException(UnsupportedDimensionException.ERROR_UNSUPPORTED_DIMENSION,
                                                        divisor.getClass());
            }

            EnumDimension other = (EnumDimension) divisor;
            return of(combine(this.dimensionMap, other.dimensionMap, Fraction::negate, Fraction::subtract));
        }

        private EnumMap<Base, Fraction> combine(Map<Base, Fraction>      first,
                                                Map<Base, Fraction>      second,
                                                UnaryOperator<Fraction>  absentOperator,
                                                BinaryOperator<Fraction> presentOperator) {
            EnumMap<Base, Fraction> newMap = new EnumMap<>(first);

            for (Map.Entry<Base, Fraction> entry : second.entrySet()) {
                Fraction value = newMap.get(entry.getKey());

                if (value == null) {
                    value = absentOperator.apply(entry.getValue());
                } else {
                    value = presentOperator.apply(value, entry.getValue());
                }

                if (Fraction.ZERO.compareTo(value) == 0) {
                    newMap.remove(entry.getKey());
                } else {
                    newMap.put(entry.getKey(), value);
                }
            }

            return newMap;
        }

        @Override
        public Dimension pow(int n) {
            if (n == 1) {
                return this;
            }

            if (this == NONE) {
                return this;
            }

            EnumDimension newDimension = this.copy();

            for (Map.Entry<Base, Fraction> entry : dimensionMap.entrySet()) {
                Fraction value = newDimension.dimensionMap.get(entry.getKey());
                value = value.multiply(n);
                newDimension.dimensionMap.put(entry.getKey(), value);
            }

            return newDimension;
        }

        @Override
        public Dimension root(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("N must be a positive integer.");
            }

            if (n == 1) {
                return this;
            }

            if (this == NONE) {
                return this;
            }

            EnumDimension newDimension = this.copy();

            for (Map.Entry<Base, Fraction> entry : dimensionMap.entrySet()) {
                Fraction value = newDimension.dimensionMap.get(entry.getKey());
                value = value.multiply(Fraction.of(1, n));
                newDimension.dimensionMap.put(entry.getKey(), value);
            }

            return newDimension;
        }

        @Override
        public Map<Dimension, Fraction> getBaseDimensions() {
            Map<Dimension, Fraction> baseDimensionMap = new HashMap<>();

            for (Map.Entry<Base, Fraction> entry : dimensionMap.entrySet()) {
                baseDimensionMap.put(getDimension(entry.getKey()), entry.getValue());
            }

            return baseDimensionMap;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimensionMap);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EnumDimension that = (EnumDimension) o;
            return Objects.equals(dimensionMap, that.dimensionMap);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<Base, Fraction> entry : dimensionMap.entrySet()) {
                sb.append(entry.getKey().symbol);
                Fraction fraction = entry.getValue();
                if (Fraction.ONE.compareTo(fraction) != 0) {
                    StringUtil.appendUnicodeString(fraction, sb);
                }
            }

            return sb.toString();
        }
    }

    private static class UnsupportedDimensionException extends RuntimeException {

        /** Error message for overflow during conversion. */
        public static final String ERROR_UNSUPPORTED_DIMENSION = "Dimension of type '{0}' not supported.";

        /** Serializable version identifier. */
        private static final long serialVersionUID = 20200320L;

        /**
         * Create an exception where the message is constructed by applying
         * the {@code format()} method from {@code java.text.MessageFormat}.
         *
         * @param message         the exception message with replaceable parameters.
         * @param formatArguments the arguments for formatting the message.
         */
        private UnsupportedDimensionException(String message, Object... formatArguments) {
            super(MessageFormat.format(message, formatArguments));
        }

    }
}
