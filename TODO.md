# TODO

## Manadatory

* fix multiply and divide methods (currently only working if using the same base units)
* more unit tests, especially for unit and quantity package
* add all quantities for which SI units exists
* tests for root units, e.g. 1 s^1/2 ~= 31.623ms1/2)
* add all SI units
* unit & quantity formatter / parser

## Desirable

* add more convenience methods for known quantity transformations: length * length = area, ...
* add more units from other systems
* Quantity interface should have methods to modify quantities with scalar values
  e.g. add(double value), multiply(double value), this would simplify some use-cases, e.g. get double the length of something