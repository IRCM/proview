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

package ca.qc.ircm.proview.thymeleaf;

import ca.qc.ircm.utils.XmlResourceBundleControl;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * IMessageResolver for Thymeleaf that resolves XML files.
 */
public class XmlClasspathMessageResolver extends StandardMessageResolver {
  @Override
  protected Map<String, String> resolveMessagesForTemplate(String template,
      ITemplateResource templateResource, Locale locale) {
    Path parent = Paths.get(template).getParent();
    Path file = parent != null ? parent.resolve(templateResource.getBaseName()) : null;
    String resourceLocation = Objects.toString(file, "");
    if (resourceLocation.startsWith("/")) {
      resourceLocation = resourceLocation.substring(1);
    }
    resourceLocation = resourceLocation.replace("/", ".");
    ResourceBundle resources =
        ResourceBundle.getBundle(resourceLocation, locale, new XmlResourceBundleControl());
    Map<String, String> messages = new HashMap<>();
    if (resources != null) {
      Enumeration<String> keys = resources.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        messages.put(key, resources.getString(key));
      }
    }
    return messages;
  }

  @Override
  protected Map<String, String> resolveMessagesForOrigin(Class<?> origin, Locale locale) {
    ResourceBundle resources =
        ResourceBundle.getBundle(origin.getName(), locale, new XmlResourceBundleControl());
    Map<String, String> messages = new HashMap<>();
    if (resources != null) {
      Enumeration<String> keys = resources.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        messages.put(key, resources.getString(key));
      }
    }
    return messages;
  }
}
