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

package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class TreatmentViewTest extends TreatmentViewPageObject {
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

    assertTrue(resources(TreatmentView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    openWithDigestion();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> protocolPanel()).isPresent());
    assertTrue(optional(() -> protocol()).isPresent());
    assertTrue(optional(() -> treatedSamplesPanel()).isPresent());
    assertTrue(optional(() -> treatedSamples()).isPresent());
  }
}
