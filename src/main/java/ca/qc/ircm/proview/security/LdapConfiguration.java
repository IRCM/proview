package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LDAP configuration.
 */
@ConfigurationProperties(prefix = LdapConfiguration.PREFIX)
@UsedBy(SPRING)
@SuppressWarnings("unused")
public record LdapConfiguration(boolean enabled, String idAttribute, String mailAttribute,
                                String objectClass) {

  public static final String PREFIX = "ldap";
}
