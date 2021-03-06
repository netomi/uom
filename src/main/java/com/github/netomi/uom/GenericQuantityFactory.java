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
package com.github.netomi.uom;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A factory interface to be able to create generic quantities.
 *
 * @param <Q> the quantity type
 *
 * @author Thomas Neidhart
 */
public interface GenericQuantityFactory<Q extends Quantity<Q>> {
    Quantity<Q> create(double value, Unit<Q> unit);

    Quantity<Q> create(BigDecimal value, Unit<Q> unit);

    Quantity<Q> create(BigDecimal value, MathContext mc, Unit<Q> unit);
}
