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

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class DerivedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

    private final Element[] elements;
    private final String    symbol;
    private final Dimension dimension;

    public static Unit<?> ofRoot(Unit<?> unit, int n) {
        List<Element> elementList = new ArrayList<>();

        if (unit instanceof DerivedUnit<?>) {
            Element[] elems = ((DerivedUnit<?>) unit).elements;
            for (int i = 0; i < elems.length; i++) {
                Fraction f = elems[i].fraction;
                elementList.add(new Element(elems[i].unit, f.multiply(Fraction.of(1, n))));
            }
        } else {
            elementList.add(new Element(unit, Fraction.of(1, n)));
        }
        return getInstance(elementList);
    }

    /**
     * Returns the product unit corresponding to this unit raised to the specified exponent.
     */
    public static Unit<?> ofPow(Unit<?> unit, int n) {
//        Element[] unitElems;
//        if (unit instanceof ProductUnit<?>) {
//            Element[] elems = ((ProductUnit<?>) unit).elements;
//            unitElems = new Element[elems.length];
//            for (int i = 0; i < elems.length; i++) {
//                int gcd = gcd(Math.abs(elems[i].pow * n), elems[i].root);
//                unitElems[i] = new Element(elems[i].unit, elems[i].pow * n / gcd, elems[i].root / gcd);
//            }
//        } else
//            unitElems = new Element[] { new Element(unit, n, 1) };
//        return getInstance(unitElems, new Element[0]);
        return null;
    }

    public static DerivedUnit<?> getProductInstance(Unit<?> left, Unit<?> right) {

        List<Element> elementList = new ArrayList<>();

        if (left instanceof DerivedUnit) {
            elementList.addAll(Arrays.asList(((DerivedUnit<?>) left).elements));
        } else {
            elementList.add(new Element(left, Fraction.ONE));
        }

        if (right instanceof DerivedUnit) {
            elementList.addAll(Arrays.asList(((DerivedUnit<?>) right).elements));
        } else {
            elementList.add(new Element(right, Fraction.ONE));
        }

        return getInstance(elementList);
    }

    public static DerivedUnit<?> getQuotientInstance(Unit<?> left, Unit<?> right) {
        List<Element> elementList = new ArrayList<>();

        if (left instanceof DerivedUnit) {
            elementList.addAll(Arrays.asList(((DerivedUnit<?>) left).elements));
        } else {
            elementList.add(new Element(left, Fraction.ONE));
        }

        if (right instanceof DerivedUnit) {
            for (Element e : ((DerivedUnit<?>) right).elements) {
                elementList.add(new Element(e.unit, e.fraction.multiply(-1)));
            }
        } else {
            elementList.add(new Element(right, Fraction.ONE.multiply(-1)));
        }

        return getInstance(elementList);
    }

    public static DerivedUnit<?> getInstance(List<Element> elements) {
        Map<Unit<?>, Fraction> map = new LinkedHashMap<>();

        for (Element element : elements) {
            Fraction fraction = map.get(element.unit);
            if (fraction != null) {
                fraction = fraction.add(element.fraction);
            } else {
                fraction = element.fraction;
            }
            map.put(element.unit, fraction);
        }

        Element[] newElements = new Element[map.size()];
        int idx = 0;
        for (Map.Entry<Unit<?>, Fraction> entry : map.entrySet()) {
            newElements[idx++] = new Element(entry.getKey(), entry.getValue());
        }

        return new DerivedUnit<>(newElements);
    }

    protected DerivedUnit() {
        this(new Element[0]);
    }

    protected DerivedUnit(Element... elements) {
        this.elements  = elements;
        this.symbol    = calculateSymbol();
        this.dimension = calculateDimension();
    }

    private String calculateSymbol() {
        if (elements.length == 0) {
            return "1";
        }

        StringBuilder sb = new StringBuilder();

        for (Element element : elements) {
            sb.append(element.unit.getSymbol());

            if (Fraction.ONE.compareTo(element.fraction) != 0) {
                sb.append('^');
                sb.append(element.fraction.getNumerator());
                if (element.fraction.getDenominator() != 1) {
                    sb.append('/');
                    sb.append(element.fraction.getDenominator());
                }
            }

            sb.append(' ');
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private Dimension calculateDimension() {
        Dimension dimension = Dimensions.NONE;
        for (Element element : elements) {
            Dimension d = element.unit.getDimension();
            d = d.pow(element.fraction.getNumerator()).root(element.fraction.getDenominator());
            dimension = dimension.multiply(d);
        }
        return dimension;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public boolean isSystemUnit() {
        return false;
    }

    @Override
    public Unit<Q> getSystemUnit() {
        Unit<?> systemUnit = Units.ONE;
        for (Element element : elements) {
            Unit unit = element.unit.getSystemUnit();
            unit = unit.pow(element.fraction.getNumerator());
            systemUnit = systemUnit.multiply(unit);
        }
        return (Unit<Q>) systemUnit;
    }

    @Override
    public UnitConverter getSystemConverter() {
        UnitConverter systemConverter = UnitConverters.identity();
        for (Element e : elements) {
            UnitConverter converter = e.unit.getSystemConverter();
            int pow  = e.fraction.getNumerator();
            int root = e.fraction.getDenominator();

            if (pow < 0) {
                pow       = -pow;
                converter = converter.inverse();
            }

            if (root > 1) {
                converter = UnitConverters.pow(converter, pow);
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
        Map<Unit<?>, Fraction> baseUnitMap = new HashMap<>();

        for (Element e : elements) {
            Map<? extends Unit<?>, Fraction> currentMap = e.unit.getBaseUnits();

            int pow  = e.fraction.getNumerator();
            int root = e.fraction.getDenominator();

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
    public Unit<Q> withPrefix(Prefix prefix) {
        Element[] newElements = new Element[elements.length];

        for (int i = 0; i < elements.length; i++) {
            Element element = elements[i];
            newElements[i] = new Element(element.unit.withPrefix(prefix), element.fraction);
        }

        return new DerivedUnit<>(newElements);
    }

    @Override
    public String toString() {
        return String.format("DerivedUnit[elements=%s]", Arrays.toString(elements));
    }

    public static class Element {
        Unit<?>  unit;
        Fraction fraction;

        private Element(Unit unit, Fraction fraction) {
            this.unit     = unit;
            this.fraction = fraction;
        }

        @Override
        public String toString() {
            return String.format("Element[unit=%s, fraction=%s]", unit, fraction);
        }
    }
}
