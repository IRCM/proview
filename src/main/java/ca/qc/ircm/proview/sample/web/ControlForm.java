package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Control form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControlForm extends ControlFormDesign implements BaseComponent {
  private static final long serialVersionUID = 5637791915365273858L;
  @Inject
  private transient ControlFormPresenter presenter;

  protected ControlForm() {
  }

  protected ControlForm(ControlFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Registration addSaveListener(SaveListener<Control> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(Control control) {
    fireEvent(new SaveEvent<>(this, control));
  }

  public ControlFormPresenter getPresenter() {
    return presenter;
  }
}
