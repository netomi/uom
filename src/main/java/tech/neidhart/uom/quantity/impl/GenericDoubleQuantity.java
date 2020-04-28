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
package tech.neidhart.uom.quantity.impl;

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.unit.Units;

@SuppressWarnings("rawtypes")
class GenericDoubleQuantity extends AbstractDoubleQuantity {

    static final Quantity<?> ONE = new GenericDoubleQuantity(1.0, Units.ONE);

    public static <Q extends Quantity<Q>> GenericDoubleQuantityFactory<Q> factory() {
        return GenericDoubleQuantity::new;
    }

    @SuppressWarnings("unchecked")
    GenericDoubleQuantity(double value, Unit unit) {
        super(value, unit);
    }

    @Override
    public DoubleQuantity with(double value, Unit unit) {
        return new GenericDoubleQuantity(value, unit);
    }
}
