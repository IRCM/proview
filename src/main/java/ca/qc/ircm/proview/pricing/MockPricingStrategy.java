/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.pricing;

import ca.qc.ircm.proview.submission.Submission;

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
   * Does not evaluate submission, it just returns an amount that varies every day.
   * <p>
   * If called multiple times on a single day, the same amount will be returned every time.
   * </p>
   *
   * @param submission
   *          not used
   * @return random amount
   */
  @Override
  public BigDecimal computePrice(Submission submission) {
    LocalDate date = LocalDate.now();
    BigDecimal price = new BigDecimal(date.getYear()).remainder(new BigDecimal(2000))
        .multiply(new BigDecimal(365));
    return price.setScale(2, RoundingMode.HALF_EVEN);
  }
}
