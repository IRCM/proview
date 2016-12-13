package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.web.component.BaseComponent;

/**
 * Sample selection form.
 */
public class SampleSelectionForm extends SampleSelectionFormDesign implements BaseComponent {
  private static final long serialVersionUID = -2890553778973734044L;
  private SampleSelectionFormPresenter presenter;

  public void setPresenter(SampleSelectionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
