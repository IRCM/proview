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

package ca.qc.ircm.proview.persistence;

import static ca.qc.ircm.proview.persistence.QueryDsl.direction;
import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link QueryDsl}.
 */
public class QueryDslTest {
  @Test
  public void qname_Test() {
    assertEquals("submission", qname(submission));
    assertEquals("user", qname(submission.user));
    assertEquals("name", qname(submission.user.name));
  }

  @Test
  public void direction_Test() {
    assertEquals(submission.experiment.asc(), direction(submission.experiment, false));
    assertEquals(submission.experiment.desc(), direction(submission.experiment, true));
    assertEquals(submission.user.name.asc(), direction(submission.user.name, false));
    assertEquals(submission.user.name.desc(), direction(submission.user.name, true));
  }
}
