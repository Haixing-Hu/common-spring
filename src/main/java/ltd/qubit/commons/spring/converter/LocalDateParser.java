////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.spring.converter;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ltd.qubit.commons.util.codec.DecodingException;
import ltd.qubit.commons.util.codec.LocalDateCodec;

/**
 * A type converter used to convert between strings and {@link LocalDate}
 * instances.
 *
 * <p>The default format pattern support the following date values:
 * <ul>
 *   <li><code>2017-01-01</code></li>
 *   <li><code>2017-1-1</code></li>
 *   <li><code>2017/01/01</code></li>
 *   <li><code>2017/1/1</code></li>
 * </ul>
 *
 * <p><b>NOTE: </b> an empty string, a blank string, or a {@code null} value will
 * be converted into a {@code null} value.
 *
 * @author Haixing Hu
 */
@Component
@CustomizedConverter
public final class LocalDateParser implements Converter<String, LocalDate> {

  public static final String ENCODE_PATTERN = "yyyy-MM-dd";

  public static final String DECODE_PATTERN = "yyyy['-']['/']M['-']['/']d";

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateParser.class);

  private final LocalDateCodec codec = new LocalDateCodec(ENCODE_PATTERN,
          DECODE_PATTERN, true, true);

  @Override
  public LocalDate convert(final String source) {
    try {
      return codec.decode(source);
    } catch (final DecodingException e) {
      LOGGER.error("Invalid date format: {}", source);
      return null;
    }
  }
}
