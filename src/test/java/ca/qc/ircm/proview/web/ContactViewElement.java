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

/**
 * {@link ContactView} element.
 */
@Element("vaadin-vertical-layout")
public class ContactViewElement extends VerticalLayoutElement {
  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public H3Element proteomicHeader() {
    return $(H3Element.class).id(styleName(PROTEOMIC, HEADER));
  }

  public AnchorElement proteomicName() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, NAME));
  }

  public AnchorElement proteomicAddress() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, ADDRESS));
  }

  public AnchorElement proteomicPhone() {
    return $(AnchorElement.class).id(styleName(PROTEOMIC, PHONE));
  }

  public H3Element websiteHeader() {
    return $(H3Element.class).id(styleName(WEBSITE, HEADER));
  }

  public AnchorElement websiteName() {
    return $(AnchorElement.class).id(styleName(WEBSITE, NAME));
  }

  public AnchorElement websiteAddress() {
    return $(AnchorElement.class).id(styleName(WEBSITE, ADDRESS));
  }

  public AnchorElement websitePhone() {
    return $(AnchorElement.class).id(styleName(WEBSITE, PHONE));
  }
}
