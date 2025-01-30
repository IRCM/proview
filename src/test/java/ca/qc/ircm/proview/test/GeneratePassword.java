package ca.qc.ircm.proview.test;

import ca.qc.ircm.proview.Main;
import java.util.Random;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Generates passwords compatible with configuration.
 */
public class GeneratePassword {

  /**
   * Generates random passwords.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    try (ConfigurableApplicationContext context = SpringApplication.run(Main.class, args)) {
      PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
      final String password = "password";
      String hash = passwordEncoder.encode(password);
      System.out.println("Hashed password");
      System.out.println("password: " + hash);
      System.out.println("---------------");
    }

    Random random = new Random();
    byte[] cipherKey = new byte[16];
    random.nextBytes(cipherKey);
    System.out.println("Cipher key, base64: " + new String(Base64.encode(cipherKey)));
    System.out.println("Cipher key, hex:    0x" + new String(Hex.encode(cipherKey)));
  }
}
