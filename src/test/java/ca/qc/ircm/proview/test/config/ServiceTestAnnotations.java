package ca.qc.ircm.proview.test.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuration for tests using the database.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@TestExecutionListeners(
    value = { VaadinLicenseExecutionListener.class,
        FixSecurityContextHolderStrategyExecutionListener.class },
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@Transactional
@Sql({ "/drop-schema.sql", "/schema-h2.sql", "/database-before-insert.sql", "/user-data.sql",
    "/sample-data.sql", "/activity-data.sql", "/analysis-data.sql", "/database-after-insert.sql" })
public @interface ServiceTestAnnotations {

}
