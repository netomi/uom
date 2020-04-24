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
package tech.neidhart.uom;

import java.text.MessageFormat;

/**
 * Represents a {@link RuntimeException} that is thrown if an operation
 * can not be performed due to a mismatch of {@link Dimension}s.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Unit_commensurability#Commensurability">Wikipedia: Unit Commensurability</a>
 *
 * @author Thomas Neidhart
 */
public class IncommensurableException extends RuntimeException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 202003202132L;

    /**
     * Create an exception where the message is constructed by applying
     * the {@code format()} method from {@code java.text.MessageFormat}.
     *
     * @param message         the exception message with replaceable parameters.
     * @param formatArguments the arguments for formatting the message.
     */
    public IncommensurableException(String message, Object... formatArguments) {
        super(MessageFormat.format(message, formatArguments));
    }

}
