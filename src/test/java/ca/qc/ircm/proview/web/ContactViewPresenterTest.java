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

import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.styleName;
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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.themes.ValoTheme;
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
  private ContactViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ContactView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ContactViewPresenter(applicationName);
    design = new ContactViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  @Test
  public void styles() {
    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.proteomicContactPanel.getStyleName().contains(PROTEOMIC));
    assertTrue(design.proteomicContactNameLink.getStyleName().contains(styleName(PROTEOMIC, NAME)));
    assertTrue(
        design.proteomicContactAddressLink.getStyleName().contains(styleName(PROTEOMIC, ADDRESS)));
    assertTrue(
        design.proteomicContactPhoneLink.getStyleName().contains(styleName(PROTEOMIC, PHONE)));
    assertTrue(design.websiteContactPanel.getStyleName().contains(WEBSITE));
    assertTrue(design.websiteContactNameLink.getStyleName().contains(styleName(WEBSITE, NAME)));
    assertTrue(
        design.websiteContactAddressLink.getStyleName().contains(styleName(WEBSITE, ADDRESS)));
    assertTrue(design.websiteContactPhoneLink.getStyleName().contains(styleName(WEBSITE, PHONE)));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    assertEquals(resources.message(PROTEOMIC), design.proteomicContactPanel.getCaption());
    assertEquals(resources.message(property(PROTEOMIC, NAME)),
        design.proteomicContactNameLink.getCaption());
    assertEquals(VaadinIcons.ENVELOPE, design.proteomicContactNameLink.getIcon());
    assertEquals(resources.message(property(PROTEOMIC, ADDRESS)),
        design.proteomicContactAddressLink.getCaption());
    assertTrue(design.proteomicContactAddressLink.isCaptionAsHtml());
    assertEquals(VaadinIcons.MAP_MARKER, design.proteomicContactAddressLink.getIcon());
    assertEquals(resources.message(property(PROTEOMIC, PHONE)),
        design.proteomicContactPhoneLink.getCaption());
    assertEquals(VaadinIcons.PHONE, design.proteomicContactPhoneLink.getIcon());
    assertEquals(resources.message(WEBSITE), design.websiteContactPanel.getCaption());
    assertEquals(resources.message(property(WEBSITE, NAME)),
        design.websiteContactNameLink.getCaption());
    assertEquals(VaadinIcons.ENVELOPE, design.websiteContactNameLink.getIcon());
    assertEquals(resources.message(property(WEBSITE, ADDRESS)),
        design.websiteContactAddressLink.getCaption());
    assertTrue(design.websiteContactAddressLink.isCaptionAsHtml());
    assertEquals(VaadinIcons.MAP_MARKER, design.websiteContactAddressLink.getIcon());
    assertEquals(resources.message(property(WEBSITE, PHONE)),
        design.websiteContactPhoneLink.getCaption());
    assertEquals(VaadinIcons.PHONE, design.websiteContactPhoneLink.getIcon());
  }

  @Test
  public void resources() {
    assertTrue(design.proteomicContactNameLink.getResource() instanceof ExternalResource);
    ExternalResource resource = (ExternalResource) design.proteomicContactNameLink.getResource();
    assertEquals(PROTEOMIC_EMAIL_RESOURCE, resource.getURL());
    assertTrue(design.proteomicContactAddressLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) design.proteomicContactAddressLink.getResource();
    assertEquals(PROTEOMIC_ADDRESS_RESOURCE, resource.getURL());
    assertTrue(design.proteomicContactPhoneLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) design.proteomicContactPhoneLink.getResource();
    assertEquals(PROTEOMIC_PHONE_RESOURCE, resource.getURL());
    assertTrue(design.websiteContactNameLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) design.websiteContactNameLink.getResource();
    assertEquals(WEBSITE_EMAIL_RESOURCE, resource.getURL());
    assertTrue(design.websiteContactAddressLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) design.websiteContactAddressLink.getResource();
    assertEquals(WEBSITE_ADDRESS_RESOURCE, resource.getURL());
    assertTrue(design.websiteContactPhoneLink.getResource() instanceof ExternalResource);
    resource = (ExternalResource) design.websiteContactPhoneLink.getResource();
    assertEquals(WEBSITE_PHONE_RESOURCE, resource.getURL());
  }
}
