package ca.qc.ircm.proview.sample;

import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * All statuses of a sample.
 */
public enum SampleStatus {
  /**
   * Sample price must be approved by manager.
   */
  TO_APPROVE, /**
               * Sample is not received yet.
               */
  TO_RECEIVE, /**
               * Sample is in solution.
               */
  RECEIVED, /**
             * Sample must be digest.
             */
  TO_DIGEST, /**
              * Sample must be enriched.
              */
  TO_ENRICH, /**
              * Sample must be analysed.
              */
  TO_ANALYSE, /**
               * Result data must be manually analysed.
               */
  DATA_ANALYSIS, /**
                  * Sample is analysed and have results.
                  */
  ANALYSED, /**
             * Sample analyse was cancelled.
             */
  CANCELLED;

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(SampleStatus.class, locale);
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}