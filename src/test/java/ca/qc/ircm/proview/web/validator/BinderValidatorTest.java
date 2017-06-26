package ca.qc.ircm.proview.web.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.UserError;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class BinderValidatorTest {
  private BinderValidator binderValidator;
  @Mock
  private Binder<User> binder;
  @Mock
  private BinderValidationStatus<User> binderValidationStatus;
  @Mock
  private Validator<String> validator;
  @Captor
  private ArgumentCaptor<ValueContext> contextCaptor;
  private TextField textField;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    binderValidator = new TestBinderValidator();
    textField = new TextField();
    when(binder.validate()).thenReturn(binderValidationStatus);
  }

  @Test
  public void validate_Binder_Ok() {
    when(binderValidationStatus.isOk()).thenReturn(true);

    boolean valid = binderValidator.validate(binder);

    verify(binderValidationStatus).isOk();
    assertTrue(valid);
  }

  @Test
  public void validate_Binder_Error() {
    when(binderValidationStatus.isOk()).thenReturn(false);

    boolean valid = binderValidator.validate(binder);

    verify(binderValidationStatus).isOk();
    assertFalse(valid);
  }

  @Test
  public void validate_Validator_Ok() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(), ValidationResult.ok(),
        ValidationResult.ok());

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertTrue(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertNull(textField.getComponentError());
  }

  @Test
  public void validate_Validator_Ok_AlreadyUserError() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(), ValidationResult.ok(),
        ValidationResult.ok());
    textField.setComponentError(new UserError("test"));

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertTrue(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof UserError);
    UserError error = (UserError) textField.getComponentError();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test", error.getFormattedHtmlMessage());
  }

  @Test
  public void validate_Validator_Ok_AlreadyCompositeError() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(), ValidationResult.ok(),
        ValidationResult.ok());
    textField.setComponentError(
        new CompositeErrorMessage(new UserError("test"), new UserError("test2")));

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertTrue(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof CompositeErrorMessage);
    CompositeErrorMessage errors = (CompositeErrorMessage) textField.getComponentError();
    Iterator<ErrorMessage> errorsIterator = errors.iterator();
    assertTrue(errorsIterator.hasNext());
    ErrorMessage error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test2", error.getFormattedHtmlMessage());
    assertFalse(errorsIterator.hasNext());
  }

  @Test
  public void validate_Validator_Error() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(),
        ValidationResult.error("invalid b"), ValidationResult.ok());

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertFalse(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof CompositeErrorMessage);
    CompositeErrorMessage errors = (CompositeErrorMessage) textField.getComponentError();
    Iterator<ErrorMessage> errorsIterator = errors.iterator();
    assertTrue(errorsIterator.hasNext());
    ErrorMessage error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;b", error.getFormattedHtmlMessage());
    assertFalse(errorsIterator.hasNext());
  }

  @Test
  public void validate_Validator_Errors() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(),
        ValidationResult.error("invalid b"), ValidationResult.error("invalid c"));

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertFalse(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof CompositeErrorMessage);
    CompositeErrorMessage errors = (CompositeErrorMessage) textField.getComponentError();
    Iterator<ErrorMessage> errorsIterator = errors.iterator();
    assertTrue(errorsIterator.hasNext());
    ErrorMessage error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;b", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;c", error.getFormattedHtmlMessage());
    assertFalse(errorsIterator.hasNext());
  }

  @Test
  public void validate_Validator_Errors_AlreadyUserError() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(),
        ValidationResult.error("invalid b"), ValidationResult.error("invalid c"));
    textField.setComponentError(new UserError("test"));

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertFalse(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof CompositeErrorMessage);
    CompositeErrorMessage errors = (CompositeErrorMessage) textField.getComponentError();
    Iterator<ErrorMessage> errorsIterator = errors.iterator();
    assertTrue(errorsIterator.hasNext());
    ErrorMessage error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;b", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;c", error.getFormattedHtmlMessage());
    assertFalse(errorsIterator.hasNext());
  }

  @Test
  public void validate_Validator_Errors_AlreadyCompositeError() {
    when(validator.apply(any(), any())).thenReturn(ValidationResult.ok(),
        ValidationResult.error("invalid b"), ValidationResult.error("invalid c"));
    textField.setComponentError(
        new CompositeErrorMessage(new UserError("test"), new UserError("test2")));

    boolean valid = binderValidator.validate(validator, textField, Arrays.asList("a", "b", "c"));

    assertFalse(valid);
    verify(validator).apply(eq("a"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("b"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    verify(validator).apply(eq("c"), contextCaptor.capture());
    assertEquals(Optional.of(textField), contextCaptor.getValue().getComponent());
    assertTrue(textField.getComponentError() instanceof CompositeErrorMessage);
    CompositeErrorMessage errors = (CompositeErrorMessage) textField.getComponentError();
    Iterator<ErrorMessage> errorsIterator = errors.iterator();
    assertTrue(errorsIterator.hasNext());
    ErrorMessage error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("test2", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;b", error.getFormattedHtmlMessage());
    assertTrue(errorsIterator.hasNext());
    error = errorsIterator.next();
    assertTrue(error instanceof UserError);
    assertEquals(ErrorLevel.ERROR, error.getErrorLevel());
    assertEquals("invalid&#32;c", error.getFormattedHtmlMessage());
    assertFalse(errorsIterator.hasNext());
  }

  @Test
  @Ignore("Needs a good way to test logging")
  public void logValidation() {
  }

  private static class TestBinderValidator implements BinderValidator {
  }
}
