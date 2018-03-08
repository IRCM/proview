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
import static org.junit.Assume.assumeTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Skip tests annotated with {@link Slow} if <code>skipSlowTests</code> system property is set.
 */
@Order(SlowTestExecutionListener.ORDER)
public class SlowTestExecutionListener extends AbstractTestExecutionListener {
  public static final int ORDER = Ordered.HIGHEST_PRECEDENCE;
  private static final Logger logger = LoggerFactory.getLogger(SlowTestExecutionListener.class);
  private final boolean skipSlowTests;

  public SlowTestExecutionListener() {
    skipSlowTests = System.getProperty("slow-tests.skip") != null
        && Boolean.valueOf(System.getProperty("slow-tests.skip"));
  }

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    if (skipSlowTests) {
      Slow slow = findAnnotation(testContext.getTestClass(), Slow.class);
      if (slow != null) {
        String message = "Test class " + testContext.getTestClass().getName() + " is skipped";
        logger.info(message);
        assumeTrue(message, false);
      }
    }
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (skipSlowTests) {
      Slow slow =
          findAnnotation(testContext.getTestClass(), testContext.getTestMethod(), Slow.class);
      if (slow != null) {
        String message = "Test " + testContext.getTestMethod().getName() + " of class "
            + testContext.getTestClass().getName() + " is skipped";
        logger.info(message);
        assumeTrue(message, false);
      }
    }
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
