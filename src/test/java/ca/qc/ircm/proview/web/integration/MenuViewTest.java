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

package ca.qc.ircm.proview.web.integration;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.IntegrationTestUrlRule;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.test.config.TestBenchLicenseRunner;
import ca.qc.ircm.proview.test.config.TestBenchRule;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.MenuBarElement;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.Locale;

@RunWith(TestBenchLicenseRunner.class)
public class MenuViewTest extends TestBenchTestCase {
  @ClassRule
  public static IntegrationTestUrlRule integrationTestUrlRule = new IntegrationTestUrlRule();
  public TestBenchRule testBenchRule = new TestBenchRule(this);
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(testBenchRule);
  private MessageResource resources;

  @Before
  public void beforeTest() {
    resources = new Menu().getResources(Locale.getDefault());
  }

  private void openTestUrl(String viewName) {
    getDriver().get(integrationTestUrlRule.getBaseUrl() + "/#!" + viewName);
  }

  private String message(String key, Object... replacements) {
    return resources.message(key, replacements);
  }

  @Test
  public void home() throws Throwable {
    openTestUrl(RegisterView.VIEW_NAME);
    MenuBarElement menu = $(MenuBarElement.class).first();

    try {
      menu.clickItem(message("home"));
    } catch (StaleElementReferenceException e) {
      // Thrown because page changes.
    }

    assertEquals(integrationTestUrlRule.getBaseUrl() + "/#!" + MainView.VIEW_NAME,
        getDriver().getCurrentUrl());
  }
}
