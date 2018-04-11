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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ContaminantsFormTest {
  private ContaminantsForm view;
  @Mock
  private ContaminantsFormPresenter presenter;
  @Mock
  private List<Contaminant> contaminants;

  @Before
  public void beforeTest() {
    view = new ContaminantsForm(presenter);
  }

  @Test
  public void validate_False() {
    when(presenter.validate()).thenReturn(false);

    assertFalse(view.validate());

    verify(presenter).validate();
  }

  @Test
  public void validate_True() {
    when(presenter.validate()).thenReturn(true);

    assertTrue(view.validate());

    verify(presenter).validate();
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(contaminants);

    assertSame(this.contaminants, view.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    view.setValue(contaminants);

    verify(presenter).setValue(contaminants);
  }

  @Test
  public void getMaxCount_2() {
    when(presenter.getMaxCount()).thenReturn(2);

    assertEquals(2, view.getMaxCount());

    verify(presenter).getMaxCount();
  }

  @Test
  public void getMaxCount_22() {
    when(presenter.getMaxCount()).thenReturn(22);

    assertEquals(22, view.getMaxCount());

    verify(presenter).getMaxCount();
  }

  @Test
  public void setMaxCount_1() {
    view.setMaxCount(1);

    verify(presenter).setMaxCount(1);
  }

  @Test
  public void setMaxCount_21() {
    view.setMaxCount(21);

    verify(presenter).setMaxCount(21);
  }

  @Test
  public void isReadOnly_False() {
    when(presenter.isReadOnly()).thenReturn(false);

    assertFalse(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void isReadOnly_True() {
    when(presenter.isReadOnly()).thenReturn(true);

    assertTrue(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void setReadOnly_False() {
    view.setReadOnly(false);

    verify(presenter).setReadOnly(false);
  }

  @Test
  public void setReadOnly_True() {
    view.setReadOnly(true);

    verify(presenter).setReadOnly(true);
  }
}
