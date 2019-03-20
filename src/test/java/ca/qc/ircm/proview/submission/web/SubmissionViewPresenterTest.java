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

import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.GUIDELINES;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.HEADER_STYLE;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.HELP;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.INACTIVE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.INVALID_SUBMISSION;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.SAMPLE_TYPE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.SUBMISSION_DESCRIPTION;
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

import ca.qc.ircm.proview.files.web.GuidelinesWindow;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.HelpWindow;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionViewPresenterTest {
  @Inject
  private SubmissionViewPresenter presenter;
  @MockBean
  private SubmissionService submissionService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private GuidelinesWindow guidelinesWindow;
  @MockBean
  private HelpWindow helpWindow;
  @Mock
  private SubmissionView view;
  @Mock
  private Submission submission;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private SubmissionViewDesign design;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new SubmissionViewDesign();
    view.design = design;
    view.submissionForm = mock(SubmissionForm.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  @Test
  public void styles() {
    assertTrue(design.headerLabel.getStyleName().contains(HEADER_STYLE));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_LINK));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_LARGE));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_ICON_ONLY));
    assertTrue(design.help.getStyleName().contains(HELP));
    assertTrue(design.sampleTypeWarning.getStyleName().contains(SAMPLE_TYPE_WARNING));
    assertTrue(design.inactiveWarning.getStyleName().contains(INACTIVE_WARNING));
    assertTrue(design.guidelines.getStyleName().contains(ValoTheme.BUTTON_FRIENDLY));
    assertTrue(design.guidelines.getStyleName().contains(GUIDELINES));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER_STYLE), design.headerLabel.getValue());
    assertEquals(resources.message(HELP), design.help.getCaption());
    assertEquals(resources.message(SAMPLE_TYPE_WARNING), design.sampleTypeWarning.getValue());
    assertEquals(resources.message(INACTIVE_WARNING), design.inactiveWarning.getValue());
    assertEquals(resources.message(GUIDELINES), design.guidelines.getCaption());
  }

  @Test
  public void help() {
    design.help.click();

    verify(helpWindow).setHelp(
        resources.message(SUBMISSION_DESCRIPTION, VaadinIcons.MENU.getHtml()), ContentMode.HTML);
    verify(view).addWindow(helpWindow);
  }

  @Test
  public void guidelines() throws Throwable {
    design.guidelines.click();

    verify(guidelinesWindow).center();
    verify(view).addWindow(guidelinesWindow);
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
