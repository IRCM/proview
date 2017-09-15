/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;
import java.util.Optional;

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
    filter.emailContains = Optional.of("test");

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertFalse(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void emailContains_Null() {
    filter.emailContains = Optional.empty();

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertTrue(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void nameContains() {
    filter.nameContains = Optional.of("test");

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertFalse(filter.test(name("Chris Poitras")));
  }

  @Test
  public void nameContains_Null() {
    filter.nameContains = Optional.empty();

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertTrue(filter.test(name("Chris Poitras")));
  }

  @Test
  public void laboratoryNameContains() {
    filter.laboratoryNameContains = Optional.of("test");

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertFalse(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void laboratoryNameContains_Null() {
    filter.laboratoryNameContains = Optional.empty();

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void organizationContains() {
    filter.organizationContains = Optional.of("test");

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertFalse(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void organizationContains_Null() {
    filter.organizationContains = Optional.empty();

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertTrue(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void active_True() {
    filter.active = Optional.of(true);

    assertTrue(filter.test(active(true)));
    assertFalse(filter.test(active(false)));
  }

  @Test
  public void active_False() {
    filter.active = Optional.of(false);

    assertFalse(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void active_Null() {
    filter.active = Optional.empty();

    assertTrue(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void emailContainsAndActive() {
    filter.emailContains = Optional.of("test");
    filter.active = Optional.of(true);

    assertTrue(filter.test(active(email("test@abc.com"), true)));
    assertFalse(filter.test(active(email("abc@abc.com"), true)));
    assertFalse(filter.test(active(email("test@abc.com"), false)));
    assertFalse(filter.test(active(email("abc@abc.com"), false)));
  }
}
