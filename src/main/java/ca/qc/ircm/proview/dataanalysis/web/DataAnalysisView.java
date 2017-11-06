package ca.qc.ircm.proview.dataanalysis.web;

import ca.qc.ircm.proview.web.component.SavedSamplesComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Data analysis view.
 */
@SpringView(name = DataAnalysisView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class DataAnalysisView extends CustomComponent implements BaseView, SavedSamplesComponent {
  private static final long serialVersionUID = -6235741301736196511L;
  public static final String VIEW_NAME = "dataanalysis";
  protected DataAnalysisViewDesign design = new DataAnalysisViewDesign();
  @Inject
  private transient DataAnalysisViewPresenter presenter;

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
