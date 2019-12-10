package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.ProfileView.ID;
import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;
import static ca.qc.ircm.proview.user.web.ProfileView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ProfileViewItTest extends AbstractTestBenchTestCase {
  @Autowired
  private UserRepository repository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
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

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(SigninView.class, locale).message(TITLE,
            new AppResources(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(ProfileView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    ProfileViewElement view = $(ProfileViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.userForm()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    ProfileViewElement view = $(ProfileViewElement.class).id(ID);
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
    view.userForm().phoneType().selectByText(phoneType.getLabel(locale));
    view.userForm().number().setValue(number);
    view.userForm().extension().setValue(extension);
    view.save().click();
    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(ProfileView.class);
    assertEquals(resources.message(SAVED), notification.getText());
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
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
