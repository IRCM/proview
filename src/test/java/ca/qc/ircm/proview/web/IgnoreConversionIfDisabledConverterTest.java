package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for {@link IgnoreConversionIfDisabledConverter}.
 */
@NonTransactionalTestAnnotations
public class IgnoreConversionIfDisabledConverterTest {
  private IgnoreConversionIfDisabledConverter<String, Double> converter;
  @Mock
  private Converter<String, Double> delegate;
  @Mock
  private ValueContext context;
  private TextField component = new TextField();
  private String errorMessage = "error message";

  @BeforeEach
  public void before() {
    converter = new IgnoreConversionIfDisabledConverter<>(delegate);
    when(context.getComponent()).thenReturn(Optional.of((Component) component));
  }

  @Test
  public void convertToModel() {
    when(delegate.convertToModel(any(), any())).thenReturn(Result.ok(0.0));
    String value = "test";
    Result<Double> result = converter.convertToModel(value, context);
    verify(delegate).convertToModel(value, context);
    assertFalse(result.isError());
    assertEquals(0.0, result.getOrThrow(ignored -> new IllegalStateException()), 0.00001);
  }

  @Test
  public void convertToModel_Error() {
    when(delegate.convertToModel(any(), any())).thenReturn(Result.error(errorMessage));
    String value = "test";
    Result<Double> result = converter.convertToModel(value, context);
    verify(delegate).convertToModel(value, context);
    assertTrue(result.isError());
    assertEquals(errorMessage, result.getMessage().orElse(""));
  }

  @Test
  public void convertToModel_ErrorDisabled() {
    component.setEnabled(false);
    when(delegate.convertToModel(any(), any())).thenReturn(Result.error(errorMessage));
    String value = "test";
    Result<Double> result = converter.convertToModel(value, context);
    verify(delegate).convertToModel(value, context);
    assertFalse(result.isError());
    assertEquals(null, result.getOrThrow(ignored -> new IllegalStateException()));
  }

  @Test
  public void convertToModel_ErrorDisabledCustomValue() {
    converter = new IgnoreConversionIfDisabledConverter<>(delegate, 1.0);
    component.setEnabled(false);
    when(delegate.convertToModel(any(), any())).thenReturn(Result.error(errorMessage));
    String value = "test";
    Result<Double> result = converter.convertToModel(value, context);
    verify(delegate).convertToModel(value, context);
    assertFalse(result.isError());
    assertEquals(1.0, result.getOrThrow(ignored -> new IllegalStateException()), 0.00001);
  }

  @Test
  public void convertToPresentation() {
    String expectedConverted = "converted value";
    when(delegate.convertToPresentation(any(), any())).thenReturn(expectedConverted);
    Double value = 1.0;
    String converted = converter.convertToPresentation(value, context);
    verify(delegate).convertToPresentation(value, context);
    assertEquals(expectedConverted, converted);
  }

  @Test
  public void convertToPresentation_Error() {
    when(delegate.convertToPresentation(any(), any())).thenThrow(new IllegalStateException());
    Double value = 1.0;
    try {
      converter.convertToPresentation(value, context);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      verify(delegate).convertToPresentation(value, context);
    }
  }

  @Test
  public void convertToPresentation_ErrorDisabled() {
    component.setEnabled(false);
    when(delegate.convertToPresentation(any(), any())).thenThrow(new IllegalStateException());
    Double value = 1.0;
    try {
      converter.convertToPresentation(value, context);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      verify(delegate).convertToPresentation(value, context);
    }
  }

  @Test
  public void convertToPresentation_ErrorDisabledCustomValue() {
    converter = new IgnoreConversionIfDisabledConverter<>(delegate, 1.0);
    component.setEnabled(false);
    when(delegate.convertToPresentation(any(), any())).thenThrow(new IllegalStateException());
    Double value = 1.0;
    try {
      converter.convertToPresentation(value, context);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      verify(delegate).convertToPresentation(value, context);
    }
  }
}
