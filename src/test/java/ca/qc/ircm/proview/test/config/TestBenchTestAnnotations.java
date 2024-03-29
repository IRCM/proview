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

import com.vaadin.testbench.TestBench;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuration for {@link TestBench} tests.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@TestExecutionListeners(
    value = { VaadinLicenseExecutionListener.class,
        FixSecurityContextHolderStrategyExecutionListener.class,
        TestBenchTestExecutionListener.class, TestBenchSecurityFilter.class },
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Headless
@Transactional
@Sql({ "/drop-schema.sql", "/schema-h2.sql", "/database-before-insert.sql", "/user-data.sql",
    "/sample-data.sql", "/activity-data.sql", "/analysis-data.sql", "/database-after-insert.sql" })
public @interface TestBenchTestAnnotations {
  /**
   * Returns true if ScreenshotOnFailureRule is used, false otherwise.
   *
   * @return true if ScreenshotOnFailureRule is used, false otherwise
   */
  public boolean useScreenshotRule() default false;
}
