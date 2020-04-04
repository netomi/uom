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
 * An efficient implementation of a {@link Dimension} using an {@link EnumMap}
 * that stores the fraction for each physical dimension (see {@link Base} for
 * the list of base dimension types).
 * <p>
 * Created dimensions are cached in a synchronized {@link WeakHashMap} to avoid
 * creating too many dimension instances. This implementation guarantees that
 * for base dimensions (e.g. the result of operations like {@link #multiply(Dimension)})
 * always the same instance is returned as these dimension have a strong reference in the
 * {@link Dimensions} class and will never be garbage collected.
 *
 * @author Thomas Neidhart
 */
class PhysicalDimension extends Dimension {

    /**
     * An enum containing supported base dimensions.
     */
    enum Base {
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
     * A cache for {@link Dimension} instances. Wrap values into a weak
     * reference as the keys are contained in the values, creating a
     * circular reference which would prevent the keys from being collected.
     */
    private static final Map<EnumMap<?, ?>, WeakReference<Dimension>> dimensionCache =
            Collections.synchronizedMap(new WeakHashMap<>());

    private final EnumMap<Base, Fraction> dimensionMap;

    static Dimension empty() {
        return new PhysicalDimension(new EnumMap<>(Base.class));
    }

    static Dimension of(Base baseDimension) {
        return new PhysicalDimension(baseDimension);
    }

    static Dimension of(EnumMap<Base, Fraction> map) {
        Dimension cachedDimension = getCachedDimension(map);
        return cachedDimension != null ? cachedDimension : new PhysicalDimension(map);
    }

    private static Dimension getCachedDimension(EnumMap<Base, Fraction> map) {
        WeakReference<Dimension> reference = dimensionCache.get(map);
        return reference != null ? reference.get() : null;
    }

    private static void putDimensionIntoCache(PhysicalDimension dimension) {
        dimensionCache.put(dimension.dimensionMap, new WeakReference<>(dimension));
    }

    PhysicalDimension(Base baseDimension) {
        dimensionMap = new EnumMap<>(Base.class);
        dimensionMap.put(baseDimension, Fraction.ONE);

        putDimensionIntoCache(this);
    }

    PhysicalDimension(Map<Base, Fraction> map) {
        dimensionMap = new EnumMap<>(map);

        putDimensionIntoCache(this);
    }

    @Override
    public Dimension multiply(Dimension multiplicand) {
        Objects.requireNonNull(multiplicand);

        // Optimization: NONE * anything = anything
        if (this == Dimensions.NONE || dimensionMap.isEmpty()) {
            return multiplicand;
        }

        if (!(multiplicand instanceof PhysicalDimension)) {
            return super.multiply(multiplicand);
        }

        // the other instance must be of type EnumDimension.
        PhysicalDimension that = (PhysicalDimension) multiplicand;
        return of(combine(this.dimensionMap, that.dimensionMap, fraction -> fraction, Fraction::add));
    }

    @Override
    public Dimension divide(Dimension divisor) {
        Objects.requireNonNull(divisor);

        if (!(divisor instanceof PhysicalDimension)) {
            return super.multiply(divisor);
        }

        // the other instance must be of type EnumDimension.
        PhysicalDimension that = (PhysicalDimension) divisor;
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

        if (this == Dimensions.NONE || dimensionMap.isEmpty()) {
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
            throw new IllegalArgumentException("n must be a positive integer.");
        }

        if (n == 1) {
            return this;
        }

        if (this == Dimensions.NONE || dimensionMap.isEmpty()) {
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
        switch (dimensionMap.size()) {
            case 0:
                return Collections.emptyMap();

            case 1:
                Map.Entry<Base, Fraction> firstEntry = dimensionMap.entrySet().iterator().next();
                return Collections.singletonMap(Dimensions.getPhysicalBaseDimension(firstEntry.getKey()),
                                                firstEntry.getValue());

            default:
                Map<Dimension, Fraction> baseDimensionMap = new LinkedHashMap<>(dimensionMap.size());
                for (Map.Entry<? extends Base, Fraction> entry : dimensionMap.entrySet()) {
                    baseDimensionMap.put(Dimensions.getPhysicalBaseDimension(entry.getKey()),
                                         entry.getValue());
                }
                return baseDimensionMap;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimensionMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhysicalDimension that = (PhysicalDimension) o;
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
