package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.ID;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SEPARATOR;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class UseForgotPasswordViewItTest extends AbstractTestBenchTestCase {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UseForgotPasswordViewItTest.class);
  @Autowired
  private ForgotPasswordRepository repository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private EntityManager entityManager;
  private String password = "test_password";
  private long id = 9;
  private String confirm = "174407008";

  private void open() {
    openView(VIEW_NAME, id + SEPARATOR + confirm);
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(UseForgotPasswordView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UseForgotPasswordViewElement view = $(UseForgotPasswordViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.message()).isPresent());
    assertTrue(optional(() -> view.passwordsForm()).isPresent());
    assertTrue(optional(() -> view.save()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    UseForgotPasswordViewElement view = $(UseForgotPasswordViewElement.class).id(ID);

    view.passwordsForm().password().setValue(password);
    view.passwordsForm().passwordConfirm().setValue(password);
    view.save().click();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    AppResources resources = this.resources(UseForgotPasswordView.class);
    assertEquals(resources.message(SAVED), notification.getText());
    ForgotPassword forgotPassword = repository.findById(id).orElse(null);
    entityManager.refresh(forgotPassword);
    assertTrue(forgotPassword.isUsed());
    User user = userRepository.findById(10L).orElse(null);
    entityManager.refresh(user);
    assertTrue(passwordEncoder.matches(password, user.getHashedPassword()));
    assertNull(user.getPasswordVersion());
    assertNull(user.getSalt());
    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
