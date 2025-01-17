package ca.qc.ircm.proview.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link DefaultAddressConfiguration}.
 */
@NonTransactionalTestAnnotations
public class DefaultAddressConfigurationTest {
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;

  @Test
  public void defaultProperties() {
    assertEquals("110 avenue des Pins Ouest", defaultAddressConfiguration.getLine());
    assertEquals("Montreal", defaultAddressConfiguration.getTown());
    assertEquals("Quebec", defaultAddressConfiguration.getState());
    assertEquals("Canada", defaultAddressConfiguration.getCountry());
    assertEquals("H2W 1R7", defaultAddressConfiguration.getPostalCode());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals("110 avenue des Pins Ouest", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("Canada", address.getCountry());
    assertEquals("H2W 1R7", address.getPostalCode());
  }
}
