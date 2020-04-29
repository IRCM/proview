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

import static ca.qc.ircm.proview.user.AddressProperties.COUNTRY;
import static ca.qc.ircm.proview.user.AddressProperties.LINE;
import static ca.qc.ircm.proview.user.AddressProperties.POSTAL_CODE;
import static ca.qc.ircm.proview.user.AddressProperties.STATE;
import static ca.qc.ircm.proview.user.AddressProperties.TOWN;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.EXTENSION;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.NUMBER;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.TYPE;
import static ca.qc.ircm.proview.user.UserProperties.ADMIN;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.MANAGER;
import static ca.qc.ircm.proview.user.UserProperties.NAME;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * User form.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserForm extends FormLayout implements LocaleChangeObserver {
  private static final long serialVersionUID = 3285639770914046262L;
  public static final String CLASS_NAME = "user-form";
  public static final String HEADER = "header";
  public static final String EMAIL_PLACEHOLDER = "john.smith@ircm.qc.ca";
  public static final String NAME_PLACEHOLDER = "John Smith";
  public static final String CREATE_NEW_LABORATORY = "createNewLaboratory";
  public static final String LABORATORY_NAME = LaboratoryProperties.NAME;
  public static final String NEW_LABORATORY_NAME = "newLaboratoryName";
  public static final String LABORATORY_NAME_PLACEHOLDER = "Translational Proteomics";
  public static final String NUMBER_PLACEHOLDER = "514-987-5500";
  protected TextField email = new TextField();
  protected TextField name = new TextField();
  protected Checkbox admin = new Checkbox();
  protected Checkbox manager = new Checkbox();
  protected PasswordsForm passwords = new PasswordsForm();
  protected ComboBox<Laboratory> laboratory = new ComboBox<>();
  protected Checkbox createNewLaboratory = new Checkbox();
  protected TextField newLaboratoryName = new TextField();
  protected TextField addressLine = new TextField();
  protected TextField town = new TextField();
  protected TextField state = new TextField();
  protected TextField country = new TextField();
  protected TextField postalCode = new TextField();
  protected ComboBox<PhoneNumberType> phoneType = new ComboBox<>();
  protected TextField number = new TextField();
  protected TextField extension = new TextField();
  private transient UserFormPresenter presenter;
  private transient DefaultAddressConfiguration defaultAddressConfiguration;

  @Autowired
  protected UserForm(UserFormPresenter presenter,
      DefaultAddressConfiguration defaultAddressConfiguration) {
    this.presenter = presenter;
    this.defaultAddressConfiguration = defaultAddressConfiguration;
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    addClassName(CLASS_NAME);
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3), new ResponsiveStep("15em", 4));
    add(new FormLayout(email, name, admin, manager, passwords),
        new FormLayout(laboratory, createNewLaboratory, newLaboratoryName),
        new FormLayout(addressLine, town, state, country, postalCode),
        new FormLayout(phoneType, number, extension));
    email.addClassName(EMAIL);
    email.setPlaceholder(EMAIL_PLACEHOLDER);
    name.addClassName(NAME);
    name.setPlaceholder(NAME_PLACEHOLDER);
    admin.addClassName(ADMIN);
    manager.addClassName(MANAGER);
    laboratory.addClassName(LABORATORY);
    createNewLaboratory.addClassName(CREATE_NEW_LABORATORY);
    newLaboratoryName.addClassName(NEW_LABORATORY_NAME);
    newLaboratoryName.setPlaceholder(LABORATORY_NAME_PLACEHOLDER);
    Address address = defaultAddressConfiguration.getAddress();
    addressLine.addClassName(LINE);
    addressLine.setPlaceholder(address.getLine());
    town.addClassName(TOWN);
    town.setPlaceholder(address.getTown());
    state.addClassName(STATE);
    state.setPlaceholder(address.getState());
    country.addClassName(COUNTRY);
    country.setPlaceholder(address.getCountry());
    postalCode.addClassName(POSTAL_CODE);
    postalCode.setPlaceholder(address.getPostalCode());
    phoneType.addClassName(TYPE);
    phoneType.setItems(PhoneNumberType.values());
    phoneType.setItemLabelGenerator(type -> type.getLabel(getLocale()));
    phoneType.setValue(PhoneNumberType.WORK);
    number.addClassName(NUMBER);
    number.setPlaceholder(NUMBER_PLACEHOLDER);
    extension.addClassName(EXTENSION);
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(UserForm.class, getLocale());
    final AppResources userResources = new AppResources(User.class, getLocale());
    final AppResources addressResources = new AppResources(Address.class, getLocale());
    final AppResources phoneNumberResources = new AppResources(PhoneNumber.class, getLocale());
    email.setLabel(userResources.message(EMAIL));
    name.setLabel(userResources.message(NAME));
    admin.setLabel(userResources.message(ADMIN));
    manager.setLabel(userResources.message(MANAGER));
    laboratory.setLabel(userResources.message(LABORATORY));
    createNewLaboratory.setLabel(resources.message(CREATE_NEW_LABORATORY));
    newLaboratoryName.setLabel(resources.message(NEW_LABORATORY_NAME));
    addressLine.setLabel(addressResources.message(LINE));
    town.setLabel(addressResources.message(TOWN));
    state.setLabel(addressResources.message(STATE));
    country.setLabel(addressResources.message(COUNTRY));
    postalCode.setLabel(addressResources.message(POSTAL_CODE));
    phoneType.setLabel(phoneNumberResources.message(TYPE));
    number.setLabel(phoneNumberResources.message(NUMBER));
    extension.setLabel(phoneNumberResources.message(EXTENSION));
    presenter.localeChange(getLocale());
  }

  public boolean isValid() {
    return presenter.isValid();
  }

  public String getPassword() {
    return presenter.getPassword();
  }

  public User getUser() {
    return presenter.getUser();
  }

  public void setUser(User user) {
    presenter.setUser(user);
  }
}
