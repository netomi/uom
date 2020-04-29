# Units of Measurement

[![Build Status](https://api.travis-ci.org/netomi/uom.svg?branch=master)](https://travis-ci.org/netomi/uom)
[![Coverage Status](https://coveralls.io/repos/github/netomi/uom/badge.svg?branch=master&service=github)](https://coveralls.io/github/netomi/uom?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

My personal take on a _units of measurement_ library for java.

The design goal of the library is to include:

* fully typed quantities:
  ```java
  Length l1 = Length.of(1, SI.METER);
  Length l2 = Length.ofMeter(2);          // convenience factory method for SI unit 
  ```
* transparent support for double and arbitrary decimal precision quantities (using BigDecimal):
  ```java
  Length doubleLength  = Quantities.createQuantity(1, SI.METER, Length.class);
  Length decimalLength = Quantities.createQuantity(BigDecimal.ONE, Imperial.YARD, Length.class);
  
  Length l3 = l1.add(l2);
  ```
* support for generic quantities:
  ```java
    Quantity<Speed> speed = Quantities.createGeneric(1, SI.METER_PER_SECOND);
  
    System.out.println(speed.add(Speed.ofMeterPerSecond(2))); // -> prints 3 m/s
  ```
* support for quantity factories:
  ```java
    QuantityFactory<Length> factory = DoubleQuantity.factory(Length.class);
  
    Length l1 = factory.create(1, SI.METRE);
  
    // default factories can be replaced
    // use decimal precision with MathContext DECIMAL128 for every quantity of type Length:
    Quantities.registerQuantityFactory(Length.class, DecimalQuantity.factory(MathContext.DECIMAL128, Length.class));
  
    // quantity factories with pooling behavior can be registered
    QuantityFactory<Length> myPoolFactory = ...;
    Quantities.registerQuantityFactory(Length.class, myPoolFactory);
  ```  
* support for the standard unit manipulations as defined by [JSR-385](https://www.jcp.org/en/jsr/detail?id=385) et al
* support for as many units as possible, the amazing [GNU units](https://www.gnu.org/software/units/) library is the reference to compare to

The library uses dynamic proxies to create concrete quantity classes for the specified
precision at runtime.

## Examples

Conversion of units between different system of units, e.g. SI and CGS

```java
ElectricCharge e1 = ElectricCharge.of(1, SI.COULOMB);
ElectricCharge e2 = e1.to(ESU.STATCOULOMB);

System.out.println(e2);
```

prints 

```2.997925e+09 statC```


Custom quantities and units:

```java
    public interface Bmi extends Quantity<Bmi> {}

    ...

    final Unit<Bmi> bmiUnit = SI.KILOGRAM.divide(SI.METRE.pow(2)).withSymbol("B").forQuantity(Bmi.class);
    Quantity<Bmi> bmiDouble  = Quantities.createGeneric(19, bmiUnit);
    Quantity<Bmi> bmiDecimal = Quantities.createGeneric(BigDecimal.valueOf(21), bmiUnit);

    System.out.println(bmiDouble);
```

License
-------
Code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).
