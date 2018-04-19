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

package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.transfer.QTransfer.transfer;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Ignore;
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
public class TransferViewTest extends TransferViewPageObject {
  @Inject
  private JPAQueryFactory jpaQueryFactory;
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

    assertTrue(resources(TransferView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence_Wells() throws Throwable {
    openWithWells();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> transferTypePanel()).isPresent());
    assertTrue(optional(() -> transferType()).isPresent());
    assertTrue(optional(() -> transfersPanel()).isPresent());
    assertTrue(optional(() -> transfers()).isPresent());
    assertTrue(optional(() -> destinationPanel()).isPresent());
    assertTrue(optional(() -> destinationPlates()).isPresent());
    assertFalse(optional(() -> destinationPlatePanel()).isPresent());
    assertFalse(optional(() -> destinationPlate()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    String plateName = "test_plate";
    setDestinationPlate(plateName);
    assertTrue(optional(() -> destinationPlatePanel()).isPresent());
    assertTrue(optional(() -> destinationPlate()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
  }

  @Test
  public void fieldsExistence_Tubes() throws Throwable {
    openWithTubes();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> transferTypePanel()).isPresent());
    assertTrue(optional(() -> transferType()).isPresent());
    assertTrue(optional(() -> transfersPanel()).isPresent());
    assertTrue(optional(() -> transfers()).isPresent());
    assertTrue(optional(() -> destinationPanel()).isPresent());
    assertTrue(optional(() -> destinationPlates()).isPresent());
    assertFalse(optional(() -> destinationPlatePanel()).isPresent());
    assertFalse(optional(() -> destinationPlate()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    setTransferType(TUBE);
    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> transferTypePanel()).isPresent());
    assertTrue(optional(() -> transferType()).isPresent());
    assertTrue(optional(() -> transfersPanel()).isPresent());
    assertTrue(optional(() -> transfers()).isPresent());
    assertFalse(optional(() -> destinationPanel()).isPresent());
    assertFalse(optional(() -> destinationPlates()).isPresent());
    assertFalse(optional(() -> destinationPlatePanel()).isPresent());
    assertFalse(optional(() -> destinationPlate()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    setTransferType(WELL);
    String plateName = "test_plate";
    setDestinationPlate(plateName);
    assertTrue(optional(() -> destinationPlatePanel()).isPresent());
    assertTrue(optional(() -> destinationPlate()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
  }

  @Test
  public void save_Error() throws Throwable {
    openWithTubes();
    String plateName = "test_plate";
    setDestinationPlate(plateName);

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  @Ignore("Test takes more than 20 minutes with phantomjs")
  public void save_PlateToPlate() throws Throwable {
    openWithWells();
    String plateName = "test_plate";
    setDestinationPlate(plateName);
    setDestinationWell(0, "A-1");
    setDestinationWell(1, "B-1");
    setDestinationWell(2, "C-1");

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(TransferView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(TransferView.VIEW_NAME).length() + 1));
    Transfer savedTransfer =
        jpaQueryFactory.select(transfer).from(transfer).where(transfer.id.eq(id)).fetchOne();
    assertEquals(3, savedTransfer.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedTransfer.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    Well well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals((Long) 559L, well.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(1, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals((Long) 560L, well.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 248L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals((Long) 444L, well.getSample().getId());
  }

  @Test
  public void save_PlateToTubes() throws Throwable {
    openWithWells();
    setTransferType(TUBE);
    setDestinationTube(0, "test_tube_1");
    setDestinationTube(1, "test_tube_2");
    setDestinationTube(2, "test_tube_3");

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(TransferView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(TransferView.VIEW_NAME).length() + 1));
    Transfer savedTransfer =
        jpaQueryFactory.select(transfer).from(transfer).where(transfer.id.eq(id)).fetchOne();
    assertEquals(3, savedTransfer.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedTransfer.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 224L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    Tube tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_1", tube.getName());
    assertEquals((Long) 559L, tube.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 236L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_2", tube.getName());
    assertEquals((Long) 560L, tube.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 248L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_3", tube.getName());
    assertEquals((Long) 444L, tube.getSample().getId());
  }

  @Test
  @Ignore("Test takes more than 20 minutes with phantomjs")
  public void save_TubesToPlate() throws Throwable {
    openWithTubes();
    String plateName = "test_plate";
    setDestinationPlate(plateName);
    setDestinationWell(0, "A-1");
    setDestinationWell(1, "B-1");
    setDestinationWell(2, "C-1");

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(TransferView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(TransferView.VIEW_NAME).length() + 1));
    Transfer savedTransfer =
        jpaQueryFactory.select(transfer).from(transfer).where(transfer.id.eq(id)).fetchOne();
    assertEquals(3, savedTransfer.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedTransfer.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 11L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    Well well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    opTs = find(savedTransfer.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 12L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(1, well.getRow());
    assertEquals(0, well.getColumn());
    opTs = find(savedTransfer.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 4L, ts.getContainer().getId());
    assertEquals(SampleContainerType.WELL, ts.getDestinationContainer().getType());
    well = (Well) ts.getDestinationContainer();
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, well.getRow());
    assertEquals(0, well.getColumn());
  }

  @Test
  public void save_TubesToTubes() throws Throwable {
    openWithTubes();
    setTransferType(TUBE);
    setDestinationTube(0, "test_tube_1");
    setDestinationTube(1, "test_tube_2");
    setDestinationTube(2, "test_tube_3");

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(TransferView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(TransferView.VIEW_NAME).length() + 1));
    Transfer savedTransfer =
        jpaQueryFactory.select(transfer).from(transfer).where(transfer.id.eq(id)).fetchOne();
    assertEquals(3, savedTransfer.getTreatedSamples().size());
    Optional<TreatedSample> opTs = find(savedTransfer.getTreatedSamples(), 559);
    assertTrue(opTs.isPresent());
    TreatedSample ts = opTs.get();
    assertEquals((Long) 559L, ts.getSample().getId());
    assertEquals((Long) 11L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    Tube tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_1", tube.getName());
    assertEquals((Long) 559L, tube.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 560);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 560L, ts.getSample().getId());
    assertEquals((Long) 12L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_2", tube.getName());
    assertEquals((Long) 560L, tube.getSample().getId());
    opTs = find(savedTransfer.getTreatedSamples(), 444);
    assertTrue(opTs.isPresent());
    ts = opTs.get();
    assertEquals((Long) 444L, ts.getSample().getId());
    assertEquals((Long) 4L, ts.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, ts.getDestinationContainer().getType());
    tube = (Tube) ts.getDestinationContainer();
    assertEquals("test_tube_3", tube.getName());
    assertEquals((Long) 444L, tube.getSample().getId());
  }
}
