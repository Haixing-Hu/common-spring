package ltd.qubit.commons.spring.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TruncatingBCryptPasswordEncoderTest {

  private static final int MAX_BYTES = 72;

  private TruncatingBCryptPasswordEncoder encoder;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    encoder = new TruncatingBCryptPasswordEncoder();
    final Logger logger = (Logger) LoggerFactory.getLogger(TruncatingBCryptPasswordEncoder.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    listAppender.stop();
    final Logger logger = (Logger) LoggerFactory.getLogger(TruncatingBCryptPasswordEncoder.class);
    logger.detachAppender(listAppender);
  }

  private void assertNoWarning() {
    assertTrue(listAppender.list.isEmpty(), "Should not log any warning.");
  }

  private void assertWarningLogged(final int actualLength, final int truncatedLength) {
    final List<ILoggingEvent> logs = listAppender.list;
    assertEquals(1, logs.size(), "Expected exactly one log message.");
    final String message = logs.get(0).getFormattedMessage();
    final String expectedMessage = String.format(
        "Password length of %d bytes exceeds BCrypt limit of %d bytes; truncated to %d bytes.",
        actualLength, MAX_BYTES, truncatedLength);
    assertEquals(expectedMessage, message);
  }

  @Test
  void testShortPassword() {
    final String shortPassword = "password";
    final String encodedPassword = encoder.encode(shortPassword);
    assertNotNull(encodedPassword);
    assertTrue(encoder.matches(shortPassword, encodedPassword));
    assertFalse(encoder.matches("wrongpassword", encodedPassword));
    assertNoWarning();
  }

  @Test
  void testPasswordAtMaxLength() {
    final String maxLengthPassword = "a".repeat(MAX_BYTES);
    assertEquals(MAX_BYTES, maxLengthPassword.getBytes(StandardCharsets.UTF_8).length);
    final String encodedPassword = encoder.encode(maxLengthPassword);
    assertTrue(encoder.matches(maxLengthPassword, encodedPassword));
    assertNoWarning();
  }

  @Test
  void testLongPasswordEncodeAndMatches() {
    final String longPassword = "a".repeat(100);
    final String truncatedPassword = "a".repeat(MAX_BYTES);

    final String encodedLongPassword = encoder.encode(longPassword);
    assertWarningLogged(100, 72);
    listAppender.list.clear(); // Clear log for next check

    // The long password should match its own encoded version
    assertTrue(encoder.matches(longPassword, encodedLongPassword));
    assertWarningLogged(100, 72);
    listAppender.list.clear();

    // The truncated version of the password should also match
    assertTrue(encoder.matches(truncatedPassword, encodedLongPassword));
    assertNoWarning(); // No truncation, so no warning

    // A different long password with the same prefix should also match
    final String anotherLongPasswordWithSamePrefix = "a".repeat(MAX_BYTES) + "b".repeat(28);
    assertTrue(encoder.matches(anotherLongPasswordWithSamePrefix, encodedLongPassword));
    assertWarningLogged(100, 72);
    listAppender.list.clear();

    // A password with a different prefix should not match
    final String differentPrefixPassword = "b".repeat(MAX_BYTES) + "a".repeat(28);
    assertFalse(encoder.matches(differentPrefixPassword, encodedLongPassword));
    assertWarningLogged(100, 72); // It still truncates and warns
    listAppender.list.clear();

    // A short password that is not the prefix should not match
    assertFalse(encoder.matches("password", encodedLongPassword));
    assertNoWarning();
  }

  @Test
  void testLongPasswordWithMultiByteCharAroundTruncationPoint() {
    // '€' is 3 bytes in UTF-8. Create a password that is 74 bytes long.
    final String longPassword = "a".repeat(71) + "€";
    final int byteLength = longPassword.getBytes(StandardCharsets.UTF_8).length;
    assertEquals(74, byteLength);

    final String encodedPassword = encoder.encode(longPassword);
    assertWarningLogged(byteLength, 71);
    listAppender.list.clear();

    assertTrue(encoder.matches(longPassword, encodedPassword));
    assertWarningLogged(byteLength, 71);
    listAppender.list.clear();

    // The correctly truncated password should also match, and not log a warning.
    final String correctlyTruncatedPassword = "a".repeat(71);
    assertTrue(encoder.matches(correctlyTruncatedPassword, encodedPassword));
    assertNoWarning();
  }
} 