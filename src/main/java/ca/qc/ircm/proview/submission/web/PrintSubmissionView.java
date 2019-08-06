package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Print submission view.
 */
@Route(value = PrintSubmissionView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
@HtmlImport("styles/print-submission-view-styles.html")
public class PrintSubmissionView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver {
  public static final String VIEW_NAME = "print-submission";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String SECOND_HEADER = "header-2";
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(PrintSubmissionView.class);
  protected H2 header = new H2();
  protected H3 secondHeader = new H3();
  protected PrintSubmission printContent;
  private PrintSubmissionViewPresenter presenter;

  @Autowired
  protected PrintSubmissionView(PrintSubmissionViewPresenter presenter,
      PrintSubmission printContent) {
    this.presenter = presenter;
    this.printContent = printContent;
  }

  @PostConstruct
  void init() {
    logger.debug("Print submission view");
    setId(ID);
    add(header, secondHeader, printContent);
    header.setId(HEADER);
    secondHeader.setId(SECOND_HEADER);
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(PrintSubmissionView.class, getLocale());
    header.setText(resources.message(HEADER));
    updateSecondHeader();
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    presenter.setParameter(parameter);
    updateSecondHeader();
  }

  private void updateSecondHeader() {
    Submission submission = presenter.getSubmission();
    if (submission != null && submission.getId() != null) {
      secondHeader.setText(submission.getService().getLabel(getLocale()));
    } else {
      secondHeader.setText(Service.LC_MS_MS.getLabel(getLocale()));
    }
  }
}
