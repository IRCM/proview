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

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;
import static ca.qc.ircm.proview.fractionation.QFraction.fraction;
import static ca.qc.ircm.proview.history.QActivity.activity;
import static ca.qc.ircm.proview.history.QUpdateActivity.updateActivity;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.plate.QWell.well;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.transfer.QTransferedSample.transferedSample;
import static ca.qc.ircm.proview.treatment.QProtocol.protocol;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.fractionation.Fraction;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.DeletionType;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.transfer.TransferedSample;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.tube.Tube;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for activity.
 */
@Service
@Transactional
public class ActivityService {
  private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;
  private boolean useFailsafeDescription = true;

  protected ActivityService() {
  }

  protected ActivityService(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService, boolean useFailsafeDescription) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
    this.useFailsafeDescription = useFailsafeDescription;
  }

  private ResourceBundle resourceBundle(Locale locale) {
    return ResourceBundle.getBundle(ActivityService.class.getName(), locale);
  }

  /**
   * Returns object associated with this activity.
   *
   * @param activity
   *          activity
   * @return object associated with this activity
   */
  public Object record(Activity activity) {
    if (activity == null || activity.getTableName() == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    switch (activity.getTableName()) {
      case Submission.TABLE_NAME: {
        JPAQuery<Submission> query = queryFactory.select(submission);
        query.from(submission);
        query.where(submission.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case Sample.TABLE_NAME: {
        JPAQuery<Sample> query = queryFactory.select(sample);
        query.from(sample);
        query.where(sample.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case Plate.TABLE_NAME: {
        JPAQuery<Plate> query = queryFactory.select(plate);
        query.from(plate);
        query.where(plate.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case Protocol.TABLE_NAME: {
        JPAQuery<Protocol> query = queryFactory.select(protocol);
        query.from(protocol);
        query.where(protocol.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case Treatment.TABLE_NAME: {
        JPAQuery<Treatment<?>> query = queryFactory.select(treatment);
        query.from(treatment);
        query.where(treatment.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case MsAnalysis.TABLE_NAME: {
        JPAQuery<MsAnalysis> query = queryFactory.select(msAnalysis);
        query.from(msAnalysis);
        query.where(msAnalysis.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      case DataAnalysis.TABLE_NAME: {
        JPAQuery<DataAnalysis> query = queryFactory.select(dataAnalysis);
        query.from(dataAnalysis);
        query.where(dataAnalysis.id.eq(activity.getRecordId()));
        return query.fetchOne();
      }
      default:
        throw new AssertionError(
            "Record type " + activity.getTableName() + " not covered in switch");
    }
  }

  /**
   * Returns all activities involving submission or one of it samples.
   *
   * @param submission
   *          submission
   * @return all activities involving submission or one of it samples
   */
  public List<Activity> all(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

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
    condition.or(activity.recordId.in(sampleIds).and(activity.tableName.eq(Sample.TABLE_NAME)));
    query.where(condition);
    activities.addAll(query.distinct().fetch());
    // Treatments.
    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(treatmentSample);
    query.where(treatmentSample.in(treatment.treatmentSamples));
    query.where(activity.tableName.eq(Treatment.TABLE_NAME));
    query.where(treatmentSample.sample.in(samples));
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
    // Data analyses.
    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(dataAnalysis);
    query.where(activity.recordId.eq(dataAnalysis.id));
    query.where(activity.tableName.eq(DataAnalysis.TABLE_NAME));
    query.where(dataAnalysis.sample.in(samples));
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
  public List<Activity> allInsertActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

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
  public List<Activity> allUpdateWellActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

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
  public List<Activity> allTreatmentActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    final List<Activity> activities = new ArrayList<>();
    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.join(treatmentSample);
    query.where(treatmentSample.in(treatment.treatmentSamples));
    query.from(well);
    query.where(well.eq(treatmentSample.container));
    query.where(activity.tableName.eq("treatment"));
    query.where(well.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(fraction);
    query.where(fraction._super.in(treatment.treatmentSamples));
    query.from(well);
    query.where(well.eq(fraction.destinationContainer));
    query.where(activity.tableName.eq("treatment"));
    query.where(well.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(transferedSample);
    query.where(transferedSample._super.in(treatment.treatmentSamples));
    query.from(well);
    query.where(well.eq(transferedSample.destinationContainer));
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
  public List<Activity> allMsAnalysisActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

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
   * Returns description of submission's activity.
   *
   * @param activity
   *          activity
   * @param submission
   *          submission
   * @param locale
   *          user's locale
   * @return description of submission's activity
   */
  public String description(Activity activity, Submission submission, Locale locale) {
    if (activity == null || submission == null || locale == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    ResourceBundle bundle = resourceBundle(locale);
    try {
      if (activity.getTableName().equals("sample")) {
        return sampleDescription(bundle, activity);
      } else if (activity.getTableName().equals("submission")) {
        return submissionDescription(bundle, activity);
      } else if (activity.getTableName().equals("dataanalysis")) {
        return dataAnalysisDescription(bundle, activity);
      } else if (activity.getTableName().equals("treatment")) {
        return treatmentDescription(bundle, activity, submission);
      } else if (activity.getTableName().equals("msanalysis")) {
        return msAnalysisDescription(bundle, activity, submission);
      } else if (statusChanged(activity)) {
        return statusDescription(bundle, activity);
      }
    } catch (RuntimeException e) {
      logger.error("Exception was throw for sample activity description", e);
    } catch (Error e) {
      logger.error("Error was throw for sample activity description", e);
    }
    if (useFailsafeDescription) {
      return failsafeDescription(bundle, activity);
    } else {
      return null;
    }
  }

  private String sampleDescription(ResourceBundle bundle, Activity activity) {
    // Insertion of control.
    if (activity.getActionType() == ActionType.INSERT) {
      return message(bundle, "Sample.INSERT");
    }

    // Update of sample.
    Sample sample = entityManager.find(Sample.class, activity.getRecordId());
    if (activity.getActionType() == ActionType.UPDATE) {
      StringBuilder message = new StringBuilder();

      for (UpdateActivity updateActivity : activity.getUpdates()) {
        if (message.length() > 0) {
          message.append("\n");
        }

        if (updateActivity.getTableName().equals("sample")) {
          switch (updateActivity.getActionType()) {
            case INSERT:
              throw new AssertionError(
                  "Unexpected ActionType INSERT for update activity on sample table");
            case UPDATE:
              if (updateActivity.getColumn().equals("structureFile")) {
                message.append(message(bundle, "Sample.UPDATE.structureFile"));
              } else {
                message.append(
                    message(bundle, "Sample.UPDATE", sample.getName(), updateActivity.getColumn(),
                        updateActivity.getOldValue(), updateActivity.getNewValue()));
              }
              break;
            case DELETE:
              throw new AssertionError(
                  "Unexpected ActionType DELETE for update activity on sample table");
            default:
          }
        } else if (updateActivity.getTableName().equals("contaminant")) {
          Contaminant contaminant =
              entityManager.find(Contaminant.class, updateActivity.getRecordId());
          switch (updateActivity.getActionType()) {
            case INSERT:
              message.append(message(bundle, "Sample.Contaminant.INSERT", contaminant.getName()));
              break;
            case UPDATE:
              message.append(message(bundle, "Sample.Contaminant.UPDATE", contaminant.getName(),
                  updateActivity.getColumn(), updateActivity.getOldValue(),
                  updateActivity.getNewValue()));
              break;
            case DELETE:
              message.append(message(bundle, "Sample.Contaminant.DELETE", contaminant.getName()));
              break;
            default:
          }
        } else if (updateActivity.getTableName().equals("standard")) {
          Standard standard = entityManager.find(Standard.class, updateActivity.getRecordId());
          switch (updateActivity.getActionType()) {
            case INSERT:
              message.append(message(bundle, "Sample.Standard.INSERT", standard.getName()));
              break;
            case UPDATE:
              message.append(message(bundle, "Sample.Standard.UPDATE", standard.getName(),
                  updateActivity.getColumn(), updateActivity.getOldValue(),
                  updateActivity.getNewValue()));
              break;
            case DELETE:
              message.append(message(bundle, "Sample.Standard.DELETE", standard.getName()));
              break;
            default:
          }
        } else if (updateActivity.getTableName().equals("samplesolvent")) {
          SampleSolvent sampleSolvent =
              entityManager.find(SampleSolvent.class, updateActivity.getRecordId());
          String solventName = sampleSolvent.getSolvent().getLabel(bundle.getLocale());
          switch (updateActivity.getActionType()) {
            case INSERT:
              message.append(message(bundle, "Sample.SampleSolvent.INSERT", solventName,
                  updateActivity.getColumn(), updateActivity.getOldValue(),
                  updateActivity.getNewValue()));
              break;
            case UPDATE:
              throw new AssertionError(
                  "Unexpected ActionType UPDATE for update activity on SampleSovent table");
            case DELETE:
              message.append(message(bundle, "Sample.SampleSolvent.DELETE", solventName,
                  updateActivity.getColumn(), updateActivity.getOldValue(),
                  updateActivity.getNewValue()));
              break;
            default:
          }
        } else {
          throw new AssertionError(
              "Unexpected update activity, activity " + updateActivity.getId());
        }
      }

      return message.toString();
    }

    throw new AssertionError("Unexpected activity, activity " + activity.getId());
  }

  private String message(ResourceBundle bundle, String key, Object... replacements) {
    return MessageFormat.format(bundle.getString(key), replacements);
  }

  private String containerMessage(ResourceBundle bundle, SampleContainer sampleContainer) {
    switch (sampleContainer.getType()) {
      case TUBE:
        Tube tube = (Tube) sampleContainer;
        return tube.getName();
      case WELL:
        Well well = (Well) sampleContainer;
        String plateName = well.getPlate().getName();
        String wellName = well.getName();
        return message(bundle, "Well.Location", plateName, wellName);
      default:
        throw new AssertionError(
            "SampleContainer type " + sampleContainer.getType() + " not coverred in switch case");
    }
  }

  private boolean statusChanged(Activity activity) {
    if (activity.getUpdates() != null) {
      for (UpdateActivity updateActivity : activity.getUpdates()) {
        if (updateActivity.getTableName().equals("sample")
            && updateActivity.getColumn().equals("status")) {
          return true;
        }
      }
    }
    return false;
  }

  private String submissionDescription(ResourceBundle bundle, Activity activity) {
    return message(bundle, "Submission.INSERT");
  }

  private String dataAnalysisDescription(ResourceBundle bundle, Activity activity) {
    StringBuilder message = new StringBuilder();

    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, activity.getRecordId());
    switch (activity.getActionType()) {
      case INSERT:
        message.append(message(bundle, "DataAnalysis.INSERT", dataAnalysis.getType().ordinal(),
            dataAnalysis.getProtein(), dataAnalysis.getPeptide(), dataAnalysis.getMaxWorkTime()));
        break;
      case UPDATE:
        message.append(message(bundle, "DataAnalysis.UPDATE", dataAnalysis.getType().ordinal(),
            dataAnalysis.getProtein(), dataAnalysis.getPeptide(), dataAnalysis.getMaxWorkTime()));
        break;
      case DELETE:
      default:
        throw new AssertionError(
            "ActionType " + activity.getActionType() + " not covered in switch case");
    }
    if (message.length() == 0) {
      throw new AssertionError(
          "ActionType " + activity.getActionType() + " not covered in switch case");
    }

    // Updates of sample status.
    for (UpdateActivity updateActivity : activity.getUpdates()) {
      if ("dataanalysis".equals(updateActivity.getTableName())) {
        message.append("\n");
        message
            .append(message(bundle, "DataAnalysis.DataAnalysis.UPDATE", updateActivity.getColumn(),
                updateActivity.getOldValue(), updateActivity.getNewValue()));
      } else if ("sample".equals(updateActivity.getTableName())) {
        Sample sample = entityManager.find(Sample.class, updateActivity.getRecordId());
        message.append("\n");
        message.append(message(bundle, "DataAnalysis.Sample.UPDATE", sample.getName(),
            updateActivity.getColumn(), updateActivity.getOldValue(),
            updateActivity.getNewValue()));
      }
    }

    return message.toString();
  }

  private String treatmentDescription(ResourceBundle bundle, Activity activity,
      Submission submission) {
    Treatment<?> treatment = entityManager.find(Treatment.class, activity.getRecordId());
    Collection<TreatmentSample> treatmentSamples = new ArrayList<>();
    Set<Long> sampleIds =
        submission.getSamples().stream().map(sa -> sa.getId()).collect(Collectors.toSet());
    for (TreatmentSample treatmentSample : treatment.getTreatmentSamples()) {
      if (sampleIds.contains(treatmentSample.getSample().getId())) {
        treatmentSamples.add(treatmentSample);
      }
    }
    return treatmentDescription(bundle, activity, treatment, treatmentSamples);
  }

  private String treatmentDescription(ResourceBundle bundle, Activity activity, Plate plate) {
    Set<Long> wellIds = new HashSet<>();
    for (Well well : plate.getWells()) {
      wellIds.add(well.getId());
    }
    Treatment<?> treatment = entityManager.find(Treatment.class, activity.getRecordId());
    Collection<TreatmentSample> treatmentSamples = new ArrayList<>();
    for (TreatmentSample treatmentSample : treatment.getTreatmentSamples()) {
      if (treatmentSample.getContainer() instanceof Well
          && wellIds.contains(treatmentSample.getContainer().getId())) {
        treatmentSamples.add(treatmentSample);
      } else if (treatmentSample instanceof TransferedSample) {
        TransferedSample transferedSample = (TransferedSample) treatmentSample;
        if (transferedSample.getDestinationContainer() instanceof Well
            && wellIds.contains(transferedSample.getDestinationContainer().getId())) {
          treatmentSamples.add(treatmentSample);
        }
      } else if (treatmentSample instanceof Fraction) {
        Fraction fraction = (Fraction) treatmentSample;
        if (fraction.getDestinationContainer() instanceof Well
            && wellIds.contains(fraction.getDestinationContainer().getId())) {
          treatmentSamples.add(treatmentSample);
        }
      }
    }
    return treatmentDescription(bundle, activity, treatment, treatmentSamples);
  }

  private String treatmentDescription(ResourceBundle bundle, Activity activity,
      Treatment<?> treatment, Collection<TreatmentSample> treatmentSamples) {
    StringBuilder message = new StringBuilder();
    String key = "Treatment." + treatment.getType();
    message.append(message(bundle, key + "." + activity.getActionType(),
        treatment.isDeleted() ? treatment.getDeletionType().ordinal()
            : DeletionType.ERRONEOUS.ordinal()));
    for (TreatmentSample treatmentSample : treatmentSamples) {
      String container = containerMessage(bundle, treatmentSample.getContainer());
      message.append("\n");
      switch (treatment.getType()) {
        case DIGESTION:
        case ENRICHMENT:
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              treatmentSample.getContainer().getType().ordinal(), container));
          break;
        case DILUTION: {
          DilutedSample dilutedSample = (DilutedSample) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              treatmentSample.getContainer().getType().ordinal(), container,
              dilutedSample.getSourceVolume(), dilutedSample.getSolvent(),
              dilutedSample.getSolventVolume()));
          break;
        }
        case FRACTIONATION: {
          Fraction fraction = (Fraction) treatmentSample;
          String destinationContainer =
              containerMessage(bundle, fraction.getDestinationContainer());
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              fraction.getContainer().getType().ordinal(), container,
              fraction.getDestinationContainer().getType().ordinal(), destinationContainer,
              fraction.getPosition()));
          break;
        }
        case SOLUBILISATION: {
          SolubilisedSample solubilisedSample = (SolubilisedSample) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              treatmentSample.getContainer().getType().ordinal(), container,
              solubilisedSample.getSolvent(), solubilisedSample.getSolventVolume()));
          break;
        }
        case STANDARD_ADDITION: {
          AddedStandard addedStandard = (AddedStandard) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              treatmentSample.getContainer().getType().ordinal(), container,
              addedStandard.getName(), addedStandard.getQuantity()));
          break;
        }
        case TRANSFER: {
          TransferedSample transferedSample = (TransferedSample) treatmentSample;
          String destinationContainer =
              containerMessage(bundle, transferedSample.getDestinationContainer());
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getName(),
              transferedSample.getContainer().getType().ordinal(), container,
              transferedSample.getDestinationContainer().getType().ordinal(),
              destinationContainer));
          break;
        }
        default:
      }
    }
    return message.toString();
  }

  private String msAnalysisDescription(ResourceBundle bundle, Activity activity,
      Submission submission) {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, activity.getRecordId());
    Collection<Acquisition> acquisitions = new ArrayList<>();
    Set<Long> sampleIds =
        submission.getSamples().stream().map(sa -> sa.getId()).collect(Collectors.toSet());
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (sampleIds.contains(acquisition.getSample().getId())) {
        acquisitions.add(acquisition);
      }
    }
    StringBuilder message = new StringBuilder();
    message.append(msAnalysisDescription(bundle, activity, msAnalysis, acquisitions));
    // Updates of sample status.
    for (UpdateActivity updateActivity : activity.getUpdates()) {
      if ("sample".equals(updateActivity.getTableName())) {
        Sample updateSample = entityManager.find(Sample.class, updateActivity.getRecordId());
        if (sampleIds.contains(updateSample.getId())) {
          message.append("\n");
          message.append(message(bundle, "MSAnalysis.Sample.UPDATE", updateSample.getName(),
              updateActivity.getColumn(), updateActivity.getOldValue(),
              updateActivity.getNewValue()));
        }
      }
    }
    return message.toString();
  }

  private String msAnalysisDescription(ResourceBundle bundle, Activity activity, Plate plate) {
    Set<Long> wellIds = new HashSet<>();
    for (Well well : plate.getWells()) {
      wellIds.add(well.getId());
    }
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, activity.getRecordId());
    Collection<Acquisition> acquisitions = new ArrayList<>();
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getContainer() instanceof Well
          && wellIds.contains(acquisition.getContainer().getId())) {
        acquisitions.add(acquisition);
      }
    }
    return msAnalysisDescription(bundle, activity, msAnalysis, acquisitions);
  }

  private String msAnalysisDescription(ResourceBundle bundle, Activity activity,
      MsAnalysis msAnalysis, Collection<Acquisition> acquisitions) {
    StringBuilder message = new StringBuilder();
    String key = "MSAnalysis";
    message.append(message(bundle, key + "." + activity.getActionType(),
        msAnalysis.isDeleted() ? msAnalysis.getDeletionType().ordinal()
            : DeletionType.ERRONEOUS.ordinal()));
    for (Acquisition acquisition : acquisitions) {
      String container = containerMessage(bundle, acquisition.getContainer());
      message.append("\n");
      message.append(message(bundle, key + ".Acquisition", acquisition.getSample().getName(),
          acquisition.getContainer().getType().ordinal(), container, acquisition.getPosition()));
    }
    return message.toString();
  }

  private String statusDescription(ResourceBundle bundle, Activity activity) {
    UpdateActivity statusChanged = null;
    Iterator<UpdateActivity> updateIter = activity.getUpdates().iterator();
    while (statusChanged == null && updateIter.hasNext()) {
      UpdateActivity updateActivity = updateIter.next();
      if (updateActivity.getTableName().equals("sample")
          && updateActivity.getColumn().equals("status")) {
        statusChanged = updateActivity;
      }
    }
    if (statusChanged == null) {
      throw new AssertionError("this methods expects at least one status change activity");
    }
    return message(bundle, "Sample.Status.UPDATE", statusChanged.getOldValue(),
        statusChanged.getNewValue(), activity.getTableName());
  }

  /**
   * Returns description of activity for plate history.
   *
   * @param plate
   *          plate
   * @param activity
   *          activity
   * @param locale
   *          user's locale
   * @return description of activity for plate history
   */
  public String plateDescription(Plate plate, Activity activity, Locale locale) {
    if (plate == null || activity == null || locale == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    ResourceBundle bundle = resourceBundle(locale);
    try {
      if (activity.getTableName().equals("plate")) {
        return plateDescription(bundle, activity);
      } else if (activity.getTableName().equals("treatment")) {
        return treatmentDescription(bundle, activity, plate);
      } else if (activity.getTableName().equals("msanalysis")) {
        return msAnalysisDescription(bundle, activity, plate);
      }
    } catch (RuntimeException e) {
      logger.error("Exception was throw for plate activity description", e);
    } catch (Error e) {
      logger.error("Error was throw for plate activity description", e);
    }
    if (useFailsafeDescription) {
      return failsafeDescription(bundle, activity);
    } else {
      return null;
    }
  }

  private String plateDescription(ResourceBundle bundle, Activity activity) {
    Plate plate = entityManager.find(Plate.class, activity.getRecordId());

    switch (activity.getActionType()) {
      case INSERT:
        return message(bundle, "Plate.INSERT", plate.getName());
      case UPDATE:
        return plateUpdateDescription(bundle, activity);
      case DELETE:
      default:
        throw new AssertionError(
            "invalid action type " + activity.getActionType() + " for plate activity");
    }
  }

  private String plateUpdateDescription(ResourceBundle bundle, Activity activity) {
    StringBuilder message = new StringBuilder();

    // We suppose we just write update.
    if (activity.getActionType() == ActionType.UPDATE) {
      for (UpdateActivity updateActivity : activity.getUpdates()) {
        if (message.length() > 0) {
          message.append("\n");
        }

        // Get well.
        Well well = entityManager.find(Well.class, updateActivity.getRecordId());

        String mainMessage = null;
        if (updateActivity.getColumn().equals("banned")) {
          mainMessage = message(bundle, "Well.Banned.UPDATE", well.getName(),
              Integer.parseInt(updateActivity.getNewValue()));
        } else {
          throw new AssertionError(
              "column " + updateActivity.getColumn() + " not covered in switch case");
        }
        message.append(mainMessage);
      }
    }

    return message.toString();
  }

  private String failsafeDescription(ResourceBundle bundle, Activity activity) {
    logger.warn("Using failsafe description for activity {} - {} {} {}", activity.getId(),
        activity.getActionType(), activity.getTableName(), activity.getRecordId());

    StringBuilder builder = new StringBuilder();
    String mainMessage = message(bundle, "failsafe", activity.getActionType().ordinal(),
        activity.getTableName(), activity.getRecordId());
    builder.append(mainMessage);

    if (activity.getUpdates() != null) {
      for (UpdateActivity updateActivity : activity.getUpdates()) {
        builder.append("\n");
        String updateMessage = message(bundle, "failsafe.update",
            updateActivity.getActionType().ordinal(), updateActivity.getTableName(),
            updateActivity.getRecordId(), updateActivity.getColumn(), updateActivity.getOldValue(),
            updateActivity.getNewValue());
        builder.append(updateMessage);
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
    activity.setTimestamp(Instant.now());

    entityManager.persist(activity);
  }
}
