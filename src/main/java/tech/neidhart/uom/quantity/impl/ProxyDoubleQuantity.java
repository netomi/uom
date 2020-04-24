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

import java.util.Objects;

class ProxyDoubleQuantity<Q extends Quantity<Q>> extends AbstractDoubleQuantity<Q> {

    private final Class<Q> quantityClass;

    public static <Q extends Quantity<Q>> DoubleQuantityFactory<Q> factory(Class<Q> quantityClass) {
        return (value, unit) -> {
            ProxyDoubleQuantity<Q> proxyImpl = new ProxyDoubleQuantity<>(value, unit, quantityClass);
            Q proxy = Proxies.delegatingProxy(proxyImpl, quantityClass, DoubleQuantity.class);
            TypeUtil.requireCommensurable(proxy, unit);
            return proxy;
        };
    }

    ProxyDoubleQuantity(double value, Unit<Q> unit, Class<Q> quantityClass) {
        super(value, unit);

        Objects.requireNonNull(quantityClass);
        this.quantityClass = quantityClass;
    }

    @Override
    public Q with(double value, Unit<Q> unit) {
        return factory(quantityClass).create(value, unit);
    }
}
