package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_COUNT;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.persistence.QueryDsl;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilter;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    columnProperties.put(SAMPLE_DELIVERY_DATE, submission.sampleDeliveryDate);
    columnProperties.put(DIGESTION_DATE, submission.digestionDate);
    columnProperties.put(ANALYSIS_DATE, submission.analysisDate);
    columnProperties.put(DATA_AVAILABLE_DATE, submission.dataAvailableDate);
    columnProperties.put(SERVICE, submission.service);
    columnProperties.put(MASS_DETECTION_INSTRUMENT, submission.massDetectionInstrument);
    columnProperties.put(SAMPLES_COUNT, submission.samples.size());
    columnProperties.put(SUBMISSION_DATE, submission.submissionDate);
    columnProperties.put(HIDDEN, submission.hidden);
    view.submissions.setDataProvider(dataProvider());
    view.user.setVisible(authorizationService.hasAnyRole(MANAGER, ADMIN));
    view.director.setVisible(authorizationService.hasRole(ADMIN));
    view.service.setVisible(authorizationService.hasRole(ADMIN));
    view.instrument.setVisible(authorizationService.hasRole(ADMIN));
    view.hidden.setVisible(authorizationService.hasRole(ADMIN));
  }

  private DataProvider<Submission, Void> dataProvider() {
    Function<Query<Submission, SubmissionFilter>, List<OrderSpecifier<?>>> filterSortOrders =
        query -> query.getSortOrders() != null
            ? query.getSortOrders().stream()
                .filter(order -> columnProperties.containsKey(order.getSorted()))
                .map(order -> QueryDsl.direction(columnProperties.get(order.getSorted()),
                    order.getDirection() == SortDirection.DESCENDING))
                .collect(Collectors.toList())
            : Collections.emptyList();
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

  void add() {
    view.dialog.setSubmission(new Submission());
    view.dialog.open();
  }

  void toggleHidden(Submission submission) {
    submission.setHidden(!submission.isHidden());
    logger.debug("Change submission {} hidden to {}", submission, submission.isHidden());
    service.update(submission, null);
  }

  SubmissionFilter filter() {
    return filter;
  }
}
