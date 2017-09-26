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

import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.HEADER_STYLE;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.INVALID_SUBMISSION;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionViewPresenterTest {
  private SubmissionViewPresenter presenter;
  @Mock
  private SubmissionView view;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Submission submission;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new SubmissionViewPresenter(submissionService, authorizationService, applicationName);
    view.headerLabel = new Label();
    view.submissionForm = mock(SubmissionForm.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER_STYLE));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER_STYLE), view.headerLabel.getValue());
  }

  @Test
  public void enter_NoSubmission_ReadOnly() {
    presenter.enter("");

    verify(submissionService, never()).get(any());
    verify(view.submissionForm, never()).setValue(any());
    verify(view.submissionForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    verify(view, never()).showWarning(any());
  }

  @Test
  public void enter_Submission() {
    when(submissionService.get(any())).thenReturn(submission);
    when(authorizationService.hasSubmissionWritePermission(any())).thenReturn(true);

    presenter.enter("1");

    verify(submissionService).get(1L);
    verify(view.submissionForm).setValue(submission);
    verify(view.submissionForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    verify(view, never()).showWarning(any());
  }

  @Test
  public void enter_Submission_ReadOnly() {
    when(submissionService.get(any())).thenReturn(submission);
    when(authorizationService.hasSubmissionWritePermission(any())).thenReturn(false);

    presenter.enter("1");

    verify(submissionService).get(1L);
    verify(view.submissionForm).setValue(submission);
    verify(view.submissionForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(view, never()).showWarning(any());
  }

  @Test
  public void enter_InvalidSubmission() {
    presenter.enter("a");

    verify(submissionService, never()).get(any());
    verify(view.submissionForm, never()).setValue(any());
    verify(view.submissionForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    verify(view).showWarning(resources.message(INVALID_SUBMISSION));
  }
}
