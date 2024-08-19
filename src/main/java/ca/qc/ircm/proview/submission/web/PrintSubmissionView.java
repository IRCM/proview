package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Print submission view.
 */
@Route(value = PrintSubmissionView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
@JsModule("./styles/print-submission-view-styles.js")
public class PrintSubmissionView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver {
  public static final String VIEW_NAME = "print-submission";
  public static final String ID = "print-submission-view";
  public static final String HEADER = "header";
  public static final String SECOND_HEADER = "header-2";
  private static final String MESSAGES_PREFIX = messagePrefix(PrintSubmissionView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(PrintSubmissionView.class);
  protected H2 header = new H2();
  protected H3 secondHeader = new H3();
  protected PrintSubmission printContent;
  private Submission submission;
  private transient SubmissionService service;

  @Autowired
  protected PrintSubmissionView(PrintSubmission printContent, SubmissionService service) {
    this.printContent = printContent;
    this.service = service;
  }

  @PostConstruct
  void init() {
    logger.debug("print submission view");
    setId(ID);
    add(header, secondHeader, printContent);
    header.setId(HEADER);
    secondHeader.setId(SECOND_HEADER);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    header.setText(getTranslation(MESSAGES_PREFIX + HEADER));
    updateSecondHeader();
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    submission = service.get(parameter).orElse(null);
    printContent.setSubmission(submission);
    updateSecondHeader();
  }

  private void updateSecondHeader() {
    if (submission != null && submission.getId() != null) {
      secondHeader.setText(getTranslation(SERVICE_PREFIX + submission.getService().name()));
    } else {
      secondHeader.setText(getTranslation(SERVICE_PREFIX + Service.LC_MS_MS.name()));
    }
  }

  Long getSubmissionId() {
    return submission != null ? submission.getId() : null;
  }
}
