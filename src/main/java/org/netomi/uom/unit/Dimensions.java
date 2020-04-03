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

import org.netomi.uom.math.Fraction;
import org.netomi.uom.util.StringUtil;

import java.lang.ref.WeakReference;
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

    /**
     * A {@link Dimension} to represent dimensionless quantities / units.
     */
    public static final Dimension NONE = EnumDimension.empty();

    /**
     * The {@link Dimension} to represent quantities of type length.
     */
    public static final Dimension LENGTH = addBaseDimension(Base.LENGTH);

    /**
     * The {@link Dimension} to represent quantities of type time.
     */
    public static final Dimension TIME = addBaseDimension(Base.TIME);

    /**
     * The {@link Dimension} to represent quantities of type temperature.
     */
    public static final Dimension TEMPERATURE = addBaseDimension(Base.TEMPERATURE);

    /**
     * The {@link Dimension} to represent quantities of type electric current.
     */
    public static final Dimension ELECTRIC_CURRENT = addBaseDimension(Base.ELECTRIC_CURRENT);

    /**
     * The {@link Dimension} to represent quantities of type mass.
     */
    public static final Dimension MASS = addBaseDimension(Base.MASS);

    /**
     * The {@link Dimension} to represent quantities of type amount of substance.
     */
    public static final Dimension AMOUNT_OF_SUBSTANCE = addBaseDimension(Base.AMOUNT_OF_SUBSTANCE);

    /**
     * The {@link Dimension} to represent quantities of type luminous intensity.
     */
    public static final Dimension LUMINOUS_INTENSITY = addBaseDimension(Base.LUMINOUS_INTENSITY);


    // Hide utility class constructor.
    private Dimensions() {}

    private static Dimension addBaseDimension(Base baseDimension) {
        Dimension dimension = EnumDimension.of(baseDimension);
        baseDimensions.put(baseDimension, dimension);
        return dimension;
    }

    static Dimension getBaseDimension(Base baseDimension) {
        return baseDimensions.get(baseDimension);
    }

    /**
     * Returns an unmodifiable {@link Collection} containing all supported base dimensions.
     */
    public static Collection<Dimension> getBaseDimensions() {
        return Collections.unmodifiableCollection(baseDimensions.values());
    }

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
     * An efficient implementation of a {@link Dimension} using an {@link EnumMap}
     * that stores the fraction for each base dimension.
     * <p>
     * Created dimensions are cached in a synchronized {@link WeakHashMap} to avoid
     * creating too many dimension instances. This implementation guarantees that
     * for base dimensions (e.g. the result of operations like {@link #multiply(Dimension)})
     * always the same instance is returned as these dimension have a strong reference in the
     * {@link Dimensions} class and will never be garbage collected.
     */
    private static class EnumDimension extends Dimension {

        private static final Map<EnumMap, WeakReference<Dimension>> dimensionCache =
                Collections.synchronizedMap(new WeakHashMap<>());

        private final EnumMap<Base, Fraction> dimensionMap;

        static Dimension empty() {
            return new EnumDimension(new EnumMap<>(Base.class));
        }

        static Dimension of(Base baseDimension) {
            return new EnumDimension(baseDimension);
        }

        static Dimension of(EnumMap<Base, Fraction> map) {
            Dimension cachedDimension = getCachedDimension(map);
            if (cachedDimension != null) {
                return cachedDimension;
            }

            return new EnumDimension(map);
        }

        private static Dimension getCachedDimension(EnumMap<Base, Fraction> map) {
            WeakReference<Dimension> reference = dimensionCache.get(map);
            if (reference != null) {
                return reference.get();
            }

            return null;
        }

        private static void putDimensionIntoCache(EnumDimension dimension) {
            dimensionCache.put(dimension.dimensionMap, new WeakReference<>(dimension));
        }

        EnumDimension(Base baseDimension) {
            dimensionMap = new EnumMap<>(Base.class);
            dimensionMap.put(baseDimension, Fraction.ONE);

            putDimensionIntoCache(this);
        }

        EnumDimension(Map<Base, Fraction> map) {
            dimensionMap = new EnumMap<>(map);

            putDimensionIntoCache(this);
        }

        @Override
        public Dimension multiply(Dimension multiplicand) {
            Objects.requireNonNull(multiplicand);

            // Optimization: NONE * anything = anything
            if (this == NONE) {
                return multiplicand;
            }

            // the other instance must be of type EnumDimension.
            EnumDimension that = (EnumDimension) multiplicand;
            return of(combine(this.dimensionMap, that.dimensionMap, fraction -> fraction, Fraction::add));
        }

        @Override
        public Dimension divide(Dimension divisor) {
            Objects.requireNonNull(divisor);

            // the other instance must be of type EnumDimension.
            EnumDimension that = (EnumDimension) divisor;
            return of(combine(this.dimensionMap, that.dimensionMap, Fraction::negate, Fraction::subtract));
        }

        private EnumMap<Base, Fraction> combine(EnumMap<Base, Fraction>  first,
                                                EnumMap<Base, Fraction>  second,
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

            EnumMap<Base, Fraction> newMap = new EnumMap<>(this.dimensionMap);

            for (Map.Entry<Base, Fraction> entry : dimensionMap.entrySet()) {
                Fraction value = newMap.get(entry.getKey());
                value = value.multiply(n);
                newMap.put(entry.getKey(), value);
            }

            return of(newMap);
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

            EnumMap<Base, Fraction> newMap = new EnumMap<>(this.dimensionMap);

            for (Map.Entry<? extends Base, Fraction> entry : dimensionMap.entrySet()) {
                Fraction value = newMap.get(entry.getKey());
                value = value.multiply(Fraction.of(1, n));
                newMap.put(entry.getKey(), value);
            }

            return of(newMap);
        }

        @Override
        public Map<Dimension, Fraction> getBaseDimensions() {
            Map<Dimension, Fraction> baseDimensionMap = new HashMap<>();

            for (Map.Entry<? extends Base, Fraction> entry : dimensionMap.entrySet()) {
                baseDimensionMap.put(getBaseDimension(entry.getKey()), entry.getValue());
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
                sb.append(entry.getKey().getSymbol());
                Fraction fraction = entry.getValue();
                if (Fraction.ONE.compareTo(fraction) != 0) {
                    StringUtil.appendUnicodeString(fraction, sb);
                }
            }

            return sb.toString();
        }
    }
}
