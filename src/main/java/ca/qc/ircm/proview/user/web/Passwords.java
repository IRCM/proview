package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.processing.GeneratePropertyNames;
import org.springframework.lang.Nullable;

/**
 * Stores password and a confirmation that should match the password.
 */
@GeneratePropertyNames
public class Passwords {

  public static final String NOT_MATCH = "notMatch";
  private String password;
  private String confirmPassword;

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nullable String password) {
    this.password = password;
  }

  @Nullable
  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(@Nullable String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}