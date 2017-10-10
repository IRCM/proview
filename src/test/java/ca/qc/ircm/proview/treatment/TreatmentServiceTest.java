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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.fractionation.Fraction;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TreatmentServiceTest {
  private TreatmentService treatmentService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    treatmentService = new TreatmentService(entityManager, authorizationService);
  }

  @Test
  public void get_Solubilisation() throws Throwable {
    Treatment<?> treatment = treatmentService.get(1L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 1L, treatment.getId());
    assertEquals(Treatment.Type.SOLUBILISATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 11, 45, 0).atZone(ZoneId.systemDefault()).toInstant(),
        treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionType());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Solubilisation);
    Solubilisation solubilisation = (Solubilisation) treatment;
    List<SolubilisedSample> solubilisedSamples = solubilisation.getTreatmentSamples();
    assertEquals(1, solubilisedSamples.size());
    SolubilisedSample solubilisedSample = solubilisedSamples.get(0);
    assertEquals((Long) 1L, solubilisedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, solubilisedSample.getContainer().getType());
    assertEquals((Long) 1L, solubilisedSample.getContainer().getId());
    assertEquals(null, solubilisedSample.getComments());
    assertEquals("Methanol", solubilisedSample.getSolvent());
    assertEquals(20.0, solubilisedSample.getSolventVolume(), 0.01);
  }

  @Test
  public void get_EnrichmentProtocol() throws Throwable {
    Treatment<?> treatment = treatmentService.get(2L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 2L, treatment.getId());
    assertEquals(Treatment.Type.FRACTIONATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 19, 12, 20, 33, 0).atZone(ZoneId.systemDefault()).toInstant(),
        treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionType());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Fractionation);
    Fractionation fractionation = (Fractionation) treatment;
    assertEquals(FractionationType.MUDPIT, fractionation.getFractionationType());
    Fraction fraction = fractionation.getTreatmentSamples().get(0);
    assertEquals((Long) 2L, fraction.getId());
    assertEquals(SampleContainerType.TUBE, fraction.getContainer().getType());
    assertEquals((Long) 1L, fraction.getContainer().getId());
    assertEquals(SampleContainerType.TUBE, fraction.getDestinationContainer().getType());
    assertEquals((Long) 6L, fraction.getDestinationContainer().getId());
    assertEquals(null, fraction.getComments());
    assertEquals((Integer) 1, fraction.getPosition());
    assertEquals((Integer) 1, fraction.getNumber());
    assertEquals(null, fraction.getPiInterval());
  }

  @Test
  public void get_Null() throws Throwable {
    Treatment<?> protocol = treatmentService.get(null);

    assertNull(protocol);
  }
}
