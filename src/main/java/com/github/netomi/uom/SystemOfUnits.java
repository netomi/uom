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

/**
 * A {@link SystemOfUnits} is basically a container for a set of {@link Unit}'s.
 */
public interface SystemOfUnits {

    /**
     * Returns the name of the container.
     *
     * @return the name of the container.
     */
    String getName();

    /**
     * Returns an {@link Iterable} containing all the units of this container.
     *
     * @return an iterable of all units contained in this container.
     */
    Iterable<Unit<?>> getUnits();
}