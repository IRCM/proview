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

package ca.qc.ircm.proview.user.web;

import com.vaadin.ui.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Add phone number form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddPhoneNumberWindow extends Window {
  private static final long serialVersionUID = -2176758855631601065L;
  private AddPhoneNumberForm view = new AddPhoneNumberForm();
  @Inject
  private AddPhoneNumberFormPresenter presenter;

  @PostConstruct
  protected void init() {
    presenter.init(view);
    setContent(view);
    presenter.addCancelClickListener(e -> close());
  }

  @Override
  public void attach() {
    super.attach();
    setCaption(view.getResources().message(AddPhoneNumberFormPresenter.HEADER_PROPERTY));
  }

  public AddPhoneNumberFormPresenter getPresenter() {
    return presenter;
  }
}
