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

import static ca.qc.ircm.proview.web.ContactViewPresenter.ADDRESS;
import static ca.qc.ircm.proview.web.ContactViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.ContactViewPresenter.NAME;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PHONE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PROTEOMIC;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PROTEOMIC_ADDRESS_RESOURCE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PROTEOMIC_EMAIL_RESOURCE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.PROTEOMIC_PHONE_RESOURCE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.TITLE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.WEBSITE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.WEBSITE_ADDRESS_RESOURCE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.WEBSITE_EMAIL_RESOURCE;
import static ca.qc.ircm.proview.web.ContactViewPresenter.WEBSITE_PHONE_RESOURCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ContactViewPresenterTest {
  private ContactViewPresenter presenter;
  @Mock
  private ContactView view;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ContactView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ContactViewPresenter(applicationName);
    view.menu = new Menu();
    view.headerLabel = new Label();
    view.proteomicContactPanel = new Panel();
    view.proteomicContactNameLink = new Link();
    view.proteomicContactAddressLink = new Link();
    view.proteomicContactPhoneLink = new Link();
    view.websiteContactPanel = new Panel();
    view.websiteContactNameLink = new Link();
    view.websiteContactAddressLink = new Link();
    view.websiteContactPhoneLink = new Link();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.proteomicContactPanel.getStyleName().contains(PROTEOMIC));
    assertTrue(view.proteomicContactNameLink.getStyleName().contains(PROTEOMIC + "-" + NAME));
    assertTrue(view.proteomicContactAddressLink.getStyleName().contains(PROTEOMIC + "-" + ADDRESS));
    assertTrue(view.proteomicContactPhoneLink.getStyleName().contains(PROTEOMIC + "-" + PHONE));
    assertTrue(view.websiteContactPanel.getStyleName().contains(WEBSITE));
    assertTrue(view.websiteContactNameLink.getStyleName().contains(WEBSITE + "-" + NAME));
    assertTrue(view.websiteContactAddressLink.getStyleName().contains(WEBSITE + "-" + ADDRESS));
    assertTrue(view.websiteContactPhoneLink.getStyleName().contains(WEBSITE + "-" + PHONE));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(PROTEOMIC), view.proteomicContactPanel.getCaption());
    assertEquals(resources.message(PROTEOMIC + "." + NAME),
        view.proteomicContactNameLink.getCaption());
    assertEquals(FontAwesome.ENVELOPE, view.proteomicContactNameLink.getIcon());
    assertEquals(resources.message(PROTEOMIC + "." + ADDRESS),
        view.proteomicContactAddressLink.getCaption());
    assertTrue(view.proteomicContactAddressLink.isCaptionAsHtml());
    assertEquals(FontAwesome.MAP_MARKER, view.proteomicContactAddressLink.getIcon());
    assertEquals(resources.message(PROTEOMIC + "." + PHONE),
        view.proteomicContactPhoneLink.getCaption());
    assertEquals(FontAwesome.PHONE, view.proteomicContactPhoneLink.getIcon());
    assertEquals(resources.message(WEBSITE), view.websiteContactPanel.getCaption());
    assertEquals(resources.message(WEBSITE + "." + NAME), view.websiteContactNameLink.getCaption());
    assertEquals(FontAwesome.ENVELOPE, view.websiteContactNameLink.getIcon());
    assertEquals(resources.message(WEBSITE + "." + ADDRESS),
        view.websiteContactAddressLink.getCaption());
    assertTrue(view.websiteContactAddressLink.isCaptionAsHtml());
    assertEquals(FontAwesome.MAP_MARKER, view.websiteContactAddressLink.getIcon());
    assertEquals(resources.message(WEBSITE + "." + PHONE),
        view.websiteContactPhoneLink.getCaption());
    assertEquals(FontAwesome.PHONE, view.websiteContactPhoneLink.getIcon());
  }

  @Test
  public void resources() {
    assertTrue(view.proteomicContactNameLink.getResource() instanceof ExternalResource);
    ExternalResource resource = (ExternalResource) view.proteomicContactNameLink.getResource();
    assertEquals(PROTEOMIC_EMAIL_RESOURCE, resource.getURL());
    assertTrue(view.proteomicContactAddressLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) view.proteomicContactAddressLink.getResource();
    assertEquals(PROTEOMIC_ADDRESS_RESOURCE, resource.getURL());
    assertTrue(view.proteomicContactPhoneLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) view.proteomicContactPhoneLink.getResource();
    assertEquals(PROTEOMIC_PHONE_RESOURCE, resource.getURL());
    assertTrue(view.websiteContactNameLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) view.websiteContactNameLink.getResource();
    assertEquals(WEBSITE_EMAIL_RESOURCE, resource.getURL());
    assertTrue(view.websiteContactAddressLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) view.websiteContactAddressLink.getResource();
    assertEquals(WEBSITE_ADDRESS_RESOURCE, resource.getURL());
    assertTrue(view.websiteContactPhoneLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) view.websiteContactPhoneLink.getResource();
    assertEquals(WEBSITE_PHONE_RESOURCE, resource.getURL());
  }
}
