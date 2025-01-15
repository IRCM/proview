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
    return obj instanceof RobotPermission;
  }

  @Override
  public String toString() {
    return "RobotPermission";
  }
}
