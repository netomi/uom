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

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;

class TransformedUnit<Q extends Quantity<Q>> extends DelegateUnit<Q> implements Unit<Q> {

    private final UnitConverter converterToParent;

    TransformedUnit(Unit<Q> parentUnit, UnitConverter converterToParent) {
        super(parentUnit);
        this.converterToParent = converterToParent;
    }

    @Override
    public String getSymbol() {
        return getDelegateUnit().getSymbol();
    }

    @Override
    public String getName() {
        return getDelegateUnit().getName();
    }

    @Override
    public boolean isSystemUnit() {
        return false;
    }

    @Override
    public UnitConverter getSystemConverter() {
        return converterToParent.andThen(getDelegateUnit().getSystemConverter());
    }
}
