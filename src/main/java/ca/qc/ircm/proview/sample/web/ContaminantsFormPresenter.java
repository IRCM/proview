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

import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Contaminant;
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
import com.vaadin.ui.Button;
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
 * Contaminants form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContaminantsFormPresenter implements BinderValidator {
  public static final String PANEL = "panel";
  public static final String COUNT = "count";
  public static final String CONTAMINANTS = submissionSample.contaminants.getMetadata().getName();
  public static final String NAME = contaminant.name.getMetadata().getName();
  public static final String QUANTITY = contaminant.quantity.getMetadata().getName();
  public static final String COMMENT = contaminant.comment.getMetadata().getName();
  public static final String DOWN = "down";
  public static final String EXAMPLE = "example";
  private static final int DEFAULT_MAX_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(ContaminantsFormPresenter.class);
  private ContaminantsForm view;
  private ContaminantsFormDesign design;
  private boolean readOnly;
  private int maxCount = DEFAULT_MAX_COUNT;
  private Binder<ItemCount> countBinder = new Binder<>(ItemCount.class);
  private ListDataProvider<Contaminant> dataProvider = DataProvider.ofCollection(new ArrayList<>());
  private Map<Contaminant, Binder<Contaminant>> binders = new LinkedHashMap<>();
  private Map<Contaminant, TextField> nameFields = new HashMap<>();
  private Map<Contaminant, TextField> quantityFields = new HashMap<>();
  private Map<Contaminant, TextField> commentFields = new HashMap<>();
  private Map<Contaminant, Button> downFields = new HashMap<>();

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ContaminantsForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    updateGrid(design.count.getValue());
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    design.panel.addStyleName(PANEL);
    design.panel.setCaption(resources.message(PANEL));
    design.count.addStyleName(COUNT);
    design.count.setCaption(resources.message(COUNT));
    design.count.addValueChangeListener(e -> updateGrid(e.getValue()));
    design.count.setReadOnly(readOnly);
    countBinder();
    design.contaminants.addStyleName(CONTAMINANTS);
    design.contaminants.addStyleName(COMPONENTS);
    design.contaminants.setDataProvider(dataProvider);
    design.contaminants.addColumn(contaminant -> nameField(contaminant), new ComponentRenderer())
        .setId(NAME).setCaption(resources.message(NAME)).setSortable(false);
    design.contaminants
        .addColumn(contaminant -> quantityField(contaminant), new ComponentRenderer())
        .setId(QUANTITY).setCaption(resources.message(QUANTITY)).setSortable(false).setWidth(170);
    design.contaminants.addColumn(contaminant -> commentField(contaminant), new ComponentRenderer())
        .setId(COMMENT).setCaption(resources.message(COMMENT)).setSortable(false).setWidth(170);
    design.contaminants.addColumn(contaminant -> downField(contaminant), new ComponentRenderer())
        .setId(DOWN).setCaption(resources.message(DOWN)).setSortable(false).setHidden(readOnly)
        .setWidth(100);
  }

  private void countBinder() {
    final MessageResource generalResources = view.getGeneralResources();
    countBinder.forField(design.count).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, maxCount), 0, maxCount))
        .bind(ItemCount::getCount, ItemCount::setCount);
  }

  private Binder<Contaminant> binder(Contaminant contaminant) {
    if (binders.containsKey(contaminant)) {
      return binders.get(contaminant);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Contaminant> binder = new BeanValidationBinder<>(Contaminant.class);
      binder.setBean(contaminant);
      binder.forField(nameField(contaminant)).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(NAME);
      binder.forField(quantityField(contaminant)).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(QUANTITY);
      binder.forField(commentField(contaminant)).withNullRepresentation("").bind(COMMENT);
      binders.put(contaminant, binder);
      return binder;
    }
  }

  private TextField nameField(Contaminant contaminant) {
    if (nameFields.containsKey(contaminant)) {
      return nameFields.get(contaminant);
    } else {
      TextField field = new TextField();
      field.addStyleName(NAME);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setWidth("100%");
      field.setReadOnly(readOnly);
      nameFields.put(contaminant, field);
      return field;
    }
  }

  private TextField quantityField(Contaminant contaminant) {
    if (quantityFields.containsKey(contaminant)) {
      return quantityFields.get(contaminant);
    } else {
      final MessageResource resources = view.getResources();
      TextField field = new TextField();
      field.addStyleName(QUANTITY);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setWidth("100%");
      field.setReadOnly(readOnly);
      field.setPlaceholder(resources.message(property(QUANTITY, EXAMPLE)));
      quantityFields.put(contaminant, field);
      return field;
    }
  }

  private TextField commentField(Contaminant contaminant) {
    if (commentFields.containsKey(contaminant)) {
      return commentFields.get(contaminant);
    } else {
      TextField field = new TextField();
      field.addStyleName(COMMENT);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setWidth("100%");
      field.setReadOnly(readOnly);
      commentFields.put(contaminant, field);
      return field;
    }
  }

  private Button downField(Contaminant contaminant) {
    if (downFields.containsKey(contaminant)) {
      return downFields.get(contaminant);
    } else {
      final MessageResource resources = view.getResources();
      Button button = new Button();
      button.addStyleName(DOWN);
      button.addStyleName(ValoTheme.BUTTON_TINY);
      button.setWidth("100%");
      button.setIcon(VaadinIcons.ARROW_DOWN);
      button.setIconAlternateText(resources.message(DOWN));
      button.addClickListener(e -> fill(contaminant));
      downFields.put(contaminant, button);
      return button;
    }
  }

  private void fill(Contaminant contaminant) {
    List<Contaminant> contaminants =
        VaadinUtils.gridItems(design.contaminants).collect(Collectors.toList());
    String name = nameFields.get(contaminant).getValue();
    String quantity = quantityFields.get(contaminant).getValue();
    String comment = commentFields.get(contaminant).getValue();
    boolean copy = false;
    for (Contaminant other : contaminants) {
      if (contaminant.equals(other)) {
        copy = true;
      } else if (copy) {
        nameFields.get(other).setValue(name);
        quantityFields.get(other).setValue(quantity);
        commentFields.get(other).setValue(comment);
      }
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
      design.contaminants.setVisible(count > 0);
      while (dataProvider.getItems().size() > count) {
        Contaminant remove = dataProvider.getItems().stream()
            .skip(dataProvider.getItems().size() - 1).findFirst().orElse(null);
        dataProvider.getItems().remove(remove);
      }
      if (dataProvider.getItems().size() < count) {
        List<Contaminant> contaminantList = new ArrayList<>(binders.keySet());
        while (dataProvider.getItems().size() < count) {
          if (dataProvider.getItems().size() < binders.size()) {
            dataProvider.getItems().add(contaminantList.get(dataProvider.getItems().size()));
          } else {
            Contaminant contaminant = new Contaminant();
            binder(contaminant);
            dataProvider.getItems().add(contaminant);
          }
        }
      }
      design.contaminants.setVisible(count > 0);
      dataProvider.refreshAll();
    }
  }

  boolean validate() {
    logger.trace("Validate contaminants");
    boolean valid = true;
    valid &= validate(countBinder);
    for (Contaminant contaminant : dataProvider.getItems()) {
      valid &= validate(binders.get(contaminant));
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
    if (design != null) {
      design.contaminants.getColumn(DOWN).setHidden(readOnly);
    }
  }

  List<Contaminant> getValue() {
    return new ArrayList<>(dataProvider.getItems());
  }

  void setValue(List<Contaminant> contaminants) {
    if (contaminants == null) {
      contaminants = new ArrayList<>();
    }
    dataProvider.getItems().clear();
    dataProvider.getItems().addAll(contaminants);
    dataProvider.refreshAll();
    contaminants.stream().forEach(contaminant -> binder(contaminant));
    countBinder.setBean(new ItemCount(contaminants.size()));
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
