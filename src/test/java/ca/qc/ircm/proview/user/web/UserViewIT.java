package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.UserView.SAVED;
import static ca.qc.ircm.proview.user.web.UserView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UserView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserViewIT extends SpringBrowserlessTest {

  private static final String MESSAGES_PREFIX = messagePrefix(UserView.class);
  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
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

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    navigate(VIEW_NAME, AccessDeniedView.class);
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    navigate(UserView.class);
  }

  @Test
  public void update() {
    UserView view = navigate(UserView.class, 2L);

    test(view.form.email).setValue(email);
    test(view.form.name).setValue(name);
    test(view.form.password).setValue(password);
    test(view.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(view.form.laboratory).selectItem(laboratory.getName());
    test(view.form.addressLine).setValue(addressLine);
    test(view.form.town).setValue(town);
    test(view.form.state).setValue(state);
    test(view.form.country).setValue(country);
    test(view.form.postalCode).setValue(postalCode);
    test(view.form.phoneType).selectItem(
        view.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(view.form.number).setValue(number);
    test(view.form.extension).setValue(extension);
    test(view.save).click();
    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, name), test(notification).getText());
    User user = repository.findByEmail(email).orElseThrow();
    assertNotNull(user);
    assertNotEquals(0, user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(LocalDateTime.of(2019, 5, 11, 13, 43, 51), user.getLastSignAttempt());
    assertEquals(Locale.CANADA_FRENCH, user.getLocale());
    assertEquals(LocalDateTime.of(2008, 8, 11, 13, 43, 51), user.getRegisterTime());
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
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
    $(UsersView.class).single();
  }

  @Test
  public void add() {
    UserView view = navigate(UserView.class);

    test(view.form.email).setValue(email);
    test(view.form.name).setValue(name);
    test(view.form.password).setValue(password);
    test(view.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(view.form.laboratory).selectItem(laboratory.getName());
    test(view.form.addressLine).setValue(addressLine);
    test(view.form.town).setValue(town);
    test(view.form.state).setValue(state);
    test(view.form.country).setValue(country);
    test(view.form.postalCode).setValue(postalCode);
    test(view.form.phoneType).selectItem(
        view.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(view.form.number).setValue(number);
    test(view.form.extension).setValue(extension);
    test(view.save).click();
    Notification notification = $(Notification.class).single();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, name), test(notification).getText());
    User user = repository.findByEmail(email).orElseThrow();
    assertNotNull(user);
    assertNotEquals(0, user.getId());
    assertEquals(name, user.getName());
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertNull(user.getLastSignAttempt());
    assertNull(user.getLocale());
    assertTrue(user.getRegisterTime().isAfter(LocalDateTime.now().minusSeconds(60)));
    assertTrue(user.getRegisterTime().isBefore(LocalDateTime.now().plusSeconds(60)));
    assertEquals(laboratory.getId(), user.getLaboratory().getId());
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
    $(UsersView.class).single();
  }
}
