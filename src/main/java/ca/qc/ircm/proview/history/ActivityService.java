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

package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.history.QActivity.activity;
import static ca.qc.ircm.proview.history.QUpdateActivity.updateActivity;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.plate.QWell.well;
import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSampleContainer.sampleContainer;
import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.QSubmissionFile.submissionFile;
import static ca.qc.ircm.proview.treatment.QProtocol.protocol;
import static ca.qc.ircm.proview.treatment.QTreatedSample.treatedSample;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.user.QAddress.address;
import static ca.qc.ircm.proview.user.QForgotPassword.forgotPassword;
import static ca.qc.ircm.proview.user.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QPhoneNumber.phoneNumber;
import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.text.MessageResource;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for activity.
 */
@Service
@Transactional
public class ActivityService {
  @Inject
  private ActivityRepository repository;
  @Inject
  private JPAQueryFactory queryFactory;

  protected ActivityService() {
  }

  /**
   * Returns object associated with this activity.
   *
   * @param activity
   *          activity
   * @return object associated with this activity
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Object record(Activity activity) {
    if (activity == null || activity.getTableName() == null) {
      return null;
    }

    return record(activity.getTableName(), activity.getRecordId());
  }

  private Object record(String tableName, Long id) {
    switch (tableName) {
      case Acquisition.TABLE_NAME: {
        JPAQuery<Acquisition> query = queryFactory.select(acquisition);
        query.from(acquisition);
        query.where(acquisition.id.eq(id));
        return query.fetchOne();
      }
      case MsAnalysis.TABLE_NAME: {
        JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis);
        query.from(msAnalysis);
        query.where(msAnalysis.id.eq(id));
        return query.fetchOne();
      }
      case Plate.TABLE_NAME: {
        JPAQuery<Plate> query = queryFactory.select(plate);
        query.from(plate);
        query.where(plate.id.eq(id));
        return query.fetchOne();
      }
      case Contaminant.TABLE_NAME: {
        JPAQuery<Contaminant> query = queryFactory.select(contaminant);
        query.from(contaminant);
        query.where(contaminant.id.eq(id));
        return query.fetchOne();
      }
      case Sample.TABLE_NAME: {
        JPAQuery<Sample> query = queryFactory.select(sample);
        query.from(sample);
        query.where(sample.id.eq(id));
        return query.fetchOne();
      }
      case SampleContainer.TABLE_NAME: {
        JPAQuery<SampleContainer> query = queryFactory.select(sampleContainer);
        query.from(sampleContainer);
        query.where(sampleContainer.id.eq(id));
        return query.fetchOne();
      }
      case Standard.TABLE_NAME: {
        JPAQuery<Standard> query = queryFactory.select(standard);
        query.from(standard);
        query.where(standard.id.eq(id));
        return query.fetchOne();
      }
      case Submission.TABLE_NAME: {
        JPAQuery<Submission> query = queryFactory.select(submission);
        query.from(submission);
        query.where(submission.id.eq(id));
        return query.fetchOne();
      }
      case SubmissionFile.TABLE_NAME: {
        JPAQuery<SubmissionFile> query = queryFactory.select(submissionFile);
        query.from(submissionFile);
        query.where(submissionFile.id.eq(id));
        return query.fetchOne();
      }
      case Protocol.TABLE_NAME: {
        JPAQuery<Protocol> query = queryFactory.select(protocol);
        query.from(protocol);
        query.where(protocol.id.eq(id));
        return query.fetchOne();
      }
      case TreatedSample.TABLE_NAME: {
        JPAQuery<TreatedSample> query = queryFactory.select(treatedSample);
        query.from(treatedSample);
        query.where(treatedSample.id.eq(id));
        return query.fetchOne();
      }
      case Treatment.TABLE_NAME: {
        JPAQuery<Treatment> query = queryFactory.select(treatment);
        query.from(treatment);
        query.where(treatment.id.eq(id));
        return query.fetchOne();
      }
      case Address.TABLE_NAME: {
        JPAQuery<Address> query = queryFactory.select(address);
        query.from(address);
        query.where(address.id.eq(id));
        return query.fetchOne();
      }
      case ForgotPassword.TABLE_NAME: {
        JPAQuery<ForgotPassword> query = queryFactory.select(forgotPassword);
        query.from(forgotPassword);
        query.where(forgotPassword.id.eq(id));
        return query.fetchOne();
      }
      case Laboratory.TABLE_NAME: {
        JPAQuery<Laboratory> query = queryFactory.select(laboratory);
        query.from(laboratory);
        query.where(laboratory.id.eq(id));
        return query.fetchOne();
      }
      case PhoneNumber.TABLE_NAME: {
        JPAQuery<PhoneNumber> query = queryFactory.select(phoneNumber);
        query.from(phoneNumber);
        query.where(phoneNumber.id.eq(id));
        return query.fetchOne();
      }
      case User.TABLE_NAME: {
        JPAQuery<User> query = queryFactory.select(user);
        query.from(user);
        query.where(user.id.eq(id));
        return query.fetchOne();
      }
      default:
        throw new AssertionError("Record type " + tableName + " not covered in switch");
    }
  }

  /**
   * Returns all activities involving submission or one of it samples.
   *
   * @param submission
   *          submission
   * @return all activities involving submission or one of it samples
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Activity> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }

    final List<Activity> activities = new ArrayList<>();
    final List<SubmissionSample> samples = submission.getSamples();
    final List<Long> sampleIds =
        samples.stream().map(sa -> sa.getId()).collect(Collectors.toList());
    // Inserts / updates.
    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    BooleanExpression condition =
        activity.recordId.eq(submission.getId()).and(activity.tableName.eq(Submission.TABLE_NAME));
    condition =
        condition.or(activity.recordId.in(sampleIds).and(activity.tableName.eq(Sample.TABLE_NAME)));
    query.where(condition);
    activities.addAll(query.distinct().fetch());
    // Treatments.
    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(treatedSample);
    query.where(treatedSample.in(treatment.treatedSamples));
    query.where(activity.tableName.eq(Treatment.TABLE_NAME));
    query.where(treatedSample.sample.in(samples));
    activities.addAll(query.distinct().fetch());
    // MS Analyses.
    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(msAnalysis);
    query.where(activity.recordId.eq(msAnalysis.id));
    query.from(acquisition);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(activity.tableName.eq(MsAnalysis.TABLE_NAME));
    query.where(acquisition.sample.in(samples));
    activities.addAll(query.distinct().fetch());
    return activities;
  }

  /**
   * Selects all activities of plate's insertion in database.
   *
   * @param plate
   *          plate
   * @return all activities of plate's insertion in database
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Activity> allInsertActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.where(activity.tableName.eq("plate"));
    query.where(activity.actionType.eq(ActionType.INSERT));
    query.where(activity.recordId.eq(plate.getId()));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  /**
   * Selects all activities of plate's wells updates in database.
   *
   * @param plate
   *          plate
   * @return all activities of plate's wells updates in database
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Activity> allUpdateWellActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.join(activity.updates, updateActivity).fetch();
    query.from(well);
    query.where(updateActivity.recordId.eq(well.id));
    query.where(activity.tableName.eq("plate"));
    query.where(updateActivity.tableName.eq("samplecontainer"));
    query.where(well.plate.eq(plate));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  /**
   * Selects treatment activities for sample.
   *
   * @param plate
   *          plate
   * @return treatment activities
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Activity> allTreatmentActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }

    final List<Activity> activities = new ArrayList<>();
    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.join(treatment.treatedSamples, treatedSample);
    query.from(well);
    query.where(activity.recordId.eq(treatment.id));
    query.where(well.eq(treatedSample.container).or(well.eq(treatedSample.destinationContainer)));
    query.where(activity.tableName.eq("treatment"));
    query.where(well.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    Collections.sort(activities, new ActivityComparator(ActivityComparator.Compare.TIMESTAMP));
    return activities;
  }

  /**
   * Selects MS analysis activities for plate.
   *
   * @param plate
   *          plate
   * @return MS analysis activities
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Activity> allMsAnalysisActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(msAnalysis);
    query.where(activity.recordId.eq(msAnalysis.id));
    query.from(acquisition);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.from(well);
    query.where(well.eq(acquisition.container));
    query.where(activity.tableName.eq("msanalysis"));
    query.where(well.plate.eq(plate));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  /**
   * Returns description of activity.
   *
   * @param activity
   *          activity
   * @param locale
   *          user's locale
   * @return description of activity
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public String description(Activity activity, Locale locale) {
    if (activity == null || locale == null) {
      return null;
    }

    MessageResource resources = new MessageResource(ActivityService.class, locale);
    StringBuilder builder = new StringBuilder();
    Object record = record(activity.getTableName(), activity.getRecordId());
    String name = record instanceof Named ? ((Named) record).getName() : "";
    builder.append(resources.message("activity", activity.getActionType().ordinal(),
        activity.getTableName(), name, activity.getRecordId()));
    if (activity.getUpdates() != null) {
      for (UpdateActivity update : activity.getUpdates()) {
        Object updateRecord = record(update.getTableName(), update.getRecordId());
        String updateName = updateRecord instanceof Named ? ((Named) updateRecord).getName() : "";
        builder.append("\n");
        builder.append(resources.message("update", update.getActionType().ordinal(),
            update.getTableName(), updateName, update.getRecordId(), update.getColumn(),
            update.getOldValue(), update.getNewValue()));
      }
    }
    return builder.toString();
  }

  /**
   * Insert activity in database.
   *
   * @param activity
   *          activity to log
   */
  public void insert(Activity activity) {
    activity.setTimestamp(LocalDateTime.now());

    repository.save(activity);
  }
}
