package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;
import static ca.qc.ircm.proview.user.web.ProfileView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.testbench.unit.SpringUIUnitTest;
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
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ProfileViewIT extends SpringUIUnitTest {

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

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  public void save() {
    ProfileView view = navigate(ProfileView.class);

    test(view.form.email).setValue(email);
    test(view.form.name).setValue(name);
    test(view.form.password).setValue(password);
    test(view.form.confirmPassword).setValue(password);
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED), test(notification).getText());
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
    $(ProfileView.class).single();
  }
}
