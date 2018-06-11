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

import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.HEADER;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Range;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlatesViewPresenterTest {
  private PlatesViewPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private PlatesView view;
  @Mock
  private PlateService plateService;
  @Mock
  private Provider<LocalDateFilterComponent> localDateFilterComponentProvider;
  @Mock
  private Provider<PlateWindow> plateWindowProvider;
  @Mock
  private PlateWindow plateWindow;
  @Mock
  private ListDataProvider<Plate> platesDataProvider;
  @Mock
  private LocalDateFilterComponent localDateFilterComponent;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession vaadinSession;
  @Captor
  private ArgumentCaptor<SaveListener<Range<LocalDate>>> localDateRangeSaveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private PlatesViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlatesView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Plate> plates = new ArrayList<>();
  private Map<Plate, Instant> lastTreatmentOrAnalysisDate = new HashMap<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new PlatesViewPresenter(applicationName);
    design = new PlatesViewDesign();
    view.design = design;
    view.platesSelection = mock(PlatesSelectionComponent.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    plates.add(entityManager.find(Plate.class, 26L));
    plates.add(entityManager.find(Plate.class, 107L));
    plates.add(entityManager.find(Plate.class, 123L));
    IntStream.range(0, plates.size() - 1).forEach(
        i -> lastTreatmentOrAnalysisDate.put(plates.get(i), Instant.now().minusSeconds(i * 2)));
    when(plateService.all(null)).thenReturn(plates);
    when(plateService.lastTreatmentOrAnalysisDate(any()))
        .thenAnswer(i -> lastTreatmentOrAnalysisDate.get(i.getArgumentAt(0, Plate.class)));
    when(plateService.get(any()))
        .thenAnswer(i -> entityManager.find(Plate.class, i.getArgumentAt(0, Long.class)));
    when(localDateFilterComponentProvider.get()).thenReturn(localDateFilterComponent);
    when(plateWindowProvider.get()).thenReturn(plateWindow);
    when(view.getUI()).thenReturn(ui);
    when(view.getParent()).thenReturn(ui);
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    design.setParent(view);
    when(ui.getSession()).thenReturn(vaadinSession);
    when(vaadinSession.hasLock()).thenReturn(true);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
  }
}
