package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ADD;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.HISTORY;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.TAB;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;

import com.vaadin.flow.component.applayout.testbench.DrawerToggleElement;
import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
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

  public TabsElement tabs() {
    return $(TabsElement.class).first();
  }

  public TabElement submissions() {
    return $(TabElement.class).id(styleName(SUBMISSIONS, TAB));
  }

  public TabElement profile() {
    return $(TabElement.class).id(styleName(PROFILE, TAB));
  }

  public TabElement users() {
    return $(TabElement.class).id(styleName(USERS, TAB));
  }

  public TabElement exitSwitchUser() {
    return $(TabElement.class).id(styleName(EXIT_SWITCH_USER, TAB));
  }

  public TabElement signout() {
    return $(TabElement.class).id(styleName(SIGNOUT, TAB));
  }

  public TabElement changeLanguage() {
    return $(TabElement.class).id(styleName(CHANGE_LANGUAGE, TAB));
  }

  public TabElement contact() {
    return $(TabElement.class).id(styleName(CONTACT, TAB));
  }

  public TabElement guidelines() {
    return $(TabElement.class).id(styleName(GUIDELINES, TAB));
  }

  public TabElement add() {
    return $(TabElement.class).id(styleName(ADD, TAB));
  }

  public TabElement edit() {
    return $(TabElement.class).id(styleName(EDIT, TAB));
  }

  public TabElement history() {
    return $(TabElement.class).id(styleName(HISTORY, TAB));
  }
}
