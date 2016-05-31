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

import static ca.qc.ircm.proview.user.web.PhoneNumberFormPresenter.EXTENSION_PROPERTY;
import static ca.qc.ircm.proview.user.web.PhoneNumberFormPresenter.NUMBER_PROPERTY;
import static ca.qc.ircm.proview.user.web.PhoneNumberFormPresenter.TYPE_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PhoneNumberFormPresenterTest {
  @InjectMocks
  private PhoneNumberFormPresenter presenter = new PhoneNumberFormPresenter();
  private PhoneNumberForm view = new PhoneNumberForm();
  private PhoneNumber phoneNumber = new PhoneNumber();
  private BeanItem<PhoneNumber> item = new BeanItem<>(phoneNumber);
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources;
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private PhoneNumberType type = PhoneNumberType.MOBILE;
  private String number = "514-555-5555";
  private String extension = "234";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
    presenter.attach();
  }

  private void setFields() {
    view.getTypeField().setValue(type);
    view.getNumberField().setValue(number);
    view.getExtensionField().setValue(extension);
  }

  private CompositeErrorMessage error(String message) {
    return new CompositeErrorMessage(new UserError(message));
  }

  @Test
  public void setPresenterInView() {
    PhoneNumberForm view = mock(PhoneNumberForm.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void styles() {
    assertTrue(view.getTypeField().getStyleName().contains(TYPE_PROPERTY));
    assertTrue(view.getNumberField().getStyleName().contains(NUMBER_PROPERTY));
    assertTrue(view.getExtensionField().getStyleName().contains(EXTENSION_PROPERTY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(TYPE_PROPERTY), view.getTypeField().getCaption());
    assertEquals(resources.message(NUMBER_PROPERTY), view.getNumberField().getCaption());
    assertEquals(resources.message(EXTENSION_PROPERTY), view.getExtensionField().getCaption());
  }

  @Test
  public void required() {
    assertTrue(view.getNumberField().isRequired());
    assertEquals(generalResources.message("required", view.getNumberField().getCaption()),
        view.getNumberField().getRequiredError());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.getTypeField().isReadOnly());
    assertTrue(view.getTypeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getNumberField().isReadOnly());
    assertTrue(view.getNumberField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getExtensionField().isReadOnly());
    assertTrue(view.getExtensionField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.getTypeField().isReadOnly());
    assertFalse(view.getTypeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getNumberField().isReadOnly());
    assertFalse(view.getNumberField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getExtensionField().isReadOnly());
    assertFalse(view.getExtensionField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.getTypeField().isReadOnly());
    assertTrue(view.getTypeField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getNumberField().isReadOnly());
    assertTrue(view.getNumberField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getExtensionField().isReadOnly());
    assertTrue(view.getExtensionField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
  }

  @Test
  public void allFieldsValid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();

    assertTrue(presenter.isValid());
    presenter.commit();
    assertEquals(type, phoneNumber.getType());
    assertEquals(number, phoneNumber.getNumber());
    assertEquals(extension, phoneNumber.getExtension());
  }

  @Test
  public void number_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getNumberField().setValue("");

    assertFalse(view.getNumberField().isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void number_Invalid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getNumberField().setValue("aaa");

    assertFalse(view.getNumberField().isValid());
    assertEquals(error(resources.message(NUMBER_PROPERTY + ".invalid")).getFormattedHtmlMessage(),
        view.getNumberField().getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void extension_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getExtensionField().setValue("");

    assertTrue(view.getExtensionField().isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void extension_Invalid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getExtensionField().setValue("aaa");

    assertFalse(view.getExtensionField().isValid());
    assertEquals(
        error(resources.message(EXTENSION_PROPERTY + ".invalid")).getFormattedHtmlMessage(),
        view.getExtensionField().getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
