package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.ADD;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link UsersView} users element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = UsersView.USERS)
public class UsersViewUsersElement extends GridElement {
  private static final int EMAIL_COLUMN = 1;
  private static final int LABORATORY_COLUMN = 3;
  private static final int EDIT_COLUMN = 0;

  public GridTHTDElement emailCell(int row) {
    return getCell(row, EMAIL_COLUMN);
  }

  public GridTHTDElement laboratoryCell(int row) {
    return getCell(row, LABORATORY_COLUMN);
  }

  public String email(int row) {
    return getCell(row, EMAIL_COLUMN).getText();
  }

  public ButtonElement edit(int row) {
    return getCell(row, EDIT_COLUMN).$(ButtonElement.class).first();
  }

  public DivElement switchFailed() {
    return $(DivElement.class).id(SWITCH_FAILED);
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public ButtonElement switchUser() {
    return $(ButtonElement.class).id(SWITCH_USER);
  }

  public UserDialogElement dialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(UserDialog.ID)))
        .wrap(UserDialogElement.class);
  }

  public LaboratoryDialogElement laboratoryDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(LaboratoryDialog.ID)))
        .wrap(LaboratoryDialogElement.class);
  }
}
