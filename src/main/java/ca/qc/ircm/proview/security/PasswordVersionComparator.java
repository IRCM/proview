package ca.qc.ircm.proview.security;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for password versions.
 */
public class PasswordVersionComparator implements Comparator<PasswordVersion>, Serializable {
  private static final long serialVersionUID = -7244984829370331243L;

  public PasswordVersionComparator() {
  }

  @Override
  public int compare(PasswordVersion o1, PasswordVersion o2) {
    return o1.version() - o2.version();
  }
}
