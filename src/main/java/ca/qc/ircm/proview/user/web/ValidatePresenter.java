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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QUser.user;

/**
 * Validate users presenter.
 */
public interface ValidatePresenter {
  public static final String EMAIL = user.email.getMetadata().getName();
  public static final String NAME = user.name.getMetadata().getName();
  public static final String LABORATORY_PREFIX = user.laboratory.getMetadata().getName() + ".";
  public static final String LABORATORY_NAME =
      LABORATORY_PREFIX + laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION =
      LABORATORY_PREFIX + laboratory.organization.getMetadata().getName();
  public static final String VIEW = "viewUser";
  public static final String VALIDATE = "validateUser";

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ValidateView view);
}
