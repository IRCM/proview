package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.HistoryView.ACTIVITIES;

import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link HistoryView#activities HistoryView activities} element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = ACTIVITIES)
public class HistoryViewActivitiesElement extends GridElement {
}
