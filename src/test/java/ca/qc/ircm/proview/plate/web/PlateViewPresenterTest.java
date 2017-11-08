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

import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.HEADER;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PLATE;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PLATE_PANEL;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.items;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateViewPresenterTest {
  private PlateViewPresenter presenter;
  @Mock
  private PlateView view;
  @Mock
  private PlateService plateService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private PlateComponent plateComponent;
  @Inject
  private PlateService realPlateService;
  @Value("${spring.application.name}")
  private String applicationName;
  private PlateViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlateView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Plate> plates;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    presenter = new PlateViewPresenter(plateService, authorizationService, applicationName);
    design = new PlateViewDesign();
    view.design = design;
    view.plateComponent = plateComponent;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(plateService.get(any(Long.class)))
        .thenAnswer(i -> realPlateService.get(i.getArgumentAt(0, Long.class)));
    plates = new ArrayList<>();
    plates.add(realPlateService.get(26L));
    plates.add(realPlateService.get(107L));
    when(plateService.all(any())).thenReturn(plates);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.plateComponentPanel.getStyleName().contains(PLATE_PANEL));
    assertTrue(design.plate.getStyleName().contains(PLATE));
  }

  @Test
  public void caption() {
    presenter.init(view);
    presenter.enter("26");

    Plate plate = realPlateService.get(26L);
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(plate.getName(), design.plateComponentPanel.getCaption());
  }

  @Test
  public void caption_NoPlate() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(null, design.plateComponentPanel.getCaption());
  }

  @Test
  public void plate() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.plate.isVisible());
    assertFalse(design.plate.isEmptySelectionAllowed());
    assertNull(design.plate.getNewItemHandler());
    List<Plate> plates = items(design.plate);
    assertEquals(this.plates.size(), plates.size());
    for (Plate plate : this.plates) {
      assertTrue(plates.contains(plate));
      assertEquals(plate.getName(), design.plate.getItemCaptionGenerator().apply(plate));
    }
    Plate plate = plates.get(1);
    design.plate.setValue(plate);
    assertEquals(plate.getName(), design.plateComponentPanel.getCaption());
    verify(plateComponent).setValue(plate);
  }

  @Test
  public void plate_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.plate.isVisible());
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(null, design.plateComponentPanel.getCaption());
    verify(plateComponent, never()).setValue(any());
  }

  @Test
  public void enter_Plate() {
    presenter.init(view);
    presenter.enter("26");

    Plate plate = realPlateService.get(26L);
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(plate, design.plate.getValue());
    assertEquals(plate.getName(), design.plateComponentPanel.getCaption());
    verify(plateComponent).setValue(plate);
  }
}
