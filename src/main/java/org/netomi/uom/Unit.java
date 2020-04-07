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

import org.netomi.uom.math.Fraction;
import org.netomi.uom.unit.Dimension;

import java.math.BigDecimal;
import java.util.Map;

public interface Unit<Q extends Quantity<Q>> {

    String getSymbol();

    String getName();

    Dimension getDimension();

    boolean isSystemUnit();

    Unit<Q> getSystemUnit();

    UnitConverter getSystemConverter();

    UnitConverter getConverterTo(Unit<Q> unit);

    UnitConverter getConverterToAny(Unit<?> unit);

    Unit<Q> shift(double offset);

    Unit<Q> multiply(double multiplier);

    Unit<Q> multiply(BigDecimal multiplier);

    Unit<Q> multiply(long numerator, long denominator);

    Unit<Q> transform(UnitConverter converter);

    Unit<?> multiply(Unit<?> that);

    Unit<?> divide(Unit<?> that);

    Unit<?> pow(int n);

    Unit<?> root(int n);

    Unit<?> inverse();

    Map<? extends Unit<?>, Fraction> getBaseUnits();

    boolean isCompatible(Unit<?> unit);

    <T extends Quantity<T>> Unit<T> asType(Class<T> clazz);

    Unit<Q> withSymbol(String symbol);

    Unit<Q> withName(String name);

    Unit<Q> withPrefix(Prefix prefix);
}
