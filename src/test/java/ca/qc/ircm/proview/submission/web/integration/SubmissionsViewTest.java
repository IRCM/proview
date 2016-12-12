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

package ca.qc.ircm.proview.submission.web.integration;

import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.submission.web.SubmissionAnalysesWindow;
import ca.qc.ircm.proview.submission.web.SubmissionWindow;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.WindowElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionsViewTest extends SubmissionsViewPageObject {
  @Value("${spring.application.name}")
  private String applicationName;
  private boolean admin;

  @Override
  protected boolean isAdmin() {
    return admin;
  }

  @Before
  public void beforeTest() {
    admin = false;
  }

  @Test
  @WithSubject(anonymous = true)
  public void security_Anonymous() throws Throwable {
    openView(MainView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(SubmissionsView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertNotNull(header());
    assertNotNull(submissionsGrid());
  }

  @Test
  public void viewSubmission() throws Throwable {
    open();

    clickViewSubmissionByRow(0);

    assertNotNull(findElement(className(SubmissionWindow.WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(SubmissionWindow.WINDOW_STYLE)));
    String experience = experienceByRow(0);
    assertTrue(resources(SubmissionWindow.class).message(SubmissionWindow.TITLE, experience)
        .contains(submissionWindow.getCaption()));
    assertNotNull(submissionWindow.findElement(className(SERVICE_PROPERTY)));
  }

  @Test
  public void viewSubmissionResults() throws Throwable {
    open();

    clickViewSubmissionResultsByRow(13);

    assertNotNull(findElement(className(SubmissionAnalysesWindow.WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(SubmissionAnalysesWindow.WINDOW_STYLE)));
    String experience = experienceByRow(13);
    assertTrue(resources(SubmissionAnalysesWindow.class)
        .message(SubmissionAnalysesWindow.TITLE, experience)
        .contains(submissionWindow.getCaption()));
    assertNotNull(submissionWindow.findElement(className(ANALYSIS)));
  }

  @Test
  @WithSubject
  public void selectSamples() throws Throwable {
    admin = true;
    open();

    fail("Program test");
  }

  @Test
  @WithSubject
  public void updateStatus() throws Throwable {
    admin = true;
    open();
    selectSubmissions(1, 3);

    clickUpdateStatusButton();

    assertEquals(viewUrl(SampleStatusView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
