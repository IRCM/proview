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

package ca.qc.ircm.proview.web.integration;

import static ca.qc.ircm.proview.web.ContactViewPresenter.ADDRESS;
import static ca.qc.ircm.proview.web.ContactViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.ContactViewPresenter.NAME;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PHONE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PROTEOMIC;
import static ca.qc.ircm.proview.web.ContactViewPresenter.WEBSITE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.web.ContactView;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.testbench.elements.PanelElement;

public abstract class ContactPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(ContactView.VIEW_NAME);
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement proteomicPanel() {
    return wrap(PanelElement.class, findElement(className(PROTEOMIC)));
  }

  protected LinkElement proteomicNameLink() {
    return wrap(LinkElement.class, findElement(className(PROTEOMIC + "-" + NAME)));
  }

  protected LinkElement proteomicAddressLink() {
    return wrap(LinkElement.class, findElement(className(PROTEOMIC + "-" + ADDRESS)));
  }

  protected LinkElement proteomicPhoneLink() {
    return wrap(LinkElement.class, findElement(className(PROTEOMIC + "-" + PHONE)));
  }

  protected PanelElement websitePanel() {
    return wrap(PanelElement.class, findElement(className(WEBSITE)));
  }

  protected LinkElement websiteNameLink() {
    return wrap(LinkElement.class, findElement(className(WEBSITE + "-" + NAME)));
  }

  protected LinkElement websiteAddressLink() {
    return wrap(LinkElement.class, findElement(className(WEBSITE + "-" + ADDRESS)));
  }

  protected LinkElement websitePhoneLink() {
    return wrap(LinkElement.class, findElement(className(WEBSITE + "-" + PHONE)));
  }
}
