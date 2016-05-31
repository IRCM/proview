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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
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
  private ComboBox typeField;
  private TextField numberField;
  private TextField extensionField;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
  }

  private void setFields() {
    typeField = view.getTypeField();
    numberField = view.getNumberField();
    extensionField = view.getExtensionField();
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
    typeField.setStyleName(TYPE_PROPERTY);
    numberField.setStyleName(NUMBER_PROPERTY);
    extensionField.setStyleName(EXTENSION_PROPERTY);
  }

  private void bindFields() {
    phoneNumberFieldGroup.setItemDataSource(new BeanItem<>(new PhoneNumber()));
    phoneNumberFieldGroup.bind(typeField, TYPE_PROPERTY);
    phoneNumberFieldGroup.bind(numberField, NUMBER_PROPERTY);
    phoneNumberFieldGroup.bind(extensionField, EXTENSION_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setTypeValues() {
    typeField.setNullSelectionAllowed(false);
    typeField.setNewItemsAllowed(false);
    typeField.removeAllItems();
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.addItem(type);
    }
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    typeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    numberField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    extensionField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      typeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      numberField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      extensionField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    typeField.setReadOnly(!editable);
    numberField.setReadOnly(!editable);
    extensionField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    typeField.setCaption(resources.message(TYPE_PROPERTY));
    numberField.setCaption(resources.message(NUMBER_PROPERTY));
    extensionField.setCaption(resources.message(EXTENSION_PROPERTY));
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
  }

  private void setRequiredErrors() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    typeField.setRequiredError(generalResources.message("required", typeField.getCaption()));
    numberField.setRequiredError(generalResources.message("required", numberField.getCaption()));
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    numberField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message(NUMBER_PROPERTY + ".invalid")));
    extensionField.addValidator(
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
