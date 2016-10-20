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

package ca.qc.ircm.proview.web;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Constants for Web.
 */
public class WebConstants {
  public static final String GENERAL_MESSAGES = "VaadinMessages";
  public static final String REQUIRED = "required";
  public static final String REQUIRED_CAPTION = "required.caption";
  public static final String INVALID_NUMBER = "invalidNumber";
  public static final String INVALID_INTEGER = "invalidInteger";
  public static final String ALREADY_EXISTS = "alreadyExists";
  public static final String OUT_OF_RANGE = "outOfRange";
  public static final String ONLY_WORDS = "onlyWords";

  /**
   * Returns all valid locales for program.
   *
   * @return all valid locales for program
   */
  public static Set<Locale> getLocales() {
    Set<Locale> locales = new HashSet<>();
    locales.add(Locale.ENGLISH);
    locales.add(Locale.FRENCH);
    return locales;
  }
}
