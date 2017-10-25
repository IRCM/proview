package ca.qc.ircm.proview.enrichment.web;

import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Enrichment view.
 */
@SpringView(name = EnrichmentView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class EnrichmentView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "enrichment";
  private static final long serialVersionUID = 4368044044162024836L;
  protected EnrichmentViewDesign design = new EnrichmentViewDesign();
  @Inject
  private EnrichmentViewPresenter presenter;

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
