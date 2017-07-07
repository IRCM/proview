package ca.qc.ircm.proview.sample.web;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Sample view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleViewPresenter {
  private SampleView view;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleView view) {
    this.view = view;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          parameters
   */
  public void enter(String parameters) {
  }
}
