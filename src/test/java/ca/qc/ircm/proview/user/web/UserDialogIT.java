package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.browserless.SpringBrowserlessTest;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Integration tests for {@link UserDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserDialogIT extends SpringBrowserlessTest {

  private static final String PHONE_NUMBER_TYPE_PREFIX = messagePrefix(PhoneNumberType.class);
  @MockitoSpyBean
  private UserService service;
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

  private void detachOnServiceGet() {
    when(service.get(anyLong())).then(a -> {
      @SuppressWarnings("unchecked") Optional<User> optionalUser = (Optional<User>) a.callRealMethod();
      optionalUser.ifPresent(d -> entityManager.detach(d));
      return optionalUser;
    });
  }

  @Test
  public void update() {
    UsersView view = navigate(UsersView.class);
    final int rows = test(view.users).size();

    test(view.users).select(1);
    test(view.edit).click();

    UserDialog dialog = find(UserDialog.class).single();
    test(dialog.form.email).setValue(email);
    test(dialog.form.name).setValue(name);
    test(dialog.form.password).setValue(password);
    test(dialog.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(dialog.form.laboratory).selectItem(laboratory.getName());
    test(dialog.form.addressLine).setValue(addressLine);
    test(dialog.form.town).setValue(town);
    test(dialog.form.state).setValue(state);
    test(dialog.form.country).setValue(country);
    test(dialog.form.postalCode).setValue(postalCode);
    test(dialog.form.phoneType).selectItem(
        dialog.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(dialog.form.number).setValue(number);
    test(dialog.form.extension).setValue(extension);

    test(dialog.save).click();

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
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
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
    assertEquals(rows, test(view.users).size());
    assertEquals(email,
        test(view.users).getCellText(1, view.users.getColumns().indexOf(view.email)));
  }

  @Test
  public void update_Cancel() {
    detachOnServiceGet();
    UsersView view = navigate(UsersView.class);
    final int rows = test(view.users).size();

    test(view.users).select(0);
    test(view.edit).click();

    UserDialog dialog = find(UserDialog.class).single();
    test(dialog.form.email).setValue(email);
    test(dialog.form.name).setValue(name);
    test(dialog.form.password).setValue(password);
    test(dialog.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(dialog.form.laboratory).selectItem(laboratory.getName());
    test(dialog.form.addressLine).setValue(addressLine);
    test(dialog.form.town).setValue(town);
    test(dialog.form.state).setValue(state);
    test(dialog.form.country).setValue(country);
    test(dialog.form.postalCode).setValue(postalCode);
    test(dialog.form.phoneType).selectItem(
        dialog.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(dialog.form.number).setValue(number);
    test(dialog.form.extension).setValue(extension);

    test(dialog.cancel).click();

    assertFalse(repository.findByEmail(email).isPresent());
    assertEquals(rows, test(view.users).size());
  }

  @Test
  public void add() {
    UsersView view = navigate(UsersView.class);
    final int rows = test(view.users).size();

    test(view.add).click();

    UserDialog dialog = find(UserDialog.class).single();
    test(dialog.form.email).setValue(email);
    test(dialog.form.name).setValue(name);
    test(dialog.form.password).setValue(password);
    test(dialog.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(dialog.form.laboratory).selectItem(laboratory.getName());
    test(dialog.form.addressLine).setValue(addressLine);
    test(dialog.form.town).setValue(town);
    test(dialog.form.state).setValue(state);
    test(dialog.form.country).setValue(country);
    test(dialog.form.postalCode).setValue(postalCode);
    test(dialog.form.phoneType).selectItem(
        dialog.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(dialog.form.number).setValue(number);
    test(dialog.form.extension).setValue(extension);

    test(dialog.save).click();

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
    assertEquals("Translational Proteomics", user.getLaboratory().getName());
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
    assertEquals(rows + 1, test(view.users).size());
  }

  @Test
  public void add_Cancel() {
    UsersView view = navigate(UsersView.class);
    final int rows = test(view.users).size();

    test(view.add).click();

    UserDialog dialog = find(UserDialog.class).single();
    test(dialog.form.email).setValue(email);
    test(dialog.form.name).setValue(name);
    test(dialog.form.password).setValue(password);
    test(dialog.form.confirmPassword).setValue(password);
    Laboratory laboratory = laboratoryRepository.findById(2L).orElseThrow();
    test(dialog.form.laboratory).selectItem(laboratory.getName());
    test(dialog.form.addressLine).setValue(addressLine);
    test(dialog.form.town).setValue(town);
    test(dialog.form.state).setValue(state);
    test(dialog.form.country).setValue(country);
    test(dialog.form.postalCode).setValue(postalCode);
    test(dialog.form.phoneType).selectItem(
        dialog.getTranslation(PHONE_NUMBER_TYPE_PREFIX + phoneType.name()));
    test(dialog.form.number).setValue(number);
    test(dialog.form.extension).setValue(extension);

    test(dialog.cancel).click();

    assertFalse(repository.findByEmail(email).isPresent());
    assertEquals(rows, test(view.users).size());
  }
}
