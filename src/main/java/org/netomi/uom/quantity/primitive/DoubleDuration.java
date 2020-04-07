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
import org.netomi.uom.quantity.Distance;
import org.netomi.uom.quantity.Duration;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.quantity.Time;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link Duration} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleDuration
        extends    AbstractTypedDoubleQuantity<DoubleDuration, Time>
        implements Duration {

    public static DoubleDuration of(double value, Unit<Time> unit) {
        return new DoubleDuration(value, unit);
    }

    public static DoubleDuration ofSecond(double value) {
        return of(value, Units.SI.SECOND);
    }

    public static DoubleQuantityFactory<DoubleDuration, Time> factory() {
        return DoubleDuration::of;
    }

    private DoubleDuration(double value, Unit<Time> unit) {
        super(value, unit);
    }

    @Override
    public DoubleDuration with(double value, Unit<Time> unit) {
        return of(value, unit);
    }
}
