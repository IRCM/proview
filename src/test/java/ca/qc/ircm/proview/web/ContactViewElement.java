package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ContactView.ADDRESS;
import static ca.qc.ircm.proview.web.ContactView.HEADER;
import static ca.qc.ircm.proview.web.ContactView.NAME;
import static ca.qc.ircm.proview.web.ContactView.PHONE;
import static ca.qc.ircm.proview.web.ContactView.PROTEOMIC;
import static ca.qc.ircm.proview.web.ContactView.WEBSITE;

import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-vertical-layout")
public class ContactViewElement extends VerticalLayoutElement {
  protected H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  protected H3Element proteomicHeader() {
    return $(H3Element.class).id(styleName(PROTEOMIC, HEADER));
  }

  protected AnchorElement proteomicName() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, NAME));
  }

  protected AnchorElement proteomicAddress() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, ADDRESS));
  }

  protected AnchorElement proteomicPhone() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, PHONE));
  }

  protected H3Element websiteHeader() {
    return $(H3Element.class).id(styleName(WEBSITE, HEADER));
  }

  protected AnchorElement websiteName() {
    return $(AnchorElement.class).id(styleName(WEBSITE, NAME));
  }

  protected AnchorElement websiteAddress() {
    return $(AnchorElement.class).id(styleName(WEBSITE, ADDRESS));
  }

  protected AnchorElement websitePhone() {
    return $(AnchorElement.class).id(styleName(WEBSITE, PHONE));
  }
}
