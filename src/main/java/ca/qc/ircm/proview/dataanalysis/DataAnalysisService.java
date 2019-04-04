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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.ArrayList;
import java.util.List;
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
}
