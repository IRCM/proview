package ca.qc.ircm.proview.user;

import java.io.Serial;

/**
 * Thrown when laboratory would have no more managers.
 */
public class UnmanagedLaboratoryException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -6024126080279259502L;
}