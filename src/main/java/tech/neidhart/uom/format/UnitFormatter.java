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

import tech.neidhart.uom.Unit;

import java.io.IOException;

/**
 * Formats instances of {@link Unit} to and from a String.
 *
 * Instances of {@code UnitFormatter} can be created by MoneyFormatterBuilder.
 *
 * This class is immutable and thread-safe.
 *
 * @author Thomas Neidhart
 */
public final class UnitFormatter {

    private final InternalFormatter<Unit<?>>[] formatters;

    UnitFormatter(InternalFormatter<Unit<?>>[] formatters) {
        this.formatters = formatters;
    }

    public String format(Unit<?> unit) throws FormatException {
        StringBuilder stringBuilder = new StringBuilder();
        formatTo(unit, stringBuilder);
        return stringBuilder.toString();
    }

    public void formatTo(Unit<?> unit, Appendable appendable) throws FormatException {
        try {
            for (InternalFormatter<Unit<?>> formatter : formatters) {
                formatter.format(unit, appendable);
            }
        } catch (IOException ex) {
            throw new FormatException(ex);
        }
    }
}
