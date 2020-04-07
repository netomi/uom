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

import org.netomi.uom.*;
import org.netomi.uom.function.UnitConverters;
import org.netomi.uom.math.Fraction;
import org.netomi.uom.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class DerivedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> implements CompositeUnit {

    private static final String EMPTY_SYMBOL = "1";

    /**
     * A cache for {@link DerivedUnit} instances. Wrap values into a weak
     * reference as the keys are contained in the values, creating a
     * circular reference which would prevent the keys from being collected.
     */
    private static final Map<UnitElementWrapper, WeakReference<DerivedUnit<?>>> unitCache =
            Collections.synchronizedMap(new WeakHashMap<>());

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

        Map<Unit<?>, Fraction> map = new LinkedHashMap<>();
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

        DerivedUnit<?> cachedUnit = getCachedUnit(newElements);
        if (cachedUnit != null) {
            return Units.getNamedUnitIfPresent(cachedUnit);
        } else {
            DerivedUnit<?> unit = new DerivedUnit<>(UnitElementWrapper.of(newElements));
            putUnitIntoCache(unit);
            return unit;
        }
    }

    private static void collectElements(Unit<?>           unit,
                                        Fraction          fraction,
                                        List<UnitElement> elements) {

        if (unit instanceof CompositeUnit) {
            for (UnitElement e : ((CompositeUnit) unit).getUnitElements().elements) {
                elements.add(e.multiply(fraction));
            }
        } else {
            elements.add(new UnitElement(unit, fraction));
        }
    }

    private static DerivedUnit<?> getCachedUnit(UnitElement[] elements) {
        WeakReference<DerivedUnit<?>> reference = unitCache.get(UnitElementWrapper.of(elements));
        return reference != null ? reference.get() : null;
    }

    private static void putUnitIntoCache(DerivedUnit<?> unit) {
        unitCache.put(unit.unitElements, new WeakReference<>(unit));
    }

    protected DerivedUnit() {
        this.unitElements = UnitElementWrapper.of(new UnitElement[0]);

        this.cachedSymbol          = EMPTY_SYMBOL;
        this.cachedDimension       = Dimensions.NONE;
        this.cachedSystemConverter = UnitConverters.identity();
        this.cachedBaseUnitMap     = Collections.emptyMap();
    }

    protected DerivedUnit(UnitElementWrapper unitElements) {
        this.unitElements          = unitElements;
        this.cachedSymbol          = calculateSymbol();
        this.cachedDimension       = calculateDimension();
        this.cachedSystemConverter = calculateSystemConverter();
        this.cachedBaseUnitMap     = calculateBaseUnitMap();
        // the system unit is lazily initialized.
        this.cachedSystemUnit      = null;
    }

    @Override
    public UnitElementWrapper getUnitElements() {
        return unitElements;
    }

    private String calculateSymbol() {
        StringBuilder numerator   = new StringBuilder();
        StringBuilder denominator = new StringBuilder();

        for (UnitElement element : unitElements.elements) {
            if (element.getFraction().signum() > 0) {
                appendUnitElementAsString(element.getUnit(), element.getFraction(), numerator);
            } else {
                appendUnitElementAsString(element.getUnit(), element.getFraction().negate(), denominator);
            }
        }

        if (numerator.length() == 0) {
            numerator.append('1');
        }

        StringBuilder sb = new StringBuilder();
        if (numerator.length() > 0) {
            sb.append(numerator);

            if (denominator.length() > 0) {
                sb.append('/');
            }
        }

        if (denominator.length() > 0) {
            sb.append(denominator);
        }

        return sb.toString();
    }

    private void appendUnitElementAsString(Unit<?> unit, Fraction fraction, StringBuilder stringBuilder) {
        stringBuilder.append(unit.getSymbol());
        if (Fraction.ONE.compareTo(fraction) != 0) {
            StringUtil.appendUnicodeString(fraction, stringBuilder);
        }
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
        return cachedSymbol;
    }

    @Override
    public String getName() {
        // Does not really make sense to return a name concatenated
        // from each unit element, return an empty string.
        return "";
    }

    @Override
    public Dimension getDimension() {
        return cachedDimension;
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

    private Unit<Q> calculateSystemUnit() {
        Unit<?> systemUnit = Units.ONE;
        for (UnitElement element : unitElements.elements) {
            Unit unit = element.getUnit().getSystemUnit();
            unit = unit.pow(element.getFraction().getNumerator())
                       .root(element.getFraction().getDenominator());
            systemUnit = systemUnit.multiply(unit);
        }

        if (!isCompatible(systemUnit)) {
            throw new AssertionError(String.format("system unit dimension != this dimension: %s, %s", systemUnit, this));
        }

        return (Unit<Q>) systemUnit;
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

            if (pow < 0) {
                pow       = -pow;
                converter = converter.inverse();
            }

            if (root > 1) {
                // TODO: unrolling the pow operation could be done always,
                //       keeping the specific pow converter is only useful
                //       for debugging.
                converter = UnitConverters.pow(converter,  pow);
                converter = UnitConverters.root(converter, root);

                systemConverter = systemConverter.andThen(converter);
            } else {
                // if we do not have a root component, unroll the power
                // operation to be able to reduce the resulting
                // unit converter.
                for (int j = 0; j < pow; j++) {
                    systemConverter = systemConverter.andThen(converter);
                }
            }
        }
        return systemConverter;
    }

    @Override
    public Map<? extends Unit<?>, Fraction> getBaseUnits() {
        return cachedBaseUnitMap;
    }

    private Map<Unit<?>, Fraction> calculateBaseUnitMap() {
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
}
