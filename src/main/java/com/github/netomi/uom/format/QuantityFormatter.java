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
package com.github.netomi.uom.format;

import com.github.netomi.uom.Quantity;

import java.io.IOException;

/**
 * Formats instances of {@link Quantity} to and from a String.
 *
 * Instances of {@code QuantityFormatter} can be created by {@link QuantityFormatterBuilder}.
 *
 * This class is immutable and thread-safe.
 *
 * @author Thomas Neidhart
 */
public final class QuantityFormatter {

    private final InternalFormatter<Quantity<?>>[] formatters;

    QuantityFormatter(InternalFormatter<Quantity<?>>[] formatters) {
        this.formatters = formatters;
    }

    public String format(Quantity<?> quantity) throws FormatException {
        StringBuilder stringBuilder = new StringBuilder();
        formatTo(quantity, stringBuilder);
        return stringBuilder.toString();
    }

    public void formatTo(Quantity<?> quantity, Appendable appendable) throws FormatException {
        try {
            for (InternalFormatter<Quantity<?>> formatter : formatters) {
                formatter.format(quantity, appendable);
            }
        } catch (IOException ex) {
            throw new FormatException(ex);
        }
    }
}
