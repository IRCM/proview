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
