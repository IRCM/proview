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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.ContactViewPresenter.TITLE;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ContactView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class ContactViewTest extends ContactPageObject {
  @Value("${spring.application.name}")
  private String applicationName;

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(ContactView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> proteomicPanel()).isPresent());
    assertTrue(optional(() -> proteomicNameLink()).isPresent());
    assertTrue(optional(() -> proteomicAddressLink()).isPresent());
    assertTrue(optional(() -> proteomicPhoneLink()).isPresent());
    assertTrue(optional(() -> websitePanel()).isPresent());
    assertTrue(optional(() -> websiteNameLink()).isPresent());
    assertTrue(optional(() -> websiteAddressLink()).isPresent());
    assertTrue(optional(() -> websitePhoneLink()).isPresent());
  }
}
