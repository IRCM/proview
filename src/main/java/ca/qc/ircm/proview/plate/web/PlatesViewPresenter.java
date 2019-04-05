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
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.text.NormalizedComparator;
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
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Plate view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlatesViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PLATES = "plates";
  public static final String ALL = "all";
  public static final String SAMPLE_COUNT = "sampleCount";
  public static final String FILTER = "filter";
  private static final Logger logger = LoggerFactory.getLogger(PlatesViewPresenter.class);
  private PlatesView view;
  private PlatesViewDesign design;
  private ListDataProvider<Plate> platesDataProvider = DataProvider.ofItems();
  private PlateFilter filter;
  private Plate updating;
  @Inject
  private PlateService plateService;
  @Inject
  private LocalDateFilterComponent insertDateFilter;
  @Inject
  private Provider<PlateWindow> plateWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected PlatesViewPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlatesView view) {
    logger.debug("Plates view");
    this.view = view;
    design = view.design;
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    final NormalizedComparator normalizedComparator = new NormalizedComparator();
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.plates.addStyleName(PLATES);
    design.plates.addStyleName(COMPONENTS);
    platesDataProvider.getItems().addAll(plateService.all(null));
    filter = new PlateFilter();
    platesDataProvider.setFilter(p -> filter.test(p));
    design.plates.setDataProvider(platesDataProvider);
    HeaderRow filterRow = design.plates.appendHeaderRow();
    Binder<Plate> binder = new BeanValidationBinder<>(Plate.class);
    design.plates.getEditor().setEnabled(true);
    design.plates.getEditor().addOpenListener(event -> {
      updating = event.getBean();
    });
    design.plates.getEditor().addSaveListener(event -> {
      plateService.update(event.getBean());
    });
    design.plates.getEditor().setBinder(binder);
    design.plates.addColumn(plate -> nameButton(plate), new ComponentRenderer()).setId(NAME)
        .setCaption(resources.message(NAME))
        .setComparator((p1, p2) -> normalizedComparator.compare(p1.getName(), p2.getName()));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.nameContains = e.getValue();
      design.plates.getDataProvider().refreshAll();
    }));
    TextField nameEditor = new TextField();
    nameEditor.addStyleName(NAME);
    design.plates.getColumn(NAME)
        .setEditorBinding(binder.forField(nameEditor).asRequired(generalResources.message(REQUIRED))
            .withNullRepresentation("")
            .withValidator((value, context) -> validateName(value, context)).bind(NAME));
    design.plates.addColumn(plate -> plate.getSampleCount()).setId(SAMPLE_COUNT)
        .setCaption(resources.message(SAMPLE_COUNT));
    design.plates.addColumn(plate -> dateFormatter.format(toLocalDate(plate.getInsertTime())))
        .setId(INSERT_TIME).setCaption(resources.message(INSERT_TIME));
    filterRow.getCell(INSERT_TIME).setComponent(instantFilter(insertDateFilter, e -> {
      filter.insertTimeRange = e.getSavedObject();
      design.plates.getDataProvider().refreshAll();
    }));
    design.plates
        .addColumn(
            plate -> plate.isSubmission() ? resources.message(property(SUBMISSION, true)) : "")
        .setId(SUBMISSION).setCaption(resources.message(SUBMISSION));
    filterRow.getCell(SUBMISSION).setComponent(comboBoxFilter(e -> {
      filter.submission = e.getValue();
      design.plates.getDataProvider().refreshAll();
    }, new Boolean[] { true, false }, value -> resources.message(property(SUBMISSION, value))));
    design.plates.sort(INSERT_TIME, SortDirection.DESCENDING);
  }

  private ValidationResult validateName(String value, ValueContext context) {
    if (value != null && !plateService.nameAvailable(value) && !value.equals(updating.getName())) {
      MessageResource generalResources = view.getGeneralResources();
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
      view.addWindow(plateWindow);
    });
    return button;
  }

  private TextField textFilter(ValueChangeListener<String> listener) {
    MessageResource resources = view.getResources();
    TextField filter = new TextField();
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private Component instantFilter(LocalDateFilterComponent filter,
      SaveListener<Range<LocalDate>> listener) {
    filter.addStyleName(ValoTheme.BUTTON_TINY);
    filter.addSaveListener(listener);
    return filter;
  }

  private <V> ComboBox<V> comboBoxFilter(ValueChangeListener<V> listener, V[] values,
      ItemCaptionGenerator<V> itemCaptionGenerator) {
    MessageResource resources = view.getResources();
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

  PlateFilter getFilter() {
    return filter;
  }
}
