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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleViewPresenter.INVALID_SAMPLE;
import static ca.qc.ircm.proview.sample.web.SampleViewPresenter.TITLE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleViewPresenterTest {
  private SampleViewPresenter presenter;
  @Mock
  private SampleView view;
  @Mock
  private SampleService sampleService;
  @Mock
  private SubmissionSample sample;
  @Mock
  private Control control;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SampleViewPresenter(sampleService, applicationName);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
  }

  @Test
  public void enter_Empty() {
    presenter.init(view);

    presenter.enter("");

    verify(view).showWarning(resources.message(INVALID_SAMPLE));
  }

  @Test
  public void enter_SubmissionSample() {
    presenter.init(view);
    final Long sampleId = 3L;
    Long submissionId = 2L;
    when(sampleService.get(any())).thenReturn(sample);
    when(sample.getSubmission()).thenReturn(mock(Submission.class));
    when(sample.getSubmission().getId()).thenReturn(submissionId);

    presenter.enter(String.valueOf(sampleId));

    verify(sampleService, atLeastOnce()).get(sampleId);
    verify(view).navigateTo(SubmissionView.VIEW_NAME, String.valueOf(submissionId));
  }

  @Test
  public void enter_Control() {
    presenter.init(view);
    Long controlId = 3L;
    when(control.getId()).thenReturn(controlId);
    when(sampleService.get(any())).thenReturn(control);

    presenter.enter(String.valueOf(controlId));

    verify(sampleService, atLeastOnce()).get(controlId);
    verify(view).navigateTo(ControlView.VIEW_NAME, String.valueOf(controlId));
  }

  @Test
  public void enter_InvalidNumber() {
    presenter.init(view);

    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_SAMPLE));
  }

  @Test
  public void enter_InvalidSample() {
    presenter.init(view);

    presenter.enter("3");

    verify(sampleService, atLeastOnce()).get(3L);
    verify(view).showWarning(resources.message(INVALID_SAMPLE));
  }
}
