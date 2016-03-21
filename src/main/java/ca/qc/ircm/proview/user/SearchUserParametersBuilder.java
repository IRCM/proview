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

package ca.qc.ircm.proview.user;

import ca.qc.ircm.proview.laboratory.Laboratory;

public class SearchUserParametersBuilder implements SearchUserParameters {
  private Laboratory laboratory;
  private boolean active;
  private boolean invalid;
  private boolean valid;
  private boolean nonProteomic;

  @Override
  public Laboratory getLaboratory() {
    return laboratory;
  }

  public SearchUserParametersBuilder inLaboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
    return this;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public SearchUserParametersBuilder onlyActive() {
    this.active = true;
    return this;
  }

  @Override
  public boolean isInvalid() {
    return invalid;
  }

  public SearchUserParametersBuilder onlyInvalid() {
    this.invalid = true;
    return this;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  public SearchUserParametersBuilder onlyValid() {
    this.valid = true;
    return this;
  }

  @Override
  public boolean isNonProteomic() {
    return nonProteomic;
  }

  public SearchUserParametersBuilder onlyNonProteomic() {
    this.nonProteomic = true;
    return this;
  }
}
