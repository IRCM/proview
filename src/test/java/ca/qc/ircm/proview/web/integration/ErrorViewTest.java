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
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.test.config.Slow;
import ca.qc.ircm.proview.test.config.TestBenchLicenseRunner;
import ca.qc.ircm.proview.test.config.TestBenchRule;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ErrorView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@RunWith(TestBenchLicenseRunner.class)
@Slow
@WithSubject(anonymous = true)
public class ErrorViewTest extends ErrorPageObject {
  public TestBenchRule testBenchRule = new TestBenchRule(this);
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(testBenchRule);

  @Override
  protected String getBaseUrl() {
    return testBenchRule.getBaseUrl();
  }

  @Test
  public void title() throws Throwable {
    open();

    Set<Locale> locales = Rules.getLocales();
    Set<String> titles = new HashSet<>();
    for (Locale locale : locales) {
      titles.add(new MessageResource(ErrorView.class, locale).message("title"));
    }
    assertTrue(titles.contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = errorLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = mainViewButton().getLocation().y;
    assertTrue(previous < current);
  }

  @Test
  public void returnMainView() throws Throwable {
    open();

    clickMainViewButton();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + MainView.VIEW_NAME,
        getDriver().getCurrentUrl());
  }
}
