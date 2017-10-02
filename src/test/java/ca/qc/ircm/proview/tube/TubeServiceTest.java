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

package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TubeServiceTest {
  private TubeService tubeService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    tubeService = new TubeService(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get_Id() throws Throwable {
    Tube tube = tubeService.get(1L);

    verify(authorizationService).checkSampleReadPermission(tube.getSample());
    assertEquals((Long) 1L, tube.getId());
    assertEquals("FAM119A_band_01", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals(null, tube.getTreatmentSample());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        tube.getTimestamp());
  }

  @Test
  public void get_NullId() throws Throwable {
    Tube tube = tubeService.get((Long) null);

    assertNull(tube);
  }

  @Test
  public void get_Name() throws Throwable {
    Tube tube = tubeService.get("FAM119A_band_01");

    verify(authorizationService).checkSampleReadPermission(tube.getSample());
    assertEquals((Long) 1L, tube.getId());
    assertEquals("FAM119A_band_01", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals(null, tube.getTreatmentSample());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        tube.getTimestamp());
  }

  @Test
  public void get_NullName() throws Throwable {
    Tube tube = tubeService.get((String) null);

    assertNull(tube);
  }

  @Test
  public void original() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    Tube tube = tubeService.original(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals((Long) 1L, tube.getId());
    assertEquals("FAM119A_band_01", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals(null, tube.getTreatmentSample());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        tube.getTimestamp());
  }

  @Test
  public void original_Null() throws Throwable {
    Tube tube = tubeService.original(null);

    assertNull(tube);
  }

  @Test
  public void last() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    Tube tube = tubeService.last(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals((Long) 7L, tube.getId());
    assertEquals("FAM119A_band_01_T1", tube.getName());
    assertEquals((Long) 1L, tube.getSample().getId());
    assertEquals((Long) 3L, tube.getTreatmentSample().getId());
    assertEquals(SampleContainerType.TUBE, tube.getType());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 15, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        tube.getTimestamp());
  }

  @Test
  public void last_Null() throws Throwable {
    Tube tube = tubeService.last(null);

    assertNull(tube);
  }

  @Test
  public void all() throws Throwable {
    Sample sample = new SubmissionSample(1L);

    List<Tube> tubes = tubeService.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(3, tubes.size());
    assertTrue(find(tubes, 1L).isPresent());
    assertTrue(find(tubes, 6L).isPresent());
    assertTrue(find(tubes, 7L).isPresent());
    assertFalse(find(tubes, 5L).isPresent());
  }

  @Test
  public void all_Null() throws Throwable {
    List<Tube> tubes = tubeService.all(null);

    assertEquals(0, tubes.size());
  }

  @Test
  public void selectNameSuggestion() throws Throwable {
    List<String> tubeNames = tubeService.selectNameSuggestion("FAM");

    verify(authorizationService).checkAdminRole();
    assertEquals(3, tubeNames.size());
    assertEquals(true, tubeNames.contains("FAM119A_band_01"));
    assertEquals(true, tubeNames.contains("FAM119A_band_01_F1"));
    assertEquals(true, tubeNames.contains("FAM119A_band_01_T1"));
    assertEquals(false, tubeNames.contains("CAP_20111017_01"));
  }

  @Test
  public void selectNameSuggestion_Null() throws Throwable {
    List<String> tubeNames = tubeService.selectNameSuggestion(null);

    assertEquals(0, tubeNames.size());
  }

  @Test
  public void generateTubeName_1() throws Throwable {
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    Set<String> exludes = Collections.emptySet();

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("FAM119A_band_01_1", tubeName);
  }

  @Test
  public void generateTubeName_1WithExcludes() throws Throwable {
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    Set<String> exludes = new HashSet<>();
    exludes.add("FAM119A_band_01_1");

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("FAM119A_band_01_2", tubeName);
  }

  @Test
  public void generateTubeName_1WithRandomExcludes() throws Throwable {
    Sample sample = new SubmissionSample(1L, "FAM119A_band_01");
    Set<String> exludes = new HashSet<>();
    exludes.add("test");

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("FAM119A_band_01_1", tubeName);
  }

  @Test
  public void generateTubeName_New() throws Throwable {
    Sample sample = new SubmissionSample(null, "test");
    Set<String> exludes = Collections.emptySet();

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("test", tubeName);
  }

  @Test
  public void generateTubeName_NewWithExcludes() throws Throwable {
    Sample sample = new SubmissionSample(null, "test");
    Set<String> exludes = new HashSet<>();
    exludes.add("test");
    exludes.add("test_1");
    exludes.add("test_2");

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("test_3", tubeName);
  }

  @Test
  public void generateTubeName_NewWithRandomExcludes() throws Throwable {
    Sample sample = new SubmissionSample(null, "test");
    Set<String> exludes = new HashSet<>();
    exludes.add("abc");
    exludes.add("abc_1");
    exludes.add("abc_2");

    String tubeName = tubeService.generateTubeName(sample, exludes);

    verify(authorizationService).checkUserRole();
    assertEquals("test", tubeName);
  }

  @Test
  public void generateTubeName_NullSample() throws Throwable {
    Set<String> exludes = Collections.emptySet();

    String tubeName = tubeService.generateTubeName(null, exludes);

    assertNull(tubeName);
  }

  @Test
  public void generateTubeName_NullExcludes() throws Throwable {
    Sample sample = new SubmissionSample(null, "test");

    String tubeName = tubeService.generateTubeName(sample, null);

    verify(authorizationService).checkUserRole();
    assertEquals("test", tubeName);
  }
}
