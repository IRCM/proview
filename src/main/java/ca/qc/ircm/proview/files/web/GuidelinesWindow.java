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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.FindbugsExplanations.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;

import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.Window;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Guidelines window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class GuidelinesWindow extends Window implements BaseComponent {
  private static final long serialVersionUID = 9032686080431923743L;
  private GuidelinesWindowDesign design = new GuidelinesWindowDesign();
  @Inject
  private GuidelinesWindowPresenter presenter;
  @Inject
  protected GuidelinesForm guidelinesForm;

  protected GuidelinesWindow() {
  }

  protected GuidelinesWindow(GuidelinesWindowPresenter presenter, GuidelinesForm guidelinesForm) {
    this.presenter = presenter;
    this.guidelinesForm = guidelinesForm;
  }

  @PostConstruct
  protected void init() {
    setContent(design);
    design.guidelinesLayout.addComponent(guidelinesForm);
    setHeight("650px");
    setWidth("500px");
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
