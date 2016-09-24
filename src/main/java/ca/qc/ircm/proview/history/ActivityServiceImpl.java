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
import static ca.qc.ircm.proview.fractionation.QFractionationDetail.fractionationDetail;
import static ca.qc.ircm.proview.history.QActivity.activity;
import static ca.qc.ircm.proview.history.QUpdateActivity.updateActivity;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QPlateSpot.plateSpot;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.AcquisitionMascotFile;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
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
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.tube.Tube;
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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for history.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {
  private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;
  private boolean useFailsafeDescription = true;

  protected ActivityServiceImpl() {
  }

  protected ActivityServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService, boolean useFailsafeDescription) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
    this.useFailsafeDescription = useFailsafeDescription;
  }

  private ResourceBundle resourceBundle(Locale locale) {
    return ResourceBundle.getBundle(ActivityService.class.getName(), locale);
  }

  @Override
  public Object getRecord(Activity activity) {
    if (activity == null) {
      return null;
    }

    if (activity.getTableName().equals("sample")) {
      return entityManager.find(Sample.class, activity.getRecordId());
    } else if (activity.getTableName().equals("plate")) {
      return entityManager.find(Plate.class, activity.getRecordId());
    } else if (activity.getTableName().equals("protocol")) {
      return entityManager.find(Protocol.class, activity.getRecordId());
    } else if (activity.getTableName().equals("submission")) {
      return entityManager.find(Submission.class, activity.getRecordId());
    } else if (activity.getTableName().equals("treatment")) {
      return entityManager.find(Treatment.class, activity.getRecordId());
    } else if (activity.getTableName().equals("msanalysis")) {
      return entityManager.find(MsAnalysis.class, activity.getRecordId());
    } else if (activity.getTableName().equals("dataanalysis")) {
      return entityManager.find(DataAnalysis.class, activity.getRecordId());
    } else if (activity.getTableName().equals("acquisition_to_mascotfile")) {
      return entityManager.find(AcquisitionMascotFile.class, activity.getRecordId());
    } else {
      return null;
    }
  }

  @Override
  public List<Activity> search(ActivitySearchParameters parameters) {
    if (parameters == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    if (parameters.getActionType() != null) {
      query.where(activity.actionType.eq(parameters.getActionType()));
    }
    if (parameters.getTableName() != null) {
      query.where(activity.tableName.eq(parameters.getTableName()));
    }
    if (parameters.getRecordId() != null) {
      query.where(activity.recordId.eq(parameters.getRecordId()));
    }
    return query.fetch();
  }

  @Override
  public List<Activity> allInsertActivities(Sample sample) {
    if (sample == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    if (sample instanceof SubmissionSample) {
      JPAQuery<Activity> query = queryFactory.select(activity);
      query.from(activity);
      query.leftJoin(activity.updates, updateActivity).fetch();
      query.from(submission);
      query.join(submission.samples, submissionSample);
      query.where(activity.recordId.eq(submission.id));
      query.where(activity.tableName.eq("submission"));
      query.where(activity.actionType.eq(ActionType.INSERT));
      query.where(submissionSample.eq((SubmissionSample) sample));
      query.orderBy(activity.timestamp.asc());
      return query.distinct().fetch();
    } else {
      JPAQuery<Activity> query = queryFactory.select(activity);
      query.from(activity);
      query.leftJoin(activity.updates, updateActivity).fetch();
      query.where(activity.tableName.eq("sample"));
      query.where(activity.actionType.eq(ActionType.INSERT));
      query.where(activity.recordId.eq(sample.getId()));
      query.orderBy(activity.timestamp.asc());
      return query.distinct().fetch();
    }
  }

  @Override
  public List<Activity> allInsertActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<Activity>();
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

  @Override
  public List<Activity> allUpdateActivities(Sample sample) {
    if (sample == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    final List<Activity> activities = new ArrayList<>();
    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.where(activity.tableName.eq("sample"));
    query.where(activity.actionType.eq(ActionType.UPDATE));
    query.where(activity.recordId.eq(sample.getId()));
    activities.addAll(query.distinct().fetch());
    query = queryFactory.select(activity);
    query.from(activity);
    query.join(activity.updates, updateActivity).fetch();
    query.where(updateActivity.tableName.eq("sample"));
    query.where(activity.tableName.in("dataanalysis", "msanalysis"));
    query.where(updateActivity.actionType.eq(ActionType.UPDATE));
    query.where(updateActivity.recordId.eq(sample.getId()));
    activities.addAll(query.distinct().fetch());
    Collections.sort(activities, new ActivityComparator(ActivityComparator.Compare.TIMESTAMP));
    return activities;
  }

  @Override
  public List<Activity> allUpdateSpotActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.join(activity.updates, updateActivity).fetch();
    query.from(plateSpot);
    query.where(updateActivity.recordId.eq(plateSpot.id));
    query.where(activity.tableName.eq("plate"));
    query.where(updateActivity.tableName.eq("samplecontainer"));
    query.where(plateSpot.plate.eq(plate));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public List<Activity> allTreatmentActivities(Sample sample) {
    if (sample == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(treatmentSample);
    query.where(treatmentSample.in(treatment.treatmentSamples));
    query.where(activity.tableName.eq("treatment"));
    query.where(treatmentSample.sample.eq(sample));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public List<Activity> allTreatmentActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    final List<Activity> activities = new ArrayList<Activity>();
    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.join(treatmentSample);
    query.where(treatmentSample.in(treatment.treatmentSamples));
    query.from(plateSpot);
    query.where(plateSpot.eq(treatmentSample.container));
    query.where(activity.tableName.eq("treatment"));
    query.where(plateSpot.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(fractionationDetail);
    query.where(fractionationDetail._super.in(treatment.treatmentSamples));
    query.from(plateSpot);
    query.where(plateSpot.eq(fractionationDetail.destinationContainer));
    query.where(activity.tableName.eq("treatment"));
    query.where(plateSpot.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(treatment);
    query.where(activity.recordId.eq(treatment.id));
    query.from(sampleTransfer);
    query.where(sampleTransfer._super.in(treatment.treatmentSamples));
    query.from(plateSpot);
    query.where(plateSpot.eq(sampleTransfer.destinationContainer));
    query.where(activity.tableName.eq("treatment"));
    query.where(plateSpot.plate.eq(plate));
    activities.addAll(query.distinct().fetch());

    Collections.sort(activities, new ActivityComparator(ActivityComparator.Compare.TIMESTAMP));
    return activities;
  }

  @Override
  public List<Activity> allMsAnalysisActivities(Sample sample) {
    if (sample == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(msAnalysis);
    query.where(activity.recordId.eq(msAnalysis.id));
    query.from(acquisition);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.where(activity.tableName.eq("msanalysis"));
    query.where(acquisition.sample.eq(sample));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public List<Activity> allMsAnalysisActivities(Plate plate) {
    if (plate == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(msAnalysis);
    query.where(activity.recordId.eq(msAnalysis.id));
    query.from(acquisition);
    query.where(msAnalysis.acquisitions.contains(acquisition));
    query.from(plateSpot);
    query.where(plateSpot.eq(acquisition.container));
    query.where(activity.tableName.eq("msanalysis"));
    query.where(plateSpot.plate.eq(plate));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public List<Activity> allDataAnalysisActivities(Sample sample) {
    if (sample == null || !(sample instanceof SubmissionSample)) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(dataAnalysis);
    query.where(activity.recordId.eq(dataAnalysis.id));
    query.where(activity.tableName.eq("dataanalysis"));
    query.where(dataAnalysis.sample.eq((SubmissionSample) sample));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public List<Activity> allMascotFileActivities(Sample sample) {
    if (sample == null) {
      return new ArrayList<Activity>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<Activity> query = queryFactory.select(activity);
    query.from(activity);
    query.leftJoin(activity.updates, updateActivity).fetch();
    query.from(acquisitionMascotFile);
    query.where(activity.recordId.eq(acquisitionMascotFile.id));
    query.from(acquisition);
    query.where(acquisition.eq(acquisitionMascotFile.acquisition));
    query.where(activity.tableName.eq("acquisition_to_mascotfile"));
    query.where(acquisition.sample.eq(sample));
    query.orderBy(activity.timestamp.asc());
    return query.distinct().fetch();
  }

  @Override
  public String sampleDescription(Sample sample, Activity activity, Locale locale) {
    if (sample == null || activity == null || locale == null) {
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
        return treatmentDescription(bundle, activity, sample);
      } else if (activity.getTableName().equals("msanalysis")) {
        return msAnalysisDescription(bundle, activity, sample);
      } else if (activity.getTableName().equals("acquisition_to_mascotfile")) {
        return mascotFileDescription(bundle, activity);
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
    if (activity.getActionType() == Activity.ActionType.INSERT) {
      return message(bundle, "Sample.INSERT");
    }

    // Update of sample.
    if (activity.getActionType() == Activity.ActionType.UPDATE) {
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
                message.append(message(bundle, "Sample.UPDATE", updateActivity.getColumn(),
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
      case SPOT:
        PlateSpot spot = (PlateSpot) sampleContainer;
        String plateName = spot.getPlate().getName();
        String spotName = spot.getName();
        return message(bundle, "PlateSpot.Location", plateName, spotName);
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
        message.append("\n");
        message.append(message(bundle, "DataAnalysis.Sample.UPDATE", updateActivity.getColumn(),
            updateActivity.getOldValue(), updateActivity.getNewValue()));
      }
    }

    return message.toString();
  }

  private String treatmentDescription(ResourceBundle bundle, Activity activity, Sample sample) {
    Treatment<?> treatment = entityManager.find(Treatment.class, activity.getRecordId());
    Collection<TreatmentSample> treatmentSamples = new ArrayList<>();
    for (TreatmentSample treatmentSample : treatment.getTreatmentSamples()) {
      if (treatmentSample.getSample().equals(sample)) {
        treatmentSamples.add(treatmentSample);
      }
    }
    return treatmentDescription(bundle, activity, treatment, treatmentSamples);
  }

  private String treatmentDescription(ResourceBundle bundle, Activity activity, Plate plate) {
    Set<Long> spotIds = new HashSet<>();
    for (PlateSpot spot : plate.getSpots()) {
      spotIds.add(spot.getId());
    }
    Treatment<?> treatment = entityManager.find(Treatment.class, activity.getRecordId());
    Collection<TreatmentSample> treatmentSamples = new ArrayList<>();
    for (TreatmentSample treatmentSample : treatment.getTreatmentSamples()) {
      if (treatmentSample.getContainer() instanceof PlateSpot
          && spotIds.contains(treatmentSample.getContainer().getId())) {
        treatmentSamples.add(treatmentSample);
      } else if (treatmentSample instanceof SampleTransfer) {
        SampleTransfer sampleTransfer = (SampleTransfer) treatmentSample;
        if (sampleTransfer.getDestinationContainer() instanceof PlateSpot
            && spotIds.contains(sampleTransfer.getDestinationContainer().getId())) {
          treatmentSamples.add(treatmentSample);
        }
      } else if (treatmentSample instanceof FractionationDetail) {
        FractionationDetail fractionationDetail = (FractionationDetail) treatmentSample;
        if (fractionationDetail.getDestinationContainer() instanceof PlateSpot
            && spotIds.contains(fractionationDetail.getDestinationContainer().getId())) {
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
        treatment.isDeleted() ? treatment.getDeletionType().ordinal() : null));
    for (TreatmentSample treatmentSample : treatmentSamples) {
      String container = containerMessage(bundle, treatmentSample.getContainer());
      message.append("\n");
      switch (treatment.getType()) {
        case DIGESTION:
        case ENRICHMENT:
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              treatmentSample.getContainer().getType().ordinal(), container));
          break;
        case DILUTION: {
          DilutedSample dilutedSample = (DilutedSample) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              treatmentSample.getContainer().getType().ordinal(), container,
              dilutedSample.getSourceVolume(), dilutedSample.getSolvent(),
              dilutedSample.getSolventVolume()));
          break;
        }
        case FRACTIONATION: {
          FractionationDetail fractionationDetail = (FractionationDetail) treatmentSample;
          String destinationContainer =
              containerMessage(bundle, fractionationDetail.getDestinationContainer());
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              fractionationDetail.getContainer().getType().ordinal(), container,
              fractionationDetail.getDestinationContainer().getType().ordinal(),
              destinationContainer, fractionationDetail.getPosition()));
          break;
        }
        case SOLUBILISATION: {
          SolubilisedSample solubilisedSample = (SolubilisedSample) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              treatmentSample.getContainer().getType().ordinal(), container,
              solubilisedSample.getSolvent(), solubilisedSample.getSolventVolume()));
          break;
        }
        case STANDARD_ADDITION: {
          AddedStandard addedStandard = (AddedStandard) treatmentSample;
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              treatmentSample.getContainer().getType().ordinal(), container,
              addedStandard.getName(), addedStandard.getQuantity()));
          break;
        }
        case TRANSFER: {
          SampleTransfer sampleTransfer = (SampleTransfer) treatmentSample;
          String destinationContainer =
              containerMessage(bundle, sampleTransfer.getDestinationContainer());
          message.append(message(bundle, key + ".Sample", treatmentSample.getSample().getLims(),
              sampleTransfer.getContainer().getType().ordinal(), container,
              sampleTransfer.getDestinationContainer().getType().ordinal(), destinationContainer));
          break;
        }
        default:
      }
    }
    return message.toString();
  }

  private String msAnalysisDescription(ResourceBundle bundle, Activity activity, Sample sample) {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, activity.getRecordId());
    Collection<Acquisition> acquisitions = new ArrayList<>();
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getSample().equals(sample)) {
        acquisitions.add(acquisition);
      }
    }
    StringBuilder message = new StringBuilder();
    message.append(msAnalysisDescription(bundle, activity, msAnalysis, acquisitions));
    // Updates of sample status.
    for (UpdateActivity updateActivity : activity.getUpdates()) {
      if ("sample".equals(updateActivity.getTableName())) {
        Sample updateSample = entityManager.find(Sample.class, updateActivity.getRecordId());
        if (sample.equals(updateSample)) {
          message.append("\n");
          message.append(message(bundle, "MSAnalysis.Sample.UPDATE", sample.getLims(),
              updateActivity.getColumn(), updateActivity.getOldValue(),
              updateActivity.getNewValue()));
        }
      }
    }
    return message.toString();
  }

  private String msAnalysisDescription(ResourceBundle bundle, Activity activity, Plate plate) {
    Set<Long> spotIds = new HashSet<>();
    for (PlateSpot spot : plate.getSpots()) {
      spotIds.add(spot.getId());
    }
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, activity.getRecordId());
    Collection<Acquisition> acquisitions = new ArrayList<>();
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      if (acquisition.getContainer() instanceof PlateSpot
          && spotIds.contains(acquisition.getContainer().getId())) {
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
        msAnalysis.isDeleted() ? msAnalysis.getDeletionType().ordinal() : null));
    for (Acquisition acquisition : acquisitions) {
      String container = containerMessage(bundle, acquisition.getContainer());
      message.append("\n");
      message.append(message(bundle, key + ".Acquisition", acquisition.getSample().getLims(),
          acquisition.getContainer().getType().ordinal(), container, acquisition.getPosition()));
    }
    return message.toString();
  }

  private String mascotFileDescription(ResourceBundle bundle, Activity activity) {
    AcquisitionMascotFile acquisitionMascotFile =
        entityManager.find(AcquisitionMascotFile.class, activity.getRecordId());
    String mainMessage = null;
    switch (activity.getActionType()) {
      case UPDATE:
        StringBuilder message = new StringBuilder();

        for (UpdateActivity updateActivity : activity.getUpdates()) {
          if (message.length() > 0) {
            message.append("\n");
          }

          message.append(message(bundle, "MascotFile." + activity.getActionType(),
              updateActivity.getColumn(), updateActivity.getOldValue(),
              updateActivity.getNewValue(), acquisitionMascotFile.getMascotFile().getName(),
              Date.from(acquisitionMascotFile.getMascotFile().getSearchDate()),
              acquisitionMascotFile.getAcquisition().getLims()));
        }

        mainMessage = message.toString();
        break;
      case INSERT:
      case DELETE:
      default:
        throw new AssertionError(
            "invalid action type " + activity.getActionType() + " for transfer activity");
    }

    return mainMessage;
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

  @Override
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
    if (activity.getActionType() == Activity.ActionType.UPDATE) {
      for (UpdateActivity updateActivity : activity.getUpdates()) {
        if (message.length() > 0) {
          message.append("\n");
        }

        // Get spot.
        PlateSpot spot = entityManager.find(PlateSpot.class, updateActivity.getRecordId());

        String mainMessage = null;
        if (updateActivity.getColumn().equals("banned")) {
          mainMessage = message(bundle, "PlateSpot.Banned.UPDATE", spot.getName(),
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

  @Override
  public void insert(Activity activity) {
    activity.setTimestamp(Instant.now());

    entityManager.persist(activity);
  }
}
