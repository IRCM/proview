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

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * User form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = -7630525674289902028L;
  protected UserFormDesign design = new UserFormDesign();
  @Inject
  private transient UserFormPresenter presenter;

  protected UserForm() {
  }

  protected UserForm(UserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  public void init() {
    setSizeFull();
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    design.phoneNumbersLayout.removeAllComponents();
    presenter.init(this);
  }

  public Registration addSaveListener(SaveListener<User> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(User user) {
    fireEvent(new SaveEvent<>(this, user));
  }

  public User getValue() {
    return presenter.getValue();
  }

  public void setValue(User user) {
    presenter.setValue(user);
  }

  @Override
  public boolean isReadOnly() {
    return presenter.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    presenter.setReadOnly(readOnly);
  }
}
