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

package ca.qc.ircm.proview.plate.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.data.SelectionModel.Multi;
import com.vaadin.data.SelectionModel.Single;
import com.vaadin.ui.Grid.SelectionMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlatesSelectionComponentTest extends AbstractComponentTestCase {
  private PlatesSelectionComponent component;
  @Mock
  private PlatesSelectionComponentPresenter presenter;
  private List<Plate> plates;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    component = new PlatesSelectionComponent(presenter);
    plates = new ArrayList<>();
    IntStream.range(0, 3).forEach(i -> plates.add(mock(Plate.class)));
  }

  @Test
  public void setSelectionMode() {
    assertTrue(component.plates.getSelectionModel() instanceof Single);
  }

  @Test
  public void setSelectionMode_Single() {
    component.setSelectionMode(SelectionMode.SINGLE);

    assertTrue(component.plates.getSelectionModel() instanceof Single);
  }

  @Test
  public void setSelectionMode_Multi() {
    component.setSelectionMode(SelectionMode.MULTI);

    assertTrue(component.plates.getSelectionModel() instanceof Multi);
  }

  @Test
  public void getSelectionItems() {
    component.plates.setItems(plates);
    component.plates.select(plates.get(0));
    component.plates.select(plates.get(1));

    Set<Plate> selection = component.getSelectionItems();

    assertEquals(1, selection.size());
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void getSelectionItems_Single() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.SINGLE);
    component.plates.select(plates.get(0));
    component.plates.select(plates.get(1));

    Set<Plate> selection = component.getSelectionItems();

    assertEquals(1, selection.size());
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void getSelectionItems_Multi() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.MULTI);
    component.plates.select(plates.get(0));
    component.plates.select(plates.get(1));

    Set<Plate> selection = component.getSelectionItems();

    assertEquals(2, selection.size());
    assertTrue(selection.contains(plates.get(0)));
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void setSelectionItems() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.SINGLE);

    component.setSelectionItems(plates.subList(0, 2));

    Set<Plate> selection = component.plates.getSelectedItems();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void setSelectionItems_Single() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.SINGLE);

    component.setSelectionItems(plates.subList(0, 2));

    Set<Plate> selection = component.plates.getSelectedItems();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void setSelectionItems_Single_Null() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.SINGLE);

    component.setSelectionItems(null);

    Set<Plate> selection = component.plates.getSelectedItems();
    assertEquals(0, selection.size());
  }

  @Test
  public void setSelectionItems_Multi() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.MULTI);
    component.plates.select(plates.get(0));

    component.setSelectionItems(plates.subList(0, 2));

    Set<Plate> selection = component.plates.getSelectedItems();
    assertEquals(2, selection.size());
    assertTrue(selection.contains(plates.get(0)));
    assertTrue(selection.contains(plates.get(1)));
  }

  @Test
  public void setSelectionItems_Null() {
    component.plates.setItems(plates);
    component.setSelectionMode(SelectionMode.MULTI);
    component.plates.select(plates.get(0));
    component.plates.select(plates.get(1));

    component.setSelectionItems(null);

    Set<Plate> selection = component.plates.getSelectedItems();
    assertEquals(0, selection.size());
  }

  @Test
  public void isExcludeSubmissionPlates_True() {
    when(presenter.isExcludeSubmissionPlates()).thenReturn(true);

    assertTrue(component.isExcludeSubmissionPlates());
    verify(presenter).isExcludeSubmissionPlates();
  }

  @Test
  public void isExcludeSubmissionPlates_False() {
    when(presenter.isExcludeSubmissionPlates()).thenReturn(false);

    assertFalse(component.isExcludeSubmissionPlates());
    verify(presenter).isExcludeSubmissionPlates();
  }

  @Test
  public void setExcludeSubmissionPlates_True() {
    component.setExcludeSubmissionPlates(true);

    verify(presenter).setExcludeSubmissionPlates(true);
  }

  @Test
  public void setExcludeSubmissionPlates_False() {
    component.setExcludeSubmissionPlates(false);

    verify(presenter).setExcludeSubmissionPlates(false);
  }
}
