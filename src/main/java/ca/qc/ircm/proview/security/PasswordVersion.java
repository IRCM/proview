package ca.qc.ircm.proview.security;

import java.io.Serializable;

/**
 * Password's versions.
 * 
 * @param version
 *          version of password
 * @param algorithm
 *          hashing algorithm
 * @param iterations
 *          hashing iterations
 */
public record PasswordVersion(int version, String algorithm, int iterations)
    implements Serializable {

  private static final long serialVersionUID = 5651330672498933304L;

  @Override
  public String toString() {
    return "PasswordVersion [version=" + version + ", algorithm=" + algorithm + ", iterations="
        + iterations + "]";
  }
}
