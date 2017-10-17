package ca.qc.ircm.proview.enrichment.web;

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

public class EnrichmentViewPageObject extends AbstractTestBenchTestCase {
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

  protected ButtonElement save() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    save().click();
  }
}
