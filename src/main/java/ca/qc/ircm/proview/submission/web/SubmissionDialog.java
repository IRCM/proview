package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.web.WebConstants.EDIT;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.englishDatePickerI18n;
import static ca.qc.ircm.proview.web.WebConstants.frenchDatePickerI18n;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import ch.carnet.kasparscherrer.VerticalScrollLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
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
  protected H2 header = new H2();
  protected PrintSubmission printContent;
  protected FormLayout datesForm = new FormLayout();
  protected DatePicker sampleDeliveryDate = new DatePicker();
  protected DatePicker digestionDate = new DatePicker();
  protected DatePicker analysisDate = new DatePicker();
  protected DatePicker dataAvailableDate = new DatePicker();
  protected Button save = new Button();
  protected Button print = new Button();
  protected Button edit = new Button();
  private SubmissionDialogPresenter presenter;
  private AuthorizationService authorizationService;

  @Autowired
  protected SubmissionDialog(SubmissionDialogPresenter presenter, PrintSubmission printContent,
      AuthorizationService authorizationService) {
    this.presenter = presenter;
    this.printContent = printContent;
    this.authorizationService = authorizationService;
  }

  @PostConstruct
  void init() {
    logger.debug("Submission dialog");
    setId(ID);
    VerticalLayout layout = new VerticalLayout();
    layout.setMaxWidth("90em");
    layout.setMinWidth("22em");
    add(layout);
    FormLayout formLayout = new FormLayout();
    VerticalScrollLayout printContentLayout = new VerticalScrollLayout(printContent);
    printContentLayout.setHeight("45em");
    if (authorizationService.hasRole(ADMIN)) {
      formLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
          new ResponsiveStep("15em", 3), new ResponsiveStep("15em", 4));
      formLayout.add(printContentLayout, 3);
    } else {
      formLayout.setResponsiveSteps(new ResponsiveStep("45em", 1));
      formLayout.add(printContentLayout);
    }
    formLayout.add(datesForm);
    datesForm.add(sampleDeliveryDate, digestionDate, analysisDate, dataAvailableDate, save);
    datesForm.setVisible(authorizationService.hasRole(ADMIN));
    HorizontalLayout buttons = new HorizontalLayout(print, edit);
    buttons.setWidthFull();
    layout.add(header, formLayout, buttons);
    header.addClassName(HEADER);
    sampleDeliveryDate.addClassName(SAMPLE_DELIVERY_DATE);
    digestionDate.addClassName(DIGESTION_DATE);
    analysisDate.addClassName(ANALYSIS_DATE);
    dataAvailableDate.addClassName(DATA_AVAILABLE_DATE);
    save.addThemeName(SUCCESS);
    save.addClassName(SAVE);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save());
    print.addClassName(PRINT);
    print.setIcon(VaadinIcon.PRINT.create());
    print.addClickListener(e -> presenter.print());
    edit.addThemeName(PRIMARY);
    edit.addClassName(EDIT);
    edit.addClassName("right");
    edit.setIcon(VaadinIcon.EDIT.create());
    edit.addClickListener(e -> presenter.edit());
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
    updateHeader();
    DatePickerI18n dateI18n = englishDatePickerI18n();
    if (FRENCH.equals(getLocale())) {
      dateI18n = frenchDatePickerI18n();
    }
    sampleDeliveryDate.setLabel(submissionResources.message(SAMPLE_DELIVERY_DATE));
    sampleDeliveryDate.setI18n(dateI18n);
    sampleDeliveryDate.setLocale(Locale.CANADA); // ISO format.
    digestionDate.setLabel(submissionResources.message(DIGESTION_DATE));
    digestionDate.setI18n(dateI18n);
    digestionDate.setLocale(Locale.CANADA); // ISO format.
    analysisDate.setLabel(submissionResources.message(ANALYSIS_DATE));
    analysisDate.setI18n(dateI18n);
    analysisDate.setLocale(Locale.CANADA); // ISO format.
    dataAvailableDate.setLabel(submissionResources.message(DATA_AVAILABLE_DATE));
    dataAvailableDate.setI18n(dateI18n);
    dataAvailableDate.setLocale(Locale.CANADA); // ISO format.
    save.setText(webResources.message(SAVE));
    edit.setText(webResources.message(EDIT));
    print.setText(webResources.message(PRINT));
    presenter.localeChange(getLocale());
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
      final MessageResource resources = new MessageResource(SubmissionDialog.class, getLocale());
      header.setText(resources.message(HEADER));
    }
  }
}
