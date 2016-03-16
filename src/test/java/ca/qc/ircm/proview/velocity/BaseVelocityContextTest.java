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

package ca.qc.ircm.proview.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.Rules;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.ResourceTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BaseVelocityContextTest {
  @Rule
  public RuleChain rules = Rules.defaultRules(this);

  @Before
  public void beforeTest() {
  }

  @Test
  public void constructor() {
    BaseVelocityContext baseVelocityContext = new BaseVelocityContext();

    assertTrue(baseVelocityContext.get("displayTool") instanceof DisplayTool);
    DisplayTool displayTool = (DisplayTool) baseVelocityContext.get("displayTool");
    assertEquals("", displayTool.alt(""));
    assertTrue(baseVelocityContext.get("numberTool") instanceof NumberTool);
    assertTrue(baseVelocityContext.get("dateTool") instanceof DateTool);
  }

  @Test
  public void constructor_Context() {
    VelocityContext innerContext = new VelocityContext();

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(innerContext);

    assertTrue(baseVelocityContext.get("displayTool") instanceof DisplayTool);
    DisplayTool displayTool = (DisplayTool) baseVelocityContext.get("displayTool");
    assertEquals("", displayTool.alt(""));
    assertTrue(baseVelocityContext.get("numberTool") instanceof NumberTool);
    assertTrue(baseVelocityContext.get("dateTool") instanceof DateTool);
  }

  @Test
  public void constructor_Context_DuplicatedValues() {
    VelocityContext innerContext = new VelocityContext();
    innerContext.put("displayTool", "test1");
    innerContext.put("numberTool", "test2");
    innerContext.put("dateTool", "test3");

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(innerContext);

    assertEquals("test1", baseVelocityContext.get("displayTool"));
    assertEquals("test2", baseVelocityContext.get("numberTool"));
    assertEquals("test3", baseVelocityContext.get("dateTool"));
  }

  @Test
  public void constructor_MapAndContext() {
    Map<Object, Object> context = new HashMap<>();
    VelocityContext innerContext = new VelocityContext();

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(context, innerContext);

    assertTrue(baseVelocityContext.get("displayTool") instanceof DisplayTool);
    DisplayTool displayTool = (DisplayTool) baseVelocityContext.get("displayTool");
    assertEquals("", displayTool.alt(""));
    assertTrue(baseVelocityContext.get("numberTool") instanceof NumberTool);
    assertTrue(baseVelocityContext.get("dateTool") instanceof DateTool);
  }

  @Test
  public void constructor_MapAndContext_DuplicatedValuesMap() {
    Map<Object, Object> context = new HashMap<>();
    context.put("displayTool", "test1");
    context.put("numberTool", "test2");
    context.put("dateTool", "test3");
    VelocityContext innerContext = new VelocityContext();

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(context, innerContext);

    assertEquals("test1", baseVelocityContext.get("displayTool"));
    assertEquals("test2", baseVelocityContext.get("numberTool"));
    assertEquals("test3", baseVelocityContext.get("dateTool"));
  }

  @Test
  public void constructor_MapAndContext_DuplicatedValuesContext() {
    Map<Object, Object> context = new HashMap<>();
    VelocityContext innerContext = new VelocityContext();
    innerContext.put("displayTool", "test1");
    innerContext.put("numberTool", "test2");
    innerContext.put("dateTool", "test3");

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(context, innerContext);

    assertEquals("test1", baseVelocityContext.get("displayTool"));
    assertEquals("test2", baseVelocityContext.get("numberTool"));
    assertEquals("test3", baseVelocityContext.get("dateTool"));
  }

  @Test
  public void constructor_Map() {
    Map<Object, Object> context = new HashMap<>();

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(context);

    assertTrue(baseVelocityContext.get("displayTool") instanceof DisplayTool);
    DisplayTool displayTool = (DisplayTool) baseVelocityContext.get("displayTool");
    assertEquals("", displayTool.alt(""));
    assertTrue(baseVelocityContext.get("numberTool") instanceof NumberTool);
    assertTrue(baseVelocityContext.get("dateTool") instanceof DateTool);
  }

  @Test
  public void constructor_Map_DuplicatedValues() {
    Map<Object, Object> context = new HashMap<>();
    context.put("displayTool", "test1");
    context.put("numberTool", "test2");
    context.put("dateTool", "test3");

    BaseVelocityContext baseVelocityContext = new BaseVelocityContext(context);

    assertEquals("test1", baseVelocityContext.get("displayTool"));
    assertEquals("test2", baseVelocityContext.get("numberTool"));
    assertEquals("test3", baseVelocityContext.get("dateTool"));
  }

  @Test
  public void setResourceTool_Class() {
    final Locale locale = Locale.ENGLISH;
    BaseVelocityContext baseVelocityContext = new BaseVelocityContext();

    baseVelocityContext.setResourceTool(locale, this.getClass());

    assertTrue(baseVelocityContext.get("resourceTool") instanceof ResourceTool);
    ResourceTool resourceTool = (ResourceTool) baseVelocityContext.get("resourceTool");
    assertEquals(locale, resourceTool.getLocale());
    assertEquals("test_value", resourceTool.get("test_key").getRaw());
  }

  @Test
  public void setResourceTool_String() {
    final Locale locale = Locale.ENGLISH;
    BaseVelocityContext baseVelocityContext = new BaseVelocityContext();

    baseVelocityContext.setResourceTool(locale, this.getClass().getName());

    assertTrue(baseVelocityContext.get("resourceTool") instanceof ResourceTool);
    ResourceTool resourceTool = (ResourceTool) baseVelocityContext.get("resourceTool");
    assertEquals(locale, resourceTool.getLocale());
    assertEquals("test_value", resourceTool.get("test_key").getRaw());
  }
}
