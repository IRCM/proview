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

import static ca.qc.ircm.proview.test.config.AnnotationFinder.findAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;

/**
 * Skips about view.
 */
public class SkipAboutTestExecutionListener extends InjectIntoTestExecutionListener {
  private static final String ANONYMOUS_VIEW = SkipAboutView.VIEW_NAME;
  private static final Logger logger =
      LoggerFactory.getLogger(SkipAboutTestExecutionListener.class);

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (isTestBenchTest(testContext)) {
      DontSkipAbout dontSkipIntro = findAnnotation(testContext.getTestClass(),
          testContext.getTestMethod(), DontSkipAbout.class);
      if (dontSkipIntro == null) {
        logger.trace("Skip introduction view");
        AbstractTestBenchTestCase testInstance =
            (AbstractTestBenchTestCase) testContext.getTestInstance();
        testInstance.openView(ANONYMOUS_VIEW);
      }
    }
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
  }

  private boolean isTestBenchTest(TestContext testContext) {
    return AbstractTestBenchTestCase.class.isAssignableFrom(testContext.getTestClass());
  }
}
