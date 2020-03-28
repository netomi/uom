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
import org.netomi.uom.math.ArithmeticUtils;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author Thomas Neidhart
 */
class SquareRootConverter implements UnitConverter {

    private final UnitConverter unitConverter;
    private final double        multiplierRooted;

    SquareRootConverter(UnitConverter unitConverter, int root) {
        if (root != 2) {
            throw new IllegalArgumentException("invalid root");
        }

        this.unitConverter = unitConverter;

        // Get the multiplier from the delegate converter
        // and calculate its root for caching reasons.
        double multiplier = unitConverter.convert(1);
        multiplierRooted = Math.sqrt(multiplier);
    }

    @Override
    public UnitConverter inverse() {
        // TODO: what is the inverse of a root converter?
        return this;
    }

    @Override
    public double convert(double value) {
        return value * multiplierRooted;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext context) {
        BigDecimal multiplier = unitConverter.convert(BigDecimal.ONE, context);
        multiplier = ArithmeticUtils.sqrt(multiplier, context);
        return value.multiply(multiplier, context);
    }

    @Override
    public String toString() {
        return String.format("(* x (sqrt '%s'))", unitConverter);
    }
}
