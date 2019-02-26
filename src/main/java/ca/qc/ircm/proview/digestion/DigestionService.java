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

package ca.qc.ircm.proview.digestion;

import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerRepository;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.user.User;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for Digestion class.
 */
@Service
@Transactional
public class DigestionService extends BaseTreatmentService {
  @Inject
  private DigestionRepository repository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SampleContainerRepository sampleContainerRepository;
  @Inject
  private ProtocolService protocolService;
  @Inject
  private DigestionActivityService digestionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionService() {
  }

  /**
   * Selects digestion from database.
   *
   * @param id
   *          database identifier of digestion
   * @return digestion
   */
  public Digestion get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return repository.findOne(id);
  }

  /**
   * Inserts a digestion into the database.
   *
   * @param digestion
   *          digestion to insert
   */
  public void insert(Digestion digestion) {
    if (digestion.getProtocol().getType() != Protocol.Type.DIGESTION) {
      throw new IllegalArgumentException(
          "Protocol must be of type " + Protocol.Type.DIGESTION.name());
    }

    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(digestion);
    User user = authorizationService.getCurrentUser();

    Instant insertTime = Instant.now();
    digestion.getTreatedSamples().forEach(ts -> ts.setTreatment(digestion));
    digestion.setUser(user);
    digestion.setInsertTime(insertTime);

    if (digestion.getProtocol().getId() == null) {
      protocolService.insert(digestion.getProtocol());
    }
    repository.save(digestion);
    digestion.getTreatedSamples().stream().map(ts -> ts.getSample())
        .filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> (SubmissionSample) sample).forEach(sample -> {
          sample.setStatus(SampleStatus.DIGESTED);
          sampleRepository.save(sample);
          Submission submission = sample.getSubmission();
          if (submission.getDigestionDate() == null) {
            submission.setDigestionDate(toLocalDate(insertTime));
            submissionRepository.save(submission);
          }
        });

    // Log insertion of digestion.
    repository.flush();
    Activity activity = digestionActivityService.insert(digestion);
    activityService.insert(activity);
  }

  /**
   * Updates digestion's information in database.
   *
   * @param digestion
   *          digestion containing new information
   * @param explanation
   *          explanation
   */
  public void update(Digestion digestion, String explanation) {
    authorizationService.checkAdminRole();

    Digestion old = repository.findOne(digestion.getId());
    Set<Long> treatedSampleIds =
        digestion.getTreatedSamples().stream().map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getTreatedSamples().stream().filter(ts -> !treatedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + TreatedSample.class.getSimpleName()
          + " from " + Digestion.class.getSimpleName() + " on update");
    }

    if (digestion.getProtocol().getId() == null) {
      protocolService.insert(digestion.getProtocol());
    }

    Optional<Activity> activity = digestionActivityService.update(digestion, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    repository.save(digestion);
  }

  /**
   * Undo digestion.
   *
   * @param digestion
   *          digestion to undo
   * @param explanation
   *          explanation
   * @param banContainers
   *          true if containers used in digestion should be banned, this will also ban any
   *          container were samples were transfered after digestion
   */
  public void undo(Digestion digestion, String explanation, boolean banContainers) {
    authorizationService.checkAdminRole();

    digestion.setDeleted(true);
    digestion.setDeletionExplanation(explanation);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during digestion.
      for (TreatedSample treatedSample : digestion.getTreatedSamples()) {
        SampleContainer container = treatedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers where sample were transfered after digestion.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        digestionActivityService.undoFailed(digestion, explanation, bannedContainers);
    activityService.insert(activity);

    repository.save(digestion);
    for (SampleContainer container : bannedContainers) {
      sampleContainerRepository.save(container);
    }
  }
}
