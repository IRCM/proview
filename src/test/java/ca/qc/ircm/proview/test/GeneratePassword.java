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

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.Random;

public class GeneratePassword {
  public static final String PASSWORD_ALGORITHM = "SHA-256";
  public static final int PASSWORD_ITERATIONS = 1000;

  /**
   * Generates random passwords.
   *
   * @param args
   *          not used
   */
  public static void main(String[] args) throws Exception {
    final String password = "password";
    Random random = new Random();
    byte[] salt = new byte[64];
    random.nextBytes(salt);
    SimpleHash hash = new SimpleHash(PASSWORD_ALGORITHM, password, salt, PASSWORD_ITERATIONS);
    System.out.println("Hashed password");
    System.out.println("password: " + hash.toHex());
    System.out.println("salt:     " + Hex.encodeToString(salt));
    System.out.println("---------------");
    byte[] cipherKey = new byte[16];
    random.nextBytes(cipherKey);
    System.out.println("Cipher key, base64: " + new String(Base64.encode(cipherKey)));
    System.out.println("Cipher key, hex:    0x" + new String(Hex.encode(cipherKey)));
  }
}
