package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.sample.SubmissionSample;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Pricing strategy that always return null.
 */
public class NullPricingStrategy implements PricingStrategy {

  protected NullPricingStrategy(Instant instant) {
  }

  /**
   * Does not evaluate sample, it just returns null.
   *
   * @param sample
   *          not used
   * @return null
   */
  @Override
  public BigDecimal computePrice(SubmissionSample sample) {
    return null;
  }
}
