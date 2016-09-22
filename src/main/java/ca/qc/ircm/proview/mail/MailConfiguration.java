package ca.qc.ircm.proview.mail;

/**
 * Mascot configuration.
 */
public interface MailConfiguration {
  public boolean isEnabled();

  public String getHost();

  public String getFrom();

  public String getTo();

  public String getSubject();
}
