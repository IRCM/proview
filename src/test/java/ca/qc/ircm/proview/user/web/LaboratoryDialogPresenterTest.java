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
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
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
 * Tests for {@link LaboratoryDialogPresenter}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogPresenterTest extends AbstractKaribuTestCase {
  @Autowired
  private LaboratoryDialogPresenter presenter;
  @Mock
  private LaboratoryDialog dialog;
  @MockBean
  private LaboratoryService service;
  @MockBean
  private AuthenticatedUser authenticatedUser;
  @Captor
  private ArgumentCaptor<Laboratory> laboratoryCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Autowired
  private LaboratoryRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(LaboratoryDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private String name = "new laboratory name";

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    dialog.name = new TextField();
    dialog.buttonsLayout = new HorizontalLayout();
    dialog.save = new Button();
    dialog.cancel = new Button();
    presenter.init(dialog);
    presenter.localeChange(locale);
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);
  }

  private void setFields() {
    dialog.name.setValue(name);
  }

  @Test
  public void save_EmptyName() {
    setFields();
    dialog.name.setValue("");

    presenter.save(locale);

    BinderValidationStatus<Laboratory> status = presenter.validate();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.name);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
    verify(service, never()).save(any());
    verify(dialog, never()).showNotification(any());
    verify(dialog, never()).close();
    verify(dialog, never()).fireSavedEvent();
  }

  @Test
  public void save_New() {
    setFields();

    presenter.save(locale);

    verify(service).save(laboratoryCaptor.capture());
    Laboratory laboratory = laboratoryCaptor.getValue();
    assertNull(laboratory.getId());
    assertEquals(name, laboratory.getName());
    assertNull(laboratory.getDirector());
    verify(dialog).showNotification(resources.message(SAVED, name));
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void save_Update() {
    presenter.setLaboratory(repository.findById(2L).get());
    setFields();

    presenter.save(locale);

    verify(service).save(laboratoryCaptor.capture());
    Laboratory laboratory = laboratoryCaptor.getValue();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals(name, laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    verify(dialog).showNotification(resources.message(SAVED, name));
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void cancel() {
    setFields();

    presenter.cancel();

    verify(service, never()).save(any());
    verify(dialog, never()).showNotification(any());
    verify(dialog).close();
    verify(dialog, never()).fireSavedEvent();
  }

  @Test
  public void getLaboratory() {
    Laboratory laboratory = repository.findById(2L).get();
    presenter.setLaboratory(laboratory);
    assertEquals(laboratory, presenter.getLaboratory());
  }

  @Test
  public void setLaboratory() {
    Laboratory laboratory = repository.findById(2L).get();
    presenter.setLaboratory(laboratory);
    assertEquals("Translational Proteomics", dialog.name.getValue());
    assertFalse(dialog.name.isReadOnly());
    assertTrue(dialog.save.isVisible());
    assertTrue(dialog.cancel.isVisible());
  }

  @Test
  public void setLaboratory_CannotWrite() {
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(false);
    Laboratory laboratory = repository.findById(2L).get();
    presenter.setLaboratory(laboratory);
    assertEquals("Translational Proteomics", dialog.name.getValue());
    assertTrue(dialog.name.isReadOnly());
    assertFalse(dialog.save.isVisible());
    assertFalse(dialog.cancel.isVisible());
  }

  @Test
  public void setLaboratory_Null() {
    presenter.setLaboratory(null);
    assertEquals("", dialog.name.getValue());
    assertFalse(dialog.name.isReadOnly());
    assertTrue(dialog.save.isVisible());
    assertTrue(dialog.cancel.isVisible());
  }
}
