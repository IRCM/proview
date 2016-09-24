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
import com.vaadin.ui.Notification;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup;

/**
 * Submission form.
 */
public class SubmissionForm extends SubmissionFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  private SubmissionFormPresenter presenter;
  protected FlexibleOptionGroup digestionFlexibleOptions = new FlexibleOptionGroup();
  protected FlexibleOptionGroup proteinIdentificationFlexibleOptions = new FlexibleOptionGroup();

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }
}
