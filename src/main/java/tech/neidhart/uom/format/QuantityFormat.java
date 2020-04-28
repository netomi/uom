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
package tech.neidhart.uom.format;

import java.text.DecimalFormat;

/**
 * @author Thomas Neidhart
 */
public final class QuantityFormat {

    private static final QuantityFormatter defaultFormatter;

    static {
        // use scientific notation for values.
        DecimalFormat decimalFormat = new DecimalFormat("0.0E0");
        decimalFormat.setMaximumFractionDigits(340);
        decimalFormat.setGroupingUsed(false);

        defaultFormatter =
            new QuantityFormatterBuilder().appendValue(decimalFormat)
                                          .appendLiteral(' ')
                                          .appendUnit(UnitFormat.symbol())
                                          .toFormatter();
    }

    // hide constructor.
    private QuantityFormat() {}

    public static QuantityFormatter defaultFormatter() {
        return defaultFormatter;
    }
}
