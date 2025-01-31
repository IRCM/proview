package ca.qc.ircm.proview.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mascot configuration.
 */
@ConfigurationProperties(prefix = MailConfiguration.PREFIX)
public record MailConfiguration(boolean enabled, String from, String to, String subject) {

  public static final String PREFIX = "email";
}
