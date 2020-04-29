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

import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.persistence.QueryDsl;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilter;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.google.common.collect.Range;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submissions view.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionsViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private Map<String, ComparableExpressionBase<?>> columnProperties = new HashMap<>();
  private SubmissionFilter filter = new SubmissionFilter();
  private SubmissionService service;
  private AuthorizationService authorizationService;

  @Autowired
  protected SubmissionsViewPresenter(SubmissionService service,
      AuthorizationService authorizationService) {
    this.service = service;
    this.authorizationService = authorizationService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  void init(SubmissionsView view) {
    this.view = view;
    columnProperties.put(EXPERIMENT, submission.experiment);
    columnProperties.put(USER, submission.user.name);
    columnProperties.put(DIRECTOR, submission.laboratory.director);
    columnProperties.put(DATA_AVAILABLE_DATE, submission.dataAvailableDate);
    columnProperties.put(SERVICE, submission.service);
    columnProperties.put(INSTRUMENT, submission.instrument);
    columnProperties.put(SAMPLES_COUNT, submission.samples.size());
    columnProperties.put(SUBMISSION_DATE, submission.submissionDate);
    columnProperties.put(HIDDEN, submission.hidden);
    view.user.setVisible(authorizationService.hasAnyRole(MANAGER, ADMIN));
    view.director.setVisible(authorizationService.hasRole(ADMIN));
    view.service.setVisible(authorizationService.hasRole(ADMIN));
    view.instrument.setVisible(authorizationService.hasRole(ADMIN));
    view.hidden.setVisible(authorizationService.hasRole(ADMIN));
    view.editStatus.setVisible(authorizationService.hasRole(ADMIN));
    loadSubmissions();
    view.dialog.addSavedListener(e -> loadSubmissions());
    view.statusDialog.addSavedListener(e -> loadSubmissions());
  }

  private void loadSubmissions() {
    view.submissions.setDataProvider(dataProvider());
  }

  private DataProvider<Submission, Void> dataProvider() {
    @SuppressWarnings("checkstyle:linelength")
    Function<Query<Submission, SubmissionFilter>, List<OrderSpecifier<?>>> filterSortOrders =
        query -> query.getSortOrders() != null && !query.getSortOrders().isEmpty()
            ? query.getSortOrders().stream()
                .filter(order -> columnProperties.containsKey(order.getSorted()))
                .map(order -> QueryDsl.direction(columnProperties.get(order.getSorted()),
                    order.getDirection() == SortDirection.DESCENDING))
                .collect(Collectors.toList())
            : Arrays.asList(submission.id.desc());
    FetchCallback<Submission, SubmissionFilter> fetchCallback = query -> {
      SubmissionFilter filter = query.getFilter().orElse(new SubmissionFilter());
      filter.sortOrders = filterSortOrders.apply(query);
      filter.offset = query.getOffset();
      filter.limit = query.getLimit();
      return service.all(filter).stream();
    };
    CountCallback<Submission, SubmissionFilter> countCallback = query -> {
      SubmissionFilter filter = query.getFilter().orElse(new SubmissionFilter());
      filter.sortOrders = filterSortOrders.apply(query);
      int count = service.count(filter);
      return count;
    };
    DataProvider<Submission, SubmissionFilter> dataProvider =
        new CallbackDataProvider<>(fetchCallback, countCallback);
    ConfigurableFilterDataProvider<Submission, Void, SubmissionFilter> wrapper =
        dataProvider.withConfigurableFilter();
    wrapper.setFilter(filter);
    return wrapper;
  }

  void filterExperiment(String value) {
    filter.experimentContains = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterUser(String value) {
    filter.userContains = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterDirector(String value) {
    filter.directorContains = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterDataAvailableDate(Range<LocalDate> range) {
    filter.dataAvailableDateRange = range;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterDate(Range<LocalDate> range) {
    filter.dateRange = range;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterService(Service value) {
    filter.service = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterInstrument(MassDetectionInstrument value) {
    filter.instrument = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterSamples(String value) {
    filter.anySampleNameContains = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterStatus(SampleStatus value) {
    filter.anySampleStatus = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void filterHidden(Boolean value) {
    filter.hidden = value;
    view.submissions.getDataProvider().refreshAll();
  }

  void view(Submission submission) {
    Submission database = service.get(submission.getId());
    view.dialog.setSubmission(database);
    view.dialog.open();
  }

  void editStatus(Submission submission) {
    if (authorizationService.hasRole(ADMIN)) {
      Submission database = service.get(submission.getId());
      view.statusDialog.setSubmission(database);
      view.statusDialog.open();
    }
  }

  void history(Submission submission) {
    if (authorizationService.hasRole(ADMIN)) {
      UI.getCurrent().navigate(HistoryView.class, submission.getId());
    }
  }

  void add() {
    UI.getCurrent().navigate(SubmissionView.class);
  }

  void editSelectedStatus(Locale locale) {
    Optional<Submission> os = view.submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      editStatus(os.get());
    } else {
      AppResources resources = new AppResources(SubmissionsView.class, locale);
      view.showNotification(resources.message(property(SUBMISSIONS, REQUIRED)));
    }
  }

  void toggleHidden(Submission submission) {
    submission.setHidden(!submission.isHidden());
    logger.debug("Change submission {} hidden to {}", submission, submission.isHidden());
    service.update(submission, null);
    view.submissions.getDataProvider().refreshAll();
  }

  SubmissionFilter filter() {
    return filter;
  }
}
