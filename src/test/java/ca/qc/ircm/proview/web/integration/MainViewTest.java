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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.test.config.Slow;
import ca.qc.ircm.proview.test.config.TestBenchLicenseRunner;
import ca.qc.ircm.proview.test.config.TestBenchRule;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Ignore;
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
public class MainViewTest extends MainPageObject {
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
      titles.add(new MessageResource(MainView.class, locale).message("title"));
    }
    assertTrue(titles.contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = header().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = signFormHeader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = signFormUsernameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = signFormPasswordField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = signFormSignButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = forgotPasswordHeader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = forgotPasswordEmailField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = forgotPasswordButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = registerHeader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = registerButton().getLocation().y;
    assertTrue(previous < current);
  }

  @Test
  public void sign_Error() throws Throwable {
    open();
    setSignFormUsername("unit.test@ircm.qc.ca");
    setSignFormPassword("password");

    clickSignFormSignButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  @Ignore("not programmed yet")
  public void sign_Proteomic() throws Throwable {
    open();
    setSignFormUsername("proview@ircm.qc.ca");
    setSignFormPassword("password");

    clickSignFormSignButton();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + MainView.VIEW_NAME,
        getDriver().getCurrentUrl());
  }

  @Test
  @Ignore("not programmed yet")
  public void sign_User() throws Throwable {
    open();
    setSignFormUsername("benoit.coulombe@ircm.qc.ca");
    setSignFormPassword("password");

    clickSignFormSignButton();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + MainView.VIEW_NAME,
        getDriver().getCurrentUrl());
  }

  @Test
  public void forgotPassword_Error() throws Throwable {
    open();
    setForgotPasswordEmail("unit.test@ircm.qc.ca");

    clickForgotPasswordButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void forgotPassword() throws Throwable {
    open();
    setForgotPasswordEmail("benoit.coulombe@ircm.qc.ca");

    clickForgotPasswordButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void register() throws Throwable {
    open();

    clickRegisterButton();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + RegisterView.VIEW_NAME,
        getDriver().getCurrentUrl());
  }
}
