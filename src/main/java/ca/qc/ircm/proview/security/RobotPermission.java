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

import java.io.Serializable;
import org.apache.shiro.authz.Permission;

/**
 * Permission of a maintenance robot.
 */
public class RobotPermission implements Permission, Serializable {
  private static final long serialVersionUID = -30223836569004334L;

  public RobotPermission() {
  }

  @Override
  public boolean implies(Permission permission) {
    return true;
  }

  @Override
  public int hashCode() {
    return -1870343872;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof RobotPermission) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "RobotPermission";
  }
}
