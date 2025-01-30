package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ShiroPasswordEncoder}.
 */
@NonTransactionalTestAnnotations
public class ShiroPasswordEncoderTest {

  private String algorithm = Sha256Hash.ALGORITHM_NAME;
  private int iterations = 1000;
  private ShiroPasswordEncoder passwordEncoder = new ShiroPasswordEncoder(algorithm, iterations);

  @Test
  public void encode() {
    String password = "password";
    String hashed = passwordEncoder.encode(password);
    String salt = hashed.substring(hashed.indexOf(ShiroPasswordEncoder.SEPARATOR) + 1);
    SimpleHash shiroHash = new SimpleHash(algorithm, password, Hex.decode(salt), iterations);
    String expected = shiroHash.toHex() + "/" + shiroHash.getSalt().toHex();
    assertEquals(expected, hashed);
  }

  @Test
  public void encode_SecondPassword() {
    String password = "test";
    String hashed = passwordEncoder.encode(password);
    String salt = hashed.substring(hashed.indexOf(ShiroPasswordEncoder.SEPARATOR) + 1);
    SimpleHash shiroHash = new SimpleHash(algorithm, password, Hex.decode(salt), iterations);
    String expected = shiroHash.toHex() + "/" + shiroHash.getSalt().toHex();
    assertEquals(expected, hashed);
  }

  @Test
  public void matches() {
    String password = "password";
    String hashed = "b29775bf7946df11a0e73216a87ee4cd44acd398570723559b1a14699330d8d7"
        + ShiroPasswordEncoder.SEPARATOR
        + "d04bf2902bf87be882795dc357490bae6db48f06d773f3cb0c0d3c544a4a7d734c"
        + "022d75d58bfe5c6a5193f520d0124beff4d39deaf65755e66eb7785c08208d";
    assertTrue(passwordEncoder.matches(password, hashed));
  }
}
