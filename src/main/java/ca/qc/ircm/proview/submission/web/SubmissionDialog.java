package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.web.WebConstants.EDIT;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.button.Button;
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
  protected Button print = new Button();
  protected Button edit = new Button();
  private SubmissionDialogPresenter presenter;

  @Autowired
  protected SubmissionDialog(SubmissionDialogPresenter presenter, PrintSubmission print) {
    this.presenter = presenter;
    this.printContent = print;
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
    formLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3)//, new ResponsiveStep("15em", 4)
    );
    formLayout.add(printContent, 3);
    //formLayout.add(new FormLayout(new Span("test")));
    HorizontalLayout buttons = new HorizontalLayout(print, edit);
    buttons.setWidthFull();
    layout.add(header, formLayout, buttons);
    header.addClassName(HEADER);
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
    final MessageResource resources = new MessageResource(SubmissionDialog.class, getLocale());
    final MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
    header.setText(resources.message(HEADER));
    edit.setText(webResources.message(EDIT));
    print.setText(webResources.message(PRINT));
    presenter.localeChange(getLocale());
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
  }
}
