package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import com.vaadin.testbench.BrowserTest;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UserDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserDialogIT extends AbstractBrowserTestCase {

  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;
  private final String email = "test@ircm.qc.ca";
  private final String name = "Test User";
  private final String password = "test_password";
  private final String addressLine = "200 My Street";
  private final String town = "My Town";
  private final String state = "My State";
  private final String country = "My Country";
  private final String postalCode = "12345";
  private final PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private final String number = "514-555-1234";
  private final String extension = "443";

  private void open() {
    openView(VIEW_NAME);
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.edit().click();
    UserDialogElement dialog = view.dialog();
    assertTrue(optional(dialog::header).isPresent());
    assertTrue(optional(() -> dialog.userForm().email()).isPresent());
    assertTrue(optional(() -> dialog.userForm().name()).isPresent());
    assertTrue(optional(() -> dialog.userForm().admin()).isPresent());
    assertTrue(optional(() -> dialog.userForm().manager()).isPresent());
    assertTrue(optional(() -> dialog.userForm().password()).isPresent());
    assertTrue(optional(() -> dialog.userForm().confirmPassword()).isPresent());
    assertTrue(optional(() -> dialog.userForm().laboratory()).isPresent());
    assertTrue(optional(() -> dialog.userForm().createNewLaboratory()).isPresent());
    assertTrue(optional(() -> dialog.userForm().address()).isPresent());
    assertTrue(optional(() -> dialog.userForm().town()).isPresent());
    assertTrue(optional(() -> dialog.userForm().state()).isPresent());
    assertTrue(optional(() -> dialog.userForm().country()).isPresent());
    assertTrue(optional(() -> dialog.userForm().postalCode()).isPresent());
    assertTrue(optional(() -> dialog.userForm().phoneType()).isPresent());
    assertTrue(optional(() -> dialog.userForm().number()).isPresent());
    assertTrue(optional(() -> dialog.userForm().extension()).isPresent());
    assertTrue(optional(dialog::save).isPresent());
    assertTrue(optional(dialog::cancel).isPresent());
  }

  @BrowserTest
  public void update() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.users().select(1);
    view.edit().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().confirmPassword().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email).orElseThrow();
    assertNotNull(user);
    assertNotEquals(0, user.getId());
    Assertions.assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    Assertions.assertEquals(LocalDateTime.of(2019, 5, 11, 13, 43, 51), user.getLastSignAttempt());
    Assertions.assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    Assertions.assertEquals(LocalDateTime.of(2008, 8, 11, 13, 43, 51), user.getRegisterTime());
    entityManager.refresh(user.getLaboratory());
    Assertions.assertEquals(laboratory.getId(), user.getLaboratory().getId());
    Assertions.assertEquals("Translational Proteomics", user.getLaboratory().getName());
    Assertions.assertEquals(1, user.getPhoneNumbers().size());
    Assertions.assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    Assertions.assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    Assertions.assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertNotNull(user.getAddress());
    Assertions.assertEquals(addressLine, user.getAddress().getLine());
    Assertions.assertEquals(town, user.getAddress().getTown());
    Assertions.assertEquals(state, user.getAddress().getState());
    Assertions.assertEquals(country, user.getAddress().getCountry());
    Assertions.assertEquals(postalCode, user.getAddress().getPostalCode());
    Assertions.assertEquals(rows, view.users().getRowCount());
    Assertions.assertEquals(email, view.users().email(1));
  }

  @BrowserTest
  public void update_Cancel() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.users().select(0);
    view.edit().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().confirmPassword().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    assertFalse(repository.findByEmail(email).isPresent());
    Assertions.assertEquals(rows, view.users().getRowCount());
  }

  @BrowserTest
  public void add() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.add().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().confirmPassword().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.save().click();
    waitUntil(driver -> !dialog.isOpen());
    User user = repository.findByEmail(email).orElseThrow();
    assertNotNull(user);
    assertNotEquals(0, user.getId());
    Assertions.assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertNull(user.getLocale());
    assertTrue(user.getRegisterTime().isAfter(LocalDateTime.now().minusSeconds(60)));
    assertTrue(user.getRegisterTime().isBefore(LocalDateTime.now().plusSeconds(60)));
    entityManager.refresh(user.getLaboratory());
    Assertions.assertEquals(laboratory.getId(), user.getLaboratory().getId());
    Assertions.assertEquals("Translational Proteomics", user.getLaboratory().getName());
    Assertions.assertEquals(1, user.getPhoneNumbers().size());
    Assertions.assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    Assertions.assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    Assertions.assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertNotNull(user.getAddress());
    Assertions.assertEquals(addressLine, user.getAddress().getLine());
    Assertions.assertEquals(town, user.getAddress().getTown());
    Assertions.assertEquals(state, user.getAddress().getState());
    Assertions.assertEquals(country, user.getAddress().getCountry());
    Assertions.assertEquals(postalCode, user.getAddress().getPostalCode());
    Assertions.assertEquals(rows + 1, view.users().getRowCount());
  }

  @BrowserTest
  public void add_Cancel() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    final int rows = view.users().getRowCount();
    final Locale locale = currentLocale();

    view.add().click();

    UserDialogElement dialog = view.dialog();
    dialog.userForm().email().setValue(email);
    dialog.userForm().name().setValue(name);
    dialog.userForm().password().setValue(password);
    dialog.userForm().confirmPassword().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    dialog.userForm().laboratory().selectByText(laboratory.getName());
    dialog.userForm().address().setValue(addressLine);
    dialog.userForm().town().setValue(town);
    dialog.userForm().state().setValue(state);
    dialog.userForm().country().setValue(country);
    dialog.userForm().postalCode().setValue(postalCode);
    dialog.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    dialog.userForm().number().setValue(number);
    dialog.userForm().extension().setValue(extension);
    dialog.cancel().click();
    waitUntil(driver -> !dialog.isOpen());
    assertFalse(repository.findByEmail(email).isPresent());
    Assertions.assertEquals(rows, view.users().getRowCount());
  }
}
