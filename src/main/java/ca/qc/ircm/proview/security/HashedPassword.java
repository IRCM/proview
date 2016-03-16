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
 * Represents an hashed password that can be saved in database.
 */
public interface HashedPassword {
  /**
   * Returns hashed password (salt was already added). Password is encoded in Hex so it is safe to
   * save it in a database.
   *
   * @return hashed password
   */
  public String getPassword();

  /**
   * Returns salt that was added to user's password during hash. Salt is encoded in Hex so it is
   * safe to save it in a database.
   *
   * @return salt
   */
  public String getSalt();

  /**
   * Returns password's version.
   *
   * @return password's version
   */
  public int getPasswordVersion();
}