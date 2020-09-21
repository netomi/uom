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
import com.github.netomi.uom.quantity.impl.DecimalQuantity;
import com.github.netomi.uom.quantity.impl.DoubleQuantity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder to create unit formatters.
 * <p>
 * <i>This class is a mutable builder intended for use from a single thread.</i>
 *
 * @author Thomas Neidhart
 */
public final class QuantityFormatterBuilder {

    private List<InternalFormatter<Quantity<?>>> formatters = new ArrayList<>();

    public QuantityFormatterBuilder appendValue() {
        return appendValue(DecimalFormat.getInstance());
    }

    public QuantityFormatterBuilder appendValue(NumberFormat format) {
        formatters.add((quantity, appendable) -> {
            if (quantity instanceof DoubleQuantity<?, ?>) {
                appendable.append(format.format(quantity.doubleValue()));
            } else if (quantity instanceof DecimalQuantity<?, ?>) {
                appendable.append(format.format(quantity.decimalValue()));
            } else {
                throw new UnsupportedOperationException("unsupported quantity class: " + quantity.getClass());
            }
        });
        return this;
    }

    public QuantityFormatterBuilder appendUnit(UnitFormatter unitFormatter) {
        formatters.add((quantity, appendable) -> unitFormatter.formatTo(quantity.getUnit(), appendable));
        return this;
    }

    public QuantityFormatterBuilder appendLiteral(char character) {
        formatters.add((object, appendable) -> appendable.append(character));
        return this;
    }

    public QuantityFormatterBuilder appendLiteral(String literal) {
        formatters.add((object, appendable) -> appendable.append(literal));
        return this;
    }

    @SuppressWarnings("unchecked")
    public QuantityFormatter toFormatter() {
        return new QuantityFormatter(formatters.toArray(new InternalFormatter[0]));
    }
}
