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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class MsAnalysisViewTest extends MsAnalysisViewPageObject {
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;

  private Optional<Acquisition> find(Collection<Acquisition> acquisitions, long sampleId) {
    return acquisitions.stream().filter(ts -> ts.getSample().getId() == sampleId).findFirst();
  }

  private List<Acquisition> findAll(Collection<Acquisition> acquisitions, long sampleId) {
    return acquisitions.stream().filter(ts -> ts.getSample().getId() == sampleId)
        .collect(Collectors.toList());
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

    assertTrue(resources(MsAnalysisView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> msAnalysisPanel()).isPresent());
    assertTrue(optional(() -> massDetectionInstrument()).isPresent());
    assertTrue(optional(() -> source()).isPresent());
    assertTrue(optional(() -> containersPanel()).isPresent());
    assertTrue(optional(() -> containers()).isPresent());
    assertTrue(optional(() -> acquisitionsPanel()).isPresent());
    assertTrue(optional(() -> acquisitions()).isPresent());
    assertTrue(optional(() -> down()).isPresent());
    assertFalse(optional(() -> explanationPanel()).isPresent());
    assertFalse(optional(() -> explanation()).isPresent());
    assertTrue(optional(() -> save()).isPresent());
    assertFalse(optional(() -> remove()).isPresent());
    assertFalse(optional(() -> banContainers()).isPresent());
  }

  @Test
  public void fieldsExistence_Update() throws Throwable {
    openWithMsAnalysis();

    assertTrue(optional(() -> header()).isPresent());
    assertFalse(optional(() -> deleted()).isPresent());
    assertTrue(optional(() -> msAnalysisPanel()).isPresent());
    assertTrue(optional(() -> massDetectionInstrument()).isPresent());
    assertTrue(optional(() -> source()).isPresent());
    assertTrue(optional(() -> containersPanel()).isPresent());
    assertTrue(optional(() -> containers()).isPresent());
    assertTrue(optional(() -> acquisitionsPanel()).isPresent());
    assertTrue(optional(() -> acquisitions()).isPresent());
    assertTrue(optional(() -> down()).isPresent());
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
    setAcquisitionCount(0, "2");
    setSampleListName(0, "sample_list");
    setAcquisitionFile(0, "acqui_01");
    setComment(0, "test comment");
    clickDown();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(MsAnalysisView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(MsAnalysisView.VIEW_NAME).length() + 1));
    MsAnalysis savedMsAnalysis = entityManager.find(MsAnalysis.class, id);
    assertEquals(MassDetectionInstrument.VELOS, savedMsAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, savedMsAnalysis.getSource());
    assertEquals(4, savedMsAnalysis.getAcquisitions().size());
    List<Acquisition> containerAcquisitions = findAll(savedMsAnalysis.getAcquisitions(), 559);
    assertEquals(2, containerAcquisitions.size());
    Optional<Acquisition> opAcquisition = containerAcquisitions.stream()
        .filter(ac -> ac.getAcquisitionFile().equals("acqui_01")).findFirst();
    assertTrue(opAcquisition.isPresent());
    Acquisition acquisition = opAcquisition.get();
    assertEquals((Long) 559L, acquisition.getSample().getId());
    assertEquals((Long) 11L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_01", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 1, acquisition.getPosition());
    opAcquisition = containerAcquisitions.stream()
        .filter(ac -> ac.getAcquisitionFile().equals("acqui_04")).findFirst();
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 559L, acquisition.getSample().getId());
    assertEquals((Long) 11L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_04", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 2, acquisition.getPosition());
    opAcquisition = find(savedMsAnalysis.getAcquisitions(), 560);
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 560L, acquisition.getSample().getId());
    assertEquals((Long) 12L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_02", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 1, acquisition.getPosition());
    opAcquisition = find(savedMsAnalysis.getAcquisitions(), 444);
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 444L, acquisition.getSample().getId());
    assertEquals((Long) 4L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_03", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 3, acquisition.getPosition());
  }

  @Test
  public void add_Wells() throws Throwable {
    openWithWells();
    setAcquisitionCount(0, "2");
    setSampleListName(0, "sample_list");
    setAcquisitionFile(0, "acqui_01");
    setComment(0, "test comment");
    clickDown();

    clickSave();

    assertTrue(getDriver().getCurrentUrl().startsWith(viewUrl(MsAnalysisView.VIEW_NAME)));
    long id = Long.parseLong(
        getDriver().getCurrentUrl().substring(viewUrl(MsAnalysisView.VIEW_NAME).length() + 1));
    MsAnalysis savedMsAnalysis = entityManager.find(MsAnalysis.class, id);
    assertEquals(MassDetectionInstrument.VELOS, savedMsAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, savedMsAnalysis.getSource());
    assertEquals(4, savedMsAnalysis.getAcquisitions().size());
    List<Acquisition> containerAcquisitions = findAll(savedMsAnalysis.getAcquisitions(), 559);
    assertEquals(2, containerAcquisitions.size());
    Optional<Acquisition> opAcquisition = containerAcquisitions.stream()
        .filter(ac -> ac.getAcquisitionFile().equals("acqui_01")).findFirst();
    assertTrue(opAcquisition.isPresent());
    Acquisition acquisition = opAcquisition.get();
    assertEquals((Long) 559L, acquisition.getSample().getId());
    assertEquals((Long) 224L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_01", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 1, acquisition.getPosition());
    opAcquisition = containerAcquisitions.stream()
        .filter(ac -> ac.getAcquisitionFile().equals("acqui_04")).findFirst();
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 559L, acquisition.getSample().getId());
    assertEquals((Long) 224L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_04", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 2, acquisition.getPosition());
    opAcquisition = find(savedMsAnalysis.getAcquisitions(), 560);
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 560L, acquisition.getSample().getId());
    assertEquals((Long) 236L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_02", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 1, acquisition.getPosition());
    opAcquisition = find(savedMsAnalysis.getAcquisitions(), 444);
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 444L, acquisition.getSample().getId());
    assertEquals((Long) 248L, acquisition.getContainer().getId());
    assertEquals("sample_list", acquisition.getSampleListName());
    assertEquals("acqui_03", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 3, acquisition.getPosition());
  }

  @Test
  public void update() throws Throwable {
    openWithMsAnalysis();
    setMassDetectionInstrument(MassDetectionInstrument.ORBITRAP_FUSION);
    setComment(0, "test comment");
    clickDown();

    clickSave();

    assertEquals(viewUrl(MsAnalysisView.VIEW_NAME, "14"), getDriver().getCurrentUrl());
    MsAnalysis savedMsAnalysis = entityManager.find(MsAnalysis.class, 14L);
    assertEquals(MassDetectionInstrument.ORBITRAP_FUSION,
        savedMsAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.LDTD, savedMsAnalysis.getSource());
    assertEquals(2, savedMsAnalysis.getAcquisitions().size());
    Optional<Acquisition> opAcquisition = find(savedMsAnalysis.getAcquisitions(), 444);
    assertTrue(opAcquisition.isPresent());
    Acquisition acquisition = opAcquisition.get();
    assertEquals((Long) 444L, acquisition.getSample().getId());
    assertEquals((Long) 4L, acquisition.getContainer().getId());
    assertEquals("XL_20111115_01", acquisition.getSampleListName());
    assertEquals("XL_20111115_COU_01", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 2, acquisition.getPosition());
    opAcquisition = find(savedMsAnalysis.getAcquisitions(), 445);
    assertTrue(opAcquisition.isPresent());
    acquisition = opAcquisition.get();
    assertEquals((Long) 445L, acquisition.getSample().getId());
    assertEquals((Long) 5L, acquisition.getContainer().getId());
    assertEquals("XL_20111115_01", acquisition.getSampleListName());
    assertEquals("XL_20111115_COU_02", acquisition.getAcquisitionFile());
    assertEquals("test comment", acquisition.getComment());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals((Integer) 1, acquisition.getPosition());
  }
}
