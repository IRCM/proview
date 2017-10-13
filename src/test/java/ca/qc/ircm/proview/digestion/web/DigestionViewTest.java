package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.digestion.QDigestion.digestion;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.digestion.DigestedSample;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class DigestionViewTest extends DigestionViewPageObject {
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Value("${spring.application.name}")
  private String applicationName;

  private Optional<DigestedSample> find(Collection<DigestedSample> tss, long sampleId) {
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
    assertTrue(optional(() -> protocolPanel()).isPresent());
    assertTrue(optional(() -> protocol()).isPresent());
    assertTrue(optional(() -> digestionsPanel()).isPresent());
    assertTrue(optional(() -> digestions()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
  }

  @Test
  public void save_Error() throws Throwable {
    open();

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void save_Tubes() throws Throwable {
    openWithTubes();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(DigestionView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(DigestionView.VIEW_NAME).length() + 1));
    Digestion savedDigestion =
        jpaQueryFactory.select(digestion).from(digestion).where(digestion.id.eq(id)).fetchOne();
    assertEquals((Long) 1L, savedDigestion.getProtocol().getId());
    assertEquals(3, savedDigestion.getTreatmentSamples().size());
    Optional<DigestedSample> opTs = find(savedDigestion.getTreatmentSamples(), 559);
    assertTrue(opTs.isPresent());
    DigestedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 11L, ts.getContainer().getId());
    opTs = find(savedDigestion.getTreatmentSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 12L, ts.getContainer().getId());
    opTs = find(savedDigestion.getTreatmentSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 4L, ts.getContainer().getId());
  }

  @Test
  public void save_Wells() throws Throwable {
    openWithWells();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(DigestionView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(DigestionView.VIEW_NAME).length() + 1));
    Digestion savedDigestion =
        jpaQueryFactory.select(digestion).from(digestion).where(digestion.id.eq(id)).fetchOne();
    assertEquals((Long) 1L, savedDigestion.getProtocol().getId());
    assertEquals(3, savedDigestion.getTreatmentSamples().size());
    Optional<DigestedSample> opTs = find(savedDigestion.getTreatmentSamples(), 559);
    assertTrue(opTs.isPresent());
    DigestedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    opTs = find(savedDigestion.getTreatmentSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    opTs = find(savedDigestion.getTreatmentSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 248L, ts.getContainer().getId());
  }
}
