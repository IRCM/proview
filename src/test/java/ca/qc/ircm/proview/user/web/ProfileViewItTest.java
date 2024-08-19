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

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
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
    ProfileViewElement view = $(ProfileViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.userForm()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    ProfileViewElement view = $(ProfileViewElement.class).waitForFirst();
    final Locale locale = currentLocale();

    view.userForm().email().setValue(email);
    view.userForm().name().setValue(name);
    view.userForm().password().setValue(password);
    view.userForm().passwordConfirm().setValue(password);
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
    User user = repository.findById(10L).orElse(null);
    assertNotNull(user);
    entityManager.refresh(user);
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(null, user.getLastSignAttempt());
    assertEquals(Locale.US, user.getLocale());
    assertEquals(LocalDateTime.of(2011, 11, 11, 9, 45, 26), user.getRegisterTime());
    entityManager.refresh(user.getLaboratory());
    assertEquals((Long) 2L, user.getLaboratory().getId());
    assertEquals(1, user.getPhoneNumbers().size());
    assertEquals(phoneType, user.getPhoneNumbers().get(0).getType());
    assertEquals(number, user.getPhoneNumbers().get(0).getNumber());
    assertEquals(extension, user.getPhoneNumbers().get(0).getExtension());
    assertEquals(addressLine, user.getAddress().getLine());
    assertEquals(town, user.getAddress().getTown());
    assertEquals(state, user.getAddress().getState());
    assertEquals(country, user.getAddress().getCountry());
    assertEquals(postalCode, user.getAddress().getPostalCode());
    $(ProfileViewElement.class).waitForFirst();
  }
}
