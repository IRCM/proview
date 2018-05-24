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

import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.EMPTY_COUNT;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.FILTER;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.HEADER;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.INSERT_TIME;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.LAST_TREATMENT;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.NAME;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.PLATES;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.SUBMISSION;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.gridStartEdit;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.gridItems;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Range;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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
    presenter = new PlatesViewPresenter(plateService, localDateFilterComponentProvider,
        plateWindowProvider, applicationName);
    design = new PlatesViewDesign();
    view.design = design;
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
    assertTrue(design.plates.getStyleName().contains(PLATES));
    assertTrue(design.plates.getStyleName().contains(COMPONENTS));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
  }

  @Test
  public void platesGrid() {
    presenter.init(view);

    assertEquals(6, design.plates.getColumns().size());
    assertEquals(NAME, design.plates.getColumns().get(0).getId());
    assertEquals(resources.message(NAME), design.plates.getColumn(NAME).getCaption());
    assertTrue(
        containsInstanceOf(design.plates.getColumn(NAME).getExtensions(), ComponentRenderer.class));
    assertTrue(design.plates.getColumn(NAME).isSortable());
    for (Plate plate : plates) {
      assertTrue(design.plates.getColumn(NAME).getValueProvider().apply(plate) instanceof Button);
      Button button = (Button) design.plates.getColumn(NAME).getValueProvider().apply(plate);
      assertTrue(button.getStyleName().contains(NAME));
      assertEquals(plate.getName(), button.getCaption());
    }
    assertEquals(EMPTY_COUNT, design.plates.getColumns().get(1).getId());
    assertEquals(resources.message(EMPTY_COUNT), design.plates.getColumn(EMPTY_COUNT).getCaption());
    assertTrue(design.plates.getColumn(EMPTY_COUNT).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.getEmptyWellCount(),
          design.plates.getColumn(EMPTY_COUNT).getValueProvider().apply(plate));
    }
    assertEquals(SAMPLE_COUNT, design.plates.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLE_COUNT),
        design.plates.getColumn(SAMPLE_COUNT).getCaption());
    assertTrue(design.plates.getColumn(SAMPLE_COUNT).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.getSampleCount(),
          design.plates.getColumn(SAMPLE_COUNT).getValueProvider().apply(plate));
    }
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    assertEquals(LAST_TREATMENT, design.plates.getColumns().get(3).getId());
    assertEquals(resources.message(LAST_TREATMENT),
        design.plates.getColumn(LAST_TREATMENT).getCaption());
    assertFalse(design.plates.getColumn(LAST_TREATMENT).isSortable());
    for (Plate plate : plates) {
      if (lastTreatmentOrAnalysisDate.get(plate) != null) {
        assertEquals(dateFormatter.format(toLocalDate(lastTreatmentOrAnalysisDate.get(plate))),
            design.plates.getColumn(LAST_TREATMENT).getValueProvider().apply(plate));
      } else {
        assertNull(design.plates.getColumn(LAST_TREATMENT).getValueProvider().apply(plate));
      }
    }
    assertEquals(INSERT_TIME, design.plates.getColumns().get(4).getId());
    assertEquals(resources.message(INSERT_TIME), design.plates.getColumn(INSERT_TIME).getCaption());
    assertTrue(design.plates.getColumn(INSERT_TIME).isSortable());
    for (Plate plate : plates) {
      assertEquals(dateFormatter.format(toLocalDate(plate.getInsertTime())),
          design.plates.getColumn(INSERT_TIME).getValueProvider().apply(plate));
    }
    assertEquals(SUBMISSION, design.plates.getColumns().get(5).getId());
    assertEquals(resources.message(SUBMISSION), design.plates.getColumn(SUBMISSION).getCaption());
    assertTrue(design.plates.getColumn(SUBMISSION).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.isSubmission() ? resources.message(property(SUBMISSION, true)) : "",
          design.plates.getColumn(SUBMISSION).getValueProvider().apply(plate));
    }
    assertEquals(1, design.plates.getSortOrder().size());
    assertEquals(INSERT_TIME, design.plates.getSortOrder().get(0).getSorted().getId());
    assertEquals(SortDirection.DESCENDING, design.plates.getSortOrder().get(0).getDirection());
  }

  @Test
  public void platesGrid_SortName() {
    presenter.init(view);

    design.plates.sort(NAME);

    List<Plate> plates = gridItems(design.plates).collect(Collectors.toList());
    assertEquals((Long) 26L, plates.get(0).getId());
    assertEquals((Long) 123L, plates.get(1).getId());
    assertEquals((Long) 107L, plates.get(2).getId());
  }

  @Test
  public void platesGrid_SortNameReverse() {
    presenter.init(view);

    design.plates.sort(NAME, SortDirection.DESCENDING);

    List<Plate> plates = gridItems(design.plates).collect(Collectors.toList());
    assertEquals((Long) 107L, plates.get(0).getId());
    assertEquals((Long) 123L, plates.get(1).getId());
    assertEquals((Long) 26L, plates.get(2).getId());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nameFilter() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.nameContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    Label label = (Label) layout.getComponent(0);
    TextField textField = (TextField) layout.getComponent(1);
    Integer filterValue = 5;
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn(filterValue.toString());

    listener.valueChange(event);

    assertEquals(resources.message(property(EMPTY_COUNT, FILTER)), label.getValue());
    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.minimumEmptyCount);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_Invalid() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn("a");

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(null, filter.minimumEmptyCount);
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        textField.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_InvalidThenValid() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    Integer filterValue = 5;
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn("a");
    event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn(filterValue.toString());

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.minimumEmptyCount);
    assertNull(textField.getErrorMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_Double() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn("2.1");

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(null, filter.minimumEmptyCount);
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        textField.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_BelowZero() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn("-1");

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(null, filter.minimumEmptyCount);
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, Plate.DEFAULT_PLATE_SIZE)),
        textField.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void insertTimeFilter() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    verify(localDateFilterComponentProvider).get();
    verify(localDateFilterComponent).addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(INSERT_TIME);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener = localDateRangeSaveListenerCaptor.getValue();
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(range, filter.insertTimeRange);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void submissionFilter() {
    presenter.init(view);
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SUBMISSION);
    ComboBox<Boolean> comboBox = (ComboBox<Boolean>) cell.getComponent();
    List<Boolean> values = items(comboBox);
    for (Boolean value : values) {
      assertEquals(resources.message(property(SUBMISSION, value)),
          comboBox.getItemCaptionGenerator().apply(value));
    }
    Boolean filterValue = true;

    comboBox.setValue(filterValue);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.submission);
  }

  @Test
  public void viewPlate() {
    presenter.init(view);
    Plate plate = plates.get(0);

    Button button = (Button) design.plates.getColumn(NAME).getValueProvider().apply(plate);
    button.click();

    verify(plateWindowProvider).get();
    verify(plateWindow).setValue(plate);
    verify(view).addWindow(plateWindow);
  }

  @Test
  public void editPlate_NoName() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(view);
    Plate plate = plates.get(0);
    gridStartEdit(design.plates, plate);
    TextField nameField = (TextField) design.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("");
    design.plates.getEditor().save();

    verify(plateService, never()).update(any());
    BindingValidationStatus<?> validation =
        design.plates.getColumn(NAME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(REQUIRED), validation.getMessage().get());
  }

  @Test
  public void editPlate_NameExists() {
    when(plateService.nameAvailable(any())).thenReturn(false);
    presenter.init(view);
    Plate plate = plates.get(0);
    gridStartEdit(design.plates, plate);
    TextField nameField = (TextField) design.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("abc");
    design.plates.getEditor().save();

    verify(plateService, never()).update(any());
    BindingValidationStatus<?> validation =
        design.plates.getColumn(NAME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(ALREADY_EXISTS), validation.getMessage().get());
  }

  @Test
  public void editPlate_NameExistsPlateName() {
    when(plateService.nameAvailable(any())).thenReturn(false);
    presenter.init(view);
    Plate plate = plates.get(0);
    gridStartEdit(design.plates, plate);
    TextField nameField = (TextField) design.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue(plate.getName());
    design.plates.getEditor().save();

    verify(plateService).update(any());
    BindingValidationStatus<?> validation =
        design.plates.getColumn(NAME).getEditorBinding().validate();
    assertFalse(validation.isError());
  }

  @Test
  public void editPlate_Save() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(view);
    Plate plate = plates.get(0);
    gridStartEdit(design.plates, plate);
    TextField nameField = (TextField) design.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("unit_test");
    design.plates.getEditor().save();

    BindingValidationStatus<?> validation =
        design.plates.getColumn(NAME).getEditorBinding().validate();
    assertFalse(validation.isError());
    verify(plateService).update(plate);
    assertEquals("unit_test", plate.getName());
  }

  @Test
  public void editPlate_Cancel() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(view);
    Plate plate = plates.get(0);
    String name = plate.getName();
    gridStartEdit(design.plates, plate);
    TextField nameField = (TextField) design.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("unit_test");
    design.plates.getEditor().cancel();

    verify(plateService, never()).update(any());
    assertEquals(name, plate.getName());
  }
}
