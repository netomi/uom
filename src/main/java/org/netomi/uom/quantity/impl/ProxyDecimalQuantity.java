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
package org.netomi.uom.quantity.impl;

import org.netomi.uom.Quantity;
import org.netomi.uom.Unit;
import org.netomi.uom.util.Proxies;
import org.netomi.uom.util.TypeUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

class ProxyDecimalQuantity<Q extends Quantity<Q>> extends AbstractDecimalQuantity<Q> {

    private final Class<Q> quantityClass;

    public static <Q extends Quantity<Q>> DecimalQuantityFactory<Q> factory(Class<Q> quantityClass) {
        return (value, mc, unit) -> {
            ProxyDecimalQuantity<Q> proxyImpl = new ProxyDecimalQuantity<>(value, mc, unit, quantityClass);
            Q proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DecimalQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    public static <Q extends Quantity<Q>> DecimalQuantityFactory<Q> factory(MathContext mc, Class<Q> quantityClass) {
        return (value, ignored, unit) -> {
            ProxyDecimalQuantity<Q> proxyImpl = new ProxyDecimalQuantity<>(value, mc, unit, quantityClass);
            Q proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DecimalQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    ProxyDecimalQuantity(BigDecimal value, MathContext mc, Unit<Q> unit, Class<Q> quantityClass) {
        super(value, mc, unit);

        Objects.requireNonNull(quantityClass);
        this.quantityClass = quantityClass;
    }

    @Override
    public Q with(BigDecimal value, MathContext mc, Unit<Q> unit) {
        return factory(quantityClass).create(value, mc, unit);
    }
}
