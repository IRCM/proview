package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.NAV;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;

import com.vaadin.flow.component.applayout.testbench.DrawerToggleElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.sidenav.testbench.SideNavElement;
import com.vaadin.flow.component.sidenav.testbench.SideNavItemElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link ViewLayout} element.
 */
@Element("vaadin-app-layout")
@Attribute(name = "id", value = ViewLayout.ID)
public class ViewLayoutElement extends VerticalLayoutElement {

  public H1Element applicationName() {
    return $(H1Element.class).first();
  }

  public H2Element header() {
    return $(H2Element.class).first();
  }

  public DrawerToggleElement drawerToggle() {
    return $(DrawerToggleElement.class).first();
  }

  /**
   * Opens side navigation. If side navigation is already open, this method does nothing.
   */
  private void openSideNav() {
    if (!"true".equals(drawerToggle().getDomAttribute("aria-expanded"))) {
      drawerToggle().click();
    }
  }

  public SideNavElement sideNav() {
    openSideNav();
    return $(SideNavElement.class).first();
  }

  public SideNavItemElement submissions() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(SUBMISSIONS, NAV));
  }

  public SideNavItemElement profile() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(PROFILE, NAV));
  }

  public SideNavItemElement users() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(USERS, NAV));
  }

  public SideNavItemElement exitSwitchUser() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(EXIT_SWITCH_USER, NAV));
  }

  public SideNavItemElement signout() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(SIGNOUT, NAV));
  }

  public SideNavItemElement contact() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(CONTACT, NAV));
  }

  public SideNavItemElement guidelines() {
    openSideNav();
    return $(SideNavItemElement.class).id(styleName(GUIDELINES, NAV));
  }

  public ButtonElement changeLanguage() {
    return $(ButtonElement.class).id(CHANGE_LANGUAGE);
  }
}
