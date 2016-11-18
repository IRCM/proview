package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Submission results form.
 */
public class SubmissionAnalysesForm extends SubmissionAnalysesFormDesign
    implements MessageResourcesComponent {
  private static final long serialVersionUID = 3301905236780067587L;
  private SubmissionAnalysesFormPresenter presenter;
  protected List<Panel> analysisPanels = new ArrayList<>();
  protected List<Grid> acquisitionsGrids = new ArrayList<>();

  public void setPresenter(SubmissionAnalysesFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    removeAllComponents();
    presenter.init(this);
  }

  /**
   * Creates a panel containing a grid.
   *
   * @return panel that was created
   */
  public Panel createAnalysisPanel() {
    Panel panel = new Panel();
    addComponent(panel);
    analysisPanels.add(panel);
    VerticalLayout layout = new VerticalLayout();
    panel.setContent(layout);
    Grid grid = new Grid();
    grid.setWidth("100%");
    layout.addComponent(grid);
    acquisitionsGrids.add(grid);
    return panel;
  }
}
