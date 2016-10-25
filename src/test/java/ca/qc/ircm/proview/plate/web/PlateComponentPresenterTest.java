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

import static ca.qc.ircm.proview.plate.web.PlateComponentPresenter.SELECTED_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateComponentPresenterTest {
  private PlateComponentPresenter presenter;
  private PlateComponent view = new PlateComponent();
  private Plate plate = new Plate();
  private int columns = Plate.Type.A.getColumnCount();
  private int rows = Plate.Type.A.getRowCount();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    initPlate();
    presenter = new PlateComponentPresenter();
    view.setPresenter(presenter);
    presenter.setPlate(plate);
  }

  private void initPlate() {
    plate.setType(Plate.Type.A);
    List<PlateSpot> spots = IntStream
        .range(0, columns).mapToObj(column -> IntStream.range(0, rows)
            .mapToObj(row -> new PlateSpot(row, column)).collect(Collectors.toList()))
        .flatMap(s -> s.stream()).collect(Collectors.toList());
    plate.setSpots(spots);
  }

  @Test
  public void selectWell() {
    presenter.init(view);
    PlateSpot spot = plate.spot(0, 0);

    presenter.selectWell(spot);

    assertTrue(view.plateLayout.getWellStyleName(spot.getColumn(), spot.getRow())
        .contains(SELECTED_STYLE));
    assertEquals(1, presenter.getSelectedSpots().size());
    assertTrue(presenter.getSelectedSpots().contains(spot));
  }

  @Test
  public void deselectWell() {
    presenter.init(view);
    PlateSpot spot = plate.spot(0, 0);
    presenter.selectWell(spot);

    presenter.deselectWell(spot);

    assertFalse(view.plateLayout.getWellStyleName(spot.getColumn(), spot.getRow())
        .contains(SELECTED_STYLE));
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void deselectAllWells() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    PlateSpot spot1 = plate.spot(0, 0);
    presenter.selectWell(spot1);
    PlateSpot spot2 = plate.spot(1, 1);
    presenter.selectWell(spot2);

    presenter.deselectAllWells();

    assertFalse(view.plateLayout.getWellStyleName(spot1.getColumn(), spot1.getRow())
        .contains(SELECTED_STYLE));
    assertFalse(view.plateLayout.getWellStyleName(spot2.getColumn(), spot2.getRow())
        .contains(SELECTED_STYLE));
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void selectColumn_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    int column = 0;

    presenter.selectColumn(column);

    for (int row = 0; row < rows; row++) {
      assertTrue(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(rows, presenter.getSelectedSpots().size());
    for (int row = 0; row < rows; row++) {
      assertTrue(presenter.getSelectedSpots().contains(plate.spot(row, column)));
    }
  }

  @Test
  public void selectColumn_NotMulti() {
    presenter.init(view);
    int column = 0;

    presenter.selectColumn(column);

    for (int row = 0; row < rows; row++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void deselectColumn_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    int column = 0;
    presenter.selectWell(plate.spot(0, column));

    presenter.deselectColumn(column);

    for (int row = 0; row < rows; row++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void deselectColumn_NotMulti() {
    presenter.init(view);
    int column = 0;
    presenter.selectWell(plate.spot(0, column));

    presenter.deselectColumn(column);

    assertTrue(view.plateLayout.getWellStyleName(column, 0).contains(SELECTED_STYLE));
    for (int row = 1; row < rows; row++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(1, presenter.getSelectedSpots().size());
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(0, column)));
  }

  @Test
  public void selectRow_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    int row = 0;

    presenter.selectRow(row);

    for (int column = 0; column < columns; column++) {
      assertTrue(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(columns, presenter.getSelectedSpots().size());
    for (int column = 0; column < columns; column++) {
      assertTrue(presenter.getSelectedSpots().contains(plate.spot(row, column)));
    }
  }

  @Test
  public void selectRow_NotMulti() {
    presenter.init(view);
    int row = 0;

    presenter.selectRow(row);

    for (int column = 0; column < columns; column++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void deselectRow_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    int row = 0;
    presenter.selectWell(plate.spot(row, 0));

    presenter.deselectRow(row);

    for (int column = 0; column < columns; column++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(0, presenter.getSelectedSpots().size());
  }

  @Test
  public void deselectRow_NotNulti() {
    presenter.init(view);
    int row = 0;
    presenter.selectWell(plate.spot(row, 0));

    presenter.deselectRow(row);

    assertTrue(view.plateLayout.getWellStyleName(0, row).contains(SELECTED_STYLE));
    for (int column = 1; column < columns; column++) {
      assertFalse(view.plateLayout.getWellStyleName(column, row).contains(SELECTED_STYLE));
    }
    assertEquals(1, presenter.getSelectedSpots().size());
    assertTrue(presenter.getSelectedSpots().contains(plate.spot(row, 0)));
  }

  @Test
  public void isMultiSelect() {
    presenter.init(view);

    assertFalse(presenter.isMultiSelect());

    presenter.setMultiSelect(true);
    assertTrue(presenter.isMultiSelect());

    presenter.setMultiSelect(false);
    assertFalse(presenter.isMultiSelect());
  }

  @Test
  public void setMultiSelect() {
    presenter.init(view);

    presenter.setMultiSelect(true);

    PlateSpot spot1 = plate.spot(0, 0);
    presenter.selectWell(spot1);
    PlateSpot spot2 = plate.spot(1, 1);
    presenter.selectWell(spot2);
    assertTrue(view.plateLayout.getWellStyleName(spot1.getColumn(), spot1.getRow())
        .contains(SELECTED_STYLE));
    assertTrue(view.plateLayout.getWellStyleName(spot2.getColumn(), spot2.getRow())
        .contains(SELECTED_STYLE));
    assertEquals(2, presenter.getSelectedSpots().size());
    assertTrue(presenter.getSelectedSpots().contains(spot1));
    assertTrue(presenter.getSelectedSpots().contains(spot2));
  }

  @Test
  public void getSelectedSpots_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    PlateSpot spot1 = plate.spot(0, 0);
    presenter.selectWell(spot1);
    PlateSpot spot2 = plate.spot(1, 1);
    presenter.selectWell(spot2);

    Collection<PlateSpot> spots = presenter.getSelectedSpots();

    assertEquals(2, spots.size());
    assertTrue(spots.contains(spot1));
    assertTrue(spots.contains(spot2));
  }

  @Test
  public void getSelectedSpots_NotMulti() {
    presenter.init(view);
    PlateSpot spot1 = plate.spot(0, 0);
    presenter.selectWell(spot1);
    PlateSpot spot2 = plate.spot(1, 1);
    presenter.selectWell(spot2);

    Collection<PlateSpot> spots = presenter.getSelectedSpots();

    assertEquals(1, spots.size());
    assertFalse(spots.contains(spot1));
    assertTrue(spots.contains(spot2));
  }

  @Test
  public void setSelectedSpots_Multi() {
    presenter.init(view);
    presenter.setMultiSelect(true);
    PlateSpot spot1 = plate.spot(0, 0);
    PlateSpot spot2 = plate.spot(1, 1);
    List<PlateSpot> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);

    presenter.setSelectedSpots(spots);

    Collection<PlateSpot> selectedSpots = presenter.getSelectedSpots();
    assertEquals(2, selectedSpots.size());
    assertTrue(selectedSpots.contains(spot1));
    assertTrue(selectedSpots.contains(spot2));
    assertTrue(view.plateLayout.getWellStyleName(spot1.getColumn(), spot1.getRow())
        .contains(SELECTED_STYLE));
    assertTrue(view.plateLayout.getWellStyleName(spot2.getColumn(), spot2.getRow())
        .contains(SELECTED_STYLE));
  }

  @Test
  public void setSelectedSpots_NotMulti() {
    presenter.init(view);
    PlateSpot spot1 = plate.spot(0, 0);
    PlateSpot spot2 = plate.spot(1, 1);
    List<PlateSpot> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);

    presenter.setSelectedSpots(spots);

    Collection<PlateSpot> selectedSpots = presenter.getSelectedSpots();
    assertEquals(1, selectedSpots.size());
    assertFalse(selectedSpots.contains(spot1));
    assertTrue(selectedSpots.contains(spot2));
    assertFalse(view.plateLayout.getWellStyleName(spot1.getColumn(), spot1.getRow())
        .contains(SELECTED_STYLE));
    assertTrue(view.plateLayout.getWellStyleName(spot2.getColumn(), spot2.getRow())
        .contains(SELECTED_STYLE));
  }

  @Test
  public void getPlate() {
    presenter.init(view);

    assertSame(plate, presenter.getPlate());
  }

  @Test
  public void setPlate() {
    initPlate();
    presenter.setPlate(plate);
    presenter.init(view);

    presenter.setPlate(plate);

    assertSame(plate, presenter.getPlate());
  }
}