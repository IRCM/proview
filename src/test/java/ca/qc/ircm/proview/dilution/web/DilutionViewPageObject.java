package ca.qc.ircm.proview.dilution.web;

import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DILUTIONS;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DILUTIONS_PANEL;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DOWN;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.HEADER;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SAVE;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOLVENT;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOURCE_VOLUME;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextFieldElement;

import java.util.Objects;

public class DilutionViewPageObject extends AbstractTestBenchTestCase {
  private static final int SOURCE_VOLUME_COLUMN = 2;
  private static final int SOLVENT_COLUMN = 3;
  private static final int SOLVENT_VOLUME_COLUMN = 4;

  protected void open() {
    openView(DilutionView.VIEW_NAME);
  }

  protected void openWithWells() {
    openView(DilutionView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(DilutionView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement dilutionsPanel() {
    return wrap(PanelElement.class, findElement(className(DILUTIONS_PANEL)));
  }

  protected GridElement dilutions() {
    return wrap(GridElement.class, findElement(className(DILUTIONS)));
  }

  protected void setSourceVolume(int row, Double sourceVolume) {
    TextFieldElement field = wrap(TextFieldElement.class, dilutions().getRow(row)
        .getCell(SOURCE_VOLUME_COLUMN).findElement(className(SOURCE_VOLUME)));
    field.setValue(Objects.toString(sourceVolume, ""));
  }

  protected void setSolvent(int row, String solvent) {
    TextFieldElement field = wrap(TextFieldElement.class,
        dilutions().getRow(row).getCell(SOLVENT_COLUMN).findElement(className(SOLVENT)));
    field.setValue(solvent);
  }

  protected void setSolventVolume(int row, Double solventVolume) {
    TextFieldElement field = wrap(TextFieldElement.class, dilutions().getRow(row)
        .getCell(SOLVENT_VOLUME_COLUMN).findElement(className(SOLVENT_VOLUME)));
    field.setValue(Objects.toString(solventVolume, ""));
  }

  protected ButtonElement down() {
    return wrap(ButtonElement.class, findElement(className(DOWN)));
  }

  protected void clickDown() {
    down().click();
  }

  protected ButtonElement save() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    save().click();
  }
}
