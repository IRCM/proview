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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.ResourceTool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * VelocityContext configured with some basic tools.
 */
public class BaseVelocityContext extends VelocityContext {
  public BaseVelocityContext() {
    this(null, null);
  }

  public BaseVelocityContext(Context innerContext) {
    this(null, innerContext);
  }

  /**
   * Creates Velocity's context for application.
   *
   * @param context
   *          context
   * @param innerContext
   *          inner context
   */
  @SuppressWarnings("rawtypes")
  public BaseVelocityContext(Map context, Context innerContext) {
    super(context, innerContext);
    // Configure display tool.
    if (!this.containsKey("displayTool")) {
      this.put("displayTool", new DisplayTool());
    }
    // Configure number tool.
    if (!this.containsKey("numberTool")) {
      this.put("numberTool", new NumberTool());
    }
    // Configure date tool.
    if (!this.containsKey("dateTool")) {
      this.put("dateTool", new DateTool());
    }
  }

  @SuppressWarnings("rawtypes")
  public BaseVelocityContext(Map context) {
    this(context, null);
  }

  /**
   * Sets resource tool in this velocity context. The bundle that resource tool will use will be the
   * exact {@link Class#getName() class name}.
   *
   * @param locale
   *          locale that resource tool will use
   * @param clazz
   *          bundle that resource tool will use match the exact {@link Class#getName() class name}
   *          of this class
   */
  public void setResourceTool(Locale locale, Class<?> clazz) {
    this.setResourceTool(locale, clazz.getName());
  }

  /**
   * Sets resource tool in this velocity context.
   *
   * @param locale
   *          locale that resource tool will use
   * @param bundles
   *          bundles that resource tool will use
   */
  public void setResourceTool(Locale locale, String... bundles) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put(ResourceTool.BUNDLES_KEY, bundles);
    parameters.put(ToolContext.LOCALE_KEY, locale);
    ResourceTool resourceTool = new ResourceTool();
    resourceTool.configure(parameters);
    this.put("resourceTool", resourceTool);
  }
}
