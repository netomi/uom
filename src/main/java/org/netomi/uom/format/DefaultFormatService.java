/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2020, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.netomi.uom.format;

import static tech.units.indriya.format.FormatBehavior.LOCALE_SENSITIVE;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.measure.format.QuantityFormat;
import javax.measure.spi.FormatService;

/**
 * Default format service.
 *
 * @author Werner Keil
 * @version 1.0, March 19, 2019
 * @since 2.0
 */
public class DefaultFormatService extends DefaultUnitFormatService implements FormatService {

  private static final String DEFAULT_FORMAT_NAME = "Simple";

  /**
   * Holds the default format instance (EBNFUnitFormat).
   */
  private static final NumberDelimiterQuantityFormat EBNF_QUANTITY_FORMAT = new NumberDelimiterQuantityFormat.Builder()
      .setNumberFormat(NumberFormat.getInstance(Locale.ROOT))
      .setUnitFormat(EBNFUnitFormat.getInstance())
      .build();
  
  private final Map<String, QuantityFormat> quantityFormats = new HashMap<>();

  public DefaultFormatService() {
    super();
    quantityFormats.put(DEFAULT_FORMAT_NAME, SimpleQuantityFormat.getInstance());
    quantityFormats.put("NumberDelimiter", NumberDelimiterQuantityFormat.getInstance());
    quantityFormats.put("EBNF", EBNF_QUANTITY_FORMAT);
    quantityFormats.put("Local", NumberDelimiterQuantityFormat.getInstance(LOCALE_SENSITIVE));
  }


  @Override
  public QuantityFormat getQuantityFormat(String name) {
    return quantityFormats.get(name);
  }

  @Override
  public QuantityFormat getQuantityFormat() {
    return getQuantityFormat(DEFAULT_FORMAT_NAME);
  }

  @Override
  public Set<String> getAvailableFormatNames(FormatType type) {
    switch (type) {
      case QUANTITY_FORMAT:
        return quantityFormats.keySet();
      default:
        return unitFormats.keySet();
    }
  }
}
