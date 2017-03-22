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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class LaboratoryFormPresenterTest {
  private LaboratoryFormPresenter presenter;
  private LaboratoryForm view = new LaboratoryForm();
  private Laboratory laboratory = new Laboratory();
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources;
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String name = "Test lab";
  private String organization = "IRCM";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new LaboratoryFormPresenter();
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
  }

  private void setFields() {
    view.organizationField.setValue(organization);
    view.nameField.setValue(name);
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }

  @Test
  public void captions() {
    assertEquals(resources.message(LaboratoryFormPresenter.ORGANIZATION_PROPERTY),
        view.organizationField.getCaption());
    assertEquals(resources.message(LaboratoryFormPresenter.NAME_PROPERTY),
        view.nameField.getCaption());
  }

  @Test
  public void required() {
    assertTrue(view.organizationField.isRequiredIndicatorVisible());
    assertTrue(view.nameField.isRequiredIndicatorVisible());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.organizationField.isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.organizationField.getStyleName());
    assertTrue(view.nameField.isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.nameField.getStyleName());
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.organizationField.isReadOnly());
    assertEquals("", view.organizationField.getStyleName());
    assertFalse(view.nameField.isReadOnly());
    assertEquals("", view.nameField.getStyleName());
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.organizationField.isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.organizationField.getStyleName());
    assertTrue(view.nameField.isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.nameField.getStyleName());
  }

  @Test
  public void allFieldsValid() throws Throwable {
    presenter.setBean(laboratory);
    presenter.setEditable(true);
    setFields();

    assertTrue(presenter.validate().isOk());
    assertEquals(organization, laboratory.getOrganization());
    assertEquals(name, laboratory.getName());
  }

  @Test
  public void organization_Empty() throws Throwable {
    presenter.setBean(laboratory);
    presenter.setEditable(true);
    setFields();
    view.organizationField.setValue("");

    assertFalse(presenter.validate().isOk());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.organizationField.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void name_Empty() throws Throwable {
    presenter.setBean(laboratory);
    presenter.setEditable(true);
    setFields();
    view.nameField.setValue("");

    assertFalse(presenter.validate().isOk());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.nameField.getErrorMessage().getFormattedHtmlMessage());
  }
}
