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

package ca.qc.ircm.proview.utils.web;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Component;

import java.util.Locale;

/**
 * Component that allows to get resources.
 */
public interface MessageResourcesComponent extends Component {
  default MessageResource getResources() {
    return getResources(getClass(), getLocale());
  }

  default MessageResource getResources(Class<?> clazz) {
    return new MessageResource(clazz, getLocale());
  }

  default MessageResource getResources(Locale locale) {
    return new MessageResource(getClass(), locale);
  }

  default MessageResource getResources(Class<?> clazz, Locale locale) {
    return new MessageResource(clazz, locale);
  }
}
