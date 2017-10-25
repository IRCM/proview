package ca.qc.ircm.proview.standard.web;

import static ca.qc.ircm.proview.standard.QStandardAddition.standardAddition;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAddition;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class StandardAdditionViewTest extends StandardAdditionViewPageObject {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Value("${spring.application.name}")
  private String applicationName;

  private Optional<AddedStandard> find(Collection<AddedStandard> tss, long sampleId) {
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

    assertTrue(resources(StandardAdditionView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> standardAdditionsPanel()).isPresent());
    assertTrue(optional(() -> standardAdditions()).isPresent());
    assertTrue(optional(() -> down()).isPresent());
    assertFalse(optional(() -> explanationPanel()).isPresent());
    assertFalse(optional(() -> explanation()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    assertFalse(optional(() -> remove()).isPresent());
    assertFalse(optional(() -> banContainers()).isPresent());
  }

  @Test
  public void fieldsExistence_Update() throws Throwable {
    openWithStandardAddition();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> standardAdditionsPanel()).isPresent());
    assertTrue(optional(() -> standardAdditions()).isPresent());
    assertTrue(optional(() -> down()).isPresent());
    assertTrue(optional(() -> explanationPanel()).isPresent());
    assertTrue(optional(() -> explanation()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    assertTrue(optional(() -> remove()).isPresent());
    assertTrue(optional(() -> banContainers()).isPresent());
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
    String name = "ch3oh";
    setName(0, name);
    String quantity = "2 ug";
    setQuantity(0, quantity);
    String comment = "test comment";
    setComment(0, comment);
    clickDown();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(StandardAdditionView.VIEW_NAME)));
    long id = Long.parseLong(getDriver().getCurrentUrl()
        .substring(viewUrl(StandardAdditionView.VIEW_NAME).length() + 1));
    StandardAddition savedStandardAddition = jpaQueryFactory.select(standardAddition)
        .from(standardAddition).where(standardAddition.id.eq(id)).fetchOne();
    assertEquals(3, savedStandardAddition.getTreatmentSamples().size());
    Optional<AddedStandard> opTs = find(savedStandardAddition.getTreatmentSamples(), 559);
    assertTrue(opTs.isPresent());
    AddedStandard ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 11L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
    opTs = find(savedStandardAddition.getTreatmentSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 12L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
    opTs = find(savedStandardAddition.getTreatmentSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 4L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
  }

  @Test
  public void save_Wells() throws Throwable {
    openWithWells();
    String name = "ch3oh";
    setName(0, name);
    String quantity = "2 ug";
    setQuantity(0, quantity);
    String comment = "test comment";
    setComment(0, comment);
    clickDown();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(StandardAdditionView.VIEW_NAME)));
    long id = Long.parseLong(getDriver().getCurrentUrl()
        .substring(viewUrl(StandardAdditionView.VIEW_NAME).length() + 1));
    StandardAddition savedStandardAddition = jpaQueryFactory.select(standardAddition)
        .from(standardAddition).where(standardAddition.id.eq(id)).fetchOne();
    assertEquals(3, savedStandardAddition.getTreatmentSamples().size());
    Optional<AddedStandard> opTs = find(savedStandardAddition.getTreatmentSamples(), 559);
    assertTrue(opTs.isPresent());
    AddedStandard ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
    opTs = find(savedStandardAddition.getTreatmentSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
    opTs = find(savedStandardAddition.getTreatmentSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 248L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
  }

  @Test
  public void update() throws Throwable {
    openWithStandardAddition();
    String name = "ch3oh";
    setName(0, name);
    String quantity = "2 ug";
    setQuantity(0, quantity);
    String comment = "test comment";
    setComment(0, comment);
    clickDown();

    clickSave();

    assertEquals(viewUrl(StandardAdditionView.VIEW_NAME, "248"), getDriver().getCurrentUrl());
    StandardAddition savedStandardAddition = entityManager.find(StandardAddition.class, 248L);
    assertEquals(2, savedStandardAddition.getTreatmentSamples().size());
    Optional<AddedStandard> opTs = find(savedStandardAddition.getTreatmentSamples(), 599);
    assertTrue(opTs.isPresent());
    AddedStandard ts = opTs.get();
    assertEquals((Long) 599L, ts.getSample().getId());
    assertEquals((Long) 997L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
    opTs = find(savedStandardAddition.getTreatmentSamples(), 600);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 600L, ts.getSample().getId());
    assertEquals((Long) 1009L, ts.getContainer().getId());
    assertEquals(name, ts.getName());
    assertEquals(quantity, ts.getQuantity());
    assertEquals(comment, ts.getComment());
  }
}
