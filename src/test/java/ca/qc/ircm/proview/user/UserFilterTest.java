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

package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.core.types.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserFilterTest {
  private UserFilter filter;

  @Before
  public void beforeTest() {
    filter = new UserFilter();
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

  private User active(boolean active) {
    return active(new User(), active);
  }

  private User active(User user, boolean active) {
    user.setActive(active);
    return user;
  }

  private User valid(boolean valid) {
    User user = new User();
    user.setValid(valid);
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

  @Test
  public void test_EmailContains() {
    filter.emailContains = "test";

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertFalse(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void test_EmailContainsNull() {
    filter.emailContains = null;

    assertTrue(filter.test(email("abctestabc@gmail.com")));
    assertTrue(filter.test(email("abc.test@gmail.com")));
    assertTrue(filter.test(email("abc@test.com")));
    assertTrue(filter.test(email("abc@gmail.com")));
  }

  @Test
  public void test_NameContains() {
    filter.nameContains = "test";

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertTrue(filter.test(name("Chris Tést Poitras")));
    assertFalse(filter.test(name("Chris Poitras")));
  }

  @Test
  public void test_NameContainsNull() {
    filter.nameContains = null;

    assertTrue(filter.test(name("Chris Test")));
    assertTrue(filter.test(name("Test Poitras")));
    assertTrue(filter.test(name("Chris Test Poitras")));
    assertTrue(filter.test(name("Chris Poitras")));
  }

  @Test
  public void test_ActiveTrue() {
    filter.active = true;

    assertTrue(filter.test(active(true)));
    assertFalse(filter.test(active(false)));
  }

  @Test
  public void test_ActiveFalse() {
    filter.active = false;

    assertFalse(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void test_ActiveNull() {
    filter.active = null;

    assertTrue(filter.test(active(true)));
    assertTrue(filter.test(active(false)));
  }

  @Test
  public void test_ValidTrue() {
    filter.valid = true;

    assertTrue(filter.test(valid(true)));
    assertFalse(filter.test(valid(false)));
  }

  @Test
  public void test_ValidFalse() {
    filter.valid = false;

    assertFalse(filter.test(valid(true)));
    assertTrue(filter.test(valid(false)));
  }

  @Test
  public void test_ValidNull() {
    filter.valid = null;

    assertTrue(filter.test(valid(true)));
    assertTrue(filter.test(valid(false)));
  }

  @Test
  public void test_LaboratoryNameContains() {
    filter.laboratoryNameContains = "test";

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Tést Proteomics")));
    assertFalse(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void test_LaboratoryNameContainsNull() {
    filter.laboratoryNameContains = null;

    assertTrue(filter.test(laboratoryName("Translational Test")));
    assertTrue(filter.test(laboratoryName("Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Test Proteomics")));
    assertTrue(filter.test(laboratoryName("Translational Proteomics")));
  }

  @Test
  public void test_OrganizationContains() {
    filter.organizationContains = "test";

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertTrue(filter.test(organization("Translational Tést Proteomics")));
    assertFalse(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void test_OrganizationContainsNull() {
    filter.organizationContains = null;

    assertTrue(filter.test(organization("Translational Test")));
    assertTrue(filter.test(organization("Test Proteomics")));
    assertTrue(filter.test(organization("Translational Test Proteomics")));
    assertTrue(filter.test(organization("Translational Proteomics")));
  }

  @Test
  public void test_EmailContainsAndActive() {
    filter.emailContains = "test";
    filter.active = true;

    assertTrue(filter.test(active(email("test@abc.com"), true)));
    assertFalse(filter.test(active(email("abc@abc.com"), true)));
    assertFalse(filter.test(active(email("test@abc.com"), false)));
    assertFalse(filter.test(active(email("abc@abc.com"), false)));
  }

  @Test
  public void predicate_EmailContains() {
    filter.emailContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.email.contains("test"));
  }

  @Test
  public void predicate_NameContains() {
    filter.nameContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.name.contains("test"));
  }

  @Test
  public void predicate_ActiveTrue() {
    filter.active = true;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.active.eq(true));
  }

  @Test
  public void predicate_ActiveFalse() {
    filter.active = false;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.active.eq(false));
  }

  @Test
  public void predicate_ValidTrue() {
    filter.valid = true;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.valid.eq(true));
  }

  @Test
  public void predicate_ValidFalse() {
    filter.valid = false;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.valid.eq(false));
  }

  @Test
  public void predicate_LaboratoryNameContains() {
    filter.laboratoryNameContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.laboratory.name.contains("test"));
  }

  @Test
  public void predicate_OrganizationContains() {
    filter.organizationContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, user.laboratory.organization.contains("test"));
  }
}
