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
import org.netomi.uom.math.BigFraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

/**
 * Base class for {@link UnitConverter} implementations, provides some default
 * implementations.
 *
 * @author Thomas Neidhart
 */
abstract class AbstractConverter implements UnitConverter {

    @Override
    public Optional<BigFraction> scale() {
        return isLinear() ?
                Optional.of(BigFraction.from(convert(BigDecimal.ONE))) :
                Optional.empty();
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
