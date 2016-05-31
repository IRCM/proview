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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
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
  private Label header;
  private PhoneNumberForm phoneNumberForm;
  private Button saveButton;
  private Button cancelButton;
  @Inject
  private PhoneNumberFormPresenter phoneNumberFormPresenter;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(AddPhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    phoneNumberFormPresenter.init(phoneNumberForm);
    phoneNumberFormPresenter.setEditable(true);
  }

  private void setFields() {
    header = view.getHeader();
    phoneNumberForm = view.getPhoneNumberForm();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message(HEADER_PROPERTY));
    saveButton.setCaption(resources.message(SAVE_PROPERTY));
    cancelButton.setCaption(resources.message(CANCEL_PROPERTY));
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
    saveButton.addClickListener(listener);
  }

  public void removeSaveClickListener(ClickListener listener) {
    saveButton.removeClickListener(listener);
  }

  public void addCancelClickListener(ClickListener listener) {
    cancelButton.addClickListener(listener);
  }

  public void removeCancelClickListener(ClickListener listener) {
    cancelButton.removeClickListener(listener);
  }
}
