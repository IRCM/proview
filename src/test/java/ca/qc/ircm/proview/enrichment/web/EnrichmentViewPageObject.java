package ca.qc.ircm.proview.enrichment.web;

import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.COMMENT;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.DOWN;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.HEADER;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.SAVE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class EnrichmentViewPageObject extends AbstractTestBenchTestCase {
  private static final int COMMENT_COLUMN = 2;

  protected void open() {
    openView(EnrichmentView.VIEW_NAME);
  }

  protected void openWithWells() {
    openView(EnrichmentView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(EnrichmentView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement protocolPanel() {
    return wrap(PanelElement.class, findElement(className(PROTOCOL_PANEL)));
  }

  protected ComboBoxElement protocol() {
    return wrap(ComboBoxElement.class, findElement(className(PROTOCOL)));
  }

  protected PanelElement enrichmentsPanel() {
    return wrap(PanelElement.class, findElement(className(ENRICHMENTS_PANEL)));
  }

  protected GridElement enrichments() {
    return wrap(GridElement.class, findElement(className(ENRICHMENTS)));
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        enrichments().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
    field.setValue(comment);
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
