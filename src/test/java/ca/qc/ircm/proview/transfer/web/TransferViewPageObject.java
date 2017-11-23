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

package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_WELL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DOWN;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.HEADER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAVE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TEST;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import org.openqa.selenium.WebElement;

public abstract class TransferViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(TransferView.VIEW_NAME);
  }

  protected void openWithWells() {
    openView(TransferView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(TransferView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement transferTypePanel() {
    return wrap(PanelElement.class, findElement(className(TRANSFER_TYPE_PANEL)));
  }

  protected RadioButtonGroupElement transferType() {
    return wrap(RadioButtonGroupElement.class, findElement(className(TRANSFER_TYPE)));
  }

  protected void setTransferType(SampleContainerType type) {
    transferType().selectByText(type.getLabel(currentLocale()));
  }

  protected PanelElement transfersPanel() {
    return wrap(PanelElement.class, findElement(className(TRANSFERS_PANEL)));
  }

  protected GridElement transfers() {
    return wrap(GridElement.class, findElement(className(TRANSFERS)));
  }

  protected void setDestinationTube(int row, String name) {
    ComboBoxElement field = wrap(ComboBoxElement.class,
        transfers().getRow(row).getCell(2).findElement(className(DESTINATION_TUBE)));
    field.selectByText(name);
    transfers().focus();
  }

  protected void setDestinationWell(int row, String name) {
    ComboBoxElement field = wrap(ComboBoxElement.class,
        transfers().getRow(row).getCell(2).findElement(className(DESTINATION_WELL)));
    field.selectByText(name);
    transfers().focus();
  }

  protected ButtonElement down() {
    return wrap(ButtonElement.class, findElement(className(DOWN)));
  }

  protected PanelElement destinationPanel() {
    return wrap(PanelElement.class, findElement(className(DESTINATION)));
  }

  protected ComboBoxElement destinationPlates() {
    return wrap(ComboBoxElement.class, findElement(className(DESTINATION_PLATES)));
  }

  protected void setDestinationPlate(String name) {
    destinationPlates().selectByText(name);
    transferType().focus();
  }

  protected PanelElement destinationPlatePanel() {
    return wrap(PanelElement.class, findElement(className(DESTINATION_PLATE_PANEL)));
  }

  protected WebElement destinationPlate() {
    return findElement(className(DESTINATION_PLATE)).findElement(className("v-spreadsheet"));
  }

  protected void selectDestinationPlateCell(int row, int column) {
    destinationPlate().findElements(className("row" + (row + 1))).stream()
        .filter(element -> element.getAttribute("class").contains("col" + (column + 1)))
        .forEach(element -> element.click());
  }

  protected ButtonElement test() {
    return wrap(ButtonElement.class, findElement(className(TEST)));
  }

  protected ButtonElement save() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    save().click();
  }
}
