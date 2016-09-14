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

package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.QPhoneNumber;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Phone number form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PhoneNumberFormPresenter {
  public static final String TYPE_PROPERTY = QPhoneNumber.phoneNumber.type.getMetadata().getName();
  public static final String NUMBER_PROPERTY =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  public static final String EXTENSION_PROPERTY =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup =
      new BeanFieldGroup<>(PhoneNumber.class);
  private PhoneNumberForm view;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setStyles();
    bindFields();
    addFieldListeners();
    setTypeValues();
    updateEditable();
    setCaptions();
    setRequiredErrors();
    addValidators();
  }

  private void setStyles() {
    view.typeField.setStyleName(TYPE_PROPERTY);
    view.numberField.setStyleName(NUMBER_PROPERTY);
    view.extensionField.setStyleName(EXTENSION_PROPERTY);
  }

  private void bindFields() {
    phoneNumberFieldGroup.setItemDataSource(new BeanItem<>(new PhoneNumber()));
    phoneNumberFieldGroup.bind(view.typeField, TYPE_PROPERTY);
    phoneNumberFieldGroup.bind(view.numberField, NUMBER_PROPERTY);
    phoneNumberFieldGroup.bind(view.extensionField, EXTENSION_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setTypeValues() {
    view.typeField.setNullSelectionAllowed(false);
    view.typeField.setNewItemsAllowed(false);
    view.typeField.removeAllItems();
    for (PhoneNumberType type : PhoneNumberType.values()) {
      view.typeField.addItem(type);
    }
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    view.typeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.numberField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.extensionField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      view.typeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.numberField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.extensionField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    view.typeField.setReadOnly(!editable);
    view.numberField.setReadOnly(!editable);
    view.extensionField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.typeField.setCaption(resources.message(TYPE_PROPERTY));
    view.numberField.setCaption(resources.message(NUMBER_PROPERTY));
    view.extensionField.setCaption(resources.message(EXTENSION_PROPERTY));
    for (PhoneNumberType type : PhoneNumberType.values()) {
      view.typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
  }

  private void setRequiredErrors() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.typeField
        .setRequiredError(generalResources.message("required", view.typeField.getCaption()));
    view.numberField
        .setRequiredError(generalResources.message("required", view.numberField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    view.numberField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message(NUMBER_PROPERTY + ".invalid")));
    view.extensionField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message(EXTENSION_PROPERTY + ".invalid")));
  }

  public void commit() throws CommitException {
    phoneNumberFieldGroup.commit();
  }

  public boolean isValid() {
    return phoneNumberFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return phoneNumberFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    phoneNumberFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
