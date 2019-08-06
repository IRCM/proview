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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PrintSubmissionsViewPresenterTest {
  @Autowired
  private PrintSubmissionViewPresenter presenter;
  @Mock
  private PrintSubmissionView view;
  @MockBean
  private SubmissionService service;
  @Mock
  private Submission submission;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    view.header = new H2();
    view.secondHeader = new H3();
    view.printContent = mock(PrintSubmission.class);
    presenter.init(view);
  }

  @Test
  public void setParameter() {
    Long parameter = 12L;
    when(service.get(any())).thenReturn(submission);

    presenter.setParameter(parameter);

    verify(service).get(parameter);
    assertEquals(submission, presenter.getSubmission());
    verify(view.printContent).setSubmission(submission);
  }

  @Test
  public void setParameter_Null() {
    presenter.setParameter(null);

    verify(service, never()).get(any());
    assertNull(presenter.getSubmission());
    verify(view.printContent).setSubmission(null);
  }
}
