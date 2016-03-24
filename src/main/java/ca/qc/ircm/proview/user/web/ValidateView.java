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
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

import java.util.Locale;

/**
 * Validate users view.
 */
public interface ValidateView extends MessageResourcesView {
  public static final String VIEW_NAME = "user/validate";

  public void setTitle(String title);

  public void showError(String message);

  public void viewUser(User user);

  public void afterSuccessfulValidate(String message);

  public Label getHeaderLabel();

  public Grid getUsersGrid();

  public Button getValidateSelectedButton();

  @Override
  default MessageResource getResources() {
    return MessageResourcesView.super.getResources(ValidateView.class);
  }

  @Override
  default MessageResource getResources(Locale locale) {
    return MessageResourcesView.super.getResources(ValidateView.class, locale);
  }
}
