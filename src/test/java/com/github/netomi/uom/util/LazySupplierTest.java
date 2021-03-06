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
package com.github.netomi.uom.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LazySupplierTest {

    @Test
    public void getter() {
        AtomicReference<Integer> atomicReference = new AtomicReference<>(0);

        Supplier<Integer> supplier = () -> atomicReference.updateAndGet(x -> x + 1);

        assertEquals(0, atomicReference.get());

        Supplier<Integer> lazySupplier = LazySupplier.of(supplier);

        assertEquals(0, atomicReference.get());

        assertEquals(1, lazySupplier.get());

        assertEquals(1, atomicReference.get());
        assertEquals(1, lazySupplier.get());
    }
}
