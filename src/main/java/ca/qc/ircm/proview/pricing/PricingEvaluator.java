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
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Compute prices of analysis and modifications asked by user.
 */
@Component
public class PricingEvaluator {

  /**
   * Compute price for submission.
   *
   * @param submission
   *          submission
   * @param instant
   *          instant of prices to use
   * @return submission's price
   */
  public BigDecimal computePrice(Submission submission, Instant instant) {
    PricingStrategy strategy = this.getPriceStrategy(instant);
    return strategy.computePrice(submission);
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
