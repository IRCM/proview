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
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Creates instances of {@link Messages}.
 */
@Component
public class MessagesFactory {
  /**
   * Strip this key from class name, if it matches.
   */
  private static final String STRIP_KEY =
      Pattern.quote(AppResources.class.getPackage().getName() + ".");
  /**
   * Message source.
   */
  private final MessageSource messageSource;

  @Autowired
  protected MessagesFactory(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public Messages with(String baseName, Locale locale) {
    String prefix = baseName.replaceFirst(STRIP_KEY, "");
    return new Messages(prefix, locale, messageSource);
  }

  public Messages with(Class<?> baseClass, Locale locale) {
    String prefix = baseClass.getName().replaceFirst(STRIP_KEY, "");
    return new Messages(prefix, locale, messageSource);
  }

}
