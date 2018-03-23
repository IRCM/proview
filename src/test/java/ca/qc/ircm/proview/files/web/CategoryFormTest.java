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

package ca.qc.ircm.proview.files.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class CategoryFormTest {
  private CategoryForm view;
  @Mock
  private CategoryFormPresenter presenter;
  @Mock
  private Category category;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession session;

  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    view = new TestableCategoryForm(presenter);
  }

  @Test
  public void setValue_Attached() {
    view.attach();
    when(ui.getSession()).thenReturn(session);

    view.setValue(category);

    verify(presenter).setValue(category);
  }

  @Test
  public void setValue_NotAttached() {
    view.setValue(category);

    view.attach();

    verify(presenter).setValue(category);
  }

  @SuppressWarnings("serial")
  public class TestableCategoryForm extends CategoryForm {
    TestableCategoryForm(CategoryFormPresenter presenter) {
      super(presenter);
    }

    @Override
    public UI getUI() {
      return ui;
    }
  }
}
