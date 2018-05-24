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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.FindbugsExplanations.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;
import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Sample selection window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class ContainerSelectionWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "samples-selection-window";
  public static final String TITLE = "title";
  private static final long serialVersionUID = 988315877226604037L;
  private static final Logger logger = LoggerFactory.getLogger(ContainerSelectionWindow.class);
  protected ContainerSelectionWindowDesign design = new ContainerSelectionWindowDesign();
  @Inject
  private ContainerSelectionForm view;

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    setContent(design);
    design.selectionLayout.addComponent(view);
    setHeight("700px");
    setWidth("1200px");
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Container selection window");
    setCaption(getResources().message(TITLE));
    view.addSaveListener(e -> close());
    closeWindowOnViewChange(this);
  }

  public Registration addSaveListener(SaveListener<List<SampleContainer>> listener) {
    return view.addSaveListener(listener);
  }

  public List<Sample> getSamples() {
    return view.getSamples();
  }

  /**
   * Sets selected samples.
   *
   * @param samples
   *          selected samples
   */
  public void setSamples(List<Sample> samples) {
    if (isAttached()) {
      view.setSamples(samples);
    } else {
      addAttachListener(e -> view.setSamples(samples));
    }
  }
}
