package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.user.web.UsersView.ADD;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_LABORATORY;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link UsersView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = UsersView.ID)
public class UsersViewElement extends VerticalLayoutElement {

  public UsersViewUsersElement users() {
    return $(UsersViewUsersElement.class).first();
  }

  public DivElement switchFailed() {
    return $(DivElement.class).id(SWITCH_FAILED);
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public ButtonElement edit() {
    return $(ButtonElement.class).id(EDIT);
  }

  public ButtonElement switchUser() {
    return $(ButtonElement.class).id(SWITCH_USER);
  }

  public ButtonElement viewLaboratory() {
    return $(ButtonElement.class).id(VIEW_LABORATORY);
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
