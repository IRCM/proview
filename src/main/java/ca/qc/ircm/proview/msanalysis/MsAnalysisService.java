package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.submission.Submission;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
  private final MsAnalysisRepository repository;
  private final JPAQueryFactory queryFactory;

  @Autowired
  protected MsAnalysisService(MsAnalysisRepository repository, JPAQueryFactory queryFactory) {
    this.repository = repository;
    this.queryFactory = queryFactory;
  }

  /**
   * Selects MS analysis from database.
   *
   * @param id
   *          database identifier of MS analysis
   * @return MS analysis
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<MsAnalysis> get(long id) {
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
    JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis).distinct();
    query.from(msAnalysis, acquisition, submission);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(acquisition.sample.in(submission.samples));
    query.where(submission.eq(submissionParam));
    return query.fetch();
  }
}
