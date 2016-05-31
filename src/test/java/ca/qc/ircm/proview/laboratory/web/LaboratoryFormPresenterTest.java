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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class LaboratoryFormPresenterTest {
  @InjectMocks
  private LaboratoryFormPresenter presenter = new LaboratoryFormPresenter();
  private LaboratoryForm view = new LaboratoryForm();
  private Laboratory laboratory = new Laboratory();
  private BeanItem<Laboratory> item = new BeanItem<>(laboratory);
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
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
    presenter.attach();
  }

  private void setFields() {
    view.getOrganizationField().setValue(organization);
    view.getNameField().setValue(name);
  }

  @Test
  public void setPresenterInView() {
    LaboratoryForm view = mock(LaboratoryForm.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void captions() {
    assertEquals(resources.message(LaboratoryFormPresenter.ORGANIZATION_PROPERTY),
        view.getOrganizationField().getCaption());
    assertEquals(resources.message(LaboratoryFormPresenter.NAME_PROPERTY),
        view.getNameField().getCaption());
  }

  @Test
  public void required() {
    assertTrue(view.getOrganizationField().isRequired());
    assertEquals(generalResources.message("required", view.getOrganizationField().getCaption()),
        view.getOrganizationField().getRequiredError());
    assertTrue(view.getNameField().isRequired());
    assertEquals(generalResources.message("required", view.getNameField().getCaption()),
        view.getNameField().getRequiredError());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.getOrganizationField().isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.getOrganizationField().getStyleName());
    assertTrue(view.getNameField().isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.getNameField().getStyleName());
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.getOrganizationField().isReadOnly());
    assertEquals("", view.getOrganizationField().getStyleName());
    assertFalse(view.getNameField().isReadOnly());
    assertEquals("", view.getNameField().getStyleName());
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.getOrganizationField().isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.getOrganizationField().getStyleName());
    assertTrue(view.getNameField().isReadOnly());
    assertEquals(ValoTheme.TEXTFIELD_BORDERLESS, view.getNameField().getStyleName());
  }

  @Test
  public void allFieldsValid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();

    assertTrue(presenter.isValid());
    presenter.commit();
    assertEquals(organization, laboratory.getOrganization());
    assertEquals(name, laboratory.getName());
  }

  @Test
  public void organization_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getOrganizationField().setValue("");

    assertFalse(view.getOrganizationField().isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void name_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setEditable(true);
    setFields();
    view.getNameField().setValue("");

    assertFalse(view.getNameField().isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
