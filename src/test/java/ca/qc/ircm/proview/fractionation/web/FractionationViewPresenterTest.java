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

package ca.qc.ircm.proview.fractionation.web;

import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.COMMENT;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.DESTINATION;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.FRACTIONS;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.FRACTIONS_PANEL;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.HEADER;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.INVALID_FRACTIONATION;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.NUMBER;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.PI_INTERVAL;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.TITLE;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.TYPE;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.TYPE_PANEL;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FractionationViewPresenterTest {
  private FractionationViewPresenter presenter;
  @Mock
  private FractionationView view;
  @Mock
  private FractionationService fractionationService;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private FractionationViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(FractionationView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new FractionationViewPresenter(fractionationService, applicationName);
    design = new FractionationViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.typePanel.getStyleName().contains(TYPE_PANEL));
    assertTrue(design.type.getStyleName().contains(TYPE));
    assertTrue(design.fractionsPanel.getStyleName().contains(FRACTIONS_PANEL));
    assertTrue(design.fractions.getStyleName().contains(FRACTIONS));
    assertTrue(design.fractions.getStyleName().contains(COMPONENTS));
  }

  @Test
  public void captions() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 203L);
    when(fractionationService.get(any())).thenReturn(fractionation);
    presenter.init(view);
    presenter.enter("203");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(TYPE_PANEL), design.typePanel.getCaption());
    assertEquals(fractionation.getFractionationType().getLabel(locale), design.type.getValue());
    assertEquals(resources.message(FRACTIONS_PANEL), design.fractionsPanel.getCaption());
  }

  @Test
  public void fractions() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 203L);
    when(fractionationService.get(any())).thenReturn(fractionation);
    presenter.init(view);
    presenter.enter("203");

    assertEquals(6, design.fractions.getColumns().size());
    assertEquals(SAMPLE, design.fractions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.fractions.getColumn(SAMPLE).getCaption());
    Collection<TreatedSample> fractions = dataProvider(design.fractions).getItems();
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getSample().getName(),
          design.fractions.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.fractions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.fractions.getColumn(CONTAINER).getCaption());
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getContainer().getFullName(),
          design.fractions.getColumn(CONTAINER).getValueProvider().apply(ts));
    }
    assertEquals(DESTINATION, design.fractions.getColumns().get(2).getId());
    assertEquals(resources.message(DESTINATION),
        design.fractions.getColumn(DESTINATION).getCaption());
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getDestinationContainer().getFullName(),
          design.fractions.getColumn(DESTINATION).getValueProvider().apply(ts));
    }
    assertEquals(NUMBER, design.fractions.getColumns().get(3).getId());
    assertEquals(resources.message(NUMBER), design.fractions.getColumn(NUMBER).getCaption());
    assertFalse(design.fractions.getColumn(NUMBER).isHidden());
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getNumber(), design.fractions.getColumn(NUMBER).getValueProvider().apply(ts));
    }
    assertEquals(PI_INTERVAL, design.fractions.getColumns().get(4).getId());
    assertEquals(resources.message(PI_INTERVAL),
        design.fractions.getColumn(PI_INTERVAL).getCaption());
    assertTrue(design.fractions.getColumn(PI_INTERVAL).isHidden());
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getPiInterval(),
          design.fractions.getColumn(PI_INTERVAL).getValueProvider().apply(ts));
    }
    assertEquals(COMMENT, design.fractions.getColumns().get(5).getId());
    assertEquals(resources.message(COMMENT), design.fractions.getColumn(COMMENT).getCaption());
    for (TreatedSample ts : fractions) {
      assertEquals(ts.getComment(),
          design.fractions.getColumn(COMMENT).getValueProvider().apply(ts));
    }
  }

  @Test
  public void fractions_PiType() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 203L);
    fractionation.setFractionationType(FractionationType.PI);
    when(fractionationService.get(any())).thenReturn(fractionation);
    presenter.init(view);
    presenter.enter("203");

    assertTrue(design.fractions.getColumn(NUMBER).isHidden());
    assertFalse(design.fractions.getColumn(PI_INTERVAL).isHidden());
  }

  @Test
  public void enter_Empty() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    verify(view).showWarning(resources.message(INVALID_FRACTIONATION));
    ListDataProvider<TreatedSample> tss = dataProvider(design.fractions);
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_Fractionation() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 203L);
    when(fractionationService.get(any())).thenReturn(fractionation);
    presenter.init(view);
    presenter.enter("203");

    verify(fractionationService).get(203L);
    assertFalse(design.deleted.isVisible());
    List<TreatedSample> tss = new ArrayList<>(dataProvider(design.fractions).getItems());
    assertEquals(fractionation.getTreatedSamples().size(), tss.size());
    for (int i = 0; i < fractionation.getTreatedSamples().size(); i++) {
      assertEquals(fractionation.getTreatedSamples().get(i), tss.get(i));
    }
  }

  @Test
  public void enter_FractionationDeleted() {
    Fractionation fractionation = entityManager.find(Fractionation.class, 203L);
    fractionation.setDeleted(true);
    when(fractionationService.get(any())).thenReturn(fractionation);
    presenter.init(view);
    presenter.enter("203");

    verify(fractionationService).get(203L);
    assertTrue(design.deleted.isVisible());
    List<TreatedSample> tss = new ArrayList<>(dataProvider(design.fractions).getItems());
    assertEquals(fractionation.getTreatedSamples().size(), tss.size());
    for (int i = 0; i < fractionation.getTreatedSamples().size(); i++) {
      assertEquals(fractionation.getTreatedSamples().get(i), tss.get(i));
    }
  }

  @Test
  public void enter_FractionationNotId() {
    presenter.init(view);
    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_FRACTIONATION));
    ListDataProvider<TreatedSample> tss = dataProvider(design.fractions);
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_FractionationIdNotExists() {
    presenter.init(view);
    presenter.enter("2");

    verify(fractionationService).get(2L);
    verify(view).showWarning(resources.message(INVALID_FRACTIONATION));
    ListDataProvider<TreatedSample> tss = dataProvider(design.fractions);
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_Containers() {
    presenter.init(view);
    presenter.enter("containers/11,12");

    verify(view).showWarning(resources.message(INVALID_FRACTIONATION));
    ListDataProvider<TreatedSample> tss = dataProvider(design.fractions);
    assertTrue(tss.getItems().isEmpty());
  }
}
