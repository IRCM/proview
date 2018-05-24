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

/**
 * Compute prices of analysis and modifications asked by user.
 */
public interface PricingStrategy {
  /**
   * Compute price for submission.
   * <p>
   * The returned price is valid for the date submission is submitted.
   * </p>
   *
   * @param submission
   *          submission
   * @return price for this sample
   */
  public BigDecimal computePrice(Submission submission);
}
