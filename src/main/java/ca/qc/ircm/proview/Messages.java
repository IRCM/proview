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

package ca.qc.ircm.proview;

import java.util.Locale;
import org.springframework.context.MessageSource;

/**
 * Friendlier version of {@link MessageSource}.
 */
public class Messages {
  private final String prefix;
  private final Locale locale;
  private final MessageSource messageSource;

  /**
   * Creates an instance of Messages.
   * 
   * @param prefix
   *          prefix to add to keys in {@link #message(String, Object...)}
   * @param locale
   *          locale
   * @param messageSource
   *          {@link MessageSource} from which to get messages
   */
  public Messages(String prefix, Locale locale, MessageSource messageSource) {
    this.prefix = prefix;
    this.locale = locale;
    this.messageSource = messageSource;
  }

  /**
   * Returns message from resource bundle with replacements. <br>
   * If key does not exists, returns <code>!{key}!</code>.
   *
   * @param key
   *          message's key
   * @param replacements
   *          message's replacements
   * @return message, or <code>!{key}!</code> if key does not exists
   */
  public String message(String key, Object... replacements) {
    return messageSource.getMessage(prefix + "." + key, replacements,
        "!{" + locale + ":" + prefix + "." + key + "}!", locale);
  }
}
