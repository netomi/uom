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
package org.netomi.uom.util;

import org.netomi.uom.Unit;
import org.netomi.uom.math.Fraction;

import java.util.Map;

/**
 * A simple utility class for string related helper functions.
 *
 * @author Thomas Neidhart
 */
public class StringUtil {

    private static final char FRACTION_SLASH = '\u2044';
    private static final char MINUS_SIGN     = '\u207B';

    private static final char[] SUPERSCRIPT_CHARS = new char[] {
            '\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079'
    };

    private static final char[] SUBSCRIPT_CHARS = new char[] {
            '\u2080', '\u2081', '\u2082', '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088', '\u2089'
    };

    // Hide constructor of utility class.
    private StringUtil() {}

    public static String toUnicodeString(Fraction fraction) {
        StringBuilder sb = new StringBuilder();
        appendUnicodeString(fraction, sb);
        return sb.toString();
    }

    public static void appendUnicodeString(Fraction fraction, StringBuilder stringBuilder) {
        appendUnicodeString(fraction.getNumerator(), SUPERSCRIPT_CHARS, stringBuilder);
        if (Math.abs(fraction.getDenominator()) != 1) {
            stringBuilder.append(FRACTION_SLASH);
            appendUnicodeString(fraction.getDenominator(), SUBSCRIPT_CHARS, stringBuilder);
        }
    }

    private static void appendUnicodeString(int number, char[] chars, StringBuilder stringBuilder) {
        if (number < 0) {
            stringBuilder.append(MINUS_SIGN);
            number = -number;
        }

        for (char ch : Integer.toString(number).toCharArray()) {
            stringBuilder.append(chars[ch - '0']);
        }
    }

    public static String toUnicodeString(Map<? extends Unit<?>, Fraction> baseUnits) {
        StringBuilder sb = new StringBuilder();
        appendUnicodeString(baseUnits, sb);
        return sb.toString();
    }

    public static void appendUnicodeString(Map<? extends Unit<?>, Fraction> baseUnits, StringBuilder stringBuilder) {
        for (Map.Entry<? extends Unit<?>, Fraction> entry : baseUnits.entrySet()) {
            stringBuilder.append(entry.getKey().getSymbol());
            appendUnicodeString(entry.getValue(), stringBuilder);
        }
    }
}
