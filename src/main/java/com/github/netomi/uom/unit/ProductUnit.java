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
package com.github.netomi.uom.unit;

import com.github.netomi.uom.Unit;
import com.github.netomi.uom.function.UnitConverters;
import com.github.netomi.uom.Dimension;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.UnitConverter;
import com.github.netomi.uom.math.Fraction;
import com.github.netomi.uom.util.ConcurrentReferenceHashMap;
import com.github.netomi.uom.util.ObjectPrinter;

import java.lang.ref.WeakReference;
import java.util.*;

import static com.github.netomi.uom.util.ConcurrentReferenceHashMap.ReferenceType;

/**
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class ProductUnit<Q extends Quantity<Q>> extends Unit<Q> {

    private static final String EMPTY_SYMBOL = "1";

    /**
     * A cache for {@link ProductUnit} instances. Wrap values into a weak
     * reference as the keys are contained in the values, creating a
     * circular reference which would prevent the keys from being collected.
     */
    private static final Map<UnitElementWrapper, WeakReference<Unit<?>>> unitCache =
            new ConcurrentReferenceHashMap<>(50, ReferenceType.WEAK, ReferenceType.WEAK);

    private final String                 symbol;
    private final String                 name;

    private final UnitElementWrapper     unitElements;
    private final String                 cachedSymbol;
    private final Dimension              cachedDimension;
    private final UnitConverter          cachedSystemConverter;
    private final Map<Unit<?>, Fraction> cachedBaseUnitMap;
    private       Unit<Q>                cachedSystemUnit;

    public static Unit<?> ofProduct(Unit<?> unit, Fraction fraction) {
        return ofProduct(unit, fraction, null, null);
    }

    public static Unit<?> ofProduct(Unit<?> left, Fraction leftFraction, Unit<?> right, Fraction rightFraction) {
        List<UnitElement> elements = new ArrayList<>();

        collectElements(left, leftFraction, elements);
        if (right != null) {
            collectElements(right, rightFraction, elements);
        }

        // create a canonical representation of the elements which is required
        // for the cache to work properly, otherwise the same elements are not
        // equal to each other.
        SortedMap<Unit<?>, Fraction> map = new TreeMap<>(Comparator.comparing(Unit::getSymbol));

        for (UnitElement element : elements) {
            Fraction fraction = map.get(element.getUnit());
            if (fraction != null) {
                fraction = fraction.add(element.getFraction());
            } else {
                fraction = element.getFraction();
            }

            if (Fraction.ZERO.compareTo(fraction) == 0) {
                map.remove(element.getUnit());
            } else {
                map.put(element.getUnit(), fraction);
            }
        }

        if (map.isEmpty()) {
            return Units.ONE;
        }

        int idx = 0;
        UnitElement[] newElements = new UnitElement[map.size()];
        for (Map.Entry<Unit<?>, Fraction> entry : map.entrySet()) {
            newElements[idx++] = new UnitElement(entry.getKey(), entry.getValue());
        }

        // If only one element with exponent 1 is left, return it.
        if (newElements.length == 1 &&
            newElements[0].getFraction().compareTo(Fraction.ONE) == 0) {
            return newElements[0].getUnit();
        }

        Unit<?> cachedUnit = getCachedUnit(newElements);
        if (cachedUnit != null) {
            return cachedUnit;
        } else {
            ProductUnit<?> unit = new ProductUnit<>(UnitElementWrapper.of(newElements));

            // if the generated product unit has an identity converter but is composed of
            // non-system units, we can return the system unit instead. This can happen
            // when e.g. multiplying km with mm which results in a product unit of km * mm
            // but it is equivalent to m^2.
            if (!unit.isSystemUnit() &&
                unit.getSystemConverter() == UnitConverters.identity()) {
                return unit.getSystemUnit();
            }

            // do not cache dimensionless units, currently they are equal to each other
            // if they have a unit system converter ("1" == "rad").
            if (unit.getDimension() != Dimensions.NONE) {
                putUnitIntoCache(unit);
            }
            return unit;
        }
    }

    private static void collectElements(Unit<?>           unit,
                                        Fraction          fraction,
                                        List<UnitElement> elements) {
        for (UnitElement e : unit.getUnitElements()) {
            elements.add(e.multiply(fraction));
        }
    }

    private static Unit<?> getCachedUnit(UnitElement[] elements) {
        WeakReference<Unit<?>> reference = unitCache.get(UnitElementWrapper.of(elements));
        return reference != null ? reference.get() : null;
    }

    private static void putUnitIntoCache(ProductUnit<?> unit) {
        unitCache.putIfAbsent(unit.unitElements, new WeakReference<>(unit));
    }

    static void putProductUnitIntoCache(Unit<?> unit) {
        if (unit instanceof ProductUnit<?>) {
            ProductUnit<?> productUnit = (ProductUnit<?>) unit;
            unitCache.put(productUnit.unitElements, new WeakReference<>(unit));
        }
    }

    protected ProductUnit() {
        this.symbol = null;
        this.name   = null;

        this.unitElements = UnitElementWrapper.of(new UnitElement[0]);

        this.cachedSymbol          = EMPTY_SYMBOL;
        this.cachedDimension       = Dimensions.NONE;
        this.cachedSystemConverter = UnitConverters.identity();
        this.cachedBaseUnitMap     = Collections.emptyMap();
    }

    protected ProductUnit(UnitElementWrapper unitElements) {
        this.symbol = null;
        this.name   = null;

        this.unitElements          = unitElements;
        this.cachedSymbol          = calculateSymbol();
        this.cachedDimension       = calculateDimension();
        this.cachedSystemConverter = calculateSystemConverter();
        this.cachedBaseUnitMap     = calculateBaseUnitMap();
        // the system unit is lazily initialized.
        this.cachedSystemUnit      = null;
    }

    protected ProductUnit(ProductUnit<Q> productUnit, String symbol, String name) {
        this.symbol                = symbol;
        this.name                  = name;

        this.unitElements          = productUnit.unitElements;
        this.cachedSymbol          = productUnit.cachedSymbol;
        this.cachedDimension       = productUnit.cachedDimension;
        this.cachedSystemConverter = productUnit.cachedSystemConverter;
        this.cachedBaseUnitMap     = productUnit.cachedBaseUnitMap;
        this.cachedSystemUnit      = productUnit.cachedSystemUnit;
    }

    @Override
    public UnitElement[] getUnitElements() {
        return unitElements.elements;
    }

    private String calculateSymbol() {
        StringBuilder sb = new StringBuilder();
        ObjectPrinter.instance().appendTo(sb, unitElements.elements);
        return sb.toString();
    }

    private Dimension calculateDimension() {
        Dimension combinedDimension = Dimensions.NONE;
        for (UnitElement element : unitElements.elements) {
            Dimension dimension = element.getUnit().getDimension();
            dimension = dimension.pow(element.getFraction().getNumerator())
                                 .root(element.getFraction().getDenominator());
            combinedDimension = combinedDimension.multiply(dimension);
        }
        return combinedDimension;
    }

    @Override
    public String getSymbol() {
        return symbol != null ? symbol : cachedSymbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Dimension getDimension() {
        return cachedDimension;
    }

    @Override
    public boolean isSystemUnit() {
        for (UnitElement element : unitElements.elements) {
            if (!element.getUnit().isSystemUnit()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Unit<Q> getSystemUnit() {
        synchronized (this) {
            if (cachedSystemUnit == null) {
                cachedSystemUnit = calculateSystemUnit();
            }

            return cachedSystemUnit;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Unit<Q> calculateSystemUnit() {
        Unit<?> systemUnit = Units.ONE;
        for (UnitElement element : unitElements.elements) {
            Unit unit = element.getUnit().getSystemUnit();
            unit = unit.pow(element.getFraction().getNumerator())
                       .root(element.getFraction().getDenominator());
            systemUnit = systemUnit.multiply(unit);
        }

        return (Unit<Q>) Units.getNamedUnitIfPresent(systemUnit);
    }

    @Override
    public UnitConverter getSystemConverter() {
        return cachedSystemConverter;
    }

    private UnitConverter calculateSystemConverter() {
        UnitConverter systemConverter = UnitConverters.identity();
        for (UnitElement e : unitElements.elements) {
            UnitConverter converter = e.getUnit().getSystemConverter();
            int pow  = e.getFraction().getNumerator();
            int root = e.getFraction().getDenominator();

            converter = UnitConverters.pow(converter,  pow);
            converter = UnitConverters.root(converter, root);

            systemConverter = systemConverter.andThen(converter);
        }
        return systemConverter;
    }

    @Override
    public Map<? extends Unit<?>, Fraction> getBaseUnits() {
        return cachedBaseUnitMap;
    }

    private Map<Unit<?>, Fraction> calculateBaseUnitMap() {
        if (unitElements.elements.length == 0) {
            return Collections.emptyMap();
        }

        Map<Unit<?>, Fraction> baseUnitMap = new LinkedHashMap<>();
        for (UnitElement e : unitElements.elements) {
            Map<? extends Unit<?>, Fraction> currentMap = e.getUnit().getBaseUnits();

            int pow  = e.getFraction().getNumerator();
            int root = e.getFraction().getDenominator();

            for (Map.Entry<? extends Unit<?>, Fraction> entry : currentMap.entrySet()) {
                Unit<?> unit = entry.getKey();

                Fraction value    = baseUnitMap.get(unit);
                Fraction newValue = entry.getValue().multiply(pow).multiply(Fraction.of(1, root));

                if (value == null) {
                    value = newValue;
                } else {
                    value = value.add(newValue);
                }

                if (Fraction.ZERO.compareTo(value) == 0) {
                    baseUnitMap.remove(entry.getKey());
                } else {
                    baseUnitMap.put(unit, value);
                }
            }
        }
        return baseUnitMap;
    }

    @Override
    public Unit<Q> withSymbol(String symbol) {
        return new ProductUnit<>(this, symbol, this.name);
    }

    @Override
    public Unit<Q> withName(String name) {
        return new ProductUnit<>(this, this.symbol, name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cachedDimension, cachedSystemConverter, unitElements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductUnit)) return false;

        // specific equals for product units: two units are considered
        // to be equal if their dimension and system converter are equal.
        // additionally make sure that their unit elements are equal, this
        // is needed to distinguish between units with dimensionless components,
        // like the unit for angular velocity: rad/s should not be equal to
        // the unit for frequency which is 1/s. This is a design decision
        // to ensure that units with the same dimension but different nature
        // are not equal to each other.

        ProductUnit<?> otherUnit = (ProductUnit<?>) o;
        return Objects.equals(cachedDimension,       otherUnit.cachedDimension)       &&
               Objects.equals(cachedSystemConverter, otherUnit.cachedSystemConverter) &&
               Objects.equals(unitElements,          otherUnit.unitElements);
    }

    /**
     * Internal class used by {@link Unit} implementations to return an
     * array of {@link UnitElement}'s this unit is composed of.
     * <p>
     * The only reason such a wrapper has been created is because plain arrays can
     * not be put into a {@link java.util.WeakHashMap} as {@link #hashCode()} and
     * {@link #equals(Object)} do not take such arrays into account. The wrapper
     * correctly implements these methods.
     */
    static class UnitElementWrapper {
        final UnitElement[] elements;

        static UnitElementWrapper of(UnitElement[] elements) {
            return new UnitElementWrapper(elements);
        }

        private UnitElementWrapper(UnitElement[] elements) {
            this.elements = elements;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(elements);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UnitElementWrapper other = (UnitElementWrapper) o;
            return Arrays.equals(elements, other.elements);
        }

        @Override
        public String toString() {
            return Arrays.toString(elements);
        }
    }
}
