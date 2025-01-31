package ca.qc.ircm.proview.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LDAP configuration.
 */
@ConfigurationProperties(prefix = LdapConfiguration.PREFIX)
public record LdapConfiguration(boolean enabled, String idAttribute, String mailAttribute,
                                String objectClass) {

  public static final String PREFIX = "ldap";
}
