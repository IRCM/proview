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

import static ca.qc.ircm.proview.plate.PlateProperties.INSERT_TIME;
import static ca.qc.ircm.proview.plate.PlateProperties.NAME;
import static ca.qc.ircm.proview.plate.PlateProperties.SUBMISSION;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.EMPTY_COUNT;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.FILTER;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.LAST_TREATMENT;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.PLATES;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.SAMPLE_COUNT;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
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
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
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
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlatesSelectionComponentPresenterTest extends AbstractComponentTestCase {
  @Inject
  private PlatesSelectionComponentPresenter presenter;
  @Inject
  private PlateRepository repository;
  @MockBean
  private PlateService plateService;
  @MockBean
  private LocalDateFilterComponent insertDateFilter;
  @MockBean
  private PlateWindow plateWindow;
  @Mock
  private PlatesSelectionComponent component;
  @Mock
  private ListDataProvider<Plate> platesDataProvider;
  @Captor
  private ArgumentCaptor<PlateFilter> filterCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<Range<LocalDate>>> localDateRangeSaveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlatesSelectionComponent.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Plate> plates = new ArrayList<>();
  private Map<Plate, Instant> lastTreatmentOrAnalysisDate = new HashMap<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(component.getLocale()).thenReturn(locale);
    when(component.getResources()).thenReturn(resources);
    when(component.getGeneralResources()).thenReturn(generalResources);
    component.plates = new Grid<>();
    component.plates.setParent(ui);
    plates.add(repository.findOne(26L));
    plates.add(repository.findOne(107L));
    plates.add(repository.findOne(123L));
    IntStream.range(0, plates.size() - 1).forEach(
        i -> lastTreatmentOrAnalysisDate.put(plates.get(i), Instant.now().minusSeconds(i * 2)));
    when(plateService.all(any())).thenReturn(new ArrayList<>(plates));
    when(plateService.lastTreatmentOrAnalysisDate(any()))
        .thenAnswer(i -> lastTreatmentOrAnalysisDate.get(i.getArgumentAt(0, Plate.class)));
    when(plateService.get(any()))
        .thenAnswer(i -> repository.findOne(i.getArgumentAt(0, Long.class)));
  }

  @Test
  public void styles() {
    presenter.init(component);

    assertTrue(component.plates.getStyleName().contains(PLATES));
    assertTrue(component.plates.getStyleName().contains(COMPONENTS));
  }

  @Test
  public void platesGrid() {
    presenter.init(component);

    assertEquals(6, component.plates.getColumns().size());
    assertEquals(NAME, component.plates.getColumns().get(0).getId());
    assertEquals(resources.message(NAME), component.plates.getColumn(NAME).getCaption());
    assertTrue(containsInstanceOf(component.plates.getColumn(NAME).getExtensions(),
        ComponentRenderer.class));
    assertTrue(component.plates.getColumn(NAME).isSortable());
    for (Plate plate : plates) {
      assertTrue(
          component.plates.getColumn(NAME).getValueProvider().apply(plate) instanceof Button);
      Button button = (Button) component.plates.getColumn(NAME).getValueProvider().apply(plate);
      assertTrue(button.getStyleName().contains(NAME));
      assertEquals(plate.getName(), button.getCaption());
    }
    assertEquals(EMPTY_COUNT, component.plates.getColumns().get(1).getId());
    assertEquals(resources.message(EMPTY_COUNT),
        component.plates.getColumn(EMPTY_COUNT).getCaption());
    assertTrue(component.plates.getColumn(EMPTY_COUNT).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.getEmptyWellCount(),
          component.plates.getColumn(EMPTY_COUNT).getValueProvider().apply(plate));
    }
    assertEquals(SAMPLE_COUNT, component.plates.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLE_COUNT),
        component.plates.getColumn(SAMPLE_COUNT).getCaption());
    assertTrue(component.plates.getColumn(SAMPLE_COUNT).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.getSampleCount(),
          component.plates.getColumn(SAMPLE_COUNT).getValueProvider().apply(plate));
    }
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    assertEquals(LAST_TREATMENT, component.plates.getColumns().get(3).getId());
    assertEquals(resources.message(LAST_TREATMENT),
        component.plates.getColumn(LAST_TREATMENT).getCaption());
    assertFalse(component.plates.getColumn(LAST_TREATMENT).isSortable());
    for (Plate plate : plates) {
      if (lastTreatmentOrAnalysisDate.get(plate) != null) {
        assertEquals(dateFormatter.format(toLocalDate(lastTreatmentOrAnalysisDate.get(plate))),
            component.plates.getColumn(LAST_TREATMENT).getValueProvider().apply(plate));
      } else {
        assertNull(component.plates.getColumn(LAST_TREATMENT).getValueProvider().apply(plate));
      }
    }
    assertEquals(INSERT_TIME, component.plates.getColumns().get(4).getId());
    assertEquals(resources.message(INSERT_TIME),
        component.plates.getColumn(INSERT_TIME).getCaption());
    assertTrue(component.plates.getColumn(INSERT_TIME).isSortable());
    for (Plate plate : plates) {
      assertEquals(dateFormatter.format(toLocalDate(plate.getInsertTime())),
          component.plates.getColumn(INSERT_TIME).getValueProvider().apply(plate));
    }
    assertEquals(SUBMISSION, component.plates.getColumns().get(5).getId());
    assertEquals(resources.message(SUBMISSION),
        component.plates.getColumn(SUBMISSION).getCaption());
    assertFalse(component.plates.getColumn(SUBMISSION).isHidden());
    assertTrue(component.plates.getColumn(SUBMISSION).isSortable());
    for (Plate plate : plates) {
      assertEquals(plate.isSubmission() ? resources.message(property(SUBMISSION, true)) : "",
          component.plates.getColumn(SUBMISSION).getValueProvider().apply(plate));
    }
    assertEquals(1, component.plates.getSortOrder().size());
    assertEquals(INSERT_TIME, component.plates.getSortOrder().get(0).getSorted().getId());
    assertEquals(SortDirection.DESCENDING, component.plates.getSortOrder().get(0).getDirection());
  }

  @Test
  public void platesGrid_SortName() {
    presenter.init(component);

    component.plates.sort(NAME);

    List<Plate> plates = gridItems(component.plates).collect(Collectors.toList());
    assertEquals((Long) 26L, plates.get(0).getId());
    assertEquals((Long) 123L, plates.get(1).getId());
    assertEquals((Long) 107L, plates.get(2).getId());
  }

  @Test
  public void platesGrid_SortNameReverse() {
    presenter.init(component);

    component.plates.sort(NAME, SortDirection.DESCENDING);

    List<Plate> plates = gridItems(component.plates).collect(Collectors.toList());
    assertEquals((Long) 107L, plates.get(0).getId());
    assertEquals((Long) 123L, plates.get(1).getId());
    assertEquals((Long) 26L, plates.get(2).getId());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nameFilter() {
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
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
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    Integer filterValue = 5;
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn(filterValue.toString());

    listener.valueChange(event);

    Label label = (Label) layout.getComponent(0);
    assertEquals(resources.message(property(EMPTY_COUNT, FILTER)), label.getValue());
    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.minimumEmptyCount);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_Invalid() {
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
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
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMPTY_COUNT);
    AbstractOrderedLayout layout = (AbstractOrderedLayout) cell.getComponent();
    TextField textField = (TextField) layout.getComponent(1);
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    when(event.getValue()).thenReturn("a");
    event = mock(ValueChangeEvent.class);
    when(event.getComponent()).thenReturn(textField);
    Integer filterValue = 5;
    when(event.getValue()).thenReturn(filterValue.toString());
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();

    listener.valueChange(event);

    verify(platesDataProvider).refreshAll();
    PlateFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.minimumEmptyCount);
    assertNull(textField.getErrorMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emptyCountFilter_Double() {
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
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
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
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
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
    verify(insertDateFilter).addSaveListener(localDateRangeSaveListenerCaptor.capture());
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
    presenter.init(component);
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.getHeaderRow(1);
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
    presenter.init(component);
    Plate plate = plates.get(0);

    Button button = (Button) component.plates.getColumn(NAME).getValueProvider().apply(plate);
    button.click();

    verify(plateWindow).setValue(plate);
    verify(component).addWindow(plateWindow);
  }

  @Test
  public void editPlate_NoName() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(component);
    Plate plate = plates.get(0);
    gridStartEdit(component.plates, plate);
    TextField nameField =
        (TextField) component.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("");
    component.plates.getEditor().save();

    verify(plateService, never()).update(any());
    BindingValidationStatus<?> validation =
        component.plates.getColumn(NAME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(REQUIRED), validation.getMessage().get());
  }

  @Test
  public void editPlate_NameExists() {
    when(plateService.nameAvailable(any())).thenReturn(false);
    presenter.init(component);
    Plate plate = plates.get(0);
    gridStartEdit(component.plates, plate);
    TextField nameField =
        (TextField) component.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("abc");
    component.plates.getEditor().save();

    verify(plateService, never()).update(any());
    BindingValidationStatus<?> validation =
        component.plates.getColumn(NAME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(ALREADY_EXISTS), validation.getMessage().get());
  }

  @Test
  public void editPlate_NameExistsPlateName() {
    when(plateService.nameAvailable(any())).thenReturn(false);
    presenter.init(component);
    Plate plate = plates.get(0);
    gridStartEdit(component.plates, plate);
    TextField nameField =
        (TextField) component.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue(plate.getName());
    component.plates.getEditor().save();

    verify(plateService).update(any());
    BindingValidationStatus<?> validation =
        component.plates.getColumn(NAME).getEditorBinding().validate();
    assertFalse(validation.isError());
  }

  @Test
  public void editPlate_Save() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(component);
    Plate plate = plates.get(0);
    gridStartEdit(component.plates, plate);
    TextField nameField =
        (TextField) component.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("unit_test");
    component.plates.getEditor().save();

    BindingValidationStatus<?> validation =
        component.plates.getColumn(NAME).getEditorBinding().validate();
    assertFalse(validation.isError());
    verify(plateService).update(plate);
    assertEquals("unit_test", plate.getName());
  }

  @Test
  public void editPlate_Cancel() {
    when(plateService.nameAvailable(any())).thenReturn(true);
    presenter.init(component);
    Plate plate = plates.get(0);
    final String name = plate.getName();
    gridStartEdit(component.plates, plate);
    TextField nameField =
        (TextField) component.plates.getColumn(NAME).getEditorBinding().getField();
    nameField.setValue("unit_test");
    component.plates.getEditor().cancel();

    verify(plateService, never()).update(any());
    assertEquals(name, plate.getName());
  }

  @Test
  public void isExcludeSubmissionPlates() {
    presenter.init(component);

    assertFalse(presenter.isExcludeSubmissionPlates());
  }

  @Test
  public void isExcludeSubmissionPlates_True() {
    presenter.init(component);
    presenter.setExcludeSubmissionPlates(true);

    assertTrue(presenter.isExcludeSubmissionPlates());
  }

  @Test
  public void isExcludeSubmissionPlates_False() {
    presenter.init(component);
    presenter.setExcludeSubmissionPlates(true);
    presenter.setExcludeSubmissionPlates(false);

    assertFalse(presenter.isExcludeSubmissionPlates());
  }

  @Test
  public void setExcludeSubmissionPlates_True() {
    presenter.setExcludeSubmissionPlates(true);

    presenter.init(component);
    assertTrue(component.plates.getColumn(SUBMISSION).isHidden());
    verify(plateService).all(null);
    assertFalse(presenter.getFilter().submission);
  }

  @Test
  public void setExcludeSubmissionPlates_False() {
    presenter.setExcludeSubmissionPlates(true);
    presenter.setExcludeSubmissionPlates(false);

    presenter.init(component);
    assertFalse(component.plates.getColumn(SUBMISSION).isHidden());
    verify(plateService, atLeastOnce()).all(null);
    assertNull(presenter.getFilter().submission);
  }

  @Test
  public void setExcludeSubmissionPlates_TrueAfterInit() {
    presenter.init(component);
    presenter.setExcludeSubmissionPlates(true);

    assertTrue(component.plates.getColumn(SUBMISSION).isHidden());
    verify(plateService).all(null);
    assertFalse(presenter.getFilter().submission);
  }

  @Test
  public void setExcludeSubmissionPlates_FalseAfterInit() {
    presenter.init(component);
    presenter.setExcludeSubmissionPlates(true);
    presenter.setExcludeSubmissionPlates(false);

    assertFalse(component.plates.getColumn(SUBMISSION).isHidden());
    verify(plateService, atLeastOnce()).all(null);
    assertNull(presenter.getFilter().submission);
  }
}
