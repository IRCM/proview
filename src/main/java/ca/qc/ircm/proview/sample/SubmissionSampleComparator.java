package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.sample.SubmissionSampleService.Sort;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for {@link SubmissionSample submitted samples}.
 */
public class SubmissionSampleComparator implements Comparator<SubmissionSample>, Serializable {
  private static final long serialVersionUID = 4809070366723354595L;

  private final Sort sort;
  private final Locale locale;

  public SubmissionSampleComparator(Sort sort, Locale locale) {
    this.sort = sort;
    this.locale = locale;
  }

  @Override
  public int compare(SubmissionSample o1, SubmissionSample o2) {
    Collator collator = Collator.getInstance(locale);
    if (sort != null) {
      switch (sort) {
        case LABORATORY:
          return collator.compare(o1.getLaboratory().getOrganization(),
              o2.getLaboratory().getOrganization());
        case USER:
          return collator.compare(o1.getUser().getEmail(), o2.getUser().getEmail());
        case SERVICE:
          return o1.getService().compareTo(o2.getService());
        case SUBMISSION:
          return o1.getSubmission().getSubmissionDate()
              .compareTo(o2.getSubmission().getSubmissionDate());
        case LIMS:
          SampleLimsComparator comparator = new SampleLimsComparator(locale);
          return comparator.compare(o1, o2);
        case NAME:
          return collator.compare(o1.getName(), o2.getName());
        case STATUS:
          return o1.getStatus().compareTo(o2.getStatus());
        case PROJECT:
          if (o1 instanceof ProteicSample && o2 instanceof ProteicSample) {
            return collator.compare(((ProteicSample) o1).getProject(),
                ((ProteicSample) o2).getProject());
          } else if (o1 instanceof ProteicSample) {
            return -1;
          } else if (o2 instanceof ProteicSample) {
            return 1;
          } else {
            return 0;
          }
        case EXPERIENCE:
          if (o1 instanceof ProteicSample && o2 instanceof ProteicSample) {
            return collator.compare(((ProteicSample) o1).getExperience(),
                ((ProteicSample) o2).getExperience());
          } else if (o1 instanceof ProteicSample) {
            return -1;
          } else if (o2 instanceof ProteicSample) {
            return 1;
          } else {
            return 0;
          }
        case SUPPORT:
          return o1.getSupport().compareTo(o2.getSupport());
        default:
          throw new AssertionError("sort " + sort + " not covered in switch");
      }
    } else {
      // Cannot compare.
      return 0;
    }
  }
}