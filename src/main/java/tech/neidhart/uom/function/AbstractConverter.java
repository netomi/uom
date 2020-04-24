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
package tech.neidhart.uom.function;

import tech.neidhart.uom.UnitConverter;
import tech.neidhart.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Base class for {@link UnitConverter} implementations, provides some default
 * implementations.
 *
 * @author Thomas Neidhart
 */
abstract class AbstractConverter implements UnitConverter {

    @Override
    public double scale() {
        if (!isLinear()) {
            throw new UnsupportedOperationException("scale() is only supported for linear converters.");
        }

        return convert(1);
    }

    @Override
    public BigFraction scaleAsFraction() {
        if (!isLinear()) {
            throw new UnsupportedOperationException("scale() is only supported for linear converters.");
        }

        return BigFraction.from(convert(BigDecimal.ONE));
    }

    @Override
    public BigDecimal scale(MathContext mc) {
        if (!isLinear()) {
            throw new UnsupportedOperationException("scale() is only supported for linear converters.");
        }

        return convert(BigDecimal.ONE, mc);
    }

    @Override
    public BigDecimal convert(BigDecimal value) {
        return convert(value, MathContext.DECIMAL128);
    }

    @Override
    public UnitConverter compose(UnitConverter before) {
        return UnitConverters.compose(before, this);
    }

    @Override
    public UnitConverter andThen(UnitConverter after) {
        return UnitConverters.compose(this, after);
    }
}
