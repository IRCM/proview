package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.sample.SubmissionSample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Pricing strategy to use for primary tests.
 */
public class MockPricingStrategy implements PricingStrategy {

  /**
   * Instant to use for price computation.
   */
  private final Instant instant;

  protected MockPricingStrategy(Instant instant) {
    if (instant == null) {
      throw new NullPointerException("instant cannot be null");
    }

    this.instant = instant;
  }

  /**
   * Does not evaluate sample, it just returns an amount that varies every day.
   * <p>
   * If called multiple times on a single day, the same amount will be returned every time.
   * </p>
   *
   * @param sample
   *          not used
   * @return random amount
   */
  @Override
  public BigDecimal computePrice(SubmissionSample sample) {
    LocalDate date = LocalDate.now();
    BigDecimal price = new BigDecimal(date.getYear()).remainder(new BigDecimal(2000))
        .multiply(new BigDecimal(365));
    return price.setScale(2, RoundingMode.HALF_EVEN);
  }
}
