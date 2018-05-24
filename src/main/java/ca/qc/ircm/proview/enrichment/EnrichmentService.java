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

package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.ProtocolService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for enrichment.
 */
@Service
@Transactional
public class EnrichmentService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private ProtocolService protocolService;
  @Inject
  private EnrichmentActivityService enrichmentActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected EnrichmentService() {
  }

  protected EnrichmentService(EntityManager entityManager, JPAQueryFactory queryFactory,
      ProtocolService protocolService, EnrichmentActivityService enrichmentActivityService,
      ActivityService activityService, AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.protocolService = protocolService;
    this.enrichmentActivityService = enrichmentActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Selects enrichment from database.
   *
   * @param id
   *          database identifier of enrichment
   * @return enrichment
   */
  public Enrichment get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(Enrichment.class, id);
  }

  /**
   * Inserts an enrichment into the database.
   *
   * @param enrichment
   *          enrichment to insert
   */
  public void insert(Enrichment enrichment) {
    authorizationService.checkAdminRole();
    chechSameUserForAllSamples(enrichment);
    User user = authorizationService.getCurrentUser();

    enrichment.getTreatedSamples().forEach(ts -> ts.setTreatment(enrichment));
    enrichment.setInsertTime(Instant.now());
    enrichment.setUser(user);

    if (enrichment.getProtocol().getId() == null) {
      protocolService.insert(enrichment.getProtocol());
    }
    entityManager.persist(enrichment);
    enrichment.getTreatedSamples().stream().map(ts -> ts.getSample())
        .filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> (SubmissionSample) sample).forEach(sample -> {
          sample.setStatus(SampleStatus.ENRICHED);
          entityManager.merge(sample);
        });

    // Log insertion of enrichment.
    entityManager.flush();
    Activity activity = enrichmentActivityService.insert(enrichment);
    activityService.insert(activity);
  }

  /**
   * Updates enrichment's information in database.
   *
   * @param enrichment
   *          enrichment containing new information
   * @param explanation
   *          explanation
   */
  public void update(Enrichment enrichment, String explanation) {
    authorizationService.checkAdminRole();

    Enrichment old = entityManager.find(Enrichment.class, enrichment.getId());
    Set<Long> treatedSampleIds =
        enrichment.getTreatedSamples().stream().map(ts -> ts.getId()).collect(Collectors.toSet());
    if (old.getTreatedSamples().stream().filter(ts -> !treatedSampleIds.contains(ts.getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot remove " + TreatedSample.class.getSimpleName()
          + " from " + Enrichment.class.getSimpleName() + " on update");
    }

    if (enrichment.getProtocol().getId() == null) {
      protocolService.insert(enrichment.getProtocol());
    }

    Optional<Activity> activity = enrichmentActivityService.update(enrichment, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(enrichment);
  }

  /**
   * Undo enrichment.
   *
   * @param enrichment
   *          enrichment to undo
   * @param explanation
   *          explanation
   * @param banContainers
   *          true if containers used in enrichment should be banned, this will also ban any
   *          container were samples were transfered after enrichment
   */
  public void undo(Enrichment enrichment, String explanation, boolean banContainers) {
    authorizationService.checkAdminRole();

    enrichment.setDeleted(true);
    enrichment.setDeletionExplanation(explanation);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during enrichment.
      for (TreatedSample treatedSample : enrichment.getTreatedSamples()) {
        SampleContainer container = treatedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after enrichment.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        enrichmentActivityService.undoFailed(enrichment, explanation, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(enrichment);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
