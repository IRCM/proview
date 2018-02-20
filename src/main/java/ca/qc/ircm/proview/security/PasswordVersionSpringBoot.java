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

import java.io.Serializable;

/**
 * Password's versions.
 */
public class PasswordVersionSpringBoot implements PasswordVersion, Serializable {
  private static final long serialVersionUID = 3312553159341536042L;
  private int version;
  private String algorithm;
  private int iterations;

  public PasswordVersionSpringBoot() {
  }

  /**
   * Creates a new password version.
   * 
   * @param version
   *          version of password
   * @param algorithm
   *          hashing algorithm
   * @param iterations
   *          hashing iterations
   */
  public PasswordVersionSpringBoot(int version, String algorithm, int iterations) {
    this.version = version;
    this.algorithm = algorithm;
    this.iterations = iterations;
  }

  @Override
  public String toString() {
    return "PasswordVersionSpringBoot [version=" + version + ", algorithm=" + algorithm
        + ", iterations=" + iterations + "]";
  }

  @Override
  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
}
