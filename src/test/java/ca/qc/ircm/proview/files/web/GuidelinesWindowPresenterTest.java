package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesWindowPresenter.TITLE;
import static ca.qc.ircm.proview.files.web.GuidelinesWindowPresenter.WINDOW_STYLE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.CloseWindowOnViewChange.CloseWindowOnViewChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class GuidelinesWindowPresenterTest {
  private GuidelinesWindowPresenter presenter;
  @Mock
  private GuidelinesWindow view;
  @Mock
  private UI ui;
  @Mock
  private Navigator navigator;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(GuidelinesWindow.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new GuidelinesWindowPresenter();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getUI()).thenReturn(ui);
    when(ui.getNavigator()).thenReturn(navigator);
  }

  @Test
  public void styles() {
    presenter.init(view);

    verify(view).addStyleName(WINDOW_STYLE);
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setCaption(resources.message(TITLE));
  }

  @Test
  public void closeWindowOnViewChange() {
    presenter.init(view);

    verify(navigator).addViewChangeListener(any(CloseWindowOnViewChangeListener.class));
  }
}
