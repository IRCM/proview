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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link TreatmentService}.
 */
@ServiceTestAnnotations
@WithMockUser(authorities = UserRole.ADMIN)
public class TreatmentServiceTest {
  @Autowired
  private TreatmentService treatmentService;
  @Autowired
  private SubmissionRepository submissionRepository;

  @Test
  public void get_Solubilisation() throws Throwable {
    Treatment treatment = treatmentService.get(1L).get();

    assertEquals((Long) 1L, treatment.getId());
    assertEquals(TreatmentType.SOLUBILISATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(LocalDateTime.of(2011, 10, 13, 11, 45, 0), treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Treatment);
    Treatment solubilisation = treatment;
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
    Treatment treatment = treatmentService.get(2L).get();

    assertEquals((Long) 2L, treatment.getId());
    assertEquals(TreatmentType.FRACTIONATION, treatment.getType());
    assertEquals((Long) 4L, treatment.getUser().getId());
    assertEquals(LocalDateTime.of(2011, 10, 19, 12, 20, 33, 0), treatment.getInsertTime());
    assertEquals(false, treatment.isDeleted());
    assertEquals(null, treatment.getDeletionExplanation());
    assertEquals(true, treatment instanceof Treatment);
    Treatment fractionation = treatment;
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
    assertFalse(treatmentService.get(null).isPresent());
  }

  @Test
  @WithAnonymousUser
  public void get_AccessDenied_Anonymous() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      treatmentService.get(1L);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void get_AccessDenied() throws Throwable {
    assertThrows(AccessDeniedException.class, () -> {
      treatmentService.get(1L);
    });
  }

  @Test
  public void all_147() {
    Submission submission = submissionRepository.findById(147L).orElse(null);

    List<Treatment> treatments = treatmentService.all(submission);

    assertEquals(2, treatments.size());
    assertTrue(find(treatments, 194L).isPresent());
    assertTrue(find(treatments, 195L).isPresent());
  }

  @Test
  public void all_149() {
    Submission submission = submissionRepository.findById(149L).orElse(null);

    List<Treatment> treatments = treatmentService.all(submission);

    assertEquals(12, treatments.size());
    for (long id = 209; id < 214; id++) {
      assertTrue(find(treatments, id).isPresent(), "Treatment " + id + " not found");
    }
    for (long id = 215; id <= 221; id++) {
      assertTrue(find(treatments, id).isPresent(), "Treatment " + id + " not found");
    }
  }

  @Test
  public void all_Null() {
    List<Treatment> treatments = treatmentService.all(null);

    assertTrue(treatments.isEmpty());
  }

  @Test
  @WithAnonymousUser
  public void all_AccessDenied_Anonymous() throws Throwable {
    Submission submission = submissionRepository.findById(149L).orElse(null);

    assertThrows(AccessDeniedException.class, () -> {
      treatmentService.all(submission);
    });
  }

  @Test
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void all_AccessDenied() throws Throwable {
    Submission submission = submissionRepository.findById(149L).orElse(null);

    assertThrows(AccessDeniedException.class, () -> {
      treatmentService.all(submission);
    });
  }
}
