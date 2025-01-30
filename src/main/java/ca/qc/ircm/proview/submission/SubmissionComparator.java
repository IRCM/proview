package ca.qc.ircm.proview.submission;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for submissions.
 */
public class SubmissionComparator implements Comparator<Submission>, Serializable {

  @Serial
  private static final long serialVersionUID = -151938109581440731L;

  @Override
  public int compare(Submission o1, Submission o2) {
    return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
  }
}
