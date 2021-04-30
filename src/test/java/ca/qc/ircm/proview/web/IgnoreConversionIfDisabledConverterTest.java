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

package ca.qc.ircm.proview.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link IgnoreConversionIfDisabledConverter}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class IgnoreConversionIfDisabledConverterTest {
  private IgnoreConversionIfDisabledConverter<String, Double> converter;
  @Mock
  private Converter<String, Double> delegate;
  @Mock
  private ValueContext context;
  private TextField component = new TextField();
  private String errorMessage = "error message";

  @Before
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
