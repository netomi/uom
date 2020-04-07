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

import org.netomi.uom.quantity.*;
import org.netomi.uom.quantity.decimal.DecimalMass;
import org.netomi.uom.quantity.decimal.DecimalQuantity;
import org.netomi.uom.unit.Prefixes;
import org.netomi.uom.unit.Units;

import java.math.BigDecimal;
import java.math.MathContext;

public class UnitTest {

    public static void main(String[] args) {

        Unit<Pressure> unit = Units.SI.PASCAL;

        System.out.println(unit);

        System.out.println(unit.getBaseUnits());
        System.out.println(unit.getDimension());

        Unit<Speed> mms = Units.SI.METRE.withPrefix(Prefixes.Metric.MILLI).divide(Units.Other.HOUR).forQuantity(Speed.class);

        System.out.println(mms);
        System.out.println(mms.getSymbol());
        System.out.println(mms.getBaseUnits());
        System.out.println(mms.getDimension());

        Quantity<Speed> q = Quantities.createQuantity(1, Units.SI.METER_PER_SECOND);

        System.out.println(q.to(mms));

        Unit<?> u = Units.SI.NEWTON.multiply(Units.SI.SECOND);

        System.out.println("Ns = " + u);

        Unit<?> testUnit = Units.SI.NEWTON.divide(Units.SI.SECOND.pow(2)).multiply(Units.SI.SECOND.pow(2));

        System.out.println(testUnit);
        System.out.println(testUnit.getSymbol());
        System.out.println(testUnit.getName());
        System.out.println(testUnit.getBaseUnits());
        System.out.println(testUnit.getSystemUnit());

//        UnitConverter unitConverter = Units.SI.NEWTON.getConverterTo((Unit) testUnit);
//        System.out.println(unitConverter);

        System.out.println(Units.Other.DEGREE.getSystemUnit());
    }
}
