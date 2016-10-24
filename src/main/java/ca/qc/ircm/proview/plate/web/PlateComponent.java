package ca.qc.ircm.proview.plate.web;

import com.vaadin.ui.CustomComponent;

/**
 * Plate component that allows selection and drag and drop.
 */
public class PlateComponent extends CustomComponent {
  private static final long serialVersionUID = -5886354033312877270L;
  private PlateComponentPresenter presenter;
  protected PlateLayout plateLayout;

  public PlateComponent() {
    plateLayout = new PlateLayout();
    setCompositionRoot(plateLayout);
  }

  public void setPresenter(PlateComponentPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
