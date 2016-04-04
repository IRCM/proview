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

package ca.qc.ircm.proview.security;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.user.User;

/**
 * Services for authorization.
 */
public interface AuthorizationService {
  /**
   * Returns current user.
   *
   * @return current user
   */
  public User getCurrentUser();

  /**
   * Returns true if current user is authenticated or remembered, false otherwise.
   *
   * @return true if current user is authenticated or remembered, false otherwise
   */
  public boolean isUser();

  /**
   * Returns true if user is 'running as' another identity other than its original one, false
   * otherwise.
   *
   * @return true if user is 'running as' another identity other than its original one, false
   *         otherwise
   */
  public boolean isRunAs();

  /**
   * Returns true if user has proteomic role, false otherwise.
   *
   * @return true if user has proteomic role, false otherwise
   */
  public boolean hasProteomicRole();

  /**
   * Returns true if user has manager role, false otherwise.
   *
   * @return true if user has manager role, false otherwise
   */
  public boolean hasManagerRole();

  /**
   * Returns true if user has manager role, false otherwise.
   *
   * @param user
   *          user
   * @return true if user has manager role, false otherwise
   */
  public boolean hasManagerRole(User user);

  /**
   * Returns true if user has user role, false otherwise.
   *
   * @return true if user has user role, false otherwise
   */
  public boolean hasUserRole();

  /**
   * Checks that current user has proteomic role.
   */
  public void checkProteomicRole();

  /**
   * Checks that current user has proteomic and manager role.
   */
  public void checkProteomicManagerRole();

  /**
   * Checks that current user has user role.
   */
  public void checkUserRole();

  /**
   * Checks that current user has robot role.
   */
  public void checkRobotRole();

  /**
   * Checks that current user can read laboratory.
   *
   * @param laboratory
   *          laboratory
   */
  public void checkLaboratoryReadPermission(Laboratory laboratory);

  /**
   * Returns true if user is a manager for laboratory, false otherwise.
   *
   * @param laboratory
   *          laboratory
   * @return true if user is a manager for laboratory, false otherwise
   */
  public boolean hasLaboratoryManagerPermission(Laboratory laboratory);

  /**
   * Checks that current user is a manager of laboratory.
   *
   * @param laboratory
   *          laboratory
   */
  public void checkLaboratoryManagerPermission(Laboratory laboratory);

  /**
   * Checks that current user can read user.
   *
   * @param user
   *          user
   */
  public void checkUserReadPermission(User user);

  /**
   * Checks that current user can write user.
   *
   * @param user
   *          user
   */
  public void checkUserWritePermission(User user);

  /**
   * Checks that current user can write user's password.
   *
   * @param user
   *          user
   */
  public void checkUserWritePasswordPermission(User user);
}