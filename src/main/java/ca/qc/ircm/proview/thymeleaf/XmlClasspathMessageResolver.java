package ca.qc.ircm.proview.thymeleaf;

import ca.qc.ircm.utils.XmlResourceBundleControl;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * IMessageResolver for Thymeleaf that resolves XML files.
 */
public class XmlClasspathMessageResolver extends StandardMessageResolver {
  @Override
  protected Map<String, String> resolveMessagesForTemplate(String template,
      ITemplateResource templateResource, Locale locale) {
    String resourceLocation =
        Paths.get(template).getParent().resolve(templateResource.getBaseName()).toString();
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
