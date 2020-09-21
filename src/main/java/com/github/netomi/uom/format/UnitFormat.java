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

/**
 * @author Thomas Neidhart
 */
public final class UnitFormat {

    private static final UnitFormatter symbolFormatter =
            new UnitFormatterBuilder().appendSymbol().toFormatter();

    private static final UnitFormatter nameFormatter =
            new UnitFormatterBuilder().appendName().toFormatter();

    private static final UnitFormatter symbolAndDimensionFormatter =
            new UnitFormatterBuilder().appendSymbol()
                                      .appendLiteral('{')
                                      .appendDimension()
                                      .appendLiteral('}')
                                      .toFormatter();

    // hide constructor.
    private UnitFormat() {}

    public static UnitFormatter symbol() {
        return symbolFormatter;
    }

    public static UnitFormatter name() {
        return nameFormatter;
    }

    public static UnitFormatter symbolAndDimension() {
        return symbolAndDimensionFormatter;
    }

}
