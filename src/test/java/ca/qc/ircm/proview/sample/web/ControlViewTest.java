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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.NotificationElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 1)
public class ControlViewTest extends ControlViewPageObject {
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private final String name = "ADH_test";
  private final SampleSupport support = SampleSupport.GEL;
  private final String quantity = "10 ug";
  private final Double volume = 102.4;
  private final ControlType controlType = ControlType.POSITIVE_CONTROL;
  private final int standardCount = 2;
  private final String standardName1 = "std-test-1";
  private final String standardQuantity1 = "1 ng";
  private final String standardComment1 = "std1-comment";
  private final String standardName2 = "std-test-2";
  private final String standardQuantity2 = "1.2 ng";
  private final String standardComment2 = "std2-comment";
  private final String explanation = "explanation-test";

  private void setFields() {
    setName(name);
    setSupport(support.getLabel(currentLocale()));
    setQuantity(quantity);
    setVolume(Objects.toString(volume));
    setControlType(controlType.getLabel(currentLocale()));
    setStandardCount(Objects.toString(standardCount));
    waitFor(() -> standardsGrid());
    waitFor(() -> standardNameField(0));
    setStandardName(0, standardName1);
    setStandardQuantity(0, standardQuantity1);
    setStandardComment(0, standardComment1);
    setStandardName(1, standardName2);
    setStandardQuantity(1, standardQuantity2);
    setStandardComment(1, standardComment2);
    if (optional(() -> explanationField()).isPresent()) {
      setExplanation(explanation);
    }
  }

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
  public void security_RegularUser() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(ControlView.class, locale).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 3)
  public void security_Manager() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(ControlView.class, locale).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void security_Admin() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(ControlView.class, locale).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(ControlView.class).message(TITLE, applicationName),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> nameField()).isPresent());
    assertTrue(optional(() -> supportField()).isPresent());
    assertTrue(optional(() -> quantityField()).isPresent());
    assertTrue(optional(() -> volumeField()).isPresent());
    assertTrue(optional(() -> controlTypeField()).isPresent());
    assertTrue(optional(() -> standardCountField()).isPresent());
    assertFalse(optional(() -> standardsGrid()).isPresent());
    assertFalse(optional(() -> fillStandardsButton()).isPresent());
    setStandardCount("2");
    assertTrue(optional(() -> standardsGrid()).isPresent());
    assertTrue(optional(() -> fillStandardsButton()).isPresent());
    assertFalse(optional(() -> explanationField()).isPresent());
    assertTrue(optional(() -> saveButton()).isPresent());
  }

  @Test
  public void fieldsExistence_Control() throws Throwable {
    open("444");

    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> nameField()).isPresent());
    assertTrue(optional(() -> supportField()).isPresent());
    assertTrue(optional(() -> quantityField()).isPresent());
    assertTrue(optional(() -> volumeField()).isPresent());
    assertTrue(optional(() -> controlTypeField()).isPresent());
    assertTrue(optional(() -> standardCountField()).isPresent());
    assertFalse(optional(() -> standardsGrid()).isPresent());
    assertFalse(optional(() -> fillStandardsButton()).isPresent());
    assertTrue(optional(() -> explanationField()).isPresent());
    assertTrue(optional(() -> saveButton()).isPresent());
  }

  @Test
  public void insert() throws Throwable {
    open();
    setFields();

    clickSave();

    String url = getDriver().getCurrentUrl();
    Matcher matcher = Pattern.compile(viewUrl(ControlView.VIEW_NAME) + "/(\\d+)").matcher(url);
    assertTrue(matcher.matches());
    Long id = Long.valueOf(matcher.group(1));
    Control control = entityManager.find(Control.class, id);
    assertEquals(name, control.getName());
    assertEquals(support, control.getSupport());
    assertEquals(quantity, control.getQuantity());
    assertEquals(volume, control.getVolume());
    assertEquals(controlType, control.getControlType());
    assertEquals(standardCount, control.getStandards().size());
    Optional<Standard> optStandard = find(control.getStandards(), standardName1);
    assertTrue(optStandard.isPresent());
    Standard standard = optStandard.get();
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    optStandard = find(control.getStandards(), standardName2);
    assertTrue(optStandard.isPresent());
    standard = optStandard.get();
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(name));
  }
}
