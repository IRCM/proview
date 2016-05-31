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

package ca.qc.ircm.proview.security.web;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * ViewAccessControl for Shiro.
 */
@Controller
public class ShiroViewAccessControl implements ViewAccessControl {
  private static final Logger logger = LoggerFactory.getLogger(ShiroViewAccessControl.class);
  @Inject
  private ApplicationContext applicationContext;

  @Override
  public boolean isAccessGranted(UI ui, String beanName) {
    Class<?> beanClass = applicationContext.getType(beanName);
    Subject subject = getSubject();
    if (beanClass.isAnnotationPresent(RolesAllowed.class)) {
      RolesAllowed rolesAllowed = beanClass.getAnnotation(RolesAllowed.class);
      String[] roles = rolesAllowed.value();
      boolean hasAnyRole = false;
      for (String role : roles) {
        hasAnyRole |= subject.hasRole(role);
      }
      logger.debug("Access to view {} granted to user {}, {}", beanName, subject.getPrincipal(),
          hasAnyRole);
      return hasAnyRole;
    } else {
      logger.debug("Access to view {} automatically granted to user {}, no {}", beanName,
          subject.getPrincipal(), RolesAllowed.class.getName());
      return true;
    }
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }
}
