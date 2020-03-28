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

public class PrefixedUnit<Q extends Quantity<Q>> extends TransformedUnit<Q> {

    private final Prefix  prefix;

    PrefixedUnit(Prefix prefix, Unit<Q> parentUnit) {
        super(parentUnit, UnitConverters.pow(prefix.getValue().intValue(), prefix.getExponent()));

        this.prefix = prefix;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    @Override
    public String getSymbol() {
        return prefix.getSymbol() + getDelegateUnit().getSymbol();
    }

    @Override
    public String getName() {
        return prefix.getName() + getDelegateUnit().getName();
    }

    @Override
    public boolean isSystemUnit() {
        return false;
    }

    @Override
    public Unit<Q> withPrefix(Prefix prefix) {
        // small optimization when concatenating a prefix to
        // an unit that already contains a prefix.
        if (getPrefix().getClass().equals(prefix.getClass())) {
            int combinedExponent = getPrefix().getExponent() + prefix.getExponent();

            if (combinedExponent == 0) {
                return getDelegateUnit();
            }

            Prefix combinedPrefix = prefix.fromExponent(combinedExponent);
            if (combinedPrefix != null) {
                new PrefixedUnit<>(combinedPrefix, getDelegateUnit());
            }
        }

        return super.withPrefix(prefix);
    }
}
