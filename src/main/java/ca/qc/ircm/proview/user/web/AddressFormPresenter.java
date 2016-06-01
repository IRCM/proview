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

import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Address form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddressFormPresenter {
  public static final String LINE_PROPERTY = QAddress.address.line.getMetadata().getName();
  public static final String TOWN_PROPERTY = QAddress.address.town.getMetadata().getName();
  public static final String STATE_PROPERTY = QAddress.address.state.getMetadata().getName();
  public static final String COUNTRY_PROPERTY = QAddress.address.country.getMetadata().getName();
  public static final String POSTAL_CODE_PROPERTY =
      QAddress.address.postalCode.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Address> addressFieldGroup = new BeanFieldGroup<>(Address.class);
  private AddressForm view;
  private TextField lineField;
  private TextField townField;
  private TextField stateField;
  private TextField countryField;
  private TextField postalCodeField;
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(AddressForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
  }

  private void setFields() {
    lineField = view.getLineField();
    townField = view.getTownField();
    stateField = view.getStateField();
    countryField = view.getCountryField();
    postalCodeField = view.getPostalCodeField();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setStyles();
    bindFields();
    addFieldListeners();
    setDefaultAddress();
    updateEditable();
    setCaptions();
    setRequired();
  }

  private void setStyles() {
    lineField.setStyleName(LINE_PROPERTY);
    townField.setStyleName(TOWN_PROPERTY);
    stateField.setStyleName(STATE_PROPERTY);
    countryField.setStyleName(COUNTRY_PROPERTY);
    postalCodeField.setStyleName(POSTAL_CODE_PROPERTY);
  }

  private void bindFields() {
    addressFieldGroup.setItemDataSource(new BeanItem<>(new Address()));
    addressFieldGroup.bind(lineField, LINE_PROPERTY);
    addressFieldGroup.bind(townField, TOWN_PROPERTY);
    addressFieldGroup.bind(stateField, STATE_PROPERTY);
    addressFieldGroup.bind(countryField, COUNTRY_PROPERTY);
    addressFieldGroup.bind(postalCodeField, POSTAL_CODE_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setDefaultAddress() {
    lineField.setValue(defaultAddressConfiguration.getAddress());
    townField.setValue(defaultAddressConfiguration.getTown());
    stateField.setValue(defaultAddressConfiguration.getState());
    countryField.setValue(defaultAddressConfiguration.getCountry());
    postalCodeField.setValue(defaultAddressConfiguration.getPostalCode());
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    lineField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    townField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    stateField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    countryField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    postalCodeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      lineField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      townField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      stateField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      countryField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      postalCodeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    lineField.setReadOnly(!editable);
    townField.setReadOnly(!editable);
    stateField.setReadOnly(!editable);
    countryField.setReadOnly(!editable);
    postalCodeField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    lineField.setCaption(resources.message(LINE_PROPERTY));
    townField.setCaption(resources.message(TOWN_PROPERTY));
    stateField.setCaption(resources.message(STATE_PROPERTY));
    countryField.setCaption(resources.message(COUNTRY_PROPERTY));
    postalCodeField.setCaption(resources.message(POSTAL_CODE_PROPERTY));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    lineField.setRequired(true);
    lineField.setRequiredError(generalResources.message("required", lineField.getCaption()));
    townField.setRequired(true);
    townField.setRequiredError(generalResources.message("required", townField.getCaption()));
    stateField.setRequired(true);
    stateField.setRequiredError(generalResources.message("required", stateField.getCaption()));
    countryField.setRequired(true);
    countryField.setRequiredError(generalResources.message("required", countryField.getCaption()));
    postalCodeField.setRequired(true);
    postalCodeField
        .setRequiredError(generalResources.message("required", postalCodeField.getCaption()));
  }

  public void commit() throws CommitException {
    addressFieldGroup.commit();
  }

  public boolean isValid() {
    return addressFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return addressFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    addressFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
