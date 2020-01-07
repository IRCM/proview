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

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * Ignores conversion to model error, but only if component is disabled.
 */
public class IgnoreConversionIfDisabledConverter<P, M> implements Converter<P, M> {
  private static final long serialVersionUID = -7086544375971670487L;
  private Converter<P, M> delegate;
  private M errorValue;

  public IgnoreConversionIfDisabledConverter(Converter<P, M> delegate) {
    this(delegate, null);
  }

  public IgnoreConversionIfDisabledConverter(Converter<P, M> delegate, M errorValue) {
    this.delegate = delegate;
    this.errorValue = errorValue;
  }

  @Override
  public Result<M> convertToModel(P value, ValueContext context) {
    Result<M> result = delegate.convertToModel(value, context);
    HasEnabled component = (HasEnabled) context.getComponent().get();
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
