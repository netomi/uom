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

import java.util.Arrays;

/**
 * Internal class used by {@link CompositeUnit} implementations to return an
 * array of {@link UnitElement}'s this unit is composed of.
 * <p>
 * The only reason such a wrapper has been created is because plain arrays can
 * not be put into a {@link java.util.WeakHashMap} as {@link #hashCode()} and
 * {@link #equals(Object)} do not take such arrays into account. The wrapper
 * correctly implements these methods.
 *
 * @author Thomas Neidhart
 */
class UnitElementWrapper {
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
