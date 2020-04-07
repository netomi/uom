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
import org.netomi.uom.quantity.Length;
import org.netomi.uom.unit.Units;

/**
 * A concrete {@link org.netomi.uom.Quantity} implementation for the quantity type
 * {@link Distance} with double precision.
 *
 * @author Thomas Neidhart
 */
public final class DoubleDistance
        extends    AbstractTypedDoubleQuantity<DoubleDistance, Length>
        implements Distance {

    public static DoubleDistance of(double value, Unit<Length> unit) {
        return new DoubleDistance(value, unit);
    }

    public static DoubleDistance ofMeter(double value) {
        return of(value, Units.SI.METRE);
    }

    public static DoubleQuantityFactory<DoubleDistance, Length> factory() {
        return DoubleDistance::of;
    }

    private DoubleDistance(double value, Unit<Length> unit) {
        super(value, unit);
    }

    @Override
    public DoubleDistance with(double value, Unit<Length> unit) {
        return of(value, unit);
    }
}
