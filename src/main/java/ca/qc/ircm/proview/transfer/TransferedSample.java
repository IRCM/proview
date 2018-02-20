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

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Transfer of a single sample from one well to another.
 */
@Entity
@DiscriminatorValue("TRANSFER")
public class TransferedSample extends TreatmentSample implements Data {
  /**
   * Transfer.
   */
  @ManyToOne
  @JoinColumn(name = "treatmentId", nullable = false)
  private Transfer transfer;
  /**
   * Sample's destination container.
   */
  @ManyToOne
  @JoinColumn(name = "destinationContainerId", nullable = false)
  private SampleContainer destinationContainer;

  @Override
  public String toString() {
    return "transferedSample [getId()=" + getId() + ", getSample()=" + getSample() + "]";
  }

  public SampleContainer getDestinationContainer() {
    return destinationContainer;
  }

  public void setDestinationContainer(SampleContainer destinationContainer) {
    this.destinationContainer = destinationContainer;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }
}
