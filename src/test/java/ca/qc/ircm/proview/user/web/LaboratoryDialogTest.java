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

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.HEADER;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.ID;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link LaboratoryDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogTest extends SpringUIUnitTest {
  private LaboratoryDialog dialog;
  @MockBean
  private LaboratoryService service;
  @Mock
  private ComponentEventListener<SavedEvent<LaboratoryDialog>> savedListener;
  @Captor
  private ArgumentCaptor<Laboratory> laboratoryCaptor;
  @Autowired
  private LaboratoryRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(LaboratoryDialog.class, locale);
  private AppResources laboratoryResources = new AppResources(Laboratory.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    UsersView view = navigate(UsersView.class);
    test(view.users).select(0);
    test(view.viewLaboratory).click();
    dialog = $(LaboratoryDialog.class).first();
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(NAME), dialog.name.getId().orElse(""));
    assertEquals(id(SAVE), dialog.save.getId().orElse(""));
    assertTrue(dialog.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    validateIcon(VaadinIcon.CHECK.create(), dialog.save.getIcon());
    assertEquals(id(CANCEL), dialog.cancel.getId().orElse(""));
    validateIcon(VaadinIcon.CLOSE.create(), dialog.cancel.getIcon());
  }

  @Test
  public void labels() {
    assertEquals(resources.message(HEADER, 0), dialog.getHeaderTitle());
    assertEquals(laboratoryResources.message(NAME), dialog.name.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(LaboratoryDialog.class, locale);
    final AppResources laboratoryResources = new AppResources(Laboratory.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER, 0), dialog.getHeaderTitle());
    assertEquals(laboratoryResources.message(NAME), dialog.name.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
  }

  @Test
  public void savedListener() {
    dialog.addSavedListener(savedListener);
    dialog.fireSavedEvent();
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void savedListener_Remove() {
    dialog.addSavedListener(savedListener).remove();
    dialog.fireSavedEvent();
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void getLaboratory() {
    Laboratory laboratory = repository.findById(2L).get();
    dialog.setLaboratory(laboratory);
    assertEquals(laboratory, dialog.getLaboratory());
  }

  @Test
  public void setLaboratory() {
    Laboratory laboratory = repository.findById(2L).get();
    dialog.setLaboratory(laboratory);
    assertEquals(resources.message(HEADER, 1, laboratory.getName()), dialog.getHeaderTitle());
    assertEquals(laboratory.getName(), dialog.name.getValue());
    assertFalse(dialog.name.isReadOnly());
    assertTrue(dialog.save.isVisible());
    assertTrue(dialog.cancel.isVisible());
  }

  @Test
  public void setLaboratory_Null() {
    dialog.setLaboratory(null);
    assertEquals(resources.message(HEADER, 0), dialog.getHeaderTitle());
    assertEquals("", dialog.name.getValue());
    assertFalse(dialog.name.isReadOnly());
    assertTrue(dialog.save.isVisible());
    assertTrue(dialog.cancel.isVisible());
  }

  @Test
  public void save_EmptyName() {
    dialog.addSavedListener(savedListener);
    dialog.name.setValue("");

    test(dialog.save).click();

    BinderValidationStatus<Laboratory> status = dialog.validate();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.name);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(service, never()).save(any());
    assertFalse($(Notification.class).exists());
    assertTrue(dialog.isOpened());
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void save_New() {
    dialog.addSavedListener(savedListener);
    String name = "My lab";
    dialog.name.setValue(name);

    test(dialog.save).click();

    verify(service).save(laboratoryCaptor.capture());
    Laboratory laboratory = laboratoryCaptor.getValue();
    assertNull(laboratory.getId());
    assertEquals(name, laboratory.getName());
    assertNull(laboratory.getDirector());
    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(SAVED, name), test(notification).getText());
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void save_Update() {
    dialog.addSavedListener(savedListener);
    dialog.setLaboratory(repository.findById(2L).get());
    String name = "My lab";
    dialog.name.setValue(name);

    test(dialog.save).click();

    verify(service).save(laboratoryCaptor.capture());
    Laboratory laboratory = laboratoryCaptor.getValue();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals(name, laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(SAVED, name), test(notification).getText());
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void cancel() {
    dialog.addSavedListener(savedListener);
    String name = "My lab";
    dialog.name.setValue(name);

    test(dialog.cancel).click();

    verify(service, never()).save(any());
    assertFalse($(Notification.class).exists());
    assertFalse(dialog.isOpened());
    verify(savedListener, never()).onComponentEvent(any());
  }
}
