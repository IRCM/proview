package ca.qc.ircm.proview.security;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * Represents an hashed password that can be saved in database.
 */
public class HashedPassword {

  /**
   * Hex encrypted hashed password.
   */
  private final String password;
  /**
   * Salt added to password before hashing.
   */
  private final String salt;
  /**
   * Password version in case encryption changes.
   */
  private final int version;

  /**
   * Create hashed password.
   *
   * @param password hashed password
   * @param salt     salt
   * @param version  version
   */
  public HashedPassword(String password, String salt, int version) {
    this.password = password;
    this.salt = salt;
    this.version = version;
  }

  /**
   * Create hashed password.
   *
   * @param hash    hash containing password and salt
   * @param version version
   */
  public HashedPassword(SimpleHash hash, int version) {
    this.password = hash.toHex();
    this.salt = hash.getSalt().toHex();
    this.version = version;
  }

  public String getPassword() {
    return password;
  }

  public String getSalt() {
    return salt;
  }

  public int getPasswordVersion() {
    return version;
  }
}