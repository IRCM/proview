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

/**
 * Builder for password's versions.
 */
public class PasswordVersionBuilder {
  private PasswordVersion passwordVersion = new PasswordVersion();

  public PasswordVersion build() {
    return passwordVersion;
  }

  public PasswordVersionBuilder version(int version) {
    passwordVersion.setVersion(version);
    return this;
  }

  public PasswordVersionBuilder algorithm(String algorithm) {
    passwordVersion.setAlgorithm(algorithm);
    return this;
  }

  public PasswordVersionBuilder iterations(int iterations) {
    passwordVersion.setIterations(iterations);
    return this;
  }
}
