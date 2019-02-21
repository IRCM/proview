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

package ca.qc.ircm.proview.dataanalysis;

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for data analysis.
 */
@Service
@Transactional
public class DataAnalysisService {
  @Inject
  private DataAnalysisRepository repository;
  @Inject
  private SubmissionSampleRepository sampleRepository;
  @Inject
  private DataAnalysisActivityService dataAnalysisActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DataAnalysisService() {
  }

  /**
   * Selects data analysis from database.
   *
   * @param id
   *          database identifier of data analysis
   * @return data analysis
   */
  public DataAnalysis get(Long id) {
    if (id == null) {
      return null;
    }
    DataAnalysis dataAnalysis = repository.findOne(id);

    authorizationService.checkDataAnalysisReadPermission(dataAnalysis);

    return dataAnalysis;
  }

  /**
   * Selects all data analyses asked for any of submission's samples.
   *
   * @param submission
   *          submission
   * @return all data analyses asked for any of submission's samples
   */
  public List<DataAnalysis> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSubmissionReadPermission(submission);

    BooleanExpression predicate = dataAnalysis.sample.in(submission.getSamples());
    return Lists.newArrayList(repository.findAll(predicate));
  }

  /**
   * Insert data analysis requests into database.
   * <p>
   * Sample's status is changed to {@link ca.qc.ircm.proview.sample.SampleStatus#DATA_ANALYSIS} .
   * </p>
   *
   * @param dataAnalyses
   *          data analysis requests
   */
  public void insert(Collection<DataAnalysis> dataAnalyses) {
    for (DataAnalysis dataAnalysis : dataAnalyses) {
      authorizationService.checkSampleReadPermission(dataAnalysis.getSample());

      // Update sample status.
      dataAnalysis.getSample().setStatus(SampleStatus.DATA_ANALYSIS);

      // Insert data analysis.
      dataAnalysis.setStatus(DataAnalysisStatus.TO_DO);
      repository.saveAndFlush(dataAnalysis);

      // Log insertion of data analysis.
      Activity activity = dataAnalysisActivityService.insert(dataAnalysis);
      activityService.insert(activity);

      sampleRepository.save(dataAnalysis.getSample());
    }
  }

  private boolean existsTodoBySampleWithExclude(DataAnalysis dataAnalysisParam) {
    BooleanExpression predicate = dataAnalysis.sample.eq(dataAnalysisParam.getSample())
        .and(dataAnalysis.status.eq(DataAnalysisStatus.TO_DO))
        .and(dataAnalysis.ne(dataAnalysisParam));
    return repository.count(predicate) > 0;
  }

  /**
   * Changes data analysis results.
   * <p>
   * If data analysis's status is changed to
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus#TO_DO}, sample's status is changed to
   * {@link ca.qc.ircm.proview.sample.SampleStatus#DATA_ANALYSIS} .
   * </p>
   * <p>
   * If data analysis's status is changed to
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus#ANALYSED} or
   * {@link ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus#CANCELLED} and sample has no more
   * data analyses with {@link ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus#TO_DO} status,
   * sample's status is changed to {@link ca.qc.ircm.proview.sample.SampleStatus#ANALYSED} .
   * </p>
   *
   * @param dataAnalysis
   *          data analysis with new information
   * @param explanation
   *          explanation for changes made to data analysis
   */
  public void update(DataAnalysis dataAnalysis, String explanation) {
    authorizationService.checkAdminRole();

    // Update sample status.
    if (dataAnalysis.getStatus() != DataAnalysisStatus.TO_DO
        && !existsTodoBySampleWithExclude(dataAnalysis)) {
      dataAnalysis.getSample().setStatus(SampleStatus.ANALYSED);
    } else {
      dataAnalysis.getSample().setStatus(SampleStatus.DATA_ANALYSIS);
    }

    // Log update of data analysis.
    Optional<Activity> activity = dataAnalysisActivityService.update(dataAnalysis, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    repository.save(dataAnalysis);
    sampleRepository.save(dataAnalysis.getSample());
  }
}
