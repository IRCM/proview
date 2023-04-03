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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.web.SavedEvent;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * User dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LaboratoryDialog extends Dialog
    implements LocaleChangeObserver, NotificationComponent {
  private static final long serialVersionUID = 3285639770914046262L;
  private static final Logger logger = LoggerFactory.getLogger(LaboratoryDialog.class);
  public static final String ID = "laboratory-dialog";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  protected TextField name = new TextField();
  protected Button save = new Button();
  protected Button cancel = new Button();
  @Autowired
  private transient LaboratoryDialogPresenter presenter;

  public LaboratoryDialog() {
  }

  LaboratoryDialog(LaboratoryDialogPresenter presenter) {
    this.presenter = presenter;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    logger.debug("laboratory dialog");
    setId(ID);
    setWidth("500px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    FormLayout form = new FormLayout();
    layout.add(form);
    layout.setSizeFull();
    form.add(name);
    form.setResponsiveSteps(new ResponsiveStep("15em", 1));
    getFooter().add(cancel, save);
    name.setId(id(NAME));
    save.setId(id(SAVE));
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save(getLocale()));
    cancel.setId(id(CANCEL));
    cancel.setIcon(VaadinIcon.CLOSE.create());
    cancel.addClickListener(e -> presenter.cancel());
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources laboratoryResources = new AppResources(Laboratory.class, getLocale());
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    updateHeader();
    name.setLabel(laboratoryResources.message(NAME));
    save.setText(webResources.message(SAVE));
    cancel.setText(webResources.message(CANCEL));
    presenter.localeChange(getLocale());
  }

  private void updateHeader() {
    final AppResources resources = new AppResources(LaboratoryDialog.class, getLocale());
    if (presenter.getLaboratory() != null && presenter.getLaboratory().getId() != null) {
      setHeaderTitle(resources.message(HEADER, 1, presenter.getLaboratory().getName()));
    } else {
      setHeaderTitle(resources.message(HEADER, 0));
    }
  }

  /**
   * Adds listener to be informed when a user was saved.
   *
   * @param listener
   *          listener
   * @return listener registration
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Registration
      addSavedListener(ComponentEventListener<SavedEvent<LaboratoryDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public Laboratory getLaboratory() {
    return presenter.getLaboratory();
  }

  public void setLaboratory(Laboratory laboratory) {
    presenter.setLaboratory(laboratory);
    updateHeader();
  }
}
