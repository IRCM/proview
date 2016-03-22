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

import static org.junit.Assume.assumeTrue;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skip tests annotated with {@link Slow} if <code>skipSlowTests</code> system property is set.
 */
public class SlowRule implements TestRule {
  private final Logger logger = LoggerFactory.getLogger(SlowRule.class);
  private final boolean skipSlowTests;

  public SlowRule() {
    skipSlowTests = System.getProperty("slow-tests.skip") != null
        && Boolean.valueOf(System.getProperty("slow-tests.skip"));
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        if (skipSlowTests) {
          Slow slow = null;
          if (description.getAnnotation(Slow.class) != null) {
            slow = description.getAnnotation(Slow.class);
          } else if (description.getTestClass().getAnnotation(Slow.class) != null) {
            slow = description.getTestClass().getAnnotation(Slow.class);
          }
          if (slow != null) {
            logger.info("Test {} of class {} is skipped", description.getMethodName(),
                description.getClassName());
            assumeTrue(false);
          }
        }
        base.evaluate();
      }
    };
  }
}