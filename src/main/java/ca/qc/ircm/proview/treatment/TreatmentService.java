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
  @Autowired
  private TreatmentRepository repository;

  protected TreatmentService() {
  }

  /**
   * Selects treatment from database.
   *
   * @param id
   *          database identifier of treatment
   * @return treatment
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Treatment> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

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
