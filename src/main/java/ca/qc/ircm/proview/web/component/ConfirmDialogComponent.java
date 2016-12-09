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

package ca.qc.ircm.proview.web.component;

import com.vaadin.ui.Component;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Component that shows confirmation dialog.
 */
public interface ConfirmDialogComponent extends Component {
  public static final String CONFIRM_DIALOG = "confirm-dialog";

  /**
   * Show confirmation dialog.
   *
   * @param windowCaption
   *          window caption
   * @param message
   *          message
   * @param okCaption
   *          caption for "Ok" button
   * @param cancelCaption
   *          caption for "Cancel" button
   * @param listener
   *          confirmation listener
   */
  default void showConfirmDialog(String windowCaption, String message, String okCaption,
      String cancelCaption, ConfirmDialog.Listener listener) {
    showConfirmDialog(windowCaption, message, okCaption, cancelCaption, null, listener);
  }

  /**
   * Show confirmation dialog.
   *
   * @param windowCaption
   *          window caption
   * @param message
   *          message
   * @param okCaption
   *          caption for "Ok" button
   * @param cancelCaption
   *          caption for "Cancel" button
   * @param notOkCaption
   *          caption for "No" button
   * @param listener
   *          confirmation listener
   */
  default void showConfirmDialog(String windowCaption, String message, String okCaption,
      String cancelCaption, String notOkCaption, ConfirmDialog.Listener listener) {
    ConfirmDialog confirmDialog = ConfirmDialog.getFactory().create(windowCaption, message,
        okCaption, cancelCaption, notOkCaption);
    confirmDialog.addStyleName(CONFIRM_DIALOG);
    confirmDialog.show(getUI(), listener, true);
  }
}
