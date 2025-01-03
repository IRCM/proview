package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;
import static ca.qc.ircm.proview.text.Strings.property;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityComparator;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ErrorNotification;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.ViewLayoutChild;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * History view.
 */
@Route(value = HistoryView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.ADMIN })
public class HistoryView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Long>,
    LocaleChangeObserver, NotificationComponent, ViewLayoutChild {
  public static final String VIEW_NAME = "history";
  public static final String ID = "history-view";
  public static final String HEADER = "header";
  public static final String ACTIVITIES = "activities";
  public static final String DESCRIPTION = "description";
  public static final String VIEW_ERROR = "description";
  public static final String DESCRIPTION_SPAN =
      "<span .title='${item.descriptionTitle}'>${item.descriptionValue}</span>";
  public static final String EXPLANATION_SPAN =
      "<span .title='${item.explanationTitle}'>${item.explanationValue}</span>";
  private static final String MESSAGES_PREFIX = messagePrefix(HistoryView.class);
  private static final String ACTIVITY_PREFIX = messagePrefix(Activity.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String ACTION_TYPE_PREFIX = messagePrefix(ActionType.class);
  private static final long serialVersionUID = -6131172448162015562L;
  private static final Logger logger = LoggerFactory.getLogger(HistoryView.class);
  protected Grid<Activity> activities = new Grid<>();
  protected Column<Activity> user;
  protected Column<Activity> type;
  protected Column<Activity> date;
  protected Column<Activity> description;
  protected Column<Activity> explanation;
  protected Button view = new Button();
  private Submission submission;
  private transient ObjectFactory<SubmissionDialog> dialogFactory;
  private transient ObjectFactory<MsAnalysisDialog> msAnalysisDialogFactory;
  private transient ObjectFactory<TreatmentDialog> treatmentDialogFactory;
  private transient ActivityService service;
  private transient SubmissionService submissionService;

  @Autowired
  protected HistoryView(ActivityService service, SubmissionService submissionService,
      ObjectFactory<SubmissionDialog> dialogFactory,
      ObjectFactory<MsAnalysisDialog> msAnalysisDialogFactory,
      ObjectFactory<TreatmentDialog> treatmentDialogFactory) {
    this.service = service;
    this.submissionService = submissionService;
    this.dialogFactory = dialogFactory;
    this.msAnalysisDialogFactory = msAnalysisDialogFactory;
    this.treatmentDialogFactory = treatmentDialogFactory;
  }

  @PostConstruct
  void init() {
    logger.debug("history view");
    setId(ID);
    setHeightFull();
    add(activities, view);
    expand(activities);
    activities.setId(ACTIVITIES);
    user = activities.addColumn(ac -> ac.getUser().getName(), USER).setKey(USER).setFlexGrow(5);
    type =
        activities.addColumn(ac -> getTranslation(ACTION_TYPE_PREFIX + ac.getActionType().name()),
            ACTION_TYPE).setKey(ACTION_TYPE);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date = activities.addColumn(ac -> dateFormatter.format(ac.getTimestamp()), TIMESTAMP)
        .setKey(TIMESTAMP).setFlexGrow(3);
    description = activities
        .addColumn(LitRenderer.<Activity>of(DESCRIPTION_SPAN)
            .withProperty("descriptionTitle", ac -> description(ac))
            .withProperty("descriptionValue", ac -> description(ac)))
        .setKey(DESCRIPTION).setSortable(false).setFlexGrow(5);
    explanation = activities
        .addColumn(LitRenderer.<Activity>of(EXPLANATION_SPAN)
            .withProperty("explanationTitle", ac -> ac.getExplanation())
            .withProperty("explanationValue", ac -> ac.getExplanation()))
        .setKey(EXPLANATION).setSortable(false).setFlexGrow(5);
    activities.addItemDoubleClickListener(e -> view(e.getItem()));
    activities.addSelectionListener(e -> view.setEnabled(e.getAllSelectedItems().size() == 1));
    view.setId(VIEW);
    view.setIcon(VaadinIcon.EYE.create());
    view.addClickListener(e -> view());
    view.setEnabled(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    String userHeader = getTranslation(ACTIVITY_PREFIX + USER);
    user.setHeader(userHeader).setFooter(userHeader);
    String typeHeader = getTranslation(ACTIVITY_PREFIX + ACTION_TYPE);
    type.setHeader(typeHeader).setFooter(typeHeader);
    String dateHeader = getTranslation(ACTIVITY_PREFIX + TIMESTAMP);
    date.setHeader(dateHeader).setFooter(dateHeader);
    String descriptionHeader = getTranslation(MESSAGES_PREFIX + DESCRIPTION);
    description.setHeader(descriptionHeader).setFooter(descriptionHeader);
    String explanationHeader = getTranslation(ACTIVITY_PREFIX + EXPLANATION);
    explanation.setHeader(explanationHeader).setFooter(explanationHeader);
    view.setText(getTranslation(CONSTANTS_PREFIX + VIEW));
    updateHeader();
  }

  private String description(Activity activity) {
    return service.description(activity, getLocale()).orElse("");
  }

  private void updateHeader() {
    if (submission != null && submission.getId() != 0) {
      viewLayout().ifPresent(layout -> layout
          .setHeaderText(getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment())));
    } else {
      viewLayout()
          .ifPresent(layout -> layout.setHeaderText(getTranslation(MESSAGES_PREFIX + HEADER, "")));
    }
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    if (parameter != null) {
      submission = submissionService.get(parameter).orElse(null);
      updateActivities();
    }
    updateHeader();
  }

  public long getSubmissionId() {
    return submission != null ? submission.getId() : 0;
  }

  private void updateActivities() {
    if (submission != null) {
      List<Activity> activities = service.all(submission);
      Collections.sort(activities,
          new ActivityComparator(ActivityComparator.Compare.TIMESTAMP).reversed());
      this.activities.setItems(activities);
    }
  }

  void view() {
    Optional<Activity> os = activities.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      view(os.get());
    } else {
      new ErrorNotification(getTranslation(MESSAGES_PREFIX + property(ACTIVITIES, REQUIRED)))
          .open();
    }
  }

  void view(Activity activity) {
    Object record = service.record(activity).orElse(new Object());
    if (record instanceof SubmissionSample) {
      record = ((SubmissionSample) record).getSubmission();
    }
    if (record instanceof Submission) {
      SubmissionDialog dialog = dialogFactory.getObject();
      dialog.setSubmissionId(((Submission) record).getId());
      dialog.open();
      dialog.addSavedListener(e -> updateActivities());
    } else if (record instanceof MsAnalysis) {
      MsAnalysisDialog msAnalysisDialog = msAnalysisDialogFactory.getObject();
      msAnalysisDialog.setMsAnalysisId(((MsAnalysis) record).getId());
      msAnalysisDialog.open();
    } else if (record instanceof Treatment) {
      TreatmentDialog treatmentDialog = treatmentDialogFactory.getObject();
      treatmentDialog.setTreatmentId(((Treatment) record).getId());
      treatmentDialog.open();
    } else {
      showNotification(
          getTranslation(MESSAGES_PREFIX + VIEW_ERROR, record.getClass().getSimpleName()));
    }
    logger.trace("view activity {}", activity);
  }
}
