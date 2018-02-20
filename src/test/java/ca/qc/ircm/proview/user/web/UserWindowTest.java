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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UserWindow.TITLE;
import static ca.qc.ircm.proview.user.web.UserWindow.UPDATE;
import static ca.qc.ircm.proview.user.web.UserWindow.WINDOW_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class UserWindowTest extends AccessPageObject {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AccessViewTest.class);

  @Test
  public void updateUser() throws Throwable {
    open();
    String email = "christopher.anderson@ircm.qc.ca";
    String name = "Christopher Anderson";

    clickViewUser(email);

    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement userWindow = wrap(WindowElement.class, findElement(className(WINDOW_STYLE)));
    assertTrue(resources(UserWindow.class).message(TITLE, name).contains(userWindow.getCaption()));
    assertNotNull(userWindow.findElement(className(UserFormPresenter.USER)));
    ButtonElement updateButton = wrap(ButtonElement.class, findElement(className(UPDATE)));
    updateButton.click();
    assertEquals(viewUrl(UserView.VIEW_NAME, "10"), getDriver().getCurrentUrl());
  }
}
