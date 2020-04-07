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
import org.netomi.uom.quantity.Length;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link Length} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleLength
        extends    AbstractTypedDoubleQuantity<DoubleLength, Length>
        implements Length {

    public static DoubleLength of(double value, Unit<Length> unit) {
        return new DoubleLength(value, unit);
    }

    public static DoubleLength ofMeter(double value) {
        return of(value, Units.SI.METRE);
    }

    public static DoubleQuantityFactory<DoubleLength, Length> factory() {
        return DoubleLength::of;
    }

    private DoubleLength(double value, Unit<Length> unit) {
        super(value, unit);
    }

    @Override
    public DoubleLength with(double value, Unit<Length> unit) {
        return of(value, unit);
    }
}
