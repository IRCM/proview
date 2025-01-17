package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main view.
 */
@Route(value = ContactView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class ContactView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle {
  public static final String VIEW_NAME = "contact";
  public static final String ID = "contact-view";
  public static final String HEADER = "header";
  public static final String PROTEOMIC = "proteomic";
  public static final String WEBSITE = "website";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";
  public static final String PHONE = "phone";
  public static final String LINK = "link";
  private static final String MESSAGES_PREFIX = messagePrefix(ContactView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final long serialVersionUID = -5066595299866514742L;
  private static final Logger logger = LoggerFactory.getLogger(ContactView.class);
  protected H3 proteomicHeader = new H3();
  protected Anchor proteomicNameAnchor = new Anchor();
  protected Span proteomicName = new Span();
  protected Anchor proteomicAddressAnchor = new Anchor();
  protected Span proteomicAddress = new Span();
  protected Anchor proteomicPhoneAnchor = new Anchor();
  protected Span proteomicPhone = new Span();
  protected H3 websiteHeader = new H3();
  protected Anchor websiteNameAnchor = new Anchor();
  protected Span websiteName = new Span();
  protected Anchor websiteAddressAnchor = new Anchor();
  protected Span websiteAddress = new Span();
  protected Anchor websitePhoneAnchor = new Anchor();
  protected Span websitePhone = new Span();

  @PostConstruct
  void init() {
    logger.debug("contact view");
    setId(ID);
    add(proteomicHeader, proteomicNameAnchor, proteomicAddressAnchor, proteomicPhoneAnchor);
    add(websiteHeader, websiteNameAnchor, websiteAddressAnchor, websitePhoneAnchor);
    proteomicHeader.setId(styleName(PROTEOMIC, HEADER));
    proteomicNameAnchor.setId(styleName(PROTEOMIC, NAME));
    proteomicNameAnchor.add(addIcon(VaadinIcon.ENVELOPE.create(), proteomicName));
    proteomicAddressAnchor.setId(styleName(PROTEOMIC, ADDRESS));
    proteomicAddressAnchor.add(addIcon(VaadinIcon.MAP_MARKER.create(), proteomicAddress));
    proteomicPhoneAnchor.setId(styleName(PROTEOMIC, PHONE));
    proteomicPhoneAnchor.add(addIcon(VaadinIcon.PHONE.create(), proteomicPhone));
    websiteHeader.setId(styleName(WEBSITE, HEADER));
    websiteNameAnchor.setId(styleName(WEBSITE, NAME));
    websiteNameAnchor.add(addIcon(VaadinIcon.ENVELOPE.create(), websiteName));
    websiteAddressAnchor.setId(styleName(WEBSITE, ADDRESS));
    websiteAddressAnchor.add(addIcon(VaadinIcon.MAP_MARKER.create(), websiteAddress));
    websitePhoneAnchor.setId(styleName(WEBSITE, PHONE));
    websitePhoneAnchor.add(addIcon(VaadinIcon.PHONE.create(), websitePhone));
  }

  private HorizontalLayout addIcon(Icon icon, Component component) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(icon, component);
    return layout;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    proteomicHeader.setText(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC)));
    proteomicName.setText(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, NAME)));
    proteomicNameAnchor.setHref(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, NAME, LINK)));
    proteomicAddress.setText(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, ADDRESS)));
    proteomicAddressAnchor
        .setHref(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, ADDRESS, LINK)));
    proteomicPhone.setText(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, PHONE)));
    proteomicPhoneAnchor
        .setHref(getTranslation(MESSAGES_PREFIX + property(PROTEOMIC, PHONE, LINK)));
    websiteHeader.setText(getTranslation(MESSAGES_PREFIX + property(WEBSITE)));
    websiteName.setText(getTranslation(MESSAGES_PREFIX + property(WEBSITE, NAME)));
    websiteNameAnchor.setHref(getTranslation(MESSAGES_PREFIX + property(WEBSITE, NAME, LINK)));
    websiteAddress.setText(getTranslation(MESSAGES_PREFIX + property(WEBSITE, ADDRESS)));
    websiteAddressAnchor
        .setHref(getTranslation(MESSAGES_PREFIX + property(WEBSITE, ADDRESS, LINK)));
    websitePhone.setText(getTranslation(MESSAGES_PREFIX + property(WEBSITE, PHONE)));
    websitePhoneAnchor.setHref(getTranslation(MESSAGES_PREFIX + property(WEBSITE, PHONE, LINK)));
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }
}
