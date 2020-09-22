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
package com.github.netomi.uom.format;

import com.github.netomi.uom.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder to create unit formatters.
 * <p>
 * <i>This class is a mutable builder intended for use from a single thread.</i>
 *
 * @author Thomas Neidhart
 */
public final class UnitFormatterBuilder {

    private List<InternalFormatter<Unit<?>>> formatters = new ArrayList<>();

    public UnitFormatterBuilder appendSymbol() {
        formatters.add((unit, appendable) -> appendable.append(unit.getSymbol()));
        return this;
    }

    public UnitFormatterBuilder appendName() {
        formatters.add((unit, appendable) -> appendable.append(unit.getName()));
        return this;
    }

    public UnitFormatterBuilder appendDimension() {
        formatters.add((unit, appendable) -> appendable.append(unit.getDimension().toString()));
        return this;
    }

    public UnitFormatterBuilder appendLiteral(char character) {
        formatters.add((object, appendable) -> appendable.append(character));
        return this;
    }

    public UnitFormatterBuilder appendLiteral(String literal) {
        formatters.add((object, appendable) -> appendable.append(literal));
        return this;
    }

    @SuppressWarnings("unchecked")
    public UnitFormatter toFormatter() {
        return new UnitFormatter(formatters.toArray(new InternalFormatter[0]));
    }

}
