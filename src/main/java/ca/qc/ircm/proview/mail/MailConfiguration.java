package ca.qc.ircm.proview.mail;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mascot configuration.
 */
@ConfigurationProperties(prefix = MailConfiguration.PREFIX)
@SuppressWarnings("unused")
@UsedBy(SPRING)
public record MailConfiguration(boolean enabled, String from, String to, String subject) {

  public static final String PREFIX = "email";
}
