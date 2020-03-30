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
import org.netomi.uom.quantity.decimal.DecimalQuantity;
import org.netomi.uom.quantity.primitive.DoubleQuantity;
import org.netomi.uom.unit.Dimensions;
import org.netomi.uom.unit.Units;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

public class DimensionTest {

    public static void main(String[] args) {

//        Dimension d = Dimensions.NONE;
//
//        d = d.multiply(Dimensions.LENGTH).multiply(Dimensions.LENGTH).divide(Dimensions.TIME);
//
//        System.out.println(d);
//
//        System.out.println(Units.METRE);
//
//        Unit<Area> squaremeter = Units.SI.METRE.pow(2).asType(Area.class);
//        System.out.println(squaremeter + " " + squaremeter.getDimension() + " " + squaremeter.getSymbol());
//
//        Unit<Area> squarecm = Units.SI.METRE.withPrefix(MetricPrefix.CENTI).pow(2).asType(Area.class);
//
//        System.out.println(squarecm);
//
//        DoubleQuantity<Area> a1 = DoubleQuantity.of(1, squaremeter);
//        DoubleQuantity<Area> a2 = a1.to(squarecm);
//        System.out.println(a1 + " " + a2);
//
//        a1 = DoubleQuantity.of(1, squarecm);
//        a2 = a1.to(squaremeter);
//        System.out.println(a1 + " " + a2);
//
//        Dimension d1 = squaremeter.getDimension();
//        Dimension d2 = Dimensions.LENGTH.pow(2);
//
//        System.out.println("d1 == d2: " + d1.equals(d2));
//
//        Unit<?> ms = Units.METRE.multiply(Units.SI.SECOND).multiply(Units.METRE).divide(Units.SI.SECOND);
//
//        System.out.println(ms);
//        System.out.println(ms.getDimension());
//
//        Unit<Length> l1 = MetricPrefix.MILLI(Units.METRE);
//        System.out.println(l1);
//        Unit<Length> l2 = MetricPrefix.KILO(l1);
//        System.out.println(l2);
//
//        Unit<?> mms = MetricPrefix.MILLI(ms);
//
//        System.out.println(mms);
//        System.out.println(mms.getDimension());
//
//        System.out.println(Units.ONE.getSymbol());
//        System.out.println(Units.METRE);
//
//        System.out.println(Units.SI.COULOMB + " " + Units.SI.COULOMB.getDimension());

        System.out.println(Units.CGS.STATCOULOMB.getDimension());
        System.out.println(Units.CGS.STATCOULOMB + " " + Units.CGS.STATCOULOMB.getDimension() + " " + Units.CGS.STATCOULOMB.getSymbol());
        System.out.println(Units.CGS.STATCOULOMB.getBaseUnits());

//
//        DoubleQuantity<ElectricCharge> e1 = DoubleQuantity.of(1, Units.SI.COULOMB);
//        DoubleQuantity<ElectricCharge> e2 = e1.to(Units.CGS.STATCOULOMB);
//
//        System.out.println(Units.CGS.STATCOULOMB.getConverterTo(Units.SI.COULOMB));
//
//        System.out.println(e1);
//        System.out.println(e2);
//
//        UnitConverter t = Units.SI.COULOMB.getConverterTo(Units.CGS.STATCOULOMB);
//
//        System.out.println(t);
//
//        Unit<?> m = Units.ONE.multiply(Units.SI.METRE).pow(2);
//        System.out.println(m.getSymbol() + " " + m.getDimension());
//
//        DecimalQuantity<Length> l1 = DecimalQuantity.of(BigDecimal.ONE, MathContext.DECIMAL128, Units.SI.METRE);
//
//        System.out.println(l1.to(Units.Imperial.FOOT));
//
//
//        System.out.println(Units.CGS.STATCOULOMB.getSymbol());
//
//        System.out.println(Units.CGS.STATCOULOMB.getBaseUnits());
//
//        Set<? extends Unit<Speed>> units = Units.unitsForQuantity(Speed.class);
//
//        for (Unit<Speed> unit : units) {
//            System.out.println(unit);
//        }
//
//        System.out.println(Units.SI.COULOMB_CONSTANT);

//        Unit<?> SC_SI = Units.CGS.STATCOULOMB.toSystem(SystemOfUnits.Type.SI);
//
//        System.out.println(SC_SI);
//        System.out.println(SC_SI.getConverterTo((Unit) Units.SI.COULOMB));
//
//        System.out.println(SC_SI.getDimension());
//        System.out.println(SC_SI.getBaseUnits());

//        Unit<?> cInCGS = Units.SI.COULOMB.toSystem(SystemOfUnits.Type.CGS);

        DoubleQuantity<ElectricCharge> e1 = DoubleQuantity.of(1, Units.SI.COULOMB);
        DoubleQuantity<ElectricCharge> e2 = e1.to(Units.CGS.STATCOULOMB);

        System.out.println(e1);
        System.out.println(e2);

        System.out.println(Units.SI.VOLT.getDimension().getBaseDimensions());
        System.out.println(Units.SI.VOLT.getBaseUnits());

        System.out.println(Dimensions.LENGTH.getBaseDimensions());
    }
}
