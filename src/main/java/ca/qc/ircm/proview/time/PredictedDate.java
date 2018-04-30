package ca.qc.ircm.proview.time;

import java.time.LocalDate;

/**
 * Expected date.
 */
public class PredictedDate {
  /**
   * Date.
   */
  public LocalDate date;
  /**
   * True if date is expected, false if date is an actual date.
   */
  public boolean expected;

  public PredictedDate() {
  }

  public PredictedDate(LocalDate date, boolean expected) {
    this.date = date;
    this.expected = expected;
  }

  @Override
  public String toString() {
    return "PredictedDate [date=" + date + ", expected=" + expected + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + (expected ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PredictedDate)) {
      return false;
    }
    PredictedDate other = (PredictedDate) obj;
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (expected != other.expected) {
      return false;
    }
    return true;
  }
}
