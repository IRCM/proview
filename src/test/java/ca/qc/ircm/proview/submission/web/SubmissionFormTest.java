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
import ca.qc.ircm.proview.sample.web.ContaminantsForm;
import ca.qc.ircm.proview.sample.web.StandardsForm;
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
  private StandardsForm standardsForm;
  @Mock
  private ContaminantsForm contaminantsForm;
  @Mock
  private GelForm gelForm;
  @Mock
  private Submission submission;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    view = new SubmissionForm(presenter, plateComponent, standardsForm, contaminantsForm,
        gelForm);
  }

  @Test
  public void init() throws Throwable {
    view.init();

    assertEquals(1, view.design.samplesPlateContainer.getComponentCount());
    assertEquals(plateComponent, view.design.samplesPlateContainer.getComponent(0));
    assertEquals(standardsForm, view.design.standardsPanel.getContent());
    assertEquals(contaminantsForm, view.design.contaminantsPanel.getContent());
    assertEquals(gelForm, view.design.gelPanel.getContent());
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(submission);

    assertEquals(submission, view.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    view.setValue(submission);

    verify(presenter).setValue(submission);
  }

  @Test
  public void isReadOnly_True() {
    when(presenter.isReadOnly()).thenReturn(true);

    assertTrue(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void isReadOnly_False() {
    when(presenter.isReadOnly()).thenReturn(false);

    assertFalse(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void setReadOnly_True() {
    view.setReadOnly(true);

    verify(presenter).setReadOnly(true);
  }

  @Test
  public void setReadOnly_False() {
    view.setReadOnly(false);

    verify(presenter).setReadOnly(false);
  }
}
