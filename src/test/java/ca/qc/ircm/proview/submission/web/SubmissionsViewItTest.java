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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionsView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ERROR;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionsViewItTest extends AbstractTestBenchTestCase {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewItTest.class);
  @Autowired
  private SubmissionRepository repository;
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new MessageResource(SigninView.class, locale).message(TITLE,
            new MessageResource(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(SubmissionsView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hide() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);

    view.clickVisible(0);
    waitUntil(driver -> view.visible(0).getAttribute("theme").equals(ERROR));

    Submission submission = repository.findById(164L).get();
    assertTrue(submission.isHidden());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void show() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);
    view.clickVisible(0);
    waitUntil(driver -> view.visible(0).getAttribute("theme").equals(ERROR));

    view.clickVisible(0);
    waitUntil(driver -> view.visible(0).getAttribute("theme").equals(SUCCESS));

    Submission submission = repository.findById(164L).get();
    assertFalse(submission.isHidden());
  }

  @Test
  public void dialog() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);

    view.doubleClickSubmission(0);

    waitUntil(driver -> $(SubmissionDialogElement.class).id(SubmissionDialog.ID));
    SubmissionDialogElement submissionDialog = $(SubmissionDialogElement.class)
        .id(SubmissionDialog.ID);
    assertEquals("POLR3B-Flag", submissionDialog.header().getText());
  }

  @Test
  public void add() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);

    view.clickAdd();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
