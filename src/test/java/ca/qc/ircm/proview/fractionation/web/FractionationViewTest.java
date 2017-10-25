package ca.qc.ircm.proview.fractionation.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class FractionationViewTest extends FractionationViewPageObject {
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;

  @Test
  @WithSubject(anonymous = true)
  public void security_Anonymous() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 10)
  public void security_User() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 3)
  public void security_Manager() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(FractionationView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> typePanel()).isPresent());
    assertTrue(optional(() -> type()).isPresent());
    assertTrue(optional(() -> fractionsPanel()).isPresent());
    assertTrue(optional(() -> fractions()).isPresent());
  }

  @Test
  public void fieldsExistence_Update() throws Throwable {
    openWithFractionation();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> typePanel()).isPresent());
    assertTrue(optional(() -> type()).isPresent());
    assertTrue(optional(() -> fractionsPanel()).isPresent());
    assertTrue(optional(() -> fractions()).isPresent());
  }
}
