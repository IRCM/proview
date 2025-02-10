package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.io.Serial;
import java.io.Serializable;

/**
 * Password's versions.
 *
 * @param version    version of password
 * @param algorithm  hashing algorithm
 * @param iterations hashing iterations
 */
@UsedBy(SPRING)
@SuppressWarnings("unused")
public record PasswordVersion(int version, String algorithm, int iterations)
    implements Serializable {

  @Serial
  private static final long serialVersionUID = 5651330672498933304L;

  @Override
  public String toString() {
    return "PasswordVersion [version=" + version + ", algorithm=" + algorithm + ", iterations="
        + iterations + "]";
  }
}
