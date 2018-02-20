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

package ca.qc.ircm.proview.pricing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Returns prices for sample MS analysis.
 */
public class PricesService {
  /**
   * Returns prices for sample MS analysis that apply to signed user.
   *
   * @return prices for sample MS analysis that apply to signed user
   * @throws IOException
   *           prices files could not be read
   */
  public File getPrices() throws IOException {
    try {
      URL pricesLocation = this.getClass().getResource("/prices.pdf");
      return new File(pricesLocation.toURI());
    } catch (URISyntaxException e) {
      throw new IOException("Prices file could not be found", e);
    }
  }
}
