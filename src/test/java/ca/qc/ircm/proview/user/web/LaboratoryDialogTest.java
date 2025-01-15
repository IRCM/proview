package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.HEADER;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.ID;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link LaboratoryDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(LaboratoryDialog.class);
  private static final String LABORATORY_PREFIX = messagePrefix(Laboratory.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private LaboratoryDialog dialog;
  @MockitoBean
  private LaboratoryService service;
  @Mock
  private ComponentEventListener<SavedEvent<LaboratoryDialog>> savedListener;
  @Captor
  private ArgumentCaptor<Laboratory> laboratoryCaptor;
  @Autowired
  private LaboratoryRepository repository;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).then(
        i -> i.getArgument(0) != null ? repository.findById(i.getArgument(0)) : Optional.empty());
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
    Laboratory laboratory = repository.findById(dialog.getLaboratoryId()).orElseThrow();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, 1, laboratory.getName()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(LABORATORY_PREFIX + NAME), dialog.name.getLabel());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + SAVE), dialog.save.getText());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + CANCEL), dialog.cancel.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    Laboratory laboratory = repository.findById(dialog.getLaboratoryId()).orElseThrow();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, 1, laboratory.getName()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(LABORATORY_PREFIX + NAME), dialog.name.getLabel());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + SAVE), dialog.save.getText());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + CANCEL), dialog.cancel.getText());
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
  public void getLaboratoryId() {
    assertEquals(1L, dialog.getLaboratoryId());
  }

  @Test
  public void setLaboratoryId() {
    Laboratory laboratory = repository.findById(2L).orElseThrow();
    dialog.setLaboratoryId(2L);
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, 1, laboratory.getName()),
        dialog.getHeaderTitle());
    assertEquals(laboratory.getName(), dialog.name.getValue());
    assertFalse(dialog.name.isReadOnly());
    assertTrue(dialog.save.isVisible());
    assertTrue(dialog.cancel.isVisible());
  }

  @Test
  public void setLaboratoryId_0() {
    assertThrows(IllegalArgumentException.class, () -> dialog.setLaboratoryId(0));
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
    assertEquals(Optional.of(dialog.getTranslation(CONSTANTS_PREFIX + REQUIRED)),
        error.getMessage());
    verify(service, never()).save(any());
    assertFalse($(Notification.class).exists());
    assertTrue(dialog.isOpened());
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void save_Update() {
    dialog.addSavedListener(savedListener);
    dialog.setLaboratoryId(2L);
    String name = "My lab";
    dialog.name.setValue(name);

    test(dialog.save).click();

    verify(service).save(laboratoryCaptor.capture());
    Laboratory laboratory = laboratoryCaptor.getValue();
    assertEquals((Long) 2L, laboratory.getId());
    assertEquals(name, laboratory.getName());
    assertEquals("Benoit Coulombe", laboratory.getDirector());
    Notification notification = $(Notification.class).first();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + SAVED, name),
        test(notification).getText());
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
