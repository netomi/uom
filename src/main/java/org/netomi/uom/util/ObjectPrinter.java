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
import org.netomi.uom.unit.UnitElement;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Thomas Neidhart
 */
public abstract class ObjectPrinter {

    public static ObjectPrinter instance() {
        return forUnicode();
    }

    public static ObjectPrinter forAscii() {
        return AsciiObjectPrinter.INSTANCE;
    }

    public static ObjectPrinter forUnicode() {
        return UnicodeObjectPrinter.INSTANCE;
    }

    private ObjectPrinter() {}

    // Fraction.

    public String print(Fraction fraction) {
        StringBuilder stringBuilder = new StringBuilder();
        appendTo(stringBuilder, fraction);
        return stringBuilder.toString();
    }

    public abstract void appendTo(Appendable appendable, Fraction fraction) throws IOException;

    public void appendTo(StringBuilder stringBuilder, Fraction fraction) {
        try {
            appendTo((Appendable) stringBuilder, fraction);
        } catch (IOException ex) {}
    }

    // Map<?, Fraction>.

    public <T> String print(Map<T, Fraction> map, Function<T, String> keyMapFunction) {
        StringBuilder stringBuilder = new StringBuilder();
        appendTo(stringBuilder, map, keyMapFunction);
        return stringBuilder.toString();
    }

    public <T> void appendTo(Appendable          appendable,
                             Map<T, Fraction>    map,
                             Function<T, String> keyMapFunction) throws IOException {
        for (Map.Entry<T, Fraction> entry : map.entrySet()) {
            appendable.append(keyMapFunction.apply(entry.getKey()));
            Fraction fraction = entry.getValue();
            if (Fraction.ONE.compareTo(fraction) != 0) {
                appendTo(appendable, fraction);
            }
        }
    }

    public <T> void appendTo(StringBuilder       stringBuilder,
                             Map<T, Fraction>    map,
                             Function<T, String> keyMapFunction) {
        try {
            appendTo((Appendable) stringBuilder, map, keyMapFunction);
        } catch (IOException ex) {}
    }

    // UnitElement[].

    public <T> String print(UnitElement[] unitElements) {
        StringBuilder stringBuilder = new StringBuilder();
        appendTo(stringBuilder, unitElements);
        return stringBuilder.toString();
    }

    public void appendTo(Appendable appendable, UnitElement[] unitElements) throws IOException {
        StringBuilder numeratorString   = new StringBuilder();
        StringBuilder denominatorString = new StringBuilder();

        for (UnitElement element : unitElements) {
            if (element.getFraction().signum() > 0) {
                appendTo(numeratorString, element.getUnit(), element.getFraction());
            } else {
                appendTo(denominatorString, element.getUnit(), element.getFraction().negate());
            }
        }

        // remove final bullet char.
        if (numeratorString.length() > 0) {
            numeratorString.deleteCharAt(numeratorString.length() - 1);
        }

        if (denominatorString.length() > 0) {
            denominatorString.deleteCharAt(denominatorString.length() - 1);
        }

        if (numeratorString.length() == 0) {
            numeratorString.append('1');
        }

        if (numeratorString.length() > 0) {
            appendable.append(numeratorString.toString());

            if (denominatorString.length() > 0) {
                appendable.append('/');
            }
        }

        if (denominatorString.length() > 0) {
            appendable.append(denominatorString.toString());
        }
    }

    private void appendTo(StringBuilder stringBuilder, Unit<?> unit, Fraction fraction) {
        stringBuilder.append(unit.getSymbol());
        if (Fraction.ONE.compareTo(fraction) != 0) {
            appendTo(stringBuilder, fraction);
        }

        stringBuilder.append('·');
    }

    public void appendTo(StringBuilder stringBuilder, UnitElement[] unitElements) {
        try {
            appendTo((Appendable) stringBuilder, unitElements);
        } catch (IOException ex) {}
    }

    // Concrete implementations for ascii and unicode.

    private static class AsciiObjectPrinter extends ObjectPrinter {

        private static final ObjectPrinter INSTANCE = new AsciiObjectPrinter();

        @Override
        public void appendTo(Appendable appendable, Fraction fraction) throws IOException {
            appendable.append('^');
            appendable.append(Integer.toString(fraction.getNumerator()));
            if (Math.abs(fraction.getDenominator()) != 1) {
                appendable.append('/');
                appendable.append(Integer.toString(fraction.getDenominator()));
            }
        }
    }

    private static class UnicodeObjectPrinter extends ObjectPrinter {

        private static final ObjectPrinter INSTANCE = new UnicodeObjectPrinter();

        private static final char FRACTION_SLASH = '\u2044';
        private static final char MINUS_SIGN     = '\u207B';

        private static final char[] SUPERSCRIPT_CHARS = new char[] {
                '\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079'
        };

        private static final char[] SUBSCRIPT_CHARS = new char[] {
                '\u2080', '\u2081', '\u2082', '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088', '\u2089'
        };

        @Override
        public void appendTo(Appendable appendable, Fraction fraction) throws IOException {
            appendTo(appendable, fraction.getNumerator(), SUPERSCRIPT_CHARS);
            if (Math.abs(fraction.getDenominator()) != 1) {
                appendable.append(FRACTION_SLASH);
                appendTo(appendable, fraction.getDenominator(), SUBSCRIPT_CHARS);
            }
        }

        private void appendTo(Appendable appendable, int number, char[] chars) throws IOException {
            if (number < 0) {
                appendable.append(MINUS_SIGN);
                number = -number;
            }

            for (char ch : Integer.toString(number).toCharArray()) {
                appendable.append(chars[ch - '0']);
            }
        }
    }
}
