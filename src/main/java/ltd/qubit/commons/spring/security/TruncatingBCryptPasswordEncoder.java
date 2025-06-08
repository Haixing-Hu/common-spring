/// /////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2025.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
/// /////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.spring.security;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 一个对输入密码超过 72 字节自动截断并打印警告日志的 BCrypt 密码编码器。
 * <p>
 * Bcrypt 底层基于 Blowfish 加密，密钥（即密码）最长只支持 72 字节。如果密码超过该长度，
 * 超出的部分会被截断（或忽略）。Spring Security 的早期实现通常是"静默截断"，即
 * {@link BCryptPasswordEncoder}仍会正常工作，只是不会告警；但从 Spring Security 5.8+
 * （或在某些版本的回归中）开始，{@code encode()}函数会在长度超过 72 字节时抛出
 * {@code IllegalArgumentException}，以避免冷启动安全漏洞或其他不一致行为。
 * <p>
 * 为了保持向后兼容性，本类在编码和匹配密码时会自动截断超过 72 字节的密码，并记录警告日志。
 *
 * @author 胡海星
 */
public class TruncatingBCryptPasswordEncoder extends BCryptPasswordEncoder {

  /** bcrypt 最大可用密码长度（字节） */
  private static final int MAX_BYTES = 72;

  private static final Logger logger = LoggerFactory.getLogger(TruncatingBCryptPasswordEncoder.class);

  public TruncatingBCryptPasswordEncoder() {
    super();
  }

  public TruncatingBCryptPasswordEncoder(final int strength) {
    super(strength);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String encode(final CharSequence rawPassword) {
    final String toEncode = truncateIfNeeded(rawPassword);
    return super.encode(toEncode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
    final String toMatch = truncateIfNeeded(rawPassword);
    return super.matches(toMatch, encodedPassword);
  }

  /**
   * 如果 UTF-8 编码后长度超过 MAX_BYTES，则截断并记录警告。
   */
  private String truncateIfNeeded(final CharSequence raw) {
    final String rawPassword = raw.toString();
    final byte[] bytes = rawPassword.getBytes(UTF_8);
    if (bytes.length <= MAX_BYTES) {
      return rawPassword;
    }
    // Truncate the password to MAX_BYTES bytes, ensuring that the truncated
    // bytes form a valid UTF-8 string.
    final ByteBuffer bb = ByteBuffer.wrap(bytes, 0, MAX_BYTES);
    final CharBuffer cb = CharBuffer.allocate(MAX_BYTES);
    final CharsetDecoder decoder = UTF_8.newDecoder();
    // Silently ignore malformed input, effectively truncating at the last
    // valid character.
    decoder.onMalformedInput(CodingErrorAction.IGNORE);
    decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
    decoder.decode(bb, cb, true);
    decoder.flush(cb);
    final String result = new String(cb.array(), 0, cb.position());
    logger.warn("Password length of {} bytes exceeds BCrypt limit of {} bytes; truncated to {} bytes.",
        bytes.length, MAX_BYTES, result.getBytes(UTF_8).length);
    return result;
  }
}
