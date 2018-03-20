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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Submission form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StandardsFormPresenter implements BinderValidator {
  public static final String COUNT = "count";
  public static final String STANDARDS = submissionSample.standards.getMetadata().getName();
  public static final String NAME = standard.name.getMetadata().getName();
  public static final String QUANTITY = standard.quantity.getMetadata().getName();
  public static final String COMMENT = standard.comment.getMetadata().getName();
  public static final String FILL = "fill";
  public static final String EXAMPLE = "example";
  private static final int DEFAULT_MAX_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(StandardsFormPresenter.class);
  private StandardsForm view;
  private StandardsFormDesign design;
  private boolean readOnly;
  private int maxCount = DEFAULT_MAX_COUNT;
  private Binder<ItemCount> countBinder = new Binder<>(ItemCount.class);
  private ListDataProvider<Standard> dataProvider = DataProvider.ofCollection(new ArrayList<>());
  private Map<Standard, Binder<Standard>> binders = new LinkedHashMap<>();
  private Map<Standard, TextField> nameFields = new HashMap<>();
  private Map<Standard, TextField> quantityFields = new HashMap<>();
  private Map<Standard, TextField> commentFields = new HashMap<>();

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(StandardsForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    updateGrid(design.count.getValue());
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    design.count.addStyleName(COUNT);
    design.count.setCaption(resources.message(COUNT));
    design.count.addValueChangeListener(e -> updateGrid(e.getValue()));
    design.count.setReadOnly(readOnly);
    countBinder();
    design.standards.addStyleName(STANDARDS);
    design.standards.addStyleName(COMPONENTS);
    design.standards.setDataProvider(dataProvider);
    design.standards.addColumn(standard -> nameField(standard), new ComponentRenderer()).setId(NAME)
        .setCaption(resources.message(NAME)).setSortable(false);
    design.standards.addColumn(standard -> quantityField(standard), new ComponentRenderer())
        .setId(QUANTITY).setCaption(resources.message(QUANTITY)).setSortable(false);
    design.standards.addColumn(standard -> commentField(standard), new ComponentRenderer())
        .setId(COMMENT).setCaption(resources.message(COMMENT)).setSortable(false);
    design.fill.addStyleName(FILL);
    design.fill.addStyleName(BUTTON_SKIP_ROW);
    design.fill.setCaption(resources.message(FILL));
    design.fill.setIcon(VaadinIcons.ARROW_DOWN);
    design.fill.addClickListener(e -> fill());
    design.fill.setVisible(!readOnly);
  }

  private void countBinder() {
    final MessageResource generalResources = view.getGeneralResources();
    countBinder.forField(design.count).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, maxCount), 0, maxCount))
        .bind(ItemCount::getCount, ItemCount::setCount);
  }

  private Binder<Standard> binder(Standard standard) {
    if (binders.containsKey(standard)) {
      return binders.get(standard);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Standard> binder = new BeanValidationBinder<>(Standard.class);
      binder.setBean(standard);
      binder.forField(nameField(standard)).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(NAME);
      binder.forField(quantityField(standard)).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(QUANTITY);
      binder.forField(commentField(standard)).withNullRepresentation("").bind(COMMENT);
      binders.put(standard, binder);
      return binder;
    }
  }

  private TextField nameField(Standard standard) {
    if (nameFields.containsKey(standard)) {
      return nameFields.get(standard);
    } else {
      TextField field = new TextField();
      field.addStyleName(NAME);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      nameFields.put(standard, field);
      return field;
    }
  }

  private TextField quantityField(Standard standard) {
    if (quantityFields.containsKey(standard)) {
      return quantityFields.get(standard);
    } else {
      final MessageResource resources = view.getResources();
      TextField field = new TextField();
      field.addStyleName(QUANTITY);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setPlaceholder(resources.message(property(QUANTITY, EXAMPLE)));
      quantityFields.put(standard, field);
      return field;
    }
  }

  private TextField commentField(Standard standard) {
    if (commentFields.containsKey(standard)) {
      return commentFields.get(standard);
    } else {
      TextField field = new TextField();
      field.addStyleName(COMMENT);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      commentFields.put(standard, field);
      return field;
    }
  }

  private void updateGrid(String countValue) {
    if (countBinder.isValid()) {
      int count;
      try {
        count = Math.max(Integer.parseInt(countValue), 0);
      } catch (NumberFormatException e) {
        count = 0;
      }
      design.standardsLayout.setVisible(count > 0);
      while (dataProvider.getItems().size() > count) {
        Standard remove = dataProvider.getItems().stream().skip(dataProvider.getItems().size() - 1)
            .findFirst().orElse(null);
        dataProvider.getItems().remove(remove);
      }
      if (dataProvider.getItems().size() < count) {
        List<Standard> standardList = new ArrayList<>(binders.keySet());
        while (dataProvider.getItems().size() < count) {
          if (dataProvider.getItems().size() < binders.size()) {
            dataProvider.getItems().add(standardList.get(dataProvider.getItems().size()));
          } else {
            Standard standard = new Standard();
            binder(standard);
            dataProvider.getItems().add(standard);
          }
        }
      }
      design.standardsLayout.setVisible(count > 0);
      dataProvider.refreshAll();
    }
  }

  private void fill() {
    List<Standard> standards = VaadinUtils.gridItems(design.standards).collect(Collectors.toList());
    if (!standards.isEmpty()) {
      Standard first = standards.get(0);
      String name = nameFields.get(first).getValue();
      String quantity = quantityFields.get(first).getValue();
      String comment = commentFields.get(first).getValue();
      for (Standard standard : standards.subList(1, standards.size())) {
        nameFields.get(standard).setValue(name);
        quantityFields.get(standard).setValue(quantity);
        commentFields.get(standard).setValue(comment);
      }
    }
  }

  boolean validate() {
    logger.trace("Validate standards");
    boolean valid = true;
    valid &= validate(countBinder);
    for (Standard standard : dataProvider.getItems()) {
      valid &= validate(binders.get(standard));
    }
    return valid;
  }

  boolean isReadOnly() {
    return readOnly;
  }

  void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    countBinder.setReadOnly(readOnly);
    binders.values().stream().forEach(binder -> binder.setReadOnly(readOnly));
    if (view != null) {
      design.fill.setVisible(!readOnly);
    }
  }

  List<Standard> getValue() {
    return new ArrayList<>(dataProvider.getItems());
  }

  void setValue(List<Standard> standards) {
    if (standards == null) {
      standards = new ArrayList<>();
    }
    dataProvider.getItems().clear();
    dataProvider.getItems().addAll(standards);
    dataProvider.refreshAll();
    standards.stream().forEach(standard -> binder(standard));
    countBinder.setBean(new ItemCount(standards.size()));
  }

  int getMaxCount() {
    return maxCount;
  }

  void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
    if (view != null) {
      countBinder();
    }
  }

  private static class ItemCount {
    private Integer count;

    private ItemCount() {
    }

    private ItemCount(Integer count) {
      this.count = count;
    }

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }
  }
}
