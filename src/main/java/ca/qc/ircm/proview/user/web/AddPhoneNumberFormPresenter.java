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
import com.vaadin.ui.Button.ClickListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Add phone number form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddPhoneNumberFormPresenter {
  public static final String HEADER_PROPERTY = "header";
  public static final String SAVE_PROPERTY = "save";
  public static final String CANCEL_PROPERTY = "cancel";
  private AddPhoneNumberForm view;
  @Inject
  private PhoneNumberFormPresenter phoneNumberFormPresenter;

  public AddPhoneNumberFormPresenter() {
  }

  protected AddPhoneNumberFormPresenter(PhoneNumberFormPresenter phoneNumberFormPresenter) {
    this.phoneNumberFormPresenter = phoneNumberFormPresenter;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(AddPhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    phoneNumberFormPresenter.init(view.phoneNumberForm);
    phoneNumberFormPresenter.setEditable(true);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.header.setValue(resources.message(HEADER_PROPERTY));
    view.saveButton.setCaption(resources.message(SAVE_PROPERTY));
    view.cancelButton.setCaption(resources.message(CANCEL_PROPERTY));
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

  public void addSaveClickListener(ClickListener listener) {
    view.saveButton.addClickListener(listener);
  }

  public void removeSaveClickListener(ClickListener listener) {
    view.saveButton.removeClickListener(listener);
  }

  public void addCancelClickListener(ClickListener listener) {
    view.cancelButton.addClickListener(listener);
  }

  public void removeCancelClickListener(ClickListener listener) {
    view.cancelButton.removeClickListener(listener);
  }
}
