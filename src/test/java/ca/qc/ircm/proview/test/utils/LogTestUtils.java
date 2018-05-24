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

package ca.qc.ircm.proview.test.utils;

import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.history.UpdateActivity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for tests of log classes.
 */
public class LogTestUtils {
  /**
   * Validates that 2 sets of activities are the same.
   *
   * @param expecteds
   *          expected activities
   * @param actuals
   *          actual activities
   */
  public static void validateUpdateActivities(Collection<UpdateActivity> expecteds,
      Collection<UpdateActivity> actuals) {
    Set<ComparableUpdateActivity> comparableExpecteds = new HashSet<ComparableUpdateActivity>();
    Set<ComparableUpdateActivity> comparableActuals = new HashSet<ComparableUpdateActivity>();
    if (expecteds != null) {
      for (UpdateActivity updateActivity : expecteds) {
        comparableExpecteds.add(new ComparableUpdateActivity(updateActivity));
      }
    }
    if (actuals != null) {
      for (UpdateActivity updateActivity : actuals) {
        comparableActuals.add(new ComparableUpdateActivity(updateActivity));
      }
    }

    for (ComparableUpdateActivity actual : comparableActuals) {
      assertTrue("Activity " + actual + " not expected", comparableExpecteds.contains(actual));
    }
    for (ComparableUpdateActivity expected : comparableExpecteds) {
      assertTrue("Expected to find " + expected + " in sample update activity",
          comparableActuals.contains(expected));
    }
  }
}
