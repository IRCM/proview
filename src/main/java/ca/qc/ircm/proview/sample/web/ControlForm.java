package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.web.component.BaseComponent;
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

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public ControlFormPresenter getPresenter() {
    return presenter;
  }
}
