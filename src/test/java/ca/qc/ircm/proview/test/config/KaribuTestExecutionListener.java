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

package ca.qc.ircm.proview.test.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.SpringServlet;
import org.mockito.internal.util.MockUtil;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Configures Karibu-Testing.
 */
public class KaribuTestExecutionListener implements TestExecutionListener {
  private Routes routes;

  private boolean isKaribuTest(TestContext testContext) {
    return AbstractKaribuTestCase.class.isAssignableFrom(testContext.getTestClass());
  }

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    if (!isKaribuTest(testContext)) {
      return;
    }
    routes = new Routes().autoDiscoverViews("ca.qc.ircm.proview");
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (!isKaribuTest(testContext)) {
      return;
    }
    AuthorizationService authorizationService =
        testContext.getApplicationContext().getBean(AuthorizationService.class);
    if (MockUtil.isMock(authorizationService)) {
      // Force isAuthorized method to return true to prevent errors when configuring routes.
      when(authorizationService.isAuthorized(any())).thenReturn(true);
    }
    AnnotationFinder
        .findAnnotation(testContext.getTestClass(), testContext.getTestMethod(), UserAgent.class)
        .ifPresent(ua -> MockVaadin.INSTANCE.setUserAgent(ua.value()));
    final SpringServlet servlet =
        new MockSpringServlet(routes, testContext.getApplicationContext(), UI::new);
    MockVaadin.setup(UI::new, servlet);
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    if (!isKaribuTest(testContext)) {
      return;
    }
    MockVaadin.tearDown();
  }
}
