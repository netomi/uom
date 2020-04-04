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

import java.util.*;

/**
 * A {@link Dimension} implementation to represent products of
 * general {@link Dimension} instances.
 *
 * @author Thomas Neidhart
 */
class ProductDimension extends Dimension {

    static class DimensionElement {
        private final Dimension dimension;
        private final Fraction  fraction;

        DimensionElement(Dimension dim, Fraction fraction) {
            this.dimension = dim;
            this.fraction  = fraction;
        }

        Dimension getDimension() {
            return dimension;
        }

        Fraction getFraction() {
            return fraction;
        }

        DimensionElement multiply(Fraction multiplicand) {
            return new DimensionElement(dimension, fraction.multiply(multiplicand));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DimensionElement element = (DimensionElement) o;
            return Objects.equals(dimension, element.dimension) &&
                   Objects.equals(fraction, element.fraction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimension, fraction);
        }

        @Override
        public String toString() {
            return String.format("%s=%s", dimension, fraction);
        }
    }

    private final Dimension          physicalDimension;
    private final DimensionElement[] dimensionElements;

    public static Dimension ofProduct(Dimension dimension, Fraction fraction) {
        return ofProduct(dimension, fraction, null, null);
    }

    public static Dimension ofProduct(Dimension left, Fraction leftFraction, Dimension right, Fraction rightFraction) {
        List<DimensionElement> physicalElements = new ArrayList<>();
        List<DimensionElement> elements         = new ArrayList<>();

        collectElements(left,  leftFraction,  physicalElements, elements);
        if (right != null) {
            collectElements(right, rightFraction, physicalElements, elements);
        }

        Dimension physicalDimension = Dimensions.NONE;
        for (DimensionElement element : physicalElements) {
            Dimension dimension =
                    element.getDimension()
                           .pow(element.getFraction().getNumerator())
                           .root(element.getFraction().getDenominator());

            physicalDimension = physicalDimension.multiply(dimension);
        }

        Map<Dimension, Fraction> map = new LinkedHashMap<>();
        for (DimensionElement element : elements) {
            Fraction fraction = map.get(element.getDimension());
            if (fraction != null) {
                fraction = fraction.add(element.getFraction());
            } else {
                fraction = element.getFraction();
            }

            if (Fraction.ZERO.compareTo(fraction) == 0) {
                map.remove(element.getDimension());
            } else {
                map.put(element.getDimension(), fraction);
            }
        }

        if (map.isEmpty()) {
            return physicalDimension;
        }

        int idx = 0;
        DimensionElement[] newElements = new DimensionElement[map.size()];
        for (Map.Entry<Dimension, Fraction> entry : map.entrySet()) {
            newElements[idx++] = new DimensionElement(entry.getKey(), entry.getValue());
        }

        // If only one element with exponent 1 is left, return it.
        if (physicalDimension  == Dimensions.NONE &&
            newElements.length == 1               &&
            newElements[0].getFraction().compareTo(Fraction.ONE) == 0) {
            return newElements[0].getDimension();
        }

        return new ProductDimension(physicalDimension, newElements);
    }

    private static void collectElements(Dimension              dimension,
                                        Fraction               fraction,
                                        List<DimensionElement> physicalDimensions,
                                        List<DimensionElement> elements) {

        if (dimension instanceof PhysicalDimension) {
            physicalDimensions.add(new DimensionElement(dimension, fraction));
        } else if (dimension instanceof ProductDimension) {
            physicalDimensions.add(new DimensionElement(((ProductDimension) dimension).physicalDimension, fraction));
            for (DimensionElement e : ((ProductDimension) dimension).dimensionElements) {
                elements.add(e.multiply(fraction));
            }
        } else {
            elements.add(new DimensionElement(dimension, fraction));
        }
    }

    private ProductDimension(Dimension physicalDimension, DimensionElement[] elements) {
        Objects.requireNonNull(physicalDimension);
        Objects.requireNonNull(elements);

        this.physicalDimension = physicalDimension;
        this.dimensionElements = elements;
    }

    @Override
    public Dimension multiply(Dimension multiplicand) {
        return ofProduct(this, Fraction.ONE, multiplicand, Fraction.ONE);
    }

    @Override
    public Dimension divide(Dimension divisor) {
        return ofProduct(this, Fraction.ONE, divisor, Fraction.of(-1));
    }

    @Override
    public Dimension pow(int n) {
        return ofProduct(this, Fraction.of(n));
    }

    @Override
    public Dimension root(int n) {
        return ofProduct(this, Fraction.of(1, n));
    }

    @Override
    public Map<Dimension, Fraction> getBaseDimensions() {
        Map<Dimension, Fraction> baseDimensionMap = new LinkedHashMap<>();

        baseDimensionMap.putAll(physicalDimension.getBaseDimensions());

        for (DimensionElement element : dimensionElements) {
            Map<Dimension, Fraction> currentMap = element.getDimension().getBaseDimensions();

            int pow  = element.getFraction().getNumerator();
            int root = element.getFraction().getDenominator();

            for (Map.Entry<Dimension, Fraction> entry : currentMap.entrySet()) {
                Dimension dimension = entry.getKey();

                Fraction value    = baseDimensionMap.get(dimension);
                Fraction newValue = entry.getValue().multiply(pow).multiply(Fraction.of(1, root));

                if (value == null) {
                    value = newValue;
                } else {
                    value = value.add(newValue);
                }

                if (Fraction.ZERO.compareTo(value) == 0) {
                    baseDimensionMap.remove(entry.getKey());
                } else {
                    baseDimensionMap.put(dimension, value);
                }
            }
        }
        return baseDimensionMap;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] { physicalDimension, dimensionElements });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDimension that = (ProductDimension) o;
        return Objects.equals(physicalDimension, that.physicalDimension) &&
               Arrays.equals(dimensionElements, that.dimensionElements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (physicalDimension != Dimensions.NONE) {
            sb.append(physicalDimension.toString());
            sb.append(',');
        }

        for (DimensionElement element : dimensionElements) {
            sb.append(element.getDimension());
            Fraction fraction = element.getFraction();
            if (Fraction.ONE.compareTo(fraction) != 0) {
                StringUtil.appendUnicodeString(fraction, sb);
            }

            sb.append(',');
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
