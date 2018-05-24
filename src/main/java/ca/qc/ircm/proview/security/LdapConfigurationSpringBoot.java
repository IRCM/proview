/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = LdapConfigurationSpringBoot.PREFIX)
public class LdapConfigurationSpringBoot implements LdapConfiguration {
  public static final String PREFIX = "spring.ldap";
  private String base;
  private String userDnTemplate;
  private String userFilter;
  private String idAttribute;
  private String mailAttribute;
  private LdapContextSource contextSource;
  @Inject
  private LdapTemplate ldapTemplate;

  @PostConstruct
  public void init() {
    contextSource = (LdapContextSource) ldapTemplate.getContextSource();
    ldapTemplate.setIgnorePartialResultException(true);
  }

  @Override
  public boolean enabled() {
    return contextSource.getUrls() != null && contextSource.getUrls().length > 0;
  }

  @Override
  public String url() {
    return contextSource.getUrls() != null && contextSource.getUrls().length > 0
        ? contextSource.getUrls()[0]
        : null;
  }

  @Override
  public String userDnTemplate() {
    return userDnTemplate;
  }

  @Override
  public String base() {
    return base;
  }

  @Override
  public String userFilter() {
    return userFilter;
  }

  @Override
  public String idAttribute() {
    return idAttribute;
  }

  @Override
  public String mailAttribute() {
    return mailAttribute;
  }

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getUserDnTemplate() {
    return userDnTemplate;
  }

  public void setUserDnTemplate(String userDnTemplate) {
    this.userDnTemplate = userDnTemplate;
  }

  public String getUserFilter() {
    return userFilter;
  }

  public void setUserFilter(String userFilter) {
    this.userFilter = userFilter;
  }

  public String getIdAttribute() {
    return idAttribute;
  }

  public void setIdAttribute(String idAttribute) {
    this.idAttribute = idAttribute;
  }

  public String getMailAttribute() {
    return mailAttribute;
  }

  public void setMailAttribute(String mailAttribute) {
    this.mailAttribute = mailAttribute;
  }
}
