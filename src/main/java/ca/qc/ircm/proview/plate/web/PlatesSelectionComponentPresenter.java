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

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Range;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.text.Collator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Plates selection component presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlatesSelectionComponentPresenter {
  public static final String PLATES = "plates";
  public static final String ALL = "all";
  public static final String NAME = plate.name.getMetadata().getName();
  public static final String EMPTY_COUNT = "emptyCount";
  public static final String SAMPLE_COUNT = "sampleCount";
  public static final String LAST_TREATMENT = "lastTreatment";
  public static final String FILTER = "filter";
  public static final String INSERT_TIME = plate.insertTime.getMetadata().getName();
  public static final String SUBMISSION = plate.submission.getMetadata().getName();
  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(PlatesSelectionComponentPresenter.class);
  private PlatesSelectionComponent component;
  private ListDataProvider<Plate> platesDataProvider = DataProvider.ofItems();
  private PlateFilter filter;
  private Plate updating;
  private boolean excludeSubmissionPlates;
  @Inject
  private PlateService plateService;
  @Inject
  private Provider<LocalDateFilterComponent> localDateFilterComponentProvider;
  @Inject
  private Provider<PlateWindow> plateWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected PlatesSelectionComponentPresenter() {
  }

  protected PlatesSelectionComponentPresenter(PlateService plateService,
      Provider<LocalDateFilterComponent> localDateFilterComponentProvider,
      Provider<PlateWindow> plateWindowProvider, String applicationName) {
    this.plateService = plateService;
    this.localDateFilterComponentProvider = localDateFilterComponentProvider;
    this.plateWindowProvider = plateWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param component
   *          component
   */
  public void init(PlatesSelectionComponent component) {
    this.component = component;
    final MessageResource resources = component.getResources();
    final MessageResource generalResources = component.getGeneralResources();
    final Locale locale = component.getLocale();
    final Collator collator = Collator.getInstance(locale);
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    component.plates.addStyleName(PLATES);
    component.plates.addStyleName(COMPONENTS);
    platesDataProvider.getItems().addAll(plateService.all(null));
    filter = new PlateFilter();
    platesDataProvider.setFilter(p -> filter.test(p));
    component.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = component.plates.appendHeaderRow();
    Binder<Plate> binder = new BeanValidationBinder<>(Plate.class);
    component.plates.getEditor().setEnabled(true);
    component.plates.getEditor().addOpenListener(event -> {
      updating = event.getBean();
    });
    component.plates.getEditor().addSaveListener(event -> {
      plateService.update(event.getBean());
    });
    component.plates.getEditor().setBinder(binder);
    component.plates.addColumn(plate -> nameButton(plate), new ComponentRenderer()).setId(NAME)
        .setCaption(resources.message(NAME))
        .setComparator((p1, p2) -> collator.compare(p1.getName(), p2.getName()));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.nameContains = e.getValue();
      component.plates.getDataProvider().refreshAll();
    }));
    TextField nameEditor = new TextField();
    nameEditor.addStyleName(NAME);
    component.plates.getColumn(NAME)
        .setEditorBinding(binder.forField(nameEditor).asRequired(generalResources.message(REQUIRED))
            .withNullRepresentation("")
            .withValidator((value, context) -> validateName(value, context)).bind(NAME));
    component.plates.addColumn(plate -> plate.getEmptyWellCount()).setId(EMPTY_COUNT)
        .setCaption(resources.message(EMPTY_COUNT));
    HorizontalLayout emptyCountLayout = new HorizontalLayout();
    emptyCountLayout.addComponent(new Label(resources.message(property(EMPTY_COUNT, FILTER))));
    emptyCountLayout.addComponent(textFilter(event -> {
      TextField field = (TextField) event.getComponent();
      field.setComponentError(null);
      Integer minimumEmptyCount;
      if (!event.getValue().isEmpty()) {
        try {
          minimumEmptyCount = Integer.valueOf(event.getValue());
          if (minimumEmptyCount < 0 || minimumEmptyCount > Plate.DEFAULT_PLATE_SIZE) {
            minimumEmptyCount = null;
            field.setComponentError(
                new UserError(generalResources.message(OUT_OF_RANGE, 0, Plate.DEFAULT_PLATE_SIZE)));
          }
        } catch (NumberFormatException e) {
          minimumEmptyCount = null;
          field.setComponentError(new UserError(generalResources.message(INVALID_INTEGER)));
        }
      } else {
        minimumEmptyCount = null;
      }
      filter.minimumEmptyCount = minimumEmptyCount;
      component.plates.getDataProvider().refreshAll();
    }));
    filterRow.getCell(EMPTY_COUNT).setComponent(emptyCountLayout);
    component.plates.addColumn(plate -> plate.getSampleCount()).setId(SAMPLE_COUNT)
        .setCaption(resources.message(SAMPLE_COUNT));
    component.plates.addColumn(plate -> {
      Instant date = plateService.lastTreatmentOrAnalysisDate(plate);
      return date != null ? dateFormatter.format(toLocalDate(date)) : null;
    }).setId(LAST_TREATMENT).setCaption(resources.message(LAST_TREATMENT)).setSortable(false);
    component.plates.addColumn(plate -> dateFormatter.format(toLocalDate(plate.getInsertTime())))
        .setId(INSERT_TIME).setCaption(resources.message(INSERT_TIME));
    filterRow.getCell(INSERT_TIME).setComponent(instantFilter(e -> {
      filter.insertTimeRange = e.getSavedObject();
      component.plates.getDataProvider().refreshAll();
    }));
    component.plates
        .addColumn(
            plate -> plate.isSubmission() ? resources.message(property(SUBMISSION, true)) : "")
        .setId(SUBMISSION).setCaption(resources.message(SUBMISSION));
    filterRow.getCell(SUBMISSION).setComponent(comboBoxFilter(e -> {
      filter.submission = e.getValue();
      component.plates.getDataProvider().refreshAll();
    }, new Boolean[] { true, false }, value -> resources.message(property(SUBMISSION, value))));
    component.plates.sort(INSERT_TIME, SortDirection.DESCENDING);
    udpateExcludeSubmission();
  }

  private ValidationResult validateName(String value, ValueContext context) {
    if (value != null && !plateService.nameAvailable(value) && !value.equals(updating.getName())) {
      MessageResource generalResources = component.getGeneralResources();
      return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
    } else {
      return ValidationResult.ok();
    }
  }

  private Button nameButton(Plate plate) {
    Button button = new Button();
    button.addStyleName(NAME);
    button.setCaption(plate.getName());
    button.addClickListener(e -> {
      PlateWindow plateWindow = plateWindowProvider.get();
      plateWindow.setValue(plate);
      plateWindow.center();
      component.addWindow(plateWindow);
    });
    return button;
  }

  private TextField textFilter(ValueChangeListener<String> listener) {
    MessageResource resources = component.getResources();
    TextField filter = new TextField();
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private Component instantFilter(SaveListener<Range<LocalDate>> listener) {
    LocalDateFilterComponent filter = localDateFilterComponentProvider.get();
    filter.addStyleName(ValoTheme.BUTTON_TINY);
    filter.addSaveListener(listener);
    return filter;
  }

  private <V> ComboBox<V> comboBoxFilter(ValueChangeListener<V> listener, V[] values,
      ItemCaptionGenerator<V> itemCaptionGenerator) {
    MessageResource resources = component.getResources();
    ComboBox<V> filter = new ComboBox<>();
    filter.setEmptySelectionAllowed(true);
    filter.setTextInputAllowed(false);
    filter.setEmptySelectionCaption(resources.message(ALL));
    filter.setPlaceholder(resources.message(ALL));
    filter.setItems(values);
    filter.setSelectedItem(null);
    filter.setItemCaptionGenerator(itemCaptionGenerator);
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.COMBOBOX_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private void udpateExcludeSubmission() {
    component.plates.getColumn(SUBMISSION).setHidden(excludeSubmissionPlates);
    filter.submission = excludeSubmissionPlates ? false : null;
    component.plates.getDataProvider().refreshAll();
  }

  boolean isExcludeSubmissionPlates() {
    return excludeSubmissionPlates;
  }

  void setExcludeSubmissionPlates(boolean excludeSubmissionPlates) {
    this.excludeSubmissionPlates = excludeSubmissionPlates;
    if (component != null) {
      udpateExcludeSubmission();
    }
  }

  PlateFilter getFilter() {
    return filter;
  }
}