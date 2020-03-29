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
package org.netomi.uom.function;

import org.netomi.uom.UnitConverter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * A {@code UnitConverter} implementation that converts values by adding a constant
 * offset to them.
 *
 * @author Thomas Neidhart
 */
class AddConverter implements UnitConverter {

    // the decimal offset to use for conversion.
    private final BigDecimal offset;
    // the offset in double precision, cached.
    private final double     offsetAsDouble;

    public AddConverter(BigDecimal value) {
        this.offset          = value;
        this.offsetAsDouble  = offset.doubleValue();
    }

    public AddConverter(double value) {
        this.offset          = BigDecimal.valueOf(value);
        this.offsetAsDouble  = offset.doubleValue();
    }

    /**
     * Returns the offset as decimal value.
     */
    public BigDecimal getOffset() {
        return offset;
    }

    @Override
    public boolean isLinear() {
        return false;
    }

    @Override
    public AddConverter inverse() {
        return new AddConverter(offset.negate());
    }

    @Override
    public UnitConverter compose(UnitConverter that) {
        if (that instanceof AddConverter) {
            BigDecimal newOffset = offset.add(((AddConverter) that).offset);
            return UnitConverters.shift(newOffset);
        }

        return UnitConverter.super.compose(that);
    }

    @Override
    public UnitConverter andThen(UnitConverter that) {
        if (that instanceof AddConverter) {
            BigDecimal newOffset = offset.add(((AddConverter) that).offset);
            return UnitConverters.shift(newOffset);
        }

        return UnitConverter.super.andThen(that);
    }

    @Override
    public double convert(double value) {
        return value + offsetAsDouble;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        return value.add(offset, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddConverter that = (AddConverter) o;
        return Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset);
    }

    @Override
    public String toString() {
        return String.format("(+ x '%s')", offset.toString());
    }
}
