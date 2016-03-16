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

package ca.qc.ircm.proview.security;

import org.apache.shiro.crypto.hash.SimpleHash;

public class HashedPasswordDefault implements HashedPassword {
  private String password;
  private String salt;
  private int version;

  public HashedPasswordDefault() {
  }

  /**
   * Create hashed password.
   *
   * @param password
   *          hashed password
   * @param salt
   *          salt
   * @param version
   *          version
   */
  public HashedPasswordDefault(String password, String salt, int version) {
    this.password = password;
    this.salt = salt;
    this.version = version;
  }

  /**
   * Create hashed password.
   *
   * @param hash
   *          hash containing password and salt
   * @param version
   *          version
   */
  public HashedPasswordDefault(SimpleHash hash, int version) {
    this.password = hash.toHex();
    this.salt = hash.getSalt().toHex();
    this.version = version;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getSalt() {
    return salt;
  }

  @Override
  public int getPasswordVersion() {
    return version;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }
}
