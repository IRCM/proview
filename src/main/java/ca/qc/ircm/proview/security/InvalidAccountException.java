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

import org.apache.shiro.authc.AccountException;

/**
 * Exception thrown when account was not yet validated.
 */
public class InvalidAccountException extends AccountException {
  private static final long serialVersionUID = 4348465312844508305L;

  public InvalidAccountException() {
    super();
  }

  public InvalidAccountException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAccountException(String message) {
    super(message);
  }

  public InvalidAccountException(Throwable cause) {
    super(cause);
  }
}
