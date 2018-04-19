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

package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class DigestionViewTest extends DigestionViewPageObject {
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;

  private Optional<TreatedSample> find(Collection<TreatedSample> tss, long sampleId) {
    return tss.stream().filter(ts -> ts.getSample().getId() == sampleId).findFirst();
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

    assertTrue(resources(DigestionView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> protocolPanel()).isPresent());
    assertTrue(optional(() -> protocol()).isPresent());
    assertTrue(optional(() -> digestionsPanel()).isPresent());
    assertTrue(optional(() -> digestions()).isPresent());
    assertFalse(optional(() -> explanationPanel()).isPresent());
    assertFalse(optional(() -> explanation()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    assertFalse(optional(() -> remove()).isPresent());
    assertFalse(optional(() -> banContainers()).isPresent());
  }

  @Test
  public void fieldsExistence_Update() throws Throwable {
    openWithDigestion();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> protocolPanel()).isPresent());
    assertTrue(optional(() -> protocol()).isPresent());
    assertTrue(optional(() -> digestionsPanel()).isPresent());
    assertTrue(optional(() -> digestions()).isPresent());
    assertTrue(optional(() -> explanationPanel()).isPresent());
    assertTrue(optional(() -> explanation()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    assertTrue(optional(() -> remove()).isPresent());
    assertTrue(optional(() -> banContainers()).isPresent());
  }

  @Test
  public void add_Error() throws Throwable {
    open();

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void add_Tubes() throws Throwable {
    openWithTubes();
    setComment(0, "test comment");
    clickDown(0);

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(DigestionView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(DigestionView.VIEW_NAME).length() + 1));
    Digestion savedDigestion = entityManager.find(Digestion.class, id);
    assertEquals((Long) 1L, savedDigestion.getProtocol().getId());
    assertEquals(3, savedDigestion.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedDigestion.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 11L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
    opTs = find(savedDigestion.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 12L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
    opTs = find(savedDigestion.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 4L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
  }

  @Test
  public void add_Wells() throws Throwable {
    openWithWells();
    setComment(0, "test comment");
    clickDown(0);

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(DigestionView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(DigestionView.VIEW_NAME).length() + 1));
    Digestion savedDigestion = entityManager.find(Digestion.class, id);
    assertEquals((Long) 1L, savedDigestion.getProtocol().getId());
    assertEquals(3, savedDigestion.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedDigestion.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
    opTs = find(savedDigestion.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
    opTs = find(savedDigestion.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 248L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
  }

  @Test
  public void update() throws Throwable {
    openWithDigestion();
    setComment(0, "test comment");
    setProtocol("digestion_protocol_2");
    clickDown(0);

    clickSave();

    assertEquals(viewUrl(DigestionView.VIEW_NAME, "195"), getDriver().getCurrentUrl());
    Digestion savedDigestion = entityManager.find(Digestion.class, 195L);
    assertEquals((Long) 3L, savedDigestion.getProtocol().getId());
    assertEquals(2, savedDigestion.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedDigestion.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
    opTs = find(savedDigestion.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    assertEquals("test comment", ts.getComment());
  }
}
