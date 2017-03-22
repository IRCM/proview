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

import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.themes.ValoTheme;
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
  private CheckBox editableProperty = new CheckBox(null, false);
  private Binder<Laboratory> laboratoryBinder = new Binder<>(Laboratory.class);
  private LaboratoryForm view;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(LaboratoryForm view) {
    this.view = view;
    prepareComponents();
    addFieldListeners();
    updateEditable();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    MessageResource generalResources = view.getGeneralResources();
    view.nameField.setCaption(resources.message(NAME_PROPERTY));
    laboratoryBinder.forField(view.nameField).asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getName, Laboratory::setName);
    view.organizationField.setCaption(resources.message(ORGANIZATION_PROPERTY));
    laboratoryBinder.forField(view.organizationField).asRequired(generalResources.message(REQUIRED))
        .bind(Laboratory::getOrganization, Laboratory::setOrganization);
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

  public BinderValidationStatus<Laboratory> validate() {
    return laboratoryBinder.validate();
  }

  public Laboratory getBean() {
    return laboratoryBinder.getBean();
  }

  public void setBean(Laboratory laboratory) {
    laboratoryBinder.setBean(laboratory);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
