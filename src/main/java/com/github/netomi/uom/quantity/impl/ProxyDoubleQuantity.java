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
package com.github.netomi.uom.quantity.impl;

import com.github.netomi.uom.util.Proxies;
import com.github.netomi.uom.Quantity;
import com.github.netomi.uom.Unit;
import com.github.netomi.uom.util.TypeUtil;

import java.util.Objects;

class ProxyDoubleQuantity<P extends Q, Q extends Quantity<Q>> extends AbstractDoubleQuantity<P, Q> {

    private final Class<P> quantityClass;

    public static <P extends Q, Q extends Quantity<Q>> DoubleQuantityFactory<P, Q> factory(Class<P> quantityClass) {
        return (value, unit) -> {
            ProxyDoubleQuantity<P, Q> proxyImpl = new ProxyDoubleQuantity<>(value, unit, quantityClass);
            P proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DoubleQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    ProxyDoubleQuantity(double value, Unit<Q> unit, Class<P> quantityClass) {
        super(value, unit);

        Objects.requireNonNull(quantityClass);
        this.quantityClass = quantityClass;
    }

    @Override
    public Class<?> getQuantityClass() {
        return quantityClass;
    }

    @Override
    public P with(double value, Unit<Q> unit) {
        return factory(quantityClass).create(value, unit);
    }
}
