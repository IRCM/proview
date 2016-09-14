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

import static ca.qc.ircm.proview.user.web.AddressFormPresenter.COUNTRY_PROPERTY;
import static ca.qc.ircm.proview.user.web.AddressFormPresenter.LINE_PROPERTY;
import static ca.qc.ircm.proview.user.web.AddressFormPresenter.POSTAL_CODE_PROPERTY;
import static ca.qc.ircm.proview.user.web.AddressFormPresenter.STATE_PROPERTY;
import static ca.qc.ircm.proview.user.web.AddressFormPresenter.TOWN_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AddressFormPresenterTest {
  private AddressFormPresenter presenter;
  @Mock
  private DefaultAddressConfiguration defaultAddressConfiguration;
  private AddressForm view = new AddressForm();
  private Address address = new Address();
  private BeanItem<Address> item = new BeanItem<>(address);
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources;
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String defaultAddress = "110 avenue des Pins Ouest";
  private String defaultTown = "Montreal";
  private String defaultState = "Quebec";
  private String defaultCountry = "Canada";
  private String defaultPostalCode = "H2W 1R7";
  private String line = "123 Papineau";
  private String town = "Laval";
  private String state = "Ontario";
  private String country = "USA";
  private String postalCode = "12345";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new AddressFormPresenter(defaultAddressConfiguration);
    view.setLocale(locale);
    resources = view.getResources();
    when(defaultAddressConfiguration.getAddress()).thenReturn(defaultAddress);
    when(defaultAddressConfiguration.getTown()).thenReturn(defaultTown);
    when(defaultAddressConfiguration.getState()).thenReturn(defaultState);
    when(defaultAddressConfiguration.getCountry()).thenReturn(defaultCountry);
    when(defaultAddressConfiguration.getPostalCode()).thenReturn(defaultPostalCode);
    presenter.init(view);
    presenter.attach();
  }

  private void setFields() {
    view.lineField.setValue(line);
    view.townField.setValue(town);
    view.stateField.setValue(state);
    view.countryField.setValue(country);
    view.postalCodeField.setValue(postalCode);
  }

  @Test
  public void setPresenterInView() {
    AddressForm view = mock(AddressForm.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void styles() {
    assertTrue(view.lineField.getStyleName().contains(LINE_PROPERTY));
    assertTrue(view.townField.getStyleName().contains(TOWN_PROPERTY));
    assertTrue(view.stateField.getStyleName().contains(STATE_PROPERTY));
    assertTrue(view.countryField.getStyleName().contains(COUNTRY_PROPERTY));
    assertTrue(view.postalCodeField.getStyleName().contains(POSTAL_CODE_PROPERTY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(LINE_PROPERTY), view.lineField.getCaption());
    assertEquals(resources.message(TOWN_PROPERTY), view.townField.getCaption());
    assertEquals(resources.message(STATE_PROPERTY), view.stateField.getCaption());
    assertEquals(resources.message(COUNTRY_PROPERTY), view.countryField.getCaption());
    assertEquals(resources.message(POSTAL_CODE_PROPERTY), view.postalCodeField.getCaption());
  }

  @Test
  public void required() {
    assertTrue(view.lineField.isRequired());
    assertEquals(generalResources.message("required", view.lineField.getCaption()),
        view.lineField.getRequiredError());
    assertTrue(view.townField.isRequired());
    assertEquals(generalResources.message("required", view.townField.getCaption()),
        view.townField.getRequiredError());
    assertTrue(view.stateField.isRequired());
    assertEquals(generalResources.message("required", view.stateField.getCaption()),
        view.stateField.getRequiredError());
    assertTrue(view.countryField.isRequired());
    assertEquals(generalResources.message("required", view.countryField.getCaption()),
        view.countryField.getRequiredError());
    assertTrue(view.postalCodeField.isRequired());
    assertEquals(generalResources.message("required", view.postalCodeField.getCaption()),
        view.postalCodeField.getRequiredError());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.lineField.isReadOnly());
    assertTrue(view.lineField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.townField.isReadOnly());
    assertTrue(view.townField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.stateField.isReadOnly());
    assertTrue(view.stateField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.countryField.isReadOnly());
    assertTrue(view.countryField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.postalCodeField.isReadOnly());
    assertTrue(view.postalCodeField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.lineField.isReadOnly());
    assertFalse(view.lineField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.townField.isReadOnly());
    assertFalse(view.townField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.stateField.isReadOnly());
    assertFalse(view.stateField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.countryField.isReadOnly());
    assertFalse(view.countryField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.postalCodeField.isReadOnly());
    assertFalse(view.postalCodeField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.lineField.isReadOnly());
    assertTrue(view.lineField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.townField.isReadOnly());
    assertTrue(view.townField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.stateField.isReadOnly());
    assertTrue(view.stateField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.countryField.isReadOnly());
    assertTrue(view.countryField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.postalCodeField.isReadOnly());
    assertTrue(view.postalCodeField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void defaults() throws Throwable {
    assertEquals(defaultAddress, view.lineField.getValue());
    assertEquals(defaultTown, view.townField.getValue());
    assertEquals(defaultState, view.stateField.getValue());
    assertEquals(defaultCountry, view.countryField.getValue());
    assertEquals(defaultPostalCode, view.postalCodeField.getValue());
  }

  @Test
  public void allFieldsValid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();

    assertTrue(presenter.isValid());
    presenter.commit();
    assertEquals(line, address.getLine());
    assertEquals(town, address.getTown());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
    assertEquals(postalCode, address.getPostalCode());
  }

  @Test
  public void line_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.lineField.setValue("");

    assertFalse(view.lineField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void town_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.townField.setValue("");

    assertFalse(view.townField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void state_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.stateField.setValue("");

    assertFalse(view.stateField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void country_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.countryField.setValue("");

    assertFalse(view.countryField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void postalCode_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.postalCodeField.setValue("");

    assertFalse(view.postalCodeField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
