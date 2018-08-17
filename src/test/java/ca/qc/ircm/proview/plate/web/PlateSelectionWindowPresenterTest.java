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

import static ca.qc.ircm.proview.plate.web.PlateSelectionWindowPresenter.NAME;
import static ca.qc.ircm.proview.plate.web.PlateSelectionWindowPresenter.SELECT;
import static ca.qc.ircm.proview.plate.web.PlateSelectionWindowPresenter.SELECT_NEW;
import static ca.qc.ircm.proview.plate.web.PlateSelectionWindowPresenter.TITLE;
import static ca.qc.ircm.proview.plate.web.PlateSelectionWindowPresenter.WINDOW_STYLE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.CloseWindowOnViewChange.CloseWindowOnViewChangeListener;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateSelectionWindowPresenterTest extends AbstractComponentTestCase {
  private PlateSelectionWindowPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private PlateSelectionWindow window;
  @Mock
  private PlateService plateService;
  @Mock
  private Plate plate;
  @Captor
  private ArgumentCaptor<Plate> plateCaptor;
  @Captor
  private ArgumentCaptor<Collection<Plate>> platesCaptor;
  private PlateSelectionWindowDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlateSelectionWindow.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Plate> plates = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new PlateSelectionWindowPresenter(plateService);
    design = new PlateSelectionWindowDesign();
    window.design = design;
    when(window.getLocale()).thenReturn(locale);
    when(window.getResources()).thenReturn(resources);
    when(window.getGeneralResources()).thenReturn(generalResources);
    when(window.getUI()).thenReturn(ui);
    //when(window.getParent()).thenReturn(ui);
    window.platesSelection = mock(PlatesSelectionComponent.class);
    plates.add(entityManager.find(Plate.class, 26L));
    plates.add(entityManager.find(Plate.class, 107L));
    plates.add(entityManager.find(Plate.class, 123L));
    when(plateService.all(any())).thenReturn(new ArrayList<>(plates));
  }

  @Test
  public void styles() {
    presenter.init(window);

    verify(window).addStyleName(WINDOW_STYLE);
    assertTrue(design.name.getStyleName().contains(NAME));
    assertTrue(design.selectNew.getStyleName().contains(SELECT_NEW));
    assertTrue(design.select.getStyleName().contains(SELECT));
  }

  @Test
  public void captions() {
    presenter.init(window);

    verify(window).setCaption(resources.message(TITLE));
    assertEquals(resources.message(NAME), design.name.getCaption());
    assertEquals(resources.message(SELECT_NEW), design.selectNew.getCaption());
    assertEquals(resources.message(SELECT), design.select.getCaption());
  }

  @Test
  public void closeWindowOnViewChange() {
    presenter.init(window);

    verify(navigator).addViewChangeListener(any(CloseWindowOnViewChangeListener.class));
  }

  @Test
  public void selectNew_NameEmpty() {
    presenter.init(window);

    design.selectNew.click();

    verify(window, never()).fireSaveEvent(any());
    assertTrue(design.name.getErrorMessage() != null);
    assertEquals(errorMessage(generalResources.message(WebConstants.REQUIRED)),
        design.name.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void selectNew_NameAlreadyExists() {
    presenter.init(window);
    when(plateService.nameAvailable(any())).thenReturn(false);
    String name = "new_plate";
    design.name.setValue(name);

    design.selectNew.click();

    verify(window, never()).fireSaveEvent(any());
    assertTrue(design.name.getErrorMessage() != null);
    assertEquals(errorMessage(generalResources.message(WebConstants.ALREADY_EXISTS)),
        design.name.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void selectNew() {
    presenter.init(window);
    when(plateService.nameAvailable(any())).thenReturn(true);
    String name = "new_plate";
    design.name.setValue(name);

    design.selectNew.click();

    verify(window).fireSaveEvent(plateCaptor.capture());
    Plate plate = plateCaptor.getValue();
    assertNotNull(plate);
    assertNull(plate.getId());
    assertEquals(name, plate.getName());
  }

  @Test
  public void select_Empty() {
    presenter.init(window);

    design.select.click();

    verify(window).fireSaveEvent(null);
  }

  @Test
  public void select() {
    presenter.init(window);
    when(window.platesSelection.getSelectedItems())
        .thenReturn(Stream.of(plate).collect(Collectors.toSet()));

    design.select.click();

    verify(window).fireSaveEvent(plate);
  }

  @Test
  public void setValue() {
    presenter.init(window);
    when(plate.getId()).thenReturn(1L);

    presenter.setValue(plate);

    verify(window.platesSelection).setSelectedItems(platesCaptor.capture());
    Collection<Plate> selection = platesCaptor.getValue();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(plate));
  }

  @Test
  public void setValue_New() {
    presenter.init(window);
    String name = "new_plate";
    Plate plate = new Plate(null, name);

    presenter.setValue(plate);

    assertEquals(name, design.name.getValue());
  }

  @Test(expected = NullPointerException.class)
  public void setValue_Null() {
    presenter.init(window);
    presenter.setValue(null);
  }
}
