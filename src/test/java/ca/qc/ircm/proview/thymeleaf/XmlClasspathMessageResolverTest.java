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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.Rules;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Locale;
import java.util.Map;

public class XmlClasspathMessageResolverTest {
  private XmlClasspathMessageResolver xmlMessageResolver;
  @Rule
  public RuleChain rules = Rules.defaultRules(this);

  @Before
  public void beforeTest() {
    xmlMessageResolver = new XmlClasspathMessageResolver();
  }

  @Test
  public void resolveMessagesForTemplate_Xml() {
    String template =
        "/" + XmlClasspathMessageResolverXml.class.getName().replace(".", "/") + ".html";
    ITemplateResource templateResource =
        new ClassLoaderTemplateResource(getClass().getClassLoader(),
            XmlClasspathMessageResolverXml.class.getName().replace(".", "/"), "UTF-8");
    Locale locale = Locale.ENGLISH;

    Map<String, String> messages =
        xmlMessageResolver.resolveMessagesForTemplate(template, templateResource, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("This is a test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForTemplate_XmlFrench() {
    String template =
        "/" + XmlClasspathMessageResolverXml.class.getName().replace(".", "/") + ".html";
    ITemplateResource templateResource =
        new ClassLoaderTemplateResource(getClass().getClassLoader(),
            XmlClasspathMessageResolverXml.class.getName().replace(".", "/"), "UTF-8");
    Locale locale = Locale.FRENCH;

    Map<String, String> messages =
        xmlMessageResolver.resolveMessagesForTemplate(template, templateResource, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("Ceci est un test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForTemplate_Properties() {
    String template =
        "/" + XmlClasspathMessageResolverProperties.class.getName().replace(".", "/") + ".html";
    ITemplateResource templateResource =
        new ClassLoaderTemplateResource(getClass().getClassLoader(),
            XmlClasspathMessageResolverProperties.class.getName().replace(".", "/"), "UTF-8");
    Locale locale = Locale.ENGLISH;

    Map<String, String> messages =
        xmlMessageResolver.resolveMessagesForTemplate(template, templateResource, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("This is a test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForTemplate_PropertiesFrench() {
    String template =
        "/" + XmlClasspathMessageResolverProperties.class.getName().replace(".", "/") + ".html";
    ITemplateResource templateResource =
        new ClassLoaderTemplateResource(getClass().getClassLoader(),
            XmlClasspathMessageResolverProperties.class.getName().replace(".", "/"), "UTF-8");
    Locale locale = Locale.FRENCH;

    Map<String, String> messages =
        xmlMessageResolver.resolveMessagesForTemplate(template, templateResource, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("Ceci est un test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForOrigin_Xml() {
    Class<?> origin = XmlClasspathMessageResolverXml.class;
    Locale locale = Locale.ENGLISH;

    Map<String, String> messages = xmlMessageResolver.resolveMessagesForOrigin(origin, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("This is a test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForOrigin_XmlFrench() {
    Class<?> origin = XmlClasspathMessageResolverXml.class;
    Locale locale = Locale.FRENCH;

    Map<String, String> messages = xmlMessageResolver.resolveMessagesForOrigin(origin, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("Ceci est un test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForOrigin_Properties() {
    Class<?> origin = XmlClasspathMessageResolverProperties.class;
    Locale locale = Locale.ENGLISH;

    Map<String, String> messages = xmlMessageResolver.resolveMessagesForOrigin(origin, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("This is a test", messages.get("message"));
  }

  @Test
  public void resolveMessagesForOrigin_PropertiesFrench() {
    Class<?> origin = XmlClasspathMessageResolverProperties.class;
    Locale locale = Locale.FRENCH;

    Map<String, String> messages = xmlMessageResolver.resolveMessagesForOrigin(origin, locale);

    assertTrue(messages.containsKey("message"));
    assertEquals("Ceci est un test", messages.get("message"));
  }
}
