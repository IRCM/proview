package ca.qc.ircm.proview.pricing;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PricesServiceTest {
  private PricesServiceImpl pricesServiceImpl;

  @Before
  public void beforeTest() {
    pricesServiceImpl = new PricesServiceImpl();
  }

  @Test
  public void getPrices() throws Throwable {
    File file = pricesServiceImpl.getPrices();
    File expectedFile = new File(this.getClass().getResource("/prices.pdf").toURI());
    assertEquals(expectedFile, file);
  }
}
