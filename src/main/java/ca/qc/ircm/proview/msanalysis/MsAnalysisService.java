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

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.submission.Submission;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for MS analysis.
 */
@Service
@Transactional
public class MsAnalysisService {
  @Autowired
  private MsAnalysisRepository repository;
  @Autowired
  private JPAQueryFactory queryFactory;

  protected MsAnalysisService() {
  }

  /**
   * Selects MS analysis from database.
   *
   * @param id
   *          database identifier of MS analysis
   * @return MS analysis
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<MsAnalysis> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Selects all MS analysis made on submission.
   *
   * @param submissionParam
   *          submission
   * @return all MS analysis made on submission
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<MsAnalysis> all(Submission submissionParam) {
    if (submissionParam == null) {
      return new ArrayList<>();
    }

    JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis).distinct();
    query.from(msAnalysis, acquisition, submission);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(acquisition.sample.in(submission.samples));
    query.where(submission.eq(submissionParam));
    return query.fetch();
  }
}
