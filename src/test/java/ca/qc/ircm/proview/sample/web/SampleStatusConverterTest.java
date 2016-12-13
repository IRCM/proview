package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ca.qc.ircm.proview.sample.SampleStatus;
import com.vaadin.data.util.converter.Converter.ConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SampleStatusConverterTest {
  private SampleStatusConverter converter;
  private Locale locale = Locale.ENGLISH;

  @Before
  public void beforeTest() {
    converter = new SampleStatusConverter();
  }

  @Test
  public void convertToModel() {
    SampleStatus status =
        converter.convertToModel(ANALYSED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(ANALYSED, status);
  }

  @Test
  public void convertToModel_Received() {
    SampleStatus status =
        converter.convertToModel(RECEIVED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(RECEIVED, status);
  }

  @Test
  public void convertToModel_French() {
    Locale locale = Locale.FRENCH;

    SampleStatus status =
        converter.convertToModel(ANALYSED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(ANALYSED, status);
  }

  @Test
  public void convertToModel_Null() {
    SampleStatus status = converter.convertToModel(null, SampleStatus.class, locale);

    assertNull(status);
  }

  @Test
  public void convertToModel_Empty() {
    SampleStatus status = converter.convertToModel("", SampleStatus.class, locale);

    assertNull(status);
  }

  @Test(expected = ConversionException.class)
  public void convertToModel_Invalid() {
    converter.convertToModel("invalid status", SampleStatus.class, locale);
  }

  @Test
  public void convertToPresentation() {
  }

  @Test
  public void getModelType() {
    assertEquals(SampleStatus.class, converter.getModelType());
  }

  @Test
  public void getPresentationType() {
    assertEquals(String.class, converter.getPresentationType());
  }
}
