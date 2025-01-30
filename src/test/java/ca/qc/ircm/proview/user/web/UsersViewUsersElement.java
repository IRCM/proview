package ca.qc.ircm.proview.user.web;

import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link UsersView} users element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = UsersView.USERS)
public class UsersViewUsersElement extends GridElement {

  private static final int EMAIL_COLUMN = 0;
  private static final int LABORATORY_COLUMN = 2;

  public GridTHTDElement emailCell(int row) {
    return getCell(row, EMAIL_COLUMN);
  }

  public GridTHTDElement laboratoryCell(int row) {
    return getCell(row, LABORATORY_COLUMN);
  }

  public String email(int row) {
    return getCell(row, EMAIL_COLUMN).getText();
  }
}
