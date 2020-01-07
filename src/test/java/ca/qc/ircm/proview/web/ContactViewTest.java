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

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
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
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ContactViewTest extends AbstractViewTestCase {
  private ContactView view;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ContactView.class, locale);
  private AppResources generalResources = new AppResources(WebConstants.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new ContactView();
    view.init();
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
    view.localeChange(mock(LocaleChangeEvent.class));
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
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(ContactView.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
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
        findChild(view.proteomicNameAnchor, Icon.class).get());
    validateIcon(VaadinIcon.MAP_MARKER.create(),
        findChild(view.proteomicAddressAnchor, Icon.class).get());
    validateIcon(VaadinIcon.PHONE.create(), findChild(view.proteomicPhoneAnchor, Icon.class).get());
    validateIcon(VaadinIcon.ENVELOPE.create(), findChild(view.websiteNameAnchor, Icon.class).get());
    validateIcon(VaadinIcon.MAP_MARKER.create(),
        findChild(view.websiteAddressAnchor, Icon.class).get());
    validateIcon(VaadinIcon.PHONE.create(), findChild(view.websitePhoneAnchor, Icon.class).get());
  }

  @Test
  public void hrefs() {
    view.localeChange(mock(LocaleChangeEvent.class));
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
