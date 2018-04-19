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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TreatmentServiceTest {
  private TreatmentService treatmentService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    treatmentService = new TreatmentService(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get_Solubilisation() throws Throwable {
    Treatment treatment = treatmentService.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, treatment.getId());
    assertEquals(TreatmentType.SOLUBILISATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 11, 45, 0).atZone(ZoneId.systemDefault()).toInstant(),
        treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Solubilisation);
    Solubilisation solubilisation = (Solubilisation) treatment;
    List<TreatedSample> treatedSamples = solubilisation.getTreatedSamples();
    assertEquals(1, treatedSamples.size());
    TreatedSample treatedSample = treatedSamples.get(0);
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    assertEquals(null, treatedSample.getComment());
    assertEquals("Methanol", treatedSample.getSolvent());
    assertEquals(20.0, treatedSample.getSolventVolume(), 0.01);
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Treatment treatment = treatmentService.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, treatment.getId());
    assertEquals(TreatmentType.FRACTIONATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 12, 20, 33, 0).atZone(ZoneId.systemDefault()).toInstant(),
        treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Fractionation);
    Fractionation fractionation = (Fractionation) treatment;
    assertEquals(FractionationType.MUDPIT, fractionation.getFractionationType());
    TreatedSample treatedSample = fractionation.getTreatedSamples().get(0);
    assertEquals((Long) 2L, treatedSample.getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getDestinationContainer().getType());
    assertEquals((Long) 6L, treatedSample.getDestinationContainer().getId());
    assertEquals(null, treatedSample.getComment());
    assertEquals((Integer) 1, treatedSample.getPosition());
    assertEquals((Integer) 1, treatedSample.getNumber());
    assertEquals(null, treatedSample.getPiInterval());
  }

  @Test
  public void get_Null() throws Throwable {
    Treatment protocol = treatmentService.get(null);

    assertNull(protocol);
  }

  @Test
  public void all_147() {
    Submission submission = entityManager.find(Submission.class, 147L);

    List<Treatment> treatments = treatmentService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(2, treatments.size());
    assertTrue(find(treatments, 194L).isPresent());
    assertTrue(find(treatments, 195L).isPresent());
  }

  @Test
  public void all_149() {
    Submission submission = entityManager.find(Submission.class, 149L);

    List<Treatment> treatments = treatmentService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(12, treatments.size());
    for (long id = 209; id < 214; id++) {
      assertTrue("Treatment " + id + " not found", find(treatments, id).isPresent());
    }
    for (long id = 215; id <= 221; id++) {
      assertTrue("Treatment " + id + " not found", find(treatments, id).isPresent());
    }
  }

  @Test
  public void all_Null() {
    List<Treatment> treatments = treatmentService.all(null);

    assertTrue(treatments.isEmpty());
  }
}
