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

import org.netomi.uom.Unit;
import org.netomi.uom.math.Fraction;

import java.util.Objects;

/**
 * Internal class representing a unit element raised to a specific
 * power/root fraction.
 *
 * @author Thomas Neidhart
 */
public class UnitElement {
    private final Unit<?>  unit;
    private final Fraction fraction;

    UnitElement(Unit<?> unit, Fraction fraction) {
        this.unit     = unit;
        this.fraction = fraction;
    }

    Unit<?> getUnit() {
        return unit;
    }

    Fraction getFraction() {
        return fraction;
    }

    UnitElement multiply(Fraction multiplicand) {
        return new UnitElement(unit, fraction.multiply(multiplicand));
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, fraction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnitElement element = (UnitElement) o;
        return Objects.equals(unit,     element.unit) &&
               Objects.equals(fraction, element.fraction);
    }

    @Override
    public String toString() {
        return String.format("%s=%s", unit, fraction);
    }
}
