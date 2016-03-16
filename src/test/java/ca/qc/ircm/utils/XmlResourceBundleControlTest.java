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

package ca.qc.ircm.utils;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.Rules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Locale;
import java.util.ResourceBundle;

public class XmlResourceBundleControlTest {
  @Rule
  public RuleChain rules = Rules.defaultRules(this);

  @Test
  public void getBundle_Xml() throws Throwable {
    ResourceBundle resources = ResourceBundle.getBundle("utils.XmlResourceBundleControlTest_xml",
        Locale.CANADA_FRENCH, new XmlResourceBundleControl());

    assertEquals("Ceci est un test", resources.getString("message"));
    assertEquals("Parent test", resources.getString("parent_message"));
  }

  @Test
  public void getBundle_Properties() throws Throwable {
    ResourceBundle resources =
        ResourceBundle.getBundle("utils.XmlResourceBundleControlTest_properties",
            Locale.CANADA_FRENCH, new XmlResourceBundleControl());

    assertEquals("Ceci est un test", resources.getString("message"));
    assertEquals("Parent test", resources.getString("parent_message"));
  }

  @Test
  public void getBundle_XmlWithParentProperties() throws Throwable {
    ResourceBundle resources = ResourceBundle.getBundle("utils.XmlResourceBundleControlTest_mix1",
        Locale.CANADA_FRENCH, new XmlResourceBundleControl());

    assertEquals("Ceci est un test", resources.getString("message"));
    assertEquals("Parent test", resources.getString("parent_message"));
  }

  @Test
  public void getBundle_PropertiesWithParentXml() throws Throwable {
    ResourceBundle resources = ResourceBundle.getBundle("utils.XmlResourceBundleControlTest_mix2",
        Locale.CANADA_FRENCH, new XmlResourceBundleControl());

    assertEquals("Ceci est un test", resources.getString("message"));
    assertEquals("Parent test", resources.getString("parent_message"));
  }
}
