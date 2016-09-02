package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.sample.SubmissionSample;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Compute prices of analysis and modifications asked by user.
 */
@Component
public class PricingEvaluator {

  /**
   * Compute price for sample.
   *
   * @param sample
   *          sample
   * @param instant
   *          instant of prices to use
   * @return sample's price
   */
  public BigDecimal computePrice(SubmissionSample sample, Instant instant) {
    PricingStrategy strategy = this.getPriceStrategy(instant);
    return strategy.computePrice(sample);
  }

  /**
   * Returns price strategy that applied for date.
   *
   * @param instant
   *          instant for prices
   * @return price strategy that applied for date
   * @throws NullPointerException
   *           if date is null
   */
  public PricingStrategy getPriceStrategy(Instant instant) {
    if (instant == null) {
      throw new NullPointerException("instant cannot be null");
    }

    NullPricingStrategy strategy = new NullPricingStrategy(instant);
    return strategy;
  }
}
