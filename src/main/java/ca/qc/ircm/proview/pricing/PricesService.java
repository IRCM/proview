package ca.qc.ircm.proview.pricing;

import java.io.File;
import java.io.IOException;

/**
 * Returns prices for sample MS analysis.
 */
public interface PricesService {
  /**
   * Returns prices for sample MS analysis that apply to signed user.
   *
   * @return prices for sample MS analysis that apply to signed user
   * @throws IOException
   *           prices files could not be read
   */
  public File getPrices() throws IOException;
}
