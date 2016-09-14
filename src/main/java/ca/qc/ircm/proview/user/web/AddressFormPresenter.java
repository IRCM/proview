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
  @Inject
  private DefaultAddressConfiguration defaultAddressConfiguration;

  public AddressFormPresenter() {
  }

  protected AddressFormPresenter(DefaultAddressConfiguration defaultAddressConfiguration) {
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(AddressForm view) {
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
    setDefaultAddress();
    updateEditable();
    setCaptions();
    setRequired();
  }

  private void setStyles() {
    view.lineField.setStyleName(LINE_PROPERTY);
    view.townField.setStyleName(TOWN_PROPERTY);
    view.stateField.setStyleName(STATE_PROPERTY);
    view.countryField.setStyleName(COUNTRY_PROPERTY);
    view.postalCodeField.setStyleName(POSTAL_CODE_PROPERTY);
  }

  private void bindFields() {
    addressFieldGroup.setItemDataSource(new BeanItem<>(new Address()));
    addressFieldGroup.bind(view.lineField, LINE_PROPERTY);
    addressFieldGroup.bind(view.townField, TOWN_PROPERTY);
    addressFieldGroup.bind(view.stateField, STATE_PROPERTY);
    addressFieldGroup.bind(view.countryField, COUNTRY_PROPERTY);
    addressFieldGroup.bind(view.postalCodeField, POSTAL_CODE_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setDefaultAddress() {
    view.lineField.setValue(defaultAddressConfiguration.getAddress());
    view.townField.setValue(defaultAddressConfiguration.getTown());
    view.stateField.setValue(defaultAddressConfiguration.getState());
    view.countryField.setValue(defaultAddressConfiguration.getCountry());
    view.postalCodeField.setValue(defaultAddressConfiguration.getPostalCode());
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    view.lineField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.townField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.stateField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.countryField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.postalCodeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      view.lineField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.townField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.stateField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.countryField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.postalCodeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    view.lineField.setReadOnly(!editable);
    view.townField.setReadOnly(!editable);
    view.stateField.setReadOnly(!editable);
    view.countryField.setReadOnly(!editable);
    view.postalCodeField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.lineField.setCaption(resources.message(LINE_PROPERTY));
    view.townField.setCaption(resources.message(TOWN_PROPERTY));
    view.stateField.setCaption(resources.message(STATE_PROPERTY));
    view.countryField.setCaption(resources.message(COUNTRY_PROPERTY));
    view.postalCodeField.setCaption(resources.message(POSTAL_CODE_PROPERTY));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.lineField.setRequired(true);
    view.lineField
        .setRequiredError(generalResources.message("required", view.lineField.getCaption()));
    view.townField.setRequired(true);
    view.townField
        .setRequiredError(generalResources.message("required", view.townField.getCaption()));
    view.stateField.setRequired(true);
    view.stateField
        .setRequiredError(generalResources.message("required", view.stateField.getCaption()));
    view.countryField.setRequired(true);
    view.countryField
        .setRequiredError(generalResources.message("required", view.countryField.getCaption()));
    view.postalCodeField.setRequired(true);
    view.postalCodeField
        .setRequiredError(generalResources.message("required", view.postalCodeField.getCaption()));
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
