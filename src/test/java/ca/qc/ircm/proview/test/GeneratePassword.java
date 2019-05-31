/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.test;

import ca.qc.ircm.proview.Main;
import java.util.Random;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GeneratePassword {
  /**
   * Generates random passwords.
   *
   * @param args
   *          not used
   */
  public static void main(String[] args) throws Exception {
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
