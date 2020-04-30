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
package tech.neidhart.uom.quantity.impl;

import tech.neidhart.uom.Quantity;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.util.Proxies;
import tech.neidhart.uom.util.TypeUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

class ProxyDecimalQuantity<P extends Q, Q extends Quantity<Q>> extends AbstractDecimalQuantity<P, Q> {

    private final Class<P> quantityClass;

    public static <P extends Q, Q extends Quantity<Q>> DecimalQuantityFactory<P, Q> factory(Class<P> quantityClass) {
        return (value, mc, unit) -> {
            ProxyDecimalQuantity<P, Q> proxyImpl = new ProxyDecimalQuantity<>(value, mc, unit, quantityClass);
            P proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DecimalQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    public static <P extends Q, Q extends Quantity<Q>> DecimalQuantityFactory<P, Q> factory(MathContext mc, Class<P> quantityClass) {
        return (value, ignored, unit) -> {
            ProxyDecimalQuantity<P, Q> proxyImpl = new ProxyDecimalQuantity<>(value, mc, unit, quantityClass);
            P proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DecimalQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    ProxyDecimalQuantity(BigDecimal value, MathContext mc, Unit<Q> unit, Class<P> quantityClass) {
        super(value, mc, unit);

        Objects.requireNonNull(quantityClass);
        this.quantityClass = quantityClass;
    }

    @Override
    protected Class<?> getQuantityClass() {
        return quantityClass;
    }

    @Override
    public P with(BigDecimal value, MathContext mc, Unit<Q> unit) {
        return factory(quantityClass).create(value, mc, unit);
    }
}
