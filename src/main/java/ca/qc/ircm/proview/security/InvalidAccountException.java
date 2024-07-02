package ca.qc.ircm.proview.security;

import org.apache.shiro.authc.AccountException;

/**
 * Exception thrown when account was not yet validated.
 */
public class InvalidAccountException extends AccountException {
  private static final long serialVersionUID = 4348465312844508305L;

  public InvalidAccountException() {
    super();
  }

  public InvalidAccountException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAccountException(String message) {
    super(message);
  }

  public InvalidAccountException(Throwable cause) {
    super(cause);
  }
}
