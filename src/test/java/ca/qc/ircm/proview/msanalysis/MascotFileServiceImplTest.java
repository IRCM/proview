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

package ca.qc.ircm.proview.msanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MascotFileServiceImplTest {
  private MascotFileServiceImpl mascotFileServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private MascotFileActivityService mascotFileActivityService;
  @Mock
  private MsAnalysisService msAnalysisService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Mock
  private MsAnalysis msAnalysis;
  @Captor
  private ArgumentCaptor<AcquisitionMascotFile> acquisitionMascotFileCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    mascotFileServiceImpl = new MascotFileServiceImpl(entityManager, queryFactory,
        mascotFileActivityService, msAnalysisService, activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get() throws Throwable {
    AcquisitionMascotFile acquisitionMascotFile = mascotFileServiceImpl.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, acquisitionMascotFile.getId());
    assertEquals((Long) 2L, acquisitionMascotFile.getMascotFile().getId());
    assertEquals(MascotServer.SERV01, acquisitionMascotFile.getMascotFile().getServer());
    assertEquals("F006101.dat", acquisitionMascotFile.getMascotFile().getName());
    assertEquals(LocalDateTime.of(2009, 10, 2, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        acquisitionMascotFile.getMascotFile().getSearchDate());
    assertEquals("C:\\mascot\\data\\20090802\\F006101.dat",
        acquisitionMascotFile.getMascotFile().getLocation());
    assertEquals("data/20090802/F006101.dat",
        acquisitionMascotFile.getMascotFile().getViewLocation());
    assertEquals("\\\\Server01\\proteomic\\LTQ-Orbitrap\\DATA\\2009\\XL_20111014_COU_01.RAW",
        acquisitionMascotFile.getMascotFile().getRawFile());
    assertEquals(true, acquisitionMascotFile.isVisible());
    assertEquals((Long) 409L, acquisitionMascotFile.getAcquisition().getId());
  }

  @Test
  public void get_Null() throws Throwable {
    AcquisitionMascotFile acquisitionMascotFile = mascotFileServiceImpl.get(null);

    assertNull(acquisitionMascotFile);
  }

  @Test
  public void all() throws Throwable {
    Acquisition acquisition = new Acquisition(409L);
    when(msAnalysisService.get(any(Acquisition.class))).thenReturn(msAnalysis);

    List<AcquisitionMascotFile> acquisitionMascotFiles = mascotFileServiceImpl.all(acquisition);

    verify(authorizationService).checkMsAnalysisReadPermission(msAnalysis);
    assertEquals(1, acquisitionMascotFiles.size());
    AcquisitionMascotFile acquisitionMascotFile = acquisitionMascotFiles.get(0);
    assertEquals((Long) 1L, acquisitionMascotFile.getId());
    assertEquals((Long) 2L, acquisitionMascotFile.getMascotFile().getId());
    assertEquals(MascotServer.SERV01, acquisitionMascotFile.getMascotFile().getServer());
    assertEquals("F006101.dat", acquisitionMascotFile.getMascotFile().getName());
    assertEquals(LocalDateTime.of(2009, 10, 2, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        acquisitionMascotFile.getMascotFile().getSearchDate());
    assertEquals("C:\\mascot\\data\\20090802\\F006101.dat",
        acquisitionMascotFile.getMascotFile().getLocation());
    assertEquals("data/20090802/F006101.dat",
        acquisitionMascotFile.getMascotFile().getViewLocation());
    assertEquals("\\\\Server01\\proteomic\\LTQ-Orbitrap\\DATA\\2009\\XL_20111014_COU_01.RAW",
        acquisitionMascotFile.getMascotFile().getRawFile());
    assertEquals(true, acquisitionMascotFile.isVisible());
    assertEquals((Long) 409L, acquisitionMascotFile.getAcquisition().getId());
  }

  @Test
  public void all_Null() throws Throwable {
    List<AcquisitionMascotFile> acquisitionMascotFiles = mascotFileServiceImpl.all(null);

    assertEquals(0, acquisitionMascotFiles.size());
  }

  @Test
  public void exists_Sample_True() throws Throwable {
    Sample sample = new SubmissionSample(442L);

    boolean exists = mascotFileServiceImpl.exists(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    verify(authorizationService).hasAdminRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_Sample_False() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    boolean exists = mascotFileServiceImpl.exists(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    verify(authorizationService).hasAdminRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Sample_Proteomic() throws Throwable {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    Sample sample = new SubmissionSample(445L);

    boolean exists = mascotFileServiceImpl.exists(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    verify(authorizationService).hasAdminRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_Sample_NonProteomic() throws Throwable {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    Sample sample = new SubmissionSample(445L);

    boolean exists = mascotFileServiceImpl.exists(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    verify(authorizationService).hasAdminRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Null() throws Throwable {
    boolean exists = mascotFileServiceImpl.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void update() throws Throwable {
    AcquisitionMascotFile acquisitionMascotFile =
        entityManager.find(AcquisitionMascotFile.class, 1L);
    entityManager.detach(acquisitionMascotFile);
    assertEquals(true, acquisitionMascotFile.isVisible());
    acquisitionMascotFile.setVisible(false);
    acquisitionMascotFile.setComments("test_new_comments");
    when(mascotFileActivityService.update(any(AcquisitionMascotFile.class)))
        .thenReturn(optionalActivity);

    mascotFileServiceImpl.update(acquisitionMascotFile);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    acquisitionMascotFile = entityManager.find(AcquisitionMascotFile.class, 1L);
    assertEquals(false, acquisitionMascotFile.isVisible());
    // Test logs.
    verify(mascotFileActivityService).update(acquisitionMascotFileCaptor.capture());
    verify(activityService).insert(activity);
    AcquisitionMascotFile newAcquisitionMascotFile = acquisitionMascotFileCaptor.getValue();
    assertEquals(acquisitionMascotFile.getId(), newAcquisitionMascotFile.getId());
    assertEquals(false, newAcquisitionMascotFile.isVisible());
    assertEquals("test_new_comments", newAcquisitionMascotFile.getComments());
  }
}
