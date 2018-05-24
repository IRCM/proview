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

import static ca.qc.ircm.proview.web.ErrorView.BUTTON;
import static ca.qc.ircm.proview.web.ErrorView.LABEL;
import static ca.qc.ircm.proview.web.ErrorView.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.utils.MessageResource;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class ErrorViewTest extends ErrorPageObject {
  @Value("${spring.application.name}")
  private String applicationName;

  private Set<String> messages(String key, Object... replacements) {
    Set<Locale> locales = WebConstants.getLocales();
    Set<String> messages = locales.stream()
        .map(locale -> new MessageResource(ErrorView.class, locale).message(key, replacements))
        .collect(Collectors.toSet());
    return messages;
  }

  @Test
  public void title() throws Throwable {
    open();

    Set<String> titles = messages(TITLE, applicationName);
    assertTrue(titles.contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertNotNull(error());
    assertNotNull(home());
  }

  @Test
  public void captions() throws Throwable {
    open();

    assertTrue(messages(LABEL).contains(error().getText()));
    assertTrue(messages(BUTTON).contains(home().getCaption()));
  }

  @Test
  public void home_NotSigned() throws Throwable {
    open();

    clickHome();

    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void home_Signed() throws Throwable {
    open();

    clickHome();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
