package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.sample.SubmissionSample;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

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
   * @param date
   *          date of prices to use
   * @return sample's price
   */
  public BigDecimal computePrice(SubmissionSample sample, Date date) {
    PricingStrategy strategy = this.getPriceStrategy(date);
    return strategy.computePrice(sample);
  }

  /**
   * Returns price strategy that applied for date.
   *
   * @param date
   *          Date for prices.
   * @return Price strategy that applied for date.
   * @throws NullPointerException
   *           if date is null
   */
  public PricingStrategy getPriceStrategy(Date date) {
    if (date == null) {
      throw new NullPointerException("date cannot be null");
    }

    NullPricingStrategy strategy = new NullPricingStrategy(date);
    return strategy;
  }
}
