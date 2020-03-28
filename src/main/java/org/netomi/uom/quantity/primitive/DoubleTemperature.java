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

package org.netomi.uom.quantity.primitive;

import org.netomi.uom.Unit;
import org.netomi.uom.quantity.Temperature;
import org.netomi.uom.quantity.Time;

public class DoubleTemperature extends AbstractTypedDoubleQuantity<DoubleTemperature, Temperature> implements Temperature {

    public DoubleTemperature(double value, Unit<Temperature> unit) {
        super(value, unit);
    }

    @Override
    protected DoubleTemperature with(double value, Unit<Temperature> unit) {
        return new DoubleTemperature(value, unit);
    }
}
