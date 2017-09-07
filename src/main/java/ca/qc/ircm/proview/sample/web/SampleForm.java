package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.web.component.BaseComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Sample form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Deprecated
public class SampleForm extends SampleFormDesign implements BaseComponent {
  private static final long serialVersionUID = -8171220518398626075L;
  @Inject
  private SampleFormPresenter presenter;

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public SubmissionSample getBean() {
    return presenter.getBean();
  }

  public void setBean(SubmissionSample sample) {
    presenter.setBean(sample);
  }
}
