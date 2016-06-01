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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AddressFormPresenterTest {
  @InjectMocks
  private AddressFormPresenter presenter = new AddressFormPresenter();
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
    view.getLineField().setValue(line);
    view.getTownField().setValue(town);
    view.getStateField().setValue(state);
    view.getCountryField().setValue(country);
    view.getPostalCodeField().setValue(postalCode);
  }

  @Test
  public void setPresenterInView() {
    AddressForm view = mock(AddressForm.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void styles() {
    assertTrue(view.getLineField().getStyleName().contains(LINE_PROPERTY));
    assertTrue(view.getTownField().getStyleName().contains(TOWN_PROPERTY));
    assertTrue(view.getStateField().getStyleName().contains(STATE_PROPERTY));
    assertTrue(view.getCountryField().getStyleName().contains(COUNTRY_PROPERTY));
    assertTrue(view.getPostalCodeField().getStyleName().contains(POSTAL_CODE_PROPERTY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(LINE_PROPERTY), view.getLineField().getCaption());
    assertEquals(resources.message(TOWN_PROPERTY), view.getTownField().getCaption());
    assertEquals(resources.message(STATE_PROPERTY), view.getStateField().getCaption());
    assertEquals(resources.message(COUNTRY_PROPERTY), view.getCountryField().getCaption());
    assertEquals(resources.message(POSTAL_CODE_PROPERTY), view.getPostalCodeField().getCaption());
  }

  @Test
  public void required() {
    assertTrue(view.getLineField().isRequired());
    assertEquals(generalResources.message("required", view.getLineField().getCaption()),
        view.getLineField().getRequiredError());
    assertTrue(view.getTownField().isRequired());
    assertEquals(generalResources.message("required", view.getTownField().getCaption()),
        view.getTownField().getRequiredError());
    assertTrue(view.getStateField().isRequired());
    assertEquals(generalResources.message("required", view.getStateField().getCaption()),
        view.getStateField().getRequiredError());
    assertTrue(view.getCountryField().isRequired());
    assertEquals(generalResources.message("required", view.getCountryField().getCaption()),
        view.getCountryField().getRequiredError());
    assertTrue(view.getPostalCodeField().isRequired());
    assertEquals(generalResources.message("required", view.getPostalCodeField().getCaption()),
        view.getPostalCodeField().getRequiredError());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.getLineField().isReadOnly());
    assertTrue(view.getLineField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getTownField().isReadOnly());
    assertTrue(view.getTownField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getStateField().isReadOnly());
    assertTrue(view.getStateField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getCountryField().isReadOnly());
    assertTrue(view.getCountryField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getPostalCodeField().isReadOnly());
    assertTrue(view.getPostalCodeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.getLineField().isReadOnly());
    assertFalse(view.getLineField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getTownField().isReadOnly());
    assertFalse(view.getTownField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getStateField().isReadOnly());
    assertFalse(view.getStateField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getCountryField().isReadOnly());
    assertFalse(view.getCountryField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getPostalCodeField().isReadOnly());
    assertFalse(view.getPostalCodeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.getLineField().isReadOnly());
    assertTrue(view.getLineField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getTownField().isReadOnly());
    assertTrue(view.getTownField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getStateField().isReadOnly());
    assertTrue(view.getStateField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getCountryField().isReadOnly());
    assertTrue(view.getCountryField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getPostalCodeField().isReadOnly());
    assertTrue(view.getPostalCodeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void defaults() throws Throwable {
    assertEquals(defaultAddress, view.getLineField().getValue());
    assertEquals(defaultTown, view.getTownField().getValue());
    assertEquals(defaultState, view.getStateField().getValue());
    assertEquals(defaultCountry, view.getCountryField().getValue());
    assertEquals(defaultPostalCode, view.getPostalCodeField().getValue());
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
    view.getLineField().setValue("");

    assertFalse(view.getLineField().isValid());
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
    view.getTownField().setValue("");

    assertFalse(view.getTownField().isValid());
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
    view.getStateField().setValue("");

    assertFalse(view.getStateField().isValid());
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
    view.getCountryField().setValue("");

    assertFalse(view.getCountryField().isValid());
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
    view.getPostalCodeField().setValue("");

    assertFalse(view.getPostalCodeField().isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
