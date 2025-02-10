package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;
import static ca.qc.ircm.proview.user.web.ProfileView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ProfileView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ProfileViewItTest extends AbstractTestBenchTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(ProfileView.class);
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Autowired
  private UserRepository repository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private MessageSource messageSource;
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

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() {
    open();
    ProfileViewElement view = $(ProfileViewElement.class).waitForFirst();
    assertTrue(optional(view::userForm).isPresent());
    assertTrue(optional(() -> view.userForm().email()).isPresent());
    assertTrue(optional(() -> view.userForm().name()).isPresent());
    assertTrue(optional(() -> view.userForm().password()).isPresent());
    assertTrue(optional(() -> view.userForm().confirmPassword()).isPresent());
    assertTrue(optional(() -> view.userForm().address()).isPresent());
    assertTrue(optional(() -> view.userForm().town()).isPresent());
    assertTrue(optional(() -> view.userForm().state()).isPresent());
    assertTrue(optional(() -> view.userForm().country()).isPresent());
    assertTrue(optional(() -> view.userForm().postalCode()).isPresent());
    assertTrue(optional(() -> view.userForm().phoneType()).isPresent());
    assertTrue(optional(() -> view.userForm().number()).isPresent());
    assertTrue(optional(() -> view.userForm().extension()).isPresent());
    assertTrue(optional(view::save).isPresent());
  }

  @Test
  public void save() {
    open();
    ProfileViewElement view = $(ProfileViewElement.class).waitForFirst();
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().confirmPassword().setValue(password);
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
    assertEquals(messageSource.getMessage(MESSAGES_PREFIX + SAVED, null, locale),
        notification.getText());
    User user = repository.findById(10L).orElseThrow();
    entityManager.refresh(user);
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertEquals(Locale.US, user.getLocale());
    assertEquals(LocalDateTime.of(2011, 11, 11, 9, 45, 26), user.getRegisterTime());
    entityManager.refresh(user.getLaboratory());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertNotNull(user.getAddress());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    $(ProfileViewElement.class).waitForFirst();
  }
}
