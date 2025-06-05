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
 * 用于在字符串和 {@link LocalDate} 实例之间进行转换的类型转换器。
 *
 * <p>默认格式模式支持以下日期值：
 * <ul>
 *   <li><code>2017-01-01</code></li>
 *   <li><code>2017-1-1</code></li>
 *   <li><code>2017/01/01</code></li>
 *   <li><code>2017/1/1</code></li>
 * </ul>
 *
 * <p><b>注意：</b>空字符串、空白字符串或 {@code null} 值将被转换为 {@code null} 值。
 *
 * @author 胡海星
 */
@Component
@CustomizedConverter
public final class LocalDateParser implements Converter<String, LocalDate> {

  /**
   * 编码（格式化）{@link LocalDate} 对象时使用的模式。
   */
  public static final String ENCODE_PATTERN = "yyyy-MM-dd";

  /**
   * 解码（解析）日期字符串时使用的模式。
   */
  public static final String DECODE_PATTERN = "yyyy['-']['/']M['-']['/']d";

  /**
   * 日志记录器。
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateParser.class);

  /**
   * 用于本地日期编解码的编解码器实例。
   */
  private final LocalDateCodec codec = new LocalDateCodec(ENCODE_PATTERN,
          DECODE_PATTERN, true, true);

  /**
   * 将给定的源字符串转换为 {@link LocalDate} 对象。
   *
   * @param source
   *     要转换的源字符串。
   * @return 转换后的 {@link LocalDate} 对象，如果源字符串无效则返回 {@code null}。
   */
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