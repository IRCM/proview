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

import static ca.qc.ircm.proview.web.MainViewPresenter.ANALYSES;
import static ca.qc.ircm.proview.web.MainViewPresenter.BIOMARKER_SITE;
import static ca.qc.ircm.proview.web.MainViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.MainViewPresenter.RESPONSABILITIES;
import static ca.qc.ircm.proview.web.MainViewPresenter.SERVICES;
import static ca.qc.ircm.proview.web.MainViewPresenter.SERVICES_DESCRIPTION;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGNIN;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.LinkElement;

public abstract class MainPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MainView.VIEW_NAME);
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement servicesDescription() {
    return wrap(LabelElement.class, findElement(className(SERVICES_DESCRIPTION)));
  }

  protected LabelElement services() {
    return wrap(LabelElement.class, findElement(className(SERVICES)));
  }

  protected LinkElement biomarkerSite() {
    return wrap(LinkElement.class, findElement(className(BIOMARKER_SITE)));
  }

  protected void clickBiomarkerSite() {
    biomarkerSite().click();
  }

  protected LabelElement analyses() {
    return wrap(LabelElement.class, findElement(className(ANALYSES)));
  }

  protected LabelElement responsabilities() {
    return wrap(LabelElement.class, findElement(className(RESPONSABILITIES)));
  }

  protected ButtonElement signin() {
    return wrap(ButtonElement.class, findElement(className(SIGNIN)));
  }

  protected void clickSignin() {
    signin().click();
  }
}
