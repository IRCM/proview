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

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Contact view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PROTEOMIC = "proteomic";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";
  public static final String PHONE = "phone";
  public static final String WEBSITE = "website";
  protected static final String PROTEOMIC_EMAIL_RESOURCE = "mailto:denis.faubert@ircm.qc.ca";
  protected static final String PROTEOMIC_ADDRESS_RESOURCE =
      "https://www.google.ca/maps/place/110+Avenue+des+Pins,+Montr%C3%A9al,+QC+H2W+1R7/@45.5135891,-73.5783748,17z/data=!3m1!4b1!4m5!3m4!1s0x4cc91a344da4b813:0xa6df1f5fce1ac6d0!8m2!3d45.5135891!4d-73.5761861?hl=en";
  protected static final String PROTEOMIC_PHONE_RESOURCE = "tel:0015149875557";
  protected static final String WEBSITE_EMAIL_RESOURCE = "mailto:christian.poitras@ircm.qc.ca";
  protected static final String WEBSITE_ADDRESS_RESOURCE =
      "https://www.google.ca/maps/place/110+Avenue+des+Pins,+Montr%C3%A9al,+QC+H2W+1R7/@45.5135891,-73.5783748,17z/data=!3m1!4b1!4m5!3m4!1s0x4cc91a344da4b813:0xa6df1f5fce1ac6d0!8m2!3d45.5135891!4d-73.5761861?hl=en";
  protected static final String WEBSITE_PHONE_RESOURCE = "tel:0015149875500";
  private static final Logger logger = LoggerFactory.getLogger(ContactViewPresenter.class);
  private ContactView view;
  @Value("${spring.application.name}")
  private String applicationName;

  public ContactViewPresenter() {
  }

  protected ContactViewPresenter(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(ContactView view) {
    logger.debug("Contact view");
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.setValue(resources.message(HEADER));
    view.proteomicContactPanel.addStyleName(PROTEOMIC);
    view.proteomicContactPanel.setCaption(resources.message(PROTEOMIC));
    view.proteomicContactNameLink.addStyleName(PROTEOMIC + "-" + NAME);
    view.proteomicContactNameLink.setCaption(resources.message(PROTEOMIC + "." + NAME));
    view.proteomicContactNameLink.setIcon(VaadinIcons.ENVELOPE);
    view.proteomicContactNameLink.setResource(new ExternalResource(PROTEOMIC_EMAIL_RESOURCE));
    view.proteomicContactAddressLink.addStyleName(PROTEOMIC + "-" + ADDRESS);
    view.proteomicContactAddressLink.setCaption(resources.message(PROTEOMIC + "." + ADDRESS));
    view.proteomicContactAddressLink.setCaptionAsHtml(true);
    view.proteomicContactAddressLink.setIcon(VaadinIcons.MAP_MARKER);
    view.proteomicContactAddressLink.setResource(new ExternalResource(PROTEOMIC_ADDRESS_RESOURCE));
    view.proteomicContactPhoneLink.addStyleName(PROTEOMIC + "-" + PHONE);
    view.proteomicContactPhoneLink.setCaption(resources.message(PROTEOMIC + "." + PHONE));
    view.proteomicContactPhoneLink.setIcon(VaadinIcons.PHONE);
    view.proteomicContactPhoneLink.setResource(new ExternalResource(PROTEOMIC_PHONE_RESOURCE));
    view.websiteContactPanel.addStyleName(WEBSITE);
    view.websiteContactPanel.setCaption(resources.message(WEBSITE));
    view.websiteContactNameLink.addStyleName(WEBSITE + "-" + NAME);
    view.websiteContactNameLink.setCaption(resources.message(WEBSITE + "." + NAME));
    view.websiteContactNameLink.setIcon(VaadinIcons.ENVELOPE);
    view.websiteContactNameLink.setResource(new ExternalResource(WEBSITE_EMAIL_RESOURCE));
    view.websiteContactAddressLink.addStyleName(WEBSITE + "-" + ADDRESS);
    view.websiteContactAddressLink.setCaption(resources.message(WEBSITE + "." + ADDRESS));
    view.websiteContactAddressLink.setCaptionAsHtml(true);
    view.websiteContactAddressLink.setIcon(VaadinIcons.MAP_MARKER);
    view.websiteContactAddressLink.setResource(new ExternalResource(WEBSITE_ADDRESS_RESOURCE));
    view.websiteContactPhoneLink.addStyleName(WEBSITE + "-" + PHONE);
    view.websiteContactPhoneLink.setCaption(resources.message(WEBSITE + "." + PHONE));
    view.websiteContactPhoneLink.setIcon(VaadinIcons.PHONE);
    view.websiteContactPhoneLink.setResource(new ExternalResource(WEBSITE_PHONE_RESOURCE));
  }
}
