package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.PostConstruct;

/**
 * Main view.
 */
@Route(value = ContactView.VIEW_NAME, layout = ViewLayout.class)
public class ContactView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle {
  public static final String VIEW_NAME = "contact";
  public static final String HEADER = "header";
  public static final String PROTEOMIC = "proteomic";
  public static final String WEBSITE = "website";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";
  public static final String PHONE = "phone";
  public static final String LINK = "link";
  private static final long serialVersionUID = -5066595299866514742L;
  protected H1 header = new H1();
  protected H2 proteomicHeader = new H2();
  protected Anchor proteomicNameAnchor = new Anchor();
  protected Span proteomicName = new Span();
  protected Anchor proteomicAddressAnchor = new Anchor();
  protected Span proteomicAddress = new Span();
  protected Anchor proteomicPhoneAnchor = new Anchor();
  protected Span proteomicPhone = new Span();
  protected H2 websiteHeader = new H2();
  protected Anchor websiteNameAnchor = new Anchor();
  protected Span websiteName = new Span();
  protected Anchor websiteAddressAnchor = new Anchor();
  protected Span websiteAddress = new Span();
  protected Anchor websitePhoneAnchor = new Anchor();
  protected Span websitePhone = new Span();

  @PostConstruct
  void init() {
    add(header);
    proteomicNameAnchor.add(addIcon(VaadinIcon.ENVELOPE.create(), proteomicName));
    proteomicAddressAnchor.add(addIcon(VaadinIcon.MAP_MARKER.create(), proteomicAddress));
    proteomicPhoneAnchor.add(addIcon(VaadinIcon.PHONE.create(), proteomicPhone));
    add(proteomicHeader, proteomicNameAnchor, proteomicAddressAnchor, proteomicPhoneAnchor);
    websiteNameAnchor.add(addIcon(VaadinIcon.ENVELOPE.create(), websiteName));
    websiteAddressAnchor.add(addIcon(VaadinIcon.MAP_MARKER.create(), websiteAddress));
    websitePhoneAnchor.add(addIcon(VaadinIcon.PHONE.create(), websitePhone));
    add(websiteHeader, websiteNameAnchor, websiteAddressAnchor, websitePhoneAnchor);
  }

  private HorizontalLayout addIcon(Icon icon, Component component) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(icon, component);
    return layout;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    header.setText(resources.message(HEADER));
    proteomicHeader.setText(resources.message(property(PROTEOMIC)));
    proteomicName.setText(resources.message(property(PROTEOMIC, NAME)));
    proteomicNameAnchor.setHref(resources.message(property(PROTEOMIC, NAME, LINK)));
    proteomicAddress.setText(resources.message(property(PROTEOMIC, ADDRESS)));
    proteomicAddressAnchor.setHref(resources.message(property(PROTEOMIC, ADDRESS, LINK)));
    proteomicPhone.setText(resources.message(property(PROTEOMIC, PHONE)));
    proteomicPhoneAnchor.setHref(resources.message(property(PROTEOMIC, PHONE, LINK)));
    websiteHeader.setText(resources.message(property(WEBSITE)));
    websiteName.setText(resources.message(property(WEBSITE, NAME)));
    websiteNameAnchor.setHref(resources.message(property(WEBSITE, NAME, LINK)));
    websiteAddress.setText(resources.message(property(WEBSITE, ADDRESS)));
    websiteAddressAnchor.setHref(resources.message(property(WEBSITE, ADDRESS, LINK)));
    websitePhone.setText(resources.message(property(WEBSITE, PHONE)));
    websitePhoneAnchor.setHref(resources.message(property(WEBSITE, PHONE, LINK)));
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
