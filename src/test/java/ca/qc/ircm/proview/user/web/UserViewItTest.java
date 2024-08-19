package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UserView.SAVED;
import static ca.qc.ircm.proview.user.web.UserView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UserView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(UserView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
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
  private String email = "test@ircm.qc.ca";
  private String name = "Test User";
  private String password = "test_password";
  private String addressLine = "200 My Street";
  private String town = "My Town";
  private String state = "My State";
  private String country = "My Country";
  private String postalCode = "12345";
  private PhoneNumberType phoneType = PhoneNumberType.MOBILE;
  private String number = "514-555-1234";
  private String extension = "443";

  private void open() {
    openView(VIEW_NAME);
  }

  private void open(Long id) {
    openView(VIEW_NAME, Objects.toString(id, ""));
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    $(UserViewElement.class).waitForFirst();
  }

  @Test
  public void title() throws Throwable {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[] { applicationName }, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UserViewElement view = $(UserViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.userForm()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void update() throws Throwable {
    open(2L);
    UserViewElement view = $(UserViewElement.class).waitForFirst();
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    view.userForm().laboratory().selectByText(laboratory.getName());
    view.userForm().address().setValue(addressLine);
    view.userForm().town().setValue(town);
    view.userForm().state().setValue(state);
    view.userForm().country().setValue(country);
    view.userForm().postalCode().setValue(postalCode);
    view.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    view.userForm().number().setValue(number);
    view.userForm().extension().setValue(extension);
    view.save().click();
    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    assertEquals(messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[] { name }, locale),
        notification.getText());
    User user = repository.findByEmail(email).get();
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(LocalDateTime.of(2019, 5, 11, 13, 43, 51), user.getLastSignAttempt());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    assertEquals(LocalDateTime.of(2008, 8, 11, 13, 43, 51), user.getRegisterTime());
    entityManager.refresh(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  public void add() throws Throwable {
    open();
    UserViewElement view = $(UserViewElement.class).waitForFirst();
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().passwordConfirm().setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).get();
    view.userForm().laboratory().selectByText(laboratory.getName());
    view.userForm().address().setValue(addressLine);
    view.userForm().town().setValue(town);
    view.userForm().state().setValue(state);
    view.userForm().country().setValue(country);
    view.userForm().postalCode().setValue(postalCode);
    view.userForm().phoneType().selectByText(
        messageSource.getMessage(PHONE_NUMBER_TYPE_PREFIX + phoneType.name(), null, locale));
    view.userForm().number().setValue(number);
    view.userForm().extension().setValue(extension);
    view.save().click();
    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    assertEquals(messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[] { name }, locale),
        notification.getText());
    User user = repository.findByEmail(email).get();
    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertNull(user.getLocale());
    assertTrue(user.getRegisterTime().isAfter(LocalDateTime.now().minusSeconds(60)));
    assertTrue(user.getRegisterTime().isBefore(LocalDateTime.now().plusSeconds(60)));
    entityManager.refresh(user.getLaboratory());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    $(UsersViewElement.class).waitForFirst();
  }
}
