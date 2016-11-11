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

import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionWindow.WINDOW_STYLE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TITLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.submission.web.SubmissionWindow;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import com.vaadin.testbench.elements.WindowElement;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionsViewTest extends SubmissionsViewPageObject {
  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(SubmissionsView.class).message(TITLE).contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = header().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = submissionsGrid().getLocation().y;
    assertTrue(previous < current);
    previous = current;
  }

  @Test
  public void viewSubmission() {
    open();

    clickViewSubmissionByRow(0);

    assertNotNull(findElement(className(WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(WINDOW_STYLE)));
    String experience = experienceByRow(0);
    assertTrue(resources(SubmissionWindow.class).message(TITLE, experience)
        .contains(submissionWindow.getCaption()));
    assertNotNull(submissionWindow.findElement(className(SERVICE_PROPERTY)));
  }

  @Test
  @Ignore("not programmed yet")
  public void viewSubmissionResults() {
    open();

    clickViewSubmissionResultsByRow(0);

    // TODO Program test.
  }
}
