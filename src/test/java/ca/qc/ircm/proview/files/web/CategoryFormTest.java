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
    public TestableCategoryForm(CategoryFormPresenter presenter) {
      super(presenter);
    }

    @Override
    public UI getUI() {
      return ui;
    }
  }
}
