package ca.qc.ircm.proview.security;

import java.security.SecureRandom;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * {@link PasswordEncoder} that supports Shiro's Hash.
 */
public class ShiroPasswordEncoder implements PasswordEncoder {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ShiroPasswordEncoder.class);
  public static final String SEPARATOR = "/";
  private final SecureRandom random = new SecureRandom();
  private final String algorithm;
  private final int iterations;

  public ShiroPasswordEncoder(String algorithm, int iterations) {
    this.algorithm = algorithm;
    this.iterations = iterations;
  }

  @Override
  public String encode(CharSequence rawPassword) {
    final byte[] salt = new byte[64];
    random.nextBytes(salt);
    final SimpleHash hash = new SimpleHash(algorithm, rawPassword, salt, iterations);
    return hash.toHex() + SEPARATOR + hash.getSalt().toHex();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    if (!encodedPassword.contains(SEPARATOR)) {
      return false;
    }
    int index = encodedPassword.indexOf(SEPARATOR);
    String salt = encodedPassword.substring(index + 1);
    SimpleHash hash = new SimpleHash(algorithm, rawPassword, Hex.decode(salt), iterations);
    String expected = hash.toHex() + SEPARATOR + hash.getSalt().toHex();
    return encodedPassword.equals(expected);
  }
}
