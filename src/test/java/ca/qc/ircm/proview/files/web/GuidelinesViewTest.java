package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class GuidelinesViewTest extends GuidelinesViewPageObject {
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;
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
  public void title() throws Throwable {
    open();

    assertTrue(resources(GuidelinesView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    List<Category> categories = guidelinesConfiguration.categories(currentLocale());
    assertEquals(categories.size(), categories().size());
    assertEquals(categories.stream().mapToInt(cat -> cat.guidelines().size()).sum(),
        guidelines().size());
  }
}
