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

/**
 * A {@code UnitConverter} implementation that converts values by adding a constant
 * offset to them.
 *
 * @author Thomas Neidhart
 */
class AddConverter implements UnitConverter {

    // the decimal offset to use for conversion.
    private final BigDecimal decimalOffset;
    // the offset in double precision, cached.
    private final double     doubleOffset;

    public AddConverter(BigDecimal offset) {
        this.decimalOffset = offset;
        this.doubleOffset  = offset.doubleValue();
    }

    public AddConverter(double offset) {
        this.decimalOffset = BigDecimal.valueOf(offset);
        this.doubleOffset  = offset;
    }

    /**
     * Returns the offset as decimal value.
     */
    public BigDecimal getDecimalOffset() {
        return decimalOffset;
    }

    /**
     * Returns the offset with double precision.
     */
    public double getOffset() {
        return doubleOffset;
    }

    @Override
    public AddConverter inverse() {
        return new AddConverter(decimalOffset.negate(MathContext.DECIMAL128));
    }

    @Override
    public UnitConverter concatenate(UnitConverter that) {
        if (that instanceof AddConverter) {
            BigDecimal offset = decimalOffset.add(((AddConverter) that).decimalOffset, MathContext.DECIMAL128);
            return UnitConverters.shift(offset);
        }

        return UnitConverter.super.concatenate(that);
    }

    @Override
    public double convert(double value) {
        return value + doubleOffset;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        return value.add(decimalOffset, context);
    }

    @Override
    public String toString() {
        return String.format("AddConverter[offset=%s]", decimalOffset.toPlainString());
    }
}
