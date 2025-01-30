package ca.qc.ircm.proview.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Default address configuration.
 */
@ConfigurationProperties(prefix = DefaultAddressConfiguration.PREFIX)
public class DefaultAddressConfiguration {

  public static final String PREFIX = "address";
  private String line;
  private String town;
  private String state;
  private String postalCode;
  private String country;

  /**
   * Returns default address.
   *
   * @return default address
   */
  public Address getAddress() {
    Address address = new Address();
    address.setLine(line);
    address.setTown(town);
    address.setState(state);
    address.setCountry(country);
    address.setPostalCode(postalCode);
    return address;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
