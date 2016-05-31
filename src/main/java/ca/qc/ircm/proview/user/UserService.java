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

import org.apache.shiro.authz.UnauthorizedException;

import java.util.Collection;
import java.util.List;

/**
 * User service class.
 */
public interface UserService {
  /**
   * Selects user from database.
   *
   * @param id
   *          database identifier of user
   * @return user
   */
  public User get(Long id);

  /**
   * Returns user with email.
   *
   * @param email
   *          email
   * @return user with email
   */
  public User get(String email);

  /**
   * Returns true if a user exists with this email.
   *
   * @param email
   *          email
   * @return true if a user exists with this email
   */
  public boolean exists(String email);

  /**
   * Returns true if email parameter is the email of a non-admin laboratory manager, false
   * otherwise.
   *
   * @param email
   *          email
   * @return true if email parameter is the email of a non-admin laboratory manager, false otherwise
   */
  public boolean isManager(String email);

  /**
   * Returns all users that match parameters.
   * <p>
   * Only admin users can search users without a laboratory.
   * </p>
   * <p>
   * Only managers can search users with a laboratory.
   * </p>
   *
   * @param parameters
   *          parameters
   * @return all users that match parameters
   */
  public List<User> all(SearchUserParameters parameters);

  /**
   * Register a new user in a laboratory.
   *
   * @param user
   *          user to register
   * @param password
   *          user's password
   * @param manager
   *          user's manager
   * @param webContext
   *          web context used to send email to managers or admin users
   */
  public void register(User user, String password, User manager, RegisterUserWebContext webContext);

  /**
   * Updates a User.
   *
   * @param user
   *          signed user with new information
   * @param newPassword
   *          new password or null if password does not change
   * @throws UnauthorizedException
   *           user must match signed user
   */
  public void update(User user, String newPassword);

  /**
   * Approve that users are valid.
   *
   * @param users
   *          users to validate
   */
  public void validate(Collection<User> users);

  /**
   * Allow users to use program.
   *
   * @param users
   *          users
   */
  public void activate(Collection<User> users);

  /**
   * Block users from using program.
   *
   * @param users
   *          users
   * @throws DeactivateManagerException
   *           managers cannot be deactivated
   */
  public void deactivate(Collection<User> users) throws DeactivateManagerException;

  /**
   * Deletes invalid users from database.
   *
   * @param users
   *          users to delete
   */
  public void delete(Collection<User> users);
}
