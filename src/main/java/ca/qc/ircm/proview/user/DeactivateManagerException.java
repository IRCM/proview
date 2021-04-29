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

/**
 * Thrown when trying to deactivate a manager.
 */
public class DeactivateManagerException extends IllegalArgumentException {
  private static final long serialVersionUID = 5018354103467804809L;
  private final User manager;

  protected DeactivateManagerException(User manager) {
    this.manager = manager;
  }

  public User getManager() {
    return manager;
  }
}