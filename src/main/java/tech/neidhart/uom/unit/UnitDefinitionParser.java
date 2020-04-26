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
package tech.neidhart.uom.unit;

import tech.neidhart.uom.Unit;
import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.function.UnitConverters;
import tech.neidhart.uom.math.BigFraction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class UnitDefinitionParser {

    private static final String COMMENT = "#";
    private static final String INCLUDE = "@include";

    private static final String SPACE         = " ";
    private static final String BASE_UNIT_ID  = "!";
    private static final String MULTIPLY      = "*";
    private static final String DIVIDE        = "/";
    private static final String ADD           = "+";
    private static final String SUBTRACT      = "-";
    private static final String OPEN_BRACKET  = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String COLON         = ":";
    private static final String ASSIGNMENT    = "=";
    private static final char   EXPONENT      = '^';
    private static final char   FRACTION      = '|';

    private static final String DELIMITERS          = SPACE;
    private static final String DELIMITERS_FORMULA  = SPACE + MULTIPLY + DIVIDE + OPEN_BRACKET + CLOSE_BRACKET + BASE_UNIT_ID + ASSIGNMENT;
    private static final String DELIMITERS_FUNCTION = SPACE + ADD + SUBTRACT + MULTIPLY + DIVIDE + OPEN_BRACKET + CLOSE_BRACKET;

    private final Map<String, Unit<?>> knownUnits;
    private final Map<String, Unit<?>> parsedUnits;

    public static Map<String, Unit<?>> parse(String       definitionFile,
                                             UnitRegistry unitRegistry) {
        return new UnitDefinitionParser(unitRegistry).parseFile(definitionFile).getParsedUnits();
    }

    private UnitDefinitionParser(UnitRegistry unitRegistry) {
        this.knownUnits  = unitRegistry.getUnits();
        this.parsedUnits = new HashMap<>();
    }

    private Map<String, Unit<?>> getParsedUnits() {
        return parsedUnits;
    }

    private UnitDefinitionParser parseFile(String definitionFile) {

        try (InputStream    is     = UnitDefinitionParser.class.getResourceAsStream(definitionFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith(COMMENT)) {
                    // read next line.
                    continue;
                } else if (line.startsWith(INCLUDE)) {
                    String fileName = line.split(SPACE)[1];
                    parseFile(fileName);
                } else {
                    Unit<?> unit = parseUnitLine(line);
                    if (unit != null) {
                        parsedUnits.put(unit.getSymbol(), unit);
                        if (unit.getName() != null) {
                            parsedUnits.put(unit.getName(), unit);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to parse unit definition file '" + definitionFile + "'", ex);
        }

        return this;
    }

    private Unit<?> parseUnitLine(String line) {
        StringTokenizer st = new StringTokenizer(line, DELIMITERS, true);

        String[] symbolAndName = st.nextToken().split(COLON);

        String symbol = symbolAndName[0];
        String name   = symbolAndName.length > 1 ? symbolAndName[1].replaceAll("_", " ") : symbol;

        // if we have already parsed a unit with the same name, ignore it.
        if (parsedUnits.get(symbol) != null) {
            return null;
        }

        Unit<?> unit = parseUnitFormula(symbol, name, st);

        unit = unit.withSymbol(symbol).withName(name);
        return unit;
    }

    private Unit<?> parseUnitFormula(String symbol, String name, StringTokenizer st) {

        Unit<?> currentUnit = Units.ONE;
        boolean multiply    = true;

        while (st.hasMoreTokens()) {
            String token = st.nextToken(DELIMITERS_FORMULA);

            Unit<?> nextUnit;

            switch (token) {
            case SPACE:
            case MULTIPLY:
                continue;

            case COMMENT:
            case CLOSE_BRACKET:
                return currentUnit;

            case DIVIDE:
                multiply = false;
                continue;

            case OPEN_BRACKET:
                nextUnit = parseUnitFormula(null, null, st);
                currentUnit = multiply ?
                        currentUnit.multiply(nextUnit) :
                        currentUnit.divide(nextUnit);
                continue;

            case ASSIGNMENT:
                UnitConverter converter = parseFunction(st);
                currentUnit = currentUnit.transform(converter);
                return currentUnit;

            case BASE_UNIT_ID:
                String dimensionSymbol = st.nextToken();
                currentUnit = dimensionSymbol.equals("1") ?
                        Units.alternateDimensionlessSystemUnit(symbol, name) :
                        Units.baseUnitForDimension(symbol, name,
                                                   Dimensions.getPhysicalBaseDimension(dimensionSymbol.charAt(0)));
                return currentUnit;

            default:
                break;
            }

            if (isNumeric(token)) {
                try {
                    long number = Long.parseLong(token);
                    nextUnit = Units.ONE.multiply(number, 1);
                } catch (NumberFormatException ex) {
                    try {
                        nextUnit = Units.ONE.multiply(new BigDecimal(token));
                    } catch (NumberFormatException ex2) {
                        int fractionIndex = token.indexOf(FRACTION);

                        long numerator   = Long.parseLong(token.substring(0, fractionIndex));
                        long denominator = Long.parseLong(token.substring(fractionIndex + 1));

                        nextUnit = Units.ONE.multiply(numerator, denominator);
                    }
                }
            } else {
                int pow  = 1;
                int root = 1;

                String symbolicName = token;
                if (token.contains(String.valueOf(EXPONENT))) {
                    int expIndex      = token.indexOf(EXPONENT);
                    int fractionIndex = token.indexOf(FRACTION);

                    if (fractionIndex != -1) {
                        pow = Integer.parseInt(token.substring(expIndex + 1, fractionIndex));
                        root = Integer.parseInt(token.substring(fractionIndex + 1));
                    } else {
                        pow = Integer.parseInt(token.substring(expIndex + 1));
                    }

                    symbolicName = token.substring(0, expIndex);
                }

                nextUnit = getUnit(symbolicName).pow(pow).root(root);
            }

            if (nextUnit != null) {
                currentUnit = multiply ?
                        currentUnit.multiply(nextUnit) :
                        currentUnit.divide(nextUnit);
            }
        }

        return currentUnit;
    }

    private UnitConverter parseFunction(StringTokenizer st) {
        UnitConverter converter = UnitConverters.identity();

        Operation nextOp = Operation.ADD;

        while (st.hasMoreTokens()) {
            String token = st.nextToken(DELIMITERS_FUNCTION);

            switch (token) {
                case SPACE:
                case "x":
                    continue;

                case ADD:
                    nextOp = Operation.ADD;
                    break;

                case SUBTRACT:
                    nextOp = Operation.SUBTRACT;
                    break;

                case MULTIPLY:
                    nextOp = Operation.MULTIPLY;
                    break;

                case DIVIDE:
                    nextOp = Operation.DIVIDE;
                    break;

                case OPEN_BRACKET:
                    UnitConverter nextConverter = parseFunction(st);
                    converter = converter.andThen(nextConverter);
                    continue;

                case CLOSE_BRACKET:
                    return converter;

                default:
                    break;
            }

            if (isNumeric(token)) {
                converter = applyOperation(token, nextOp, converter);
            }
        }

        return converter;
    }

    private UnitConverter applyOperation(String token, Operation op, UnitConverter converter) {
        try {
            BigDecimal  number   = new BigDecimal(token);
            BigFraction fraction = BigFraction.from(number);

            switch (op) {
                case SUBTRACT:
                    number = number.negate();

                case ADD:
                    return converter.andThen(UnitConverters.shift(number));

                case DIVIDE:
                    fraction = fraction.reciprocal();

                case MULTIPLY:
                    return converter.andThen(UnitConverters.multiply(fraction));

                default:
                    break;
            }
        } catch (NumberFormatException ex) {
            int fractionIndex = token.indexOf(FRACTION);

            long numerator   = Long.parseLong(token.substring(0, fractionIndex));
            long denominator = Long.parseLong(token.substring(fractionIndex + 1));

            return converter.andThen(UnitConverters.multiply(numerator, denominator));
        }

        return converter;
    }

    private Unit<?> getUnit(String symbolicName) {
        Unit<?> unit = parsedUnits.getOrDefault(symbolicName, knownUnits.get(symbolicName));
        if (unit == null) {
            throw new RuntimeException("undefined unit with name " + symbolicName);
        }
        return unit;
    }

    private static boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }

        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return isFraction(s);
        }
    }

    private static boolean isFraction(String s) {
        return s.matches("\\d+\\|\\d+");
    }

    private enum Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }
}
