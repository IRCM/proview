package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * History view.
 */
@Route(value = HistoryView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.ADMIN })
public class HistoryView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver, NotificationComponent {
  public static final String VIEW_NAME = "history";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String ACTIVITIES = "activities";
  public static final String DESCRIPTION = "description";
  public static final String VIEW_ERROR = "description";
  private static final long serialVersionUID = -6131172448162015562L;
  private static final Logger logger = LoggerFactory.getLogger(HistoryView.class);
  protected H2 header = new H2();
  protected Grid<Activity> activities = new Grid<>();
  protected Column<Activity> user;
  protected Column<Activity> type;
  protected Column<Activity> date;
  protected Column<Activity> description;
  protected Column<Activity> explanation;
  protected SubmissionDialog dialog;
  private HistoryViewPresenter presenter;

  @Autowired
  protected HistoryView(HistoryViewPresenter presenter, SubmissionDialog dialog) {
    this.presenter = presenter;
    this.dialog = dialog;
  }

  @PostConstruct
  void init() {
    logger.debug("history view");
    setId(ID);
    setSizeFull();
    add(activities);
    header.setId(HEADER);
    activities.setId(ACTIVITIES);
    activities.setSizeFull();
    user = activities.addColumn(ac -> ac.getUser().getName(), USER).setKey(USER);
    type = activities.addColumn(ac -> ac.getActionType().getLabel(getLocale()), ACTION_TYPE)
        .setKey(ACTION_TYPE);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date = activities.addColumn(ac -> dateFormatter.format(ac.getTimestamp()), TIMESTAMP)
        .setKey(TIMESTAMP);
    description = activities.addColumn(ac -> presenter.description(ac, getLocale()), DESCRIPTION)
        .setKey(DESCRIPTION);
    explanation = activities.addColumn(ac -> ac.getExplanation(), EXPLANATION).setKey(EXPLANATION);
    activities.addItemDoubleClickListener(e -> presenter.view(e.getItem(), getLocale()));
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    AppResources resources = new AppResources(getClass(), getLocale());
    AppResources activityResources = new AppResources(Activity.class, getLocale());
    String userHeader = activityResources.message(USER);
    user.setHeader(userHeader).setFooter(userHeader);
    String typeHeader = activityResources.message(ACTION_TYPE);
    type.setHeader(typeHeader).setFooter(typeHeader);
    String dateHeader = activityResources.message(TIMESTAMP);
    date.setHeader(dateHeader).setFooter(dateHeader);
    String descriptionHeader = resources.message(DESCRIPTION);
    description.setHeader(descriptionHeader).setFooter(descriptionHeader);
    String explanationHeader = activityResources.message(EXPLANATION);
    explanation.setHeader(explanationHeader).setFooter(explanationHeader);
    updateHeader();
  }

  private void updateHeader() {
    AppResources resources = new AppResources(getClass(), getLocale());
    Submission submission = presenter.getSubmission();
    if (submission != null && submission.getId() != null) {
      header.setText(resources.message(HEADER, submission.getExperiment()));
    } else {
      header.setText(resources.message(HEADER, ""));
    }
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    presenter.setParameter(parameter);
    updateHeader();
  }
}
