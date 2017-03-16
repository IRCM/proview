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

package ca.qc.ircm.proview.web.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TableFieldFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ValidatableTableFieldFactoryTest {
  private ValidatableTableFieldFactory factory;
  @Mock
  private TableFieldFactory delegate;
  @Mock
  private Container container;
  @Mock
  private Component uiContext;
  @Captor
  private ArgumentCaptor<DetachListener> detachListenerCaptor;
  private Object itemId = "test";
  private Object propertyId = "test";
  private Object propertyId2 = "test2";

  @Before
  public void beforeTest() {
    factory = new ValidatableTableFieldFactory(delegate);
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void createField() {
    Field delegateField = mock(Field.class);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField);

    Field<?> field = factory.createField(container, itemId, propertyId, uiContext);

    assertSame(delegateField, field);
    verify(delegate).createField(container, itemId, propertyId, uiContext);
    verify(delegateField).addDetachListener(any());
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getFields() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);

    List<Field<?>> fields = factory.getFields();

    assertEquals(2, fields.size());
    assertTrue(fields.contains(delegateField1));
    assertTrue(fields.contains(delegateField2));
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getFields_ByPropertyId() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId2, uiContext);

    List<Field<?>> fields = factory.getFields(propertyId);

    assertEquals(1, fields.size());
    assertTrue(fields.contains(delegateField1));

    fields = factory.getFields(propertyId2);

    assertEquals(1, fields.size());
    assertTrue(fields.contains(delegateField2));
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void isValid_True() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegateField1.isValid()).thenReturn(true);
    when(delegateField2.isValid()).thenReturn(true);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);

    boolean valid = factory.isValid();

    assertTrue(valid);
    verify(delegateField1).isValid();
    verify(delegateField2).isValid();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void isValid_False() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegateField1.isValid()).thenReturn(false);
    when(delegateField2.isValid()).thenReturn(true);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);

    boolean valid = factory.isValid();

    assertFalse(valid);
    verify(delegateField1).isValid();
    verify(delegateField2).isValid();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void isValid_SkipDetached() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegateField1.isValid()).thenReturn(false);
    when(delegateField2.isValid()).thenReturn(true);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);
    verify(delegateField1).addDetachListener(detachListenerCaptor.capture());
    detachListenerCaptor.getValue().detach(mock(DetachEvent.class));

    boolean valid = factory.isValid();

    assertTrue(valid);
    verify(delegateField1, never()).isValid();
    verify(delegateField2).isValid();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void commit() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);

    factory.commit();

    verify(delegateField1).commit();
    verify(delegateField2).commit();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void commit_SkipDetach() {
    Field delegateField1 = mock(Field.class);
    Field delegateField2 = mock(Field.class);
    when(delegate.createField(any(), any(), any(), any())).thenReturn(delegateField1,
        delegateField2);
    factory.createField(container, itemId, propertyId, uiContext);
    factory.createField(container, itemId, propertyId, uiContext);
    verify(delegateField1).addDetachListener(detachListenerCaptor.capture());
    detachListenerCaptor.getValue().detach(mock(DetachEvent.class));

    factory.commit();

    verify(delegateField1, never()).commit();
    verify(delegateField2).commit();
  }
}
