package ca.qc.ircm.proview.msanalysis;

import java.io.Serial;

/**
 * Thrown when inserting a MS analysis containing samples from more than one user.
 */
public class SamplesFromMultipleUserException extends Exception {

  @Serial
  private static final long serialVersionUID = 3547984386040611594L;
}