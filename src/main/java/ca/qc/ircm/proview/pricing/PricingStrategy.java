package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.sample.SubmissionSample;

import java.math.BigDecimal;

/**
 * Compute prices of analysis and modifications asked by user.
 */
public interface PricingStrategy {

  /**
   * Compute price for sample.
   * <p>
   * The returned price is valid for the date sample is submitted.
   * </p>
   *
   * @param sample
   *          sample
   * @return price for this sample
   */
  public BigDecimal computePrice(SubmissionSample sample);

}
