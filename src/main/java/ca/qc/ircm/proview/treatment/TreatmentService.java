package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.submission.Submission;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for treatments.
 */
@Service
@Transactional
public class TreatmentService {
  private final TreatmentRepository repository;

  @Autowired
  protected TreatmentService(TreatmentRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects treatment from database.
   *
   * @param id
   *          database identifier of treatment
   * @return treatment
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Treatment> get(long id) {
    return repository.findById(id);
  }

  /**
   * Returns all treatments where one of the submission's samples was treated.
   *
   * @param submission
   *          submission
   * @return all treatments where one of the submission's samples was treated
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Treatment> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }

    BooleanExpression predicate = treatment.treatedSamples.any().sample.in(submission.getSamples())
        .and(treatment.deleted.eq(false));
    return Lists.newArrayList(repository.findAll(predicate));
  }
}
