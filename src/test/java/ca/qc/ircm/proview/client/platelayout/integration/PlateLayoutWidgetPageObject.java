package ca.qc.ircm.proview.client.platelayout.integration;

import static ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView.COLUMNS;
import static ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView.RESIZE;
import static ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView.ROWS;
import static ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView.STYLE;
import static ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView.STYLE_BUTTON;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.client.platelayout.PlateLayoutWidget;
import ca.qc.ircm.proview.plate.web.test.TestPlateLayoutView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elementsbase.AbstractElement;

public abstract class PlateLayoutWidgetPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(TestPlateLayoutView.VIEW_NAME);
  }

  protected AbstractElement plateLayout() {
    return wrap(AbstractElement.class, findElement(className(PlateLayoutWidget.CLASSNAME)));
  }

  protected int getPlateLayoutColumns() {
    return plateLayout().findElement(tagName("tr")).findElements(tagName("td")).size();
  }

  protected int getPlateLayoutRows() {
    return plateLayout().findElements(tagName("tr")).size();
  }

  protected AbstractElement plateLayoutCell(int column, int row) {
    return wrap(AbstractElement.class,
        plateLayout().findElements(tagName("tr")).get(row).findElements(tagName("td")).get(column));
  }

  protected LabelElement plateLayoutCellLabel(int column, int row) {
    return wrap(AbstractElement.class,
        plateLayout().findElements(tagName("tr")).get(row).findElements(tagName("td")).get(column))
            .$(LabelElement.class).first();
  }

  protected TextFieldElement columnsField() {
    return $(TextFieldElement.class).id(COLUMNS);
  }

  protected void setColumns(int columns) {
    columnsField().setValue(String.valueOf(columns));
  }

  protected TextFieldElement rowsField() {
    return $(TextFieldElement.class).id(ROWS);
  }

  protected void setRows(int rows) {
    rowsField().setValue(String.valueOf(rows));
  }

  protected ButtonElement resizeButton() {
    return $(ButtonElement.class).id(RESIZE);
  }

  protected void clickResize() {
    resizeButton().click();
  }

  protected TextFieldElement styleField() {
    return $(TextFieldElement.class).id(STYLE);
  }

  protected void setStyle(String style) {
    styleField().setValue(style);
  }

  protected ButtonElement styleButton() {
    return $(ButtonElement.class).id(STYLE_BUTTON);
  }

  protected void clickStyle() {
    styleButton().click();
  }
}
