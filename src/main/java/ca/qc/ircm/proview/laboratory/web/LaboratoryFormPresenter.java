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

package ca.qc.ircm.proview.laboratory.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.ObjectProperty;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Laboratory form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LaboratoryFormPresenter {
  public static final String NAME_PROPERTY = QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION_PROPERTY =
      QLaboratory.laboratory.organization.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private LaboratoryForm view;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(LaboratoryForm view) {
    this.view = view;
    view.setPresenter(this);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    bindFields();
    addFieldListeners();
    updateEditable();
    setCaptions();
    setRequired();
  }

  private void bindFields() {
    laboratoryFieldGroup.setItemDataSource(new BeanItem<>(new Laboratory()));
    laboratoryFieldGroup.bind(view.nameField, NAME_PROPERTY);
    laboratoryFieldGroup.bind(view.organizationField, ORGANIZATION_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    view.organizationField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    view.organizationField.setReadOnly(!editable);
    view.nameField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    view.nameField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.nameField.setCaption(resources.message(NAME_PROPERTY));
    view.organizationField.setCaption(resources.message(ORGANIZATION_PROPERTY));
  }

  private void setRequired() {
    final MessageResource generalResources = view.getGeneralResources();
    view.nameField.setRequired(true);
    view.nameField
        .setRequiredError(generalResources.message("required", view.nameField.getCaption()));
    view.organizationField.setRequired(true);
    view.organizationField.setRequiredError(
        generalResources.message("required", view.organizationField.getCaption()));
  }

  public void commit() throws CommitException {
    laboratoryFieldGroup.commit();
  }

  public boolean isValid() {
    return laboratoryFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return laboratoryFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    laboratoryFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
