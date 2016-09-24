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

package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

/**
 * Destination container(s) is used in another treatment and sample cannot be remove.
 */
public class DestinationUsedInTreatmentException extends Exception {
  private static final long serialVersionUID = -6800335650110838829L;
  public final Collection<SampleContainer> containers;

  public DestinationUsedInTreatmentException(String message,
      Collection<SampleContainer> containers) {
    super(message);
    this.containers = containers;
  }
}