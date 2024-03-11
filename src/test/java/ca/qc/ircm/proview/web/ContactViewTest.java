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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ContactView.ADDRESS;
import static ca.qc.ircm.proview.web.ContactView.HEADER;
import static ca.qc.ircm.proview.web.ContactView.ID;
import static ca.qc.ircm.proview.web.ContactView.LINK;
import static ca.qc.ircm.proview.web.ContactView.NAME;
import static ca.qc.ircm.proview.web.ContactView.PHONE;
import static ca.qc.ircm.proview.web.ContactView.PROTEOMIC;
import static ca.qc.ircm.proview.web.ContactView.WEBSITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link ContactView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ContactViewTest extends SpringUIUnitTest {
  private ContactView view;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ContactView.class, locale);
  private AppResources generalResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(ContactView.class);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(styleName(PROTEOMIC, HEADER), view.proteomicHeader.getId().orElse(""));
    assertEquals(styleName(PROTEOMIC, NAME), view.proteomicNameAnchor.getId().orElse(""));
    assertEquals(styleName(PROTEOMIC, ADDRESS), view.proteomicAddressAnchor.getId().orElse(""));
    assertEquals(styleName(PROTEOMIC, PHONE), view.proteomicPhoneAnchor.getId().orElse(""));
    assertEquals(styleName(WEBSITE, HEADER), view.websiteHeader.getId().orElse(""));
    assertEquals(styleName(WEBSITE, NAME), view.websiteNameAnchor.getId().orElse(""));
    assertEquals(styleName(WEBSITE, ADDRESS), view.websiteAddressAnchor.getId().orElse(""));
    assertEquals(styleName(WEBSITE, PHONE), view.websitePhoneAnchor.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(resources.message(PROTEOMIC), view.proteomicHeader.getText());
    assertEquals(resources.message(property(PROTEOMIC, NAME)), view.proteomicName.getText());
    assertEquals(resources.message(property(PROTEOMIC, ADDRESS)), view.proteomicAddress.getText());
    assertEquals(resources.message(property(PROTEOMIC, PHONE)), view.proteomicPhone.getText());
    assertEquals(resources.message(WEBSITE), view.websiteHeader.getText());
    assertEquals(resources.message(property(WEBSITE, NAME)), view.websiteName.getText());
    assertEquals(resources.message(property(WEBSITE, ADDRESS)), view.websiteAddress.getText());
    assertEquals(resources.message(property(WEBSITE, PHONE)), view.websitePhone.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(ContactView.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(resources.message(PROTEOMIC), view.proteomicHeader.getText());
    assertEquals(resources.message(property(PROTEOMIC, NAME)), view.proteomicName.getText());
    assertEquals(resources.message(property(PROTEOMIC, ADDRESS)), view.proteomicAddress.getText());
    assertEquals(resources.message(property(PROTEOMIC, PHONE)), view.proteomicPhone.getText());
    assertEquals(resources.message(WEBSITE), view.websiteHeader.getText());
    assertEquals(resources.message(property(WEBSITE, NAME)), view.websiteName.getText());
    assertEquals(resources.message(property(WEBSITE, ADDRESS)), view.websiteAddress.getText());
    assertEquals(resources.message(property(WEBSITE, PHONE)), view.websitePhone.getText());
  }

  @Test
  public void icons() {
    validateIcon(VaadinIcon.ENVELOPE.create(),
        test(view.proteomicNameAnchor).find(Icon.class).first());
    validateIcon(VaadinIcon.MAP_MARKER.create(),
        test(view.proteomicAddressAnchor).find(Icon.class).first());
    validateIcon(VaadinIcon.PHONE.create(),
        test(view.proteomicPhoneAnchor).find(Icon.class).first());
    validateIcon(VaadinIcon.ENVELOPE.create(),
        test(view.websiteNameAnchor).find(Icon.class).first());
    validateIcon(VaadinIcon.MAP_MARKER.create(),
        test(view.websiteAddressAnchor).find(Icon.class).first());
    validateIcon(VaadinIcon.PHONE.create(), test(view.websitePhoneAnchor).find(Icon.class).first());
  }

  @Test
  public void hrefs() {
    assertEquals(resources.message(property(PROTEOMIC, NAME, LINK)),
        view.proteomicNameAnchor.getHref());
    assertEquals(resources.message(property(PROTEOMIC, ADDRESS, LINK)),
        view.proteomicAddressAnchor.getHref());
    assertEquals(resources.message(property(PROTEOMIC, PHONE, LINK)),
        view.proteomicPhoneAnchor.getHref());
    assertEquals(resources.message(property(WEBSITE, NAME, LINK)),
        view.websiteNameAnchor.getHref());
    assertEquals(resources.message(property(WEBSITE, ADDRESS, LINK)),
        view.websiteAddressAnchor.getHref());
    assertEquals(resources.message(property(WEBSITE, PHONE, LINK)),
        view.websitePhoneAnchor.getHref());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, generalResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }
}
