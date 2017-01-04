package ca.qc.ircm.proview.client.platelayout;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface PlateLayoutServerRpc extends ServerRpc {
  /**
   * Column header was clicked.
   *
   * @param column
   *          column index
   * @param mouseDetails
   *          mouse details
   */
  public void columnHeaderClicked(int column, MouseEventDetails mouseDetails);

  /**
   * Row header was clicked.
   *
   * @param row
   *          row index
   * @param mouseDetails
   *          mouse details
   */
  public void rowHeaderClicked(int row, MouseEventDetails mouseDetails);

  /**
   * Well was clicked.
   *
   * @param column
   *          column index
   * @param row
   *          row index
   * @param mouseDetails
   *          mouse details
   */
  public void wellClicked(int column, int row, MouseEventDetails mouseDetails);
}
