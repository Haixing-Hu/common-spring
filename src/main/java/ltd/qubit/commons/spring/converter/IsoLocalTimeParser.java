////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.spring.converter;

import java.time.LocalTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ltd.qubit.commons.util.codec.DecodingException;
import ltd.qubit.commons.util.codec.IsoLocalTimeCodec;

/**
 * 将字符串转换为 {@link LocalTime} 对象的转换器。
 *
 * <p>该转换器使用 {@link IsoLocalTimeCodec} 将符合 ISO 8601 格式的字符串解析为 {@link LocalTime} 对象。
 *
 * @author 胡海星
 */
@Component
@CustomizedConverter
public class IsoLocalTimeParser implements Converter<String, LocalTime> {

  /**
   * 用于解码符合 ISO 8601 格式的本地时间的编解码器。
   */
  private final IsoLocalTimeCodec codec = new IsoLocalTimeCodec();

  /**
   * 将给定的字符串转换为 {@link LocalTime} 对象。
   *
   * @param s
   *     要转换的字符串，应符合 ISO 8601 本地时间格式 (例如 "10:15:30")。
   * @return 转换后的 {@link LocalTime} 对象。
   * @throws RuntimeException
   *     如果解析过程中发生 {@link DecodingException}。
   */
  @Override
  public LocalTime convert(final String s) {
    try {
      return codec.decode(s);
    } catch (final DecodingException e) {
      throw new RuntimeException(e);
    }
  }
}