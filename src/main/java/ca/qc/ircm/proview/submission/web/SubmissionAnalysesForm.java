/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
