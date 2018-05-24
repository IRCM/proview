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

import java.util.List;
import org.apache.shiro.realm.Realm;

/**
 * Security configuration.
 */
public interface SecurityConfiguration {
  public String realmName();

  public String authorizationCacheName();

  public Realm shiroRealm();

  public List<PasswordVersion> getPasswordVersions();

  public PasswordVersion getPasswordVersion();

  public byte[] getCipherKeyBytes();

  public int maximumSignAttemps();

  public long maximumSignAttempsDelay();

  public int disableSignAttemps();
}
