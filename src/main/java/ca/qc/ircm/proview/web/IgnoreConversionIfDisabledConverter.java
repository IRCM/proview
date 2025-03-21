package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import java.io.Serial;
import org.springframework.lang.Nullable;

/**
 * Ignores conversion to model error, but only if component is disabled.
 */
public class IgnoreConversionIfDisabledConverter<P, M> implements Converter<P, M> {

  @Serial
  private static final long serialVersionUID = -7086544375971670487L;
  private final Converter<P, M> delegate;
  private final M errorValue;

  public IgnoreConversionIfDisabledConverter(Converter<P, M> delegate) {
    this(delegate, null);
  }

  public IgnoreConversionIfDisabledConverter(Converter<P, M> delegate, @Nullable M errorValue) {
    this.delegate = delegate;
    this.errorValue = errorValue;
  }

  @Override
  public Result<M> convertToModel(P value, ValueContext context) {
    Result<M> result = delegate.convertToModel(value, context);
    HasEnabled component = (HasEnabled) context.getComponent().orElseThrow();
    if (component.isEnabled() || !result.isError()) {
      return result;
    } else {
      return Result.ok(errorValue);
    }
  }

  @Override
  public P convertToPresentation(M value, ValueContext context) {
    return delegate.convertToPresentation(value, context);
  }
}
