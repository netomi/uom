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

import java.util.*;
import java.util.function.UnaryOperator;

public class Dimensions {

    private static final List<Dimension> baseDimensions = new ArrayList<>();

    private Dimensions() {}

    private static Dimension addDimension(Dimension dimension) {
        baseDimensions.add(dimension);
        return dimension;
    }

    public static final Dimension NONE = EnumDimension.NONE;

    public static final Dimension LENGTH = addDimension(EnumDimension.of(Base.LENGTH));

    public static final Dimension TIME = addDimension(EnumDimension.of(Base.TIME));

    public static final Dimension TEMPERATURE = addDimension(EnumDimension.of(Base.TEMPERATURE));

    public static final Dimension ELECTRIC_CURRENT = addDimension(EnumDimension.of(Base.ELECTRIC_CURRENT));

    public static final Dimension MASS = addDimension(EnumDimension.of(Base.MASS));

    public static final Dimension AMOUNT_OF_SUBSTANCE = addDimension(EnumDimension.of(Base.AMOUNT_OF_SUBSTANCE));

    public static final Dimension LUMINOUS_INTENSITY = addDimension(EnumDimension.of(Base.LUMINOUS_INTENSITY));

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

    private static class EnumDimension implements Dimension {

        private static final EnumDimension NONE = new EnumDimension();

        private final Map<Base, Fraction> dimensionMap;

        static Dimension of(Base dimension) {
            return new EnumDimension(dimension);
        }

        EnumDimension() {
            dimensionMap = new EnumMap<>(Base.class);
        }

        EnumDimension(Base dimension) {
            this();
            dimensionMap.put(dimension, Fraction.ONE);
        }

        private EnumDimension(EnumDimension other) {
            dimensionMap = new EnumMap<Base, Fraction>(other.dimensionMap);
        }

        private EnumDimension(EnumMap<Base, Fraction> map) {
            dimensionMap = map;
        }

        private EnumDimension copy() {
            return new EnumDimension(this);
        }

        @Override
        public Dimension multiply(Dimension multiplicand) {
            if (!(multiplicand instanceof EnumDimension)) {
                throw new IllegalArgumentException("incompatible dimension");
            }

            EnumDimension other = (EnumDimension) multiplicand;
            return new EnumDimension(combine(this.dimensionMap, other.dimensionMap, fraction -> fraction));
        }

        @Override
        public Dimension divide(Dimension divisor) {
            if (!(divisor instanceof EnumDimension)) {
                throw new IllegalArgumentException("incompatible dimension");
            }

            EnumDimension other = (EnumDimension) divisor;
            return new EnumDimension(combine(this.dimensionMap, other.dimensionMap, Fraction::negate));
        }

        private EnumMap<Base, Fraction> combine(Map<Base, Fraction> first, Map<Base, Fraction> second, UnaryOperator<Fraction> operator) {
            EnumMap<Base, Fraction> newMap = new EnumMap<>(first);

            for (Map.Entry<Base, Fraction> entry : second.entrySet()) {
                Fraction value = newMap.get(entry.getKey());

                if (value == null) {
                    value = operator.apply(entry.getValue());
                } else {
                    value = value.add(entry.getValue());
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
            if (n == 1) {
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
            return null;
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
                    sb.append('^');
                    sb.append(fraction.getNumerator());
                    if (fraction.getDenominator() != 1) {
                        sb.append('/');
                        sb.append(fraction.getDenominator());
                    }
                }
            }

            return sb.toString();
        }
    }

    public static List<Dimension> getBaseDimensions() {
        return Collections.unmodifiableList(baseDimensions);
    }

}
