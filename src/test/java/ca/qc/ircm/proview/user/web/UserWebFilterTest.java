package ca.qc.ircm.proview.user.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserWebFilterTest {
  private UserWebFilter filter;
  private Locale locale = Locale.FRENCH;

  @Before
  public void beforeTest() {
    filter = new UserWebFilter(locale);
  }

  private User email(String email) {
    return email(new User(), email);
  }

  private User email(User user, String email) {
    user.setEmail(email);
    return user;
  }

  private User name(String name) {
    User user = new User();
    user.setName(name);
    return user;
  }

  private User laboratoryName(String name) {
    User user = new User();
    user.setLaboratory(new Laboratory());
    user.getLaboratory().setName(name);
    return user;
  }

  private User organization(String organization) {
    User user = new User();
    user.setLaboratory(new Laboratory());
    user.getLaboratory().setOrganization(organization);
    return user;
  }

  private User active(boolean active) {
    return active(new User(), active);
  }

  private User active(User user, boolean active) {
    user.setActive(active);
    return user;
  }

  @Test
  public void emailContains() {
    filter.setEmailContains("test");

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertFalse(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void emailContains_Null() {
    filter.setEmailContains(null);

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertTrue(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void nameContains() {
    filter.setNameContains("test");

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertFalse(filter.test(name("Chris Poitras")));
  }

  @Test
  public void nameContains_Null() {
    filter.setNameContains(null);

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertTrue(filter.test(name("Chris Poitras")));
  }

  @Test
  public void laboratoryNameContains() {
    filter.setLaboratoryNameContains("test");

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertFalse(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void laboratoryNameContains_Null() {
    filter.setLaboratoryNameContains(null);

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void organizationContains() {
    filter.setOrganizationContains("test");

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertFalse(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void organizationContains_Null() {
    filter.setOrganizationContains(null);

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertTrue(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void active_True() {
    filter.setActive(true);

    assertTrue(filter.test(active(true)));
    assertFalse(filter.test(active(false)));
  }

  @Test
  public void active_False() {
    filter.setActive(false);

    assertFalse(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void active_Null() {
    filter.setActive(null);

    assertTrue(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void emailContainsAndActive() {
    filter.setEmailContains("test");
    filter.setActive(true);

    assertTrue(filter.test(active(email("test@abc.com"), true)));
    assertFalse(filter.test(active(email("abc@abc.com"), true)));
    assertFalse(filter.test(active(email("test@abc.com"), false)));
    assertFalse(filter.test(active(email("abc@abc.com"), false)));
  }
}
