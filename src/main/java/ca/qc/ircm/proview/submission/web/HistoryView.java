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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
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
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * History view.
 */
@Route(value = HistoryView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.ADMIN })
public class HistoryView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver, NotificationComponent {
  public static final String VIEW_NAME = "history";
  public static final String ID = "history-view";
  public static final String HEADER = "header";
  public static final String ACTIVITIES = "activities";
  public static final String DESCRIPTION = "description";
  public static final String VIEW_ERROR = "description";
  public static final String VIEW_BUTTON =
      "<vaadin-button class='" + VIEW + "' theme='icon' @click='${view}'>"
          + "<vaadin-icon icon='vaadin:eye' slot='prefix'></vaadin-icon>" + "</vaadin-button>";
  public static final String DESCRIPTION_SPAN =
      "<span .title='${item.descriptionTitle}'>${item.descriptionValue}</span>";
  public static final String EXPLANATION_SPAN =
      "<span .title='${item.explanationTitle}'>${item.explanationValue}</span>";
  private static final long serialVersionUID = -6131172448162015562L;
  private static final Logger logger = LoggerFactory.getLogger(HistoryView.class);
  protected H2 header = new H2();
  protected Grid<Activity> activities = new Grid<>();
  protected Column<Activity> view;
  protected Column<Activity> user;
  protected Column<Activity> type;
  protected Column<Activity> date;
  protected Column<Activity> description;
  protected Column<Activity> explanation;
  protected ObjectFactory<SubmissionDialog> dialogFactory;
  protected ObjectFactory<MsAnalysisDialog> msAnalysisDialogFactory;
  protected ObjectFactory<TreatmentDialog> treatmentDialogFactory;
  private transient HistoryViewPresenter presenter;

  @Autowired
  protected HistoryView(HistoryViewPresenter presenter,
      ObjectFactory<SubmissionDialog> dialogFactory,
      ObjectFactory<MsAnalysisDialog> msAnalysisDialogFactory,
      ObjectFactory<TreatmentDialog> treatmentDialogFactory) {
    this.presenter = presenter;
    this.dialogFactory = dialogFactory;
    this.msAnalysisDialogFactory = msAnalysisDialogFactory;
    this.treatmentDialogFactory = treatmentDialogFactory;
  }

  @PostConstruct
  void init() {
    logger.debug("history view");
    setId(ID);
    setSizeFull();
    add(header, activities);
    expand(activities);
    header.setId(HEADER);
    activities.setId(ACTIVITIES);
    activities.setSizeFull();
    view =
        activities
            .addColumn(LitRenderer.<Activity>of(VIEW_BUTTON).withFunction("view",
                ac -> presenter.view(ac, getLocale())))
            .setKey(VIEW).setSortable(false).setFlexGrow(0);
    user = activities.addColumn(ac -> ac.getUser().getName(), USER).setKey(USER).setFlexGrow(5);
    type = activities.addColumn(ac -> ac.getActionType().getLabel(getLocale()), ACTION_TYPE)
        .setKey(ACTION_TYPE);
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
    activities.addItemDoubleClickListener(e -> presenter.view(e.getItem(), getLocale()));
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    AppResources resources = new AppResources(getClass(), getLocale());
    AppResources activityResources = new AppResources(Activity.class, getLocale());
    AppResources webResources = new AppResources(Constants.class, getLocale());
    String viewHeader = webResources.message(VIEW);
    view.setHeader(viewHeader).setFooter(viewHeader);
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

  private String description(Activity activity) {
    return presenter.description(activity, getLocale());
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
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    presenter.setParameter(parameter);
    updateHeader();
  }
}
