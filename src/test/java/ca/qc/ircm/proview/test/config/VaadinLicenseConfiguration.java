package ca.qc.ircm.proview.test.config;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vaadin license configuration.
 */
@ConfigurationProperties(prefix = VaadinLicenseConfiguration.PREFIX)
@UsedBy(SPRING)
@SuppressWarnings("unused")
public record VaadinLicenseConfiguration(boolean assume, List<String> paths) {

  public static final String PREFIX = "vaadin.license";
}
