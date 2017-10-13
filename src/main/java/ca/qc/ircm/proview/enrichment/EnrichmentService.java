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

import static ca.qc.ircm.proview.enrichment.QEnrichedSample.enrichedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichment.enrichment;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.BaseTreatmentService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for enrichment.
 */
@Service
@Transactional
public class EnrichmentService extends BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private EnrichmentProtocolService enrichmentProtocolService;
  @Inject
  private EnrichmentActivityService enrichmentActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected EnrichmentService() {
  }

  protected EnrichmentService(EntityManager entityManager, JPAQueryFactory queryFactory,
      EnrichmentProtocolService enrichmentProtocolService,
      EnrichmentActivityService enrichmentActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    super(entityManager, queryFactory);
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.enrichmentProtocolService = enrichmentProtocolService;
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
   * Returns all enrichments where one of submission's samples was enriched.
   *
   * @param submission
   *          submission
   * @return all enrichments where one of submission's samples was enriched
   */
  public List<Enrichment> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Enrichment> query = queryFactory.select(enrichment);
    query.from(enrichment, enrichedSample);
    query.where(enrichedSample._super.in(enrichment.treatmentSamples));
    query.where(enrichedSample.sample.in(submission.getSamples()));
    query.where(enrichment.deleted.eq(false));
    return query.distinct().fetch();
  }

  /**
   * Inserts an enrichment into the database.
   *
   * @param enrichment
   *          enrichment to insert
   */
  public void insert(Enrichment enrichment) {
    authorizationService.checkAdminRole();
    User user = authorizationService.getCurrentUser();

    enrichment.setInsertTime(Instant.now());
    enrichment.setUser(user);

    if (enrichment.getProtocol().getId() == null) {
      enrichmentProtocolService.insert(enrichment.getProtocol());
    }
    entityManager.persist(enrichment);
    enrichment.getTreatmentSamples().stream().map(ts -> ts.getSample())
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
   * Undo erroneous enrichment that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * enrichment are not the right ones. So, in practice, the enrichment never actually occurred.
   *
   * @param enrichment
   *          erroneous enrichment to undo
   * @param explanation
   *          explanation of what was incorrect with the enrichment
   */
  public void undoErroneous(Enrichment enrichment, String explanation) {
    authorizationService.checkAdminRole();

    enrichment.setDeleted(true);
    enrichment.setDeletionType(Treatment.DeletionType.ERRONEOUS);
    enrichment.setDeletionExplanation(explanation);

    // Log changes.
    Activity activity = enrichmentActivityService.undoErroneous(enrichment, explanation);
    activityService.insert(activity);

    entityManager.merge(enrichment);
  }

  /**
   * Report that a problem occurred during enrichment causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the enrichment was done but the incorrect
   * enrichment could only be detected later in the sample processing. Thus the enrichment is not
   * undone but flagged as having failed.
   *
   * @param enrichment
   *          enrichment to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in enrichment should be banned, this will also ban any
   *          container were samples were transfered after enrichment
   */
  public void undoFailed(Enrichment enrichment, String failedDescription, boolean banContainers) {
    authorizationService.checkAdminRole();

    enrichment.setDeleted(true);
    enrichment.setDeletionType(Treatment.DeletionType.FAILED);
    enrichment.setDeletionExplanation(failedDescription);
    Collection<SampleContainer> bannedContainers = new LinkedHashSet<>();
    if (banContainers) {
      // Ban containers used during enrichment.
      for (EnrichedSample enrichedSample : enrichment.getTreatmentSamples()) {
        SampleContainer container = enrichedSample.getContainer();
        container.setBanned(true);
        bannedContainers.add(container);

        // Ban containers were sample were transfered after enrichment.
        this.banDestinations(container, bannedContainers);
      }
    }

    // Log changes.
    Activity activity =
        enrichmentActivityService.undoFailed(enrichment, failedDescription, bannedContainers);
    activityService.insert(activity);

    entityManager.merge(enrichment);
    for (SampleContainer container : bannedContainers) {
      entityManager.merge(container);
    }
  }
}
