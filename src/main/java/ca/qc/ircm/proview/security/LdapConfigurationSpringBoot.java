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

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class LdapConfigurationSpringBoot implements LdapConfiguration {
  private boolean enabled;
  private String url;
  private String userDnTemplate;
  private String searchBase;
  private String searchFilter;
  private String mailAttribute;
  private LdapContextSource contextSource;
  @Inject
  private LdapTemplate ldapTemplate;

  @PostConstruct
  public void init() {
    contextSource = (LdapContextSource) ldapTemplate.getContextSource();
  }

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public String url() {
    return contextSource.getUrls()[0];
  }

  @Override
  public String userDnTemplate() {
    return userDnTemplate;
  }

  @Override
  public String searchBase() {
    return searchBase;
  }

  @Override
  public String searchFilter() {
    return searchFilter;
  }

  @Override
  public String mailAttribute() {
    return mailAttribute;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUserDnTemplate() {
    return userDnTemplate;
  }

  public void setUserDnTemplate(String userDnTemplate) {
    this.userDnTemplate = userDnTemplate;
  }

  public String getSearchBase() {
    return searchBase;
  }

  public void setSearchBase(String searchBase) {
    this.searchBase = searchBase;
  }

  public String getSearchFilter() {
    return searchFilter;
  }

  public void setSearchFilter(String searchFilter) {
    this.searchFilter = searchFilter;
  }

  public String getMailAttribute() {
    return mailAttribute;
  }

  public void setMailAttribute(String mailAttribute) {
    this.mailAttribute = mailAttribute;
  }
}
