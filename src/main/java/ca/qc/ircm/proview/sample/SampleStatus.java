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