# Units of Measurement

[![Build Status](https://api.travis-ci.org/netomi/uom.svg?branch=master)](https://travis-ci.org/netomi/uom)
[![Coverage Status](https://coveralls.io/repos/github/netomi/uom/badge.svg?branch=master&service=github)](https://coveralls.io/github/netomi/uom?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

My personal take on a _units of measurement_ library for java.

The design goal of the library is to include the following:

* fully typed quantities:
  ```
  Length l = Length.of(1, Units.METER);
  ```
* support for double and arbitrary decimal precision quantities (BigDecimal): ``` ```
  ```
  DoubleLength  l1 = Length.of(1, Units.METER);
  DecimalLength l2 = Length.decimalOf(1, Units.YARD);
  
  Length l3 = l1.add(l2);
  ```
* unit conversions are performed with user-defined precision
  ```
  DecimalLength l1 = ...
  DecimalLength l2 = l1.to(Units.YARD, MathContext.DECIMAL128);
  ```
* support for the standard unit manipulations as defined by [JSR-385](https://www.jcp.org/en/jsr/detail?id=385) et al
* support for as many units as possible, the amazing [GUN units](https://www.gnu.org/software/units/) library is the reference to compare to

## Examples

Conversion of units between different system of units, e.g. SI and CGS

```
DoubleQuantity<ElectricCharge> e1 = DoubleQuantity.of(1, Units.SI.COULOMB);
DoubleQuantity<ElectricCharge> e2 = e1.to(Units.CGS.STATCOULOMB);

System.out.println(e2);
```

prints 

```2.997925e+09 statC```


License
-------
Code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).