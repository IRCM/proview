package ca.qc.ircm.proview.msanalysis.web;

import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Dilution view.
 */
@SpringView(name = MsAnalysisView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class MsAnalysisView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "msanalysis";
  private static final long serialVersionUID = 6117296449537842206L;
  protected MsAnalysisViewDesign design = new MsAnalysisViewDesign();
  @Inject
  private transient MsAnalysisViewPresenter presenter;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
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
