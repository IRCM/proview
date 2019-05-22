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

import ca.qc.ircm.proview.security.AuthorizationService;
import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

/**
 * ViewAccessControl for Shiro.
 */
@Controller
public class SpringViewAccessControl implements ViewAccessControl {
  private static final Logger logger = LoggerFactory.getLogger(SpringViewAccessControl.class);
  @Inject
  private ApplicationContext applicationContext;
  @Inject
  private AuthorizationService authorizationService;

  protected SpringViewAccessControl() {
  }

  @Override
  public boolean isAccessGranted(UI ui, String beanName) {
    Long userId = authorizationService.getCurrentUser().getId();
    Class<?> beanClass = applicationContext.getType(beanName);
    boolean authorized = authorizationService.isAuthorized(beanClass);
    logger.debug("Access to view {} granted to user {}, {}", beanName, userId, authorized);
    return authorized;
  }

  void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
