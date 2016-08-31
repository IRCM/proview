package ca.qc.ircm.proview.pricing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Returns prices for sample MS analysis.
 */
public class PricesServiceImpl implements PricesService {
  @Override
  public File getPrices() throws IOException {
    try {
      URL pricesLocation = this.getClass().getResource("/prices.pdf");
      return new File(pricesLocation.toURI());
    } catch (URISyntaxException e) {
      throw new IOException("Prices file could not be found", e);
    }
  }
}
