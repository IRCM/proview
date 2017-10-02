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
  private ContactViewDesign design;
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
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    design.proteomicContactPanel.addStyleName(PROTEOMIC);
    design.proteomicContactPanel.setCaption(resources.message(PROTEOMIC));
    design.proteomicContactNameLink.addStyleName(PROTEOMIC + "-" + NAME);
    design.proteomicContactNameLink.setCaption(resources.message(PROTEOMIC + "." + NAME));
    design.proteomicContactNameLink.setIcon(VaadinIcons.ENVELOPE);
    design.proteomicContactNameLink.setResource(new ExternalResource(PROTEOMIC_EMAIL_RESOURCE));
    design.proteomicContactAddressLink.addStyleName(PROTEOMIC + "-" + ADDRESS);
    design.proteomicContactAddressLink.setCaption(resources.message(PROTEOMIC + "." + ADDRESS));
    design.proteomicContactAddressLink.setCaptionAsHtml(true);
    design.proteomicContactAddressLink.setIcon(VaadinIcons.MAP_MARKER);
    design.proteomicContactAddressLink
        .setResource(new ExternalResource(PROTEOMIC_ADDRESS_RESOURCE));
    design.proteomicContactPhoneLink.addStyleName(PROTEOMIC + "-" + PHONE);
    design.proteomicContactPhoneLink.setCaption(resources.message(PROTEOMIC + "." + PHONE));
    design.proteomicContactPhoneLink.setIcon(VaadinIcons.PHONE);
    design.proteomicContactPhoneLink.setResource(new ExternalResource(PROTEOMIC_PHONE_RESOURCE));
    design.websiteContactPanel.addStyleName(WEBSITE);
    design.websiteContactPanel.setCaption(resources.message(WEBSITE));
    design.websiteContactNameLink.addStyleName(WEBSITE + "-" + NAME);
    design.websiteContactNameLink.setCaption(resources.message(WEBSITE + "." + NAME));
    design.websiteContactNameLink.setIcon(VaadinIcons.ENVELOPE);
    design.websiteContactNameLink.setResource(new ExternalResource(WEBSITE_EMAIL_RESOURCE));
    design.websiteContactAddressLink.addStyleName(WEBSITE + "-" + ADDRESS);
    design.websiteContactAddressLink.setCaption(resources.message(WEBSITE + "." + ADDRESS));
    design.websiteContactAddressLink.setCaptionAsHtml(true);
    design.websiteContactAddressLink.setIcon(VaadinIcons.MAP_MARKER);
    design.websiteContactAddressLink.setResource(new ExternalResource(WEBSITE_ADDRESS_RESOURCE));
    design.websiteContactPhoneLink.addStyleName(WEBSITE + "-" + PHONE);
    design.websiteContactPhoneLink.setCaption(resources.message(WEBSITE + "." + PHONE));
    design.websiteContactPhoneLink.setIcon(VaadinIcons.PHONE);
    design.websiteContactPhoneLink.setResource(new ExternalResource(WEBSITE_PHONE_RESOURCE));
  }
}
