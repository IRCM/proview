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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionFormTest {
  private SubmissionForm view;
  @Mock
  private SubmissionFormPresenter presenter;
  @Mock
  private PlateComponent plateComponent;
  @Mock
  private Submission submission;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    view = new SubmissionForm(presenter, plateComponent);
  }

  @Test
  public void init() throws Throwable {
    view.init();

    assertEquals(1, view.samplesPlateContainer.getComponentCount());
    assertEquals(plateComponent, view.samplesPlateContainer.getComponent(0));
  }

  @Test
  public void getBean() {
    when(presenter.getBean()).thenReturn(submission);

    assertEquals(submission, view.getBean());

    verify(presenter).getBean();
  }

  @Test
  public void setBean() {
    view.setBean(submission);

    verify(presenter).setBean(submission);
  }

  @Test
  public void isEditable_True() {
    when(presenter.isEditable()).thenReturn(true);

    assertTrue(view.isEditable());

    verify(presenter).isEditable();
  }

  @Test
  public void isEditable_False() {
    when(presenter.isEditable()).thenReturn(false);

    assertFalse(view.isEditable());

    verify(presenter).isEditable();
  }

  @Test
  public void setEditable_True() {
    view.setEditable(true);

    verify(presenter).setEditable(true);
  }

  @Test
  public void setEditable_False() {
    view.setEditable(false);

    verify(presenter).setEditable(false);
  }
}
