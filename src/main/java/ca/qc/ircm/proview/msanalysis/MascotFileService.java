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

import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for Mascot file.
 */
@Service
@Transactional
public class MascotFileService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private MascotFileActivityService mascotFileActivityService;
  @Inject
  private MsAnalysisService msAnalysisService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected MascotFileService() {
  }

  protected MascotFileService(EntityManager entityManager, JPAQueryFactory queryFactory,
      MascotFileActivityService mascotFileActivityService, MsAnalysisService msAnalysisService,
      ActivityService activityService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.mascotFileActivityService = mascotFileActivityService;
    this.msAnalysisService = msAnalysisService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  /**
   * Returns link between acquisistion and mascot file.
   *
   * @param id
   *          link identifier
   * @return link between acquisistion and mascot file
   */
  public AcquisitionMascotFile get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(AcquisitionMascotFile.class, id);
  }

  /**
   * Returns all links Mascot files linked to acquisition.
   *
   * @param acquisition
   *          acquisition
   * @return all Mascot files linked to acquisition
   */
  public List<AcquisitionMascotFile> all(Acquisition acquisition) {
    if (acquisition == null) {
      return new ArrayList<>();
    }
    MsAnalysis msAnalysis = msAnalysisService.get(acquisition);
    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    JPAQuery<AcquisitionMascotFile> query = queryFactory.select(acquisitionMascotFile);
    query.from(acquisitionMascotFile);
    query.where(acquisitionMascotFile.acquisition.eq(acquisition));
    return query.fetch();
  }

  /**
   * Returns true if sample is linked to at least one visible Mascot file, false otherwise.
   *
   * @param sample
   *          sample
   * @return true if sample is linked to at least one visible Mascot file, false otherwise
   */
  public boolean exists(Sample sample) {
    if (sample == null) {
      return false;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Long> query = queryFactory.select(acquisitionMascotFile.id);
    query.from(acquisitionMascotFile);
    query.where(acquisitionMascotFile.acquisition.sample.eq(sample));
    if (!authorizationService.hasAdminRole()) {
      query.where(acquisitionMascotFile.visible.eq(true));
    }
    return query.fetchCount() > 0;
  }

  /**
   * Updates link between acquisistion and mascot file.
   *
   * @param acquisitionMascotFile
   *          link between acquisistion and mascot file
   */
  public void update(AcquisitionMascotFile acquisitionMascotFile) {
    authorizationService.checkAdminRole();

    // Log change in database.
    Optional<Activity> activity = mascotFileActivityService.update(acquisitionMascotFile);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(acquisitionMascotFile);
  }
}
