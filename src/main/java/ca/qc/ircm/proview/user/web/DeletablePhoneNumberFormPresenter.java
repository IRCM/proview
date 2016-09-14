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

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button.ClickListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Deletable phone number form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeletablePhoneNumberFormPresenter {
  public static final String DELETE_PROPERTY = "delete";
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private DeletablePhoneNumberForm view;
  @Inject
  private PhoneNumberFormPresenter phoneNumberFormPresenter;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(DeletablePhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    addListeners();
    updateEditable();
    phoneNumberFormPresenter.init(view.phoneNumberForm);
  }

  private void addListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    phoneNumberFormPresenter.setEditable(editable);
    view.deleteButton.setVisible(editable);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.deleteButton.setCaption(resources.message(DELETE_PROPERTY));
  }

  public void commit() throws CommitException {
    phoneNumberFormPresenter.commit();
  }

  public boolean isValid() {
    return phoneNumberFormPresenter.isValid();
  }

  public Item getItemDataSource() {
    return phoneNumberFormPresenter.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    phoneNumberFormPresenter.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }

  public void addDeleteClickListener(ClickListener listener) {
    view.deleteButton.addClickListener(listener);
  }

  public void removeDeleteClickListener(ClickListener listener) {
    view.deleteButton.removeClickListener(listener);
  }
}
