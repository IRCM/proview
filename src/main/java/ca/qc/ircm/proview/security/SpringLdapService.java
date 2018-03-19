/*
 * Copyright (c) 2007 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.security;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.inject.Inject;
import javax.naming.NamingException;

/**
 * Services for LDAP (active directory).
 */
@Component
public class SpringLdapService {
  private static final Logger logger = LoggerFactory.getLogger(SpringLdapService.class);
  @Inject
  private LdapTemplate ldapTemplate;
  @Inject
  private LdapConfiguration ldapConfiguration;

  protected SpringLdapService() {
  }

  public SpringLdapService(LdapTemplate ldapTemplate, LdapConfiguration ldapConfiguration) {
    this.ldapTemplate = ldapTemplate;
    this.ldapConfiguration = ldapConfiguration;
  }

  /**
   * Returns true if user exists in LDAP and password is valid, false otherwise.
   *
   * @param username
   *          username
   * @param password
   *          password
   * @return true if user exists in LDAP and password is valid, false otherwise
   */
  public boolean passwordValid(String username, String password) {
    try {
      LdapQuery query = query().where(ldapConfiguration.idAttribute()).is(username);
      ldapTemplate.authenticate(query, password);
      logger.debug("Valid LDAP password for user [{}]", username);
      return true;
    } catch (Exception e) {
      logger.debug("Invalid LDAP password for user [{}]", username, e);
      return false;
    }
  }

  /**
   * Returns user's email from LDAP.
   *
   * @param username
   *          username
   * @return user's email from LDAP or null if user does not exists
   */
  public String email(String username) {
    LdapQuery query = query().attributes(ldapConfiguration.mailAttribute())
        .where(ldapConfiguration.idAttribute()).is(username);
    AttributesMapper<String> mapper =
        attrs -> Optional.ofNullable(attrs.get(ldapConfiguration.mailAttribute())).map(attr -> {
          try {
            return attr.get();
          } catch (NamingException e) {
            return null;
          }
        }).map(value -> value.toString()).orElse(null);
    String email = ldapTemplate.search(query, mapper).stream().findFirst().orElse(null);
    logger.debug("Found LDAP email {} for user [{}]", username, email);
    return email;
  }

  /**
   * Returns user's username on LDAP.
   *
   * @param email
   *          user's email
   * @return user's username on LDAP or null if user does not exists
   */
  public String username(String email) {
    LdapQuery query = query().attributes(ldapConfiguration.idAttribute())
        .where(ldapConfiguration.mailAttribute()).is(email);
    AttributesMapper<String> mapper =
        attrs -> Optional.ofNullable(attrs.get(ldapConfiguration.idAttribute())).map(attr -> {
          try {
            return attr.get();
          } catch (NamingException e) {
            return null;
          }
        }).map(value -> value.toString()).orElse(null);
    String username = ldapTemplate.search(query, mapper).stream().findFirst().orElse(null);
    logger.debug("Found LDAP username {} for user [{}]", username, email);
    return username;
  }
}