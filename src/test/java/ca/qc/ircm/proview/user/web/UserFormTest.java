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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
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
import static ca.qc.ircm.proview.user.web.UserForm.CREATE_NEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UserForm.EMAIL_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.ID;
import static ca.qc.ircm.proview.user.web.UserForm.LABORATORY_NAME_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.NAME_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.NEW_LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.UserForm.NUMBER_PLACEHOLDER;
import static ca.qc.ircm.proview.user.web.UserForm.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UserForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class UserFormTest extends AbstractKaribuTestCase {
  private UserForm form;
  @Mock
  private UserFormPresenter presenter;
  @Mock
  private User user;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UserForm.class, locale);
  private AppResources userResources = new AppResources(User.class, locale);
  private AppResources addressResources = new AppResources(Address.class, locale);
  private AppResources phoneNumberResources = new AppResources(PhoneNumber.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    form = new UserForm(presenter, defaultAddressConfiguration);
    form.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(form);
  }

  @Test
  public void styles() {
    assertEquals(ID, form.getId().orElse(""));
    assertEquals(id(EMAIL), form.email.getId().orElse(""));
    assertEquals(id(NAME), form.name.getId().orElse(""));
    assertEquals(id(ADMIN), form.admin.getId().orElse(""));
    assertEquals(id(MANAGER), form.manager.getId().orElse(""));
    assertEquals(id(LABORATORY), form.laboratory.getId().orElse(""));
    assertEquals(id(CREATE_NEW_LABORATORY), form.createNewLaboratory.getId().orElse(""));
    assertEquals(id(NEW_LABORATORY_NAME), form.newLaboratoryName.getId().orElse(""));
    assertEquals(id(LINE), form.addressLine.getId().orElse(""));
    assertEquals(id(TOWN), form.town.getId().orElse(""));
    assertEquals(id(STATE), form.state.getId().orElse(""));
    assertEquals(id(COUNTRY), form.country.getId().orElse(""));
    assertEquals(id(POSTAL_CODE), form.postalCode.getId().orElse(""));
    assertEquals(id(TYPE), form.phoneType.getId().orElse(""));
    assertEquals(id(NUMBER), form.number.getId().orElse(""));
    assertEquals(id(EXTENSION), form.extension.getId().orElse(""));
  }

  @Test
  public void placeholder() {
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(EMAIL_PLACEHOLDER, form.email.getPlaceholder());
    assertEquals(NAME_PLACEHOLDER, form.name.getPlaceholder());
    assertEquals(LABORATORY_NAME_PLACEHOLDER, form.newLaboratoryName.getPlaceholder());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals(address.getLine(), form.addressLine.getPlaceholder());
    assertEquals(address.getTown(), form.town.getPlaceholder());
    assertEquals(address.getState(), form.state.getPlaceholder());
    assertEquals(address.getCountry(), form.country.getPlaceholder());
    assertEquals(address.getPostalCode(), form.postalCode.getPlaceholder());
    assertEquals(NUMBER_PLACEHOLDER, form.number.getPlaceholder());
  }

  @Test
  public void labels() {
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(userResources.message(EMAIL), form.email.getLabel());
    assertEquals(userResources.message(NAME), form.name.getLabel());
    assertEquals(userResources.message(ADMIN), form.admin.getLabel());
    assertEquals(userResources.message(MANAGER), form.manager.getLabel());
    assertEquals(userResources.message(LABORATORY), form.laboratory.getLabel());
    assertEquals(resources.message(CREATE_NEW_LABORATORY), form.createNewLaboratory.getLabel());
    assertEquals(resources.message(NEW_LABORATORY_NAME), form.newLaboratoryName.getLabel());
    assertEquals(addressResources.message(LINE), form.addressLine.getLabel());
    assertEquals(addressResources.message(TOWN), form.town.getLabel());
    assertEquals(addressResources.message(STATE), form.state.getLabel());
    assertEquals(addressResources.message(COUNTRY), form.country.getLabel());
    assertEquals(addressResources.message(POSTAL_CODE), form.postalCode.getLabel());
    assertEquals(phoneNumberResources.message(TYPE), form.phoneType.getLabel());
    for (PhoneNumberType type : PhoneNumberType.values()) {
      assertEquals(type.getLabel(locale), form.phoneType.getItemLabelGenerator().apply(type));
    }
    assertEquals(phoneNumberResources.message(NUMBER), form.number.getLabel());
    assertEquals(phoneNumberResources.message(EXTENSION), form.extension.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    form.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(UserForm.class, locale);
    final AppResources userResources = new AppResources(User.class, locale);
    final AppResources addressResources = new AppResources(Address.class, locale);
    final AppResources phoneNumberResources = new AppResources(PhoneNumber.class, locale);
    ui.setLocale(locale);
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(userResources.message(EMAIL), form.email.getLabel());
    assertEquals(userResources.message(NAME), form.name.getLabel());
    assertEquals(userResources.message(ADMIN), form.admin.getLabel());
    assertEquals(userResources.message(MANAGER), form.manager.getLabel());
    assertEquals(userResources.message(LABORATORY), form.laboratory.getLabel());
    assertEquals(resources.message(CREATE_NEW_LABORATORY), form.createNewLaboratory.getLabel());
    assertEquals(resources.message(NEW_LABORATORY_NAME), form.newLaboratoryName.getLabel());
    assertEquals(addressResources.message(LINE), form.addressLine.getLabel());
    assertEquals(addressResources.message(TOWN), form.town.getLabel());
    assertEquals(addressResources.message(STATE), form.state.getLabel());
    assertEquals(addressResources.message(COUNTRY), form.country.getLabel());
    assertEquals(addressResources.message(POSTAL_CODE), form.postalCode.getLabel());
    assertEquals(phoneNumberResources.message(TYPE), form.phoneType.getLabel());
    for (PhoneNumberType type : PhoneNumberType.values()) {
      assertEquals(type.getLabel(locale), form.phoneType.getItemLabelGenerator().apply(type));
    }
    assertEquals(phoneNumberResources.message(NUMBER), form.number.getLabel());
    assertEquals(phoneNumberResources.message(EXTENSION), form.extension.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void isValid_True() {
    when(presenter.isValid()).thenReturn(true);
    assertTrue(form.isValid());
    verify(presenter).isValid();
  }

  @Test
  public void isValid_False() {
    assertFalse(form.isValid());
    verify(presenter).isValid();
  }

  @Test
  public void getPassword() {
    String password = "test_password";
    when(presenter.getPassword()).thenReturn(password);
    assertEquals(password, form.getPassword());
    verify(presenter).getPassword();
  }

  @Test
  public void getUser() {
    when(presenter.getUser()).thenReturn(user);
    assertEquals(user, form.getUser());
    verify(presenter).getUser();
  }

  @Test
  public void setUser_NewUser() {
    User user = new User();
    when(presenter.getUser()).thenReturn(user);

    form.localeChange(mock(LocaleChangeEvent.class));
    form.setUser(user);

    verify(presenter).setUser(user);
  }

  @Test
  public void setUser_User() {
    User user = userRepository.findById(2L).get();
    when(presenter.getUser()).thenReturn(user);

    form.localeChange(mock(LocaleChangeEvent.class));
    form.setUser(user);

    verify(presenter).setUser(user);
  }

  @Test
  public void setUser_UserBeforeLocaleChange() {
    User user = userRepository.findById(2L).get();
    when(presenter.getUser()).thenReturn(user);

    form.setUser(user);
    form.localeChange(mock(LocaleChangeEvent.class));

    verify(presenter).setUser(user);
  }

  @Test
  public void setUser_Null() {
    form.localeChange(mock(LocaleChangeEvent.class));
    form.setUser(null);

    verify(presenter).setUser(null);
  }
}
