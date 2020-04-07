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

import org.netomi.uom.quantity.Area;
import org.netomi.uom.quantity.Length;
import org.netomi.uom.quantity.Quantities;
import org.netomi.uom.quantity.Speed;
import org.netomi.uom.unit.Dimensions;
import org.netomi.uom.unit.Prefixes;
import org.netomi.uom.unit.Units;

public class Test {

    private static interface TestQuantity extends Quantity<TestQuantity> {}
    private static Unit<TestQuantity> testUnit = Units.baseUnitForDimension("test", "TEST", Dimensions.ofName("TEST"));

    public static void main(String[] args) throws InterruptedException {

//        Quantity<TestQuantity> l = Quantities.createQuantity(1, testUnit, TestQuantity.class);
//
//        System.out.println(l.asQuantity(Length.class));

        Unit<Area> mms = Units.SI.METRE.withPrefix(Prefixes.Metric.MILLI).divide(Units.Other.HOUR).forQuantity(Area.class);

        System.out.println(mms);

    }
}
