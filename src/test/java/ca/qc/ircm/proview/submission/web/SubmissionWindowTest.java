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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionWindow.UPDATE;
import static ca.qc.ircm.proview.submission.web.SubmissionWindow.WINDOW_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import com.vaadin.testbench.elements.WindowElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionWindowTest extends SubmissionsViewPageObject {
  private boolean admin;
  private boolean manager;

  @Override
  protected boolean isAdmin() {
    return admin;
  }

  @Override
  protected boolean isManager() {
    return manager;
  }

  @Before
  public void beforeTest() {
    admin = false;
    manager = false;
  }

  @Test
  public void buttonExistence_Owner() throws Throwable {
    open();

    clickViewSubmissionByRow(0);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window1 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertFalse(optional(() -> window1.findElement(className(UPDATE))).isPresent());
    window1.close();

    clickViewSubmissionByRow(12);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window2 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertTrue(optional(() -> window2.findElement(className(UPDATE))).isPresent());
    window2.close();
  }

  @Test
  @WithSubject(userId = 3)
  public void buttonExistence_Manager() throws Throwable {
    open();
    manager = true;

    clickViewSubmissionByRow(0);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window1 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertFalse(optional(() -> window1.findElement(className(UPDATE))).isPresent());
    window1.close();

    clickViewSubmissionByRow(12);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window2 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertTrue(optional(() -> window2.findElement(className(UPDATE))).isPresent());
    window2.close();
  }

  @Test
  @WithSubject
  public void buttonExistence_Admin() throws Throwable {
    open();
    admin = true;

    clickViewSubmissionByRow(0);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window1 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertTrue(optional(() -> window1.findElement(className(UPDATE))).isPresent());
    window1.close();

    clickViewSubmissionByRow(12);
    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement window2 =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    assertTrue(optional(() -> window2.findElement(className(UPDATE))).isPresent());
    window2.close();
  }

  @Test
  public void updateSubmission() throws Throwable {
    open();

    clickViewSubmissionByRow(12);

    assertNotNull(findElement(className(SubmissionWindow.WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    String experience = experienceByRow(12);
    assertTrue(resources(SubmissionWindow.class).message(SubmissionWindow.TITLE, experience)
        .contains(submissionWindow.getCaption()));
    assertTrue(
        optional(() -> submissionWindow.findElement(className(SERVICE_PROPERTY))).isPresent());
    submissionWindow.findElement(className(UPDATE)).click();
    assertEquals(viewUrl(SubmissionView.VIEW_NAME, "36"), getDriver().getCurrentUrl());
  }
}
