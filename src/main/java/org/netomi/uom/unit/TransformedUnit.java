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
package org.netomi.uom.unit;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.UnitConverter;
import org.netomi.uom.math.Fraction;

import java.util.Objects;

/**
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
class TransformedUnit<Q extends Quantity<Q>> extends DelegateUnit<Q> {

    protected final String symbol;
    protected final String name;

    private final UnitConverter converterToDelegate;

    static <Q extends Quantity<Q>> Unit<Q> of(Unit<Q> unit, UnitConverter converterToDelegate) {
        if (unit instanceof TransformedUnit<?>) {
            TransformedUnit<Q> transformedUnit = (TransformedUnit<Q>) unit;

            return new TransformedUnit<>(transformedUnit.getDelegateUnit(),
                                         converterToDelegate.andThen(transformedUnit.converterToDelegate));
        } else {
            return new TransformedUnit<>(unit, converterToDelegate);
        }
    }

    protected TransformedUnit(Unit<Q> delegateUnit, UnitConverter converterToDelegate) {
        this(delegateUnit, null, null, converterToDelegate);
    }

    protected TransformedUnit(Unit<Q> delegateUnit, String symbol, String name, UnitConverter converterToDelegate) {
        super(delegateUnit);
        Objects.requireNonNull(converterToDelegate);

        this.symbol = symbol;
        this.name   = name;

        this.converterToDelegate = converterToDelegate;
    }

    @Override
    public String getSymbol() {
        return symbol != null ? symbol : super.getSymbol();
    }

    @Override
    public String getName() {
        return name != null ? name : super.getName();
    }

    @Override
    public boolean isSystemUnit() {
        return converterToDelegate.isIdentity() && getDelegateUnit().isSystemUnit();
    }

    @Override
    public UnitConverter getSystemConverter() {
        return converterToDelegate.andThen(super.getSystemConverter());
    }

    @Override
    public UnitElement[] getUnitElements() {
        return new UnitElement[] { new UnitElement(this, Fraction.ONE) };
    }

    @Override
    public Unit<Q> withSymbol(String symbol) {
        return new TransformedUnit<>(getDelegateUnit(), symbol, this.name, converterToDelegate);
    }

    @Override
    public Unit<Q> withName(String name) {
        return new TransformedUnit<>(getDelegateUnit(), this.symbol, name, converterToDelegate);
    }

    @Override
    public String toString() {
        return symbol != null ?
                super.toString() :
                String.format("(transform %s %s)", getDelegateUnit(), getSystemConverter());
    }
}
