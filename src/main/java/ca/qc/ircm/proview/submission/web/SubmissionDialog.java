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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.Constants.RIGHT;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.datePickerI18n;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.SavedEvent;
import ch.carnet.kasparscherrer.VerticalScrollLayout;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionDialog extends Dialog implements LocaleChangeObserver {
  private static final long serialVersionUID = 8452988829428470601L;
  public static final String ID = "submission-dialog";
  public static final String HEADER = "header";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialog.class);
  protected H3 header = new H3();
  protected FormLayout submissionForm = new FormLayout();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  protected DatePicker dataAvailableDate = new DatePicker();
  protected Button save = new Button();
  protected Button print = new Button();
  protected Button edit = new Button();
  @Autowired
  protected PrintSubmission printContent;
  @Autowired
  private transient SubmissionDialogPresenter presenter;
  @Autowired
  private transient AuthorizationService authorizationService;

  public SubmissionDialog() {
  }

  SubmissionDialog(SubmissionDialogPresenter presenter, PrintSubmission printContent,
      AuthorizationService authorizationService) {
    this.presenter = presenter;
    this.printContent = printContent;
    this.authorizationService = authorizationService;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    logger.debug("submission dialog");
    setId(ID);
    setWidth("1400px");
    setHeight("800px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    HorizontalLayout formLayout = new HorizontalLayout();
    VerticalScrollLayout printContentLayout = new VerticalScrollLayout(printContent);
    printContentLayout.setHeight("600px");
    formLayout.add(printContentLayout);
    formLayout.add(submissionForm);
    formLayout.expand(printContentLayout);
    submissionForm.add(instrument, dataAvailableDate, save);
    submissionForm.setVisible(authorizationService.hasRole(ADMIN));
    HorizontalLayout buttons = new HorizontalLayout(print, edit);
    buttons.setWidthFull();
    layout.add(header, formLayout, buttons);
    layout.setSizeFull();
    layout.expand(formLayout);
    header.setId(id(HEADER));
    instrument.setId(id(INSTRUMENT));
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    dataAvailableDate.setId(id(DATA_AVAILABLE_DATE));
    save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
    save.setId(id(SAVE));
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save());
    print.setId(id(PRINT));
    print.setIcon(VaadinIcon.PRINT.create());
    print.addClickListener(e -> presenter.print());
    edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    edit.setId(id(EDIT));
    edit.addClassName(RIGHT);
    edit.setIcon(VaadinIcon.EDIT.create());
    edit.addClickListener(e -> presenter.edit());
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources submissionResources = new AppResources(Submission.class, getLocale());
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    updateHeader();
    DatePickerI18n dateI18n = datePickerI18n(getLocale());
    instrument.setLabel(submissionResources.message(INSTRUMENT));
    dataAvailableDate.setLabel(submissionResources.message(DATA_AVAILABLE_DATE));
    dataAvailableDate.setI18n(dateI18n);
    dataAvailableDate.setLocale(Locale.CANADA); // ISO format.
    save.setText(webResources.message(SAVE));
    edit.setText(webResources.message(EDIT));
    print.setText(webResources.message(PRINT));
    presenter.localeChange(getLocale());
  }

  /**
   * Adds listener to be informed when a submission was saved.
   *
   * @param listener
   *          listener
   * @return listener registration
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Registration
      addSavedListener(ComponentEventListener<SavedEvent<SubmissionDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
    updateHeader();
  }

  private void updateHeader() {
    Submission submission = presenter.getSubmission();
    if (submission != null && submission.getId() != null) {
      header.setText(submission.getExperiment());
    } else {
      final AppResources resources = new AppResources(SubmissionDialog.class, getLocale());
      header.setText(resources.message(HEADER));
    }
  }
}
