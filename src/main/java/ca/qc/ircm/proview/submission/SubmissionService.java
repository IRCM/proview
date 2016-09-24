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

package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.user.User;

import java.util.List;
import java.util.Map;

/**
 * Service for submission.
 */
public interface SubmissionService {
  /**
   * Report containing submitted samples.
   */
  public static interface Report {
    List<Submission> getSubmissions();

    Map<Submission, Boolean> getLinkedToResults();
  }

  /**
   * Selects submission from database.
   *
   * @param id
   *          database identifier of submission
   * @return submission
   */
  public Submission get(Long id);

  /**
   * Selects submission from database.
   *
   * @param filter
   *          filters submissions
   * @return submission
   */
  public Report report(SubmissionFilter filter);

  /**
   * Selects submission from database for admin users.
   *
   * @param filter
   *          filters submissions
   * @return submission
   */
  public Report adminReport(SubmissionFilter filter);

  /**
   * Add a submission to database.<br>
   * Submission's date should not be older than yesterday.
   *
   * @param submission
   *          submission
   */
  public void insert(Submission submission);

  /**
   * Updates submission.
   *
   * @param submission
   *          submission with new information
   * @param owner
   *          new submission's owner
   * @param justification
   *          justification for changes made to submission
   */
  public void update(Submission submission, User owner, String justification);
}
