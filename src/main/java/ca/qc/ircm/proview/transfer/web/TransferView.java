package ca.qc.ircm.proview.transfer.web;

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.plate.web.PlateComponentPresenter;
import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.component.SavedSamplesComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Sample transfer view.
 */
@SpringView(name = TransferView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class TransferView extends TransferViewDesign implements BaseView, SavedSamplesComponent {
  public static final String VIEW_NAME = "transfer";
  private static final long serialVersionUID = -4719228370965227442L;
  @Inject
  private TransferViewPresenter presenter;
  protected Menu menu = new Menu();
  @Inject
  protected PlateComponent sourcePlateForm;
  @Inject
  protected PlateComponent destinationPlateForm;
  protected PlateComponentPresenter sourcePlateFormPresenter;
  protected PlateComponentPresenter destinationPlateFormPresenter;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    sourcePlateForm.setWidth("100%");
    sourcePlateFormLayout.addComponent(sourcePlateForm);
    destinationPlateForm.setWidth("100%");
    destinationPlateFormLayout.addComponent(destinationPlateForm);
    sourcePlateFormPresenter = sourcePlateForm.getPresenter();
    destinationPlateFormPresenter = destinationPlateForm.getPresenter();
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
