/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.SigninView.DISABLED;
import static ca.qc.ircm.proview.web.SigninView.FAIL;
import static ca.qc.ircm.proview.web.SigninView.ID;
import static ca.qc.ircm.proview.web.SigninView.LOCKED;
import static ca.qc.ircm.proview.web.SigninView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.text.MessageResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class SigninViewItTest extends AbstractTestBenchTestCase {
  @Autowired
  private transient SecurityConfiguration configuration;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(SigninView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).id(ID);
    assertTrue(optional(() -> view.getUsernameField()).isPresent());
    assertTrue(optional(() -> view.getPasswordField()).isPresent());
    assertTrue(optional(() -> view.getSubmitButton()).isPresent());
    // TODO Forgot password button should be available.
  }

  @Test
  public void sign_Fail() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).id(ID);
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("notright");
    view.getSubmitButton().click();
    assertEquals(new MessageResource(SigninView.class, currentLocale()).message(FAIL),
        view.getErrorMessage());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign_Disabled() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).id(ID);
    view.getUsernameField().setValue("robert.stlouis@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    assertEquals(new MessageResource(SigninView.class, currentLocale()).message(DISABLED),
        view.getErrorMessage());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign_Locked() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).id(ID);
    for (int i = 0; i < 6; i++) {
      view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
      view.getPasswordField().setValue("notright");
      view.getSubmitButton().click();
    }
    assertEquals(new MessageResource(SigninView.class, currentLocale()).message(LOCKED,
        configuration.getLockDuration().getSeconds() / 60), view.getErrorMessage());
    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(VIEW_NAME) + "?"));
  }

  @Test
  public void sign() throws Throwable {
    open();
    SigninViewElement view = $(SigninViewElement.class).id(ID);
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void already_User() throws Throwable {
    open();
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}