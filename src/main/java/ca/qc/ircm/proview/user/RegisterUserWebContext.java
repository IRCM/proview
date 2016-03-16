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

package ca.qc.ircm.proview.user;

import java.util.Locale;

/**
 * Web context for register user.
 */
public interface RegisterUserWebContext {
  /**
   * Returns URL that leads to validate user function. This URL must begin with with a
   * <code>/</code> and must begin with the context path, if applicable.
   *
   * @param locale
   *          adapt URL to specified locale
   * @return URL that leads to validate user function
   */
  public String getValidateUserUrl(Locale locale);

  /**
   * Returns URL that leads to validate manager function. This URL must begin with with a
   * <code>/</code> and must begin with the context path, if applicable.
   *
   * @param locale
   *          adapt URL to specified locale
   * @return URL that leads to validate user function
   */
  public String getValidateManagerUrl(Locale locale);
}