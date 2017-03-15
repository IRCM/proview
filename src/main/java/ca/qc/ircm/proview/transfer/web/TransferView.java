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
  @Inject
  protected PlateComponentPresenter sourcePlateFormPresenter;
  @Inject
  protected PlateComponentPresenter destinationPlateFormPresenter;
  protected Menu menu = new Menu();
  protected PlateComponent sourcePlateForm = new PlateComponent();
  protected PlateComponent destinationPlateForm = new PlateComponent();

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    sourcePlateForm.setWidth("100%");
    sourcePlateForm.setPresenter(sourcePlateFormPresenter);
    sourcePlateFormLayout.addComponent(sourcePlateForm);
    destinationPlateForm.setWidth("100%");
    destinationPlateForm.setPresenter(destinationPlateFormPresenter);
    destinationPlateFormLayout.addComponent(destinationPlateForm);
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
