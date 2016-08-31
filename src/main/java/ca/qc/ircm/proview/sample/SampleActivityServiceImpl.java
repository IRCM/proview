package ca.qc.ircm.proview.sample;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.history.UpdateActivityBuilder;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SampleActivityServiceImpl implements SampleActivityService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected SampleActivityServiceImpl() {
  }

  protected SampleActivityServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insertControl(final Control control) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(control.getId());
    activity.setUser(user);
    activity.setTableName("sample");
    activity.setJustification(null);
    activity.setUpdates(null);
    return activity;
  }

  @Override
  public Optional<Activity> update(final Sample newSample, final String justification) {
    User user = authorizationService.getCurrentUser();

    final Sample oldSample = entityManager.find(Sample.class, newSample.getId());

    final Collection<UpdateActivityBuilder> updateBuilders = new ArrayList<UpdateActivityBuilder>();
    class SampleUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("sample");
        actionType(ActionType.UPDATE);
        recordId(newSample.getId());
      }
    }

    class ContaminantUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("contaminant");
        actionType(ActionType.UPDATE);
      }

      ContaminantUpdateActivityBuilder oldContaminant(Contaminant oldContaminant) {
        recordId(oldContaminant.getId());
        return this;
      }

      ContaminantUpdateActivityBuilder newContaminant(Contaminant newContaminant) {
        recordId(newContaminant.getId());
        return this;
      }
    }

    class StandardUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("standard");
        actionType(ActionType.UPDATE);
      }

      StandardUpdateActivityBuilder oldStandard(Standard oldStandard) {
        recordId(oldStandard.getId());
        return this;
      }

      StandardUpdateActivityBuilder newStandard(Standard newStandard) {
        recordId(newStandard.getId());
        return this;
      }
    }

    class SolventUpdateActivityBuilder extends UpdateActivityBuilder {
      {
        tableName("solvent");
      }
    }

    updateBuilders.add(new SampleUpdateActivityBuilder().column("lims")
        .oldValue(oldSample.getLims()).newValue(newSample.getLims()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("comments")
        .oldValue(oldSample.getComments()).newValue(newSample.getComments()));
    updateBuilders.add(new SampleUpdateActivityBuilder().column("name")
        .oldValue(oldSample.getName()).newValue(newSample.getName()));
    // Standards.
    List<Standard> oldStandards =
        oldSample.getStandards() != null ? oldSample.getStandards() : new ArrayList<Standard>();
    List<Standard> newStandards =
        newSample.getStandards() != null ? newSample.getStandards() : new ArrayList<Standard>();
    for (Standard oldStandard : oldStandards) {
      boolean deleted = true;
      for (Standard newStandard : newStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          deleted = false;
        }
      }
      if (deleted) {
        updateBuilders.add(new StandardUpdateActivityBuilder().oldStandard(oldStandard)
            .actionType(ActionType.DELETE));
      }
    }
    for (Standard oldStandard : oldStandards) {
      for (Standard newStandard : newStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          updateBuilders.add(new StandardUpdateActivityBuilder().newStandard(newStandard)
              .column("name").oldValue(oldStandard.getName()).newValue(newStandard.getName()));
          updateBuilders
              .add(new StandardUpdateActivityBuilder().newStandard(newStandard).column("quantity")
                  .oldValue(oldStandard.getQuantity()).newValue(newStandard.getQuantity()));
          updateBuilders.add(
              new StandardUpdateActivityBuilder().newStandard(newStandard).column("quantityUnit")
                  .oldValue(oldStandard.getQuantityUnit()).newValue(newStandard.getQuantityUnit()));
          updateBuilders
              .add(new StandardUpdateActivityBuilder().newStandard(newStandard).column("comments")
                  .oldValue(oldStandard.getComments()).newValue(newStandard.getComments()));
        }
      }
    }
    for (Standard newStandard : newStandards) {
      boolean inserted = true;
      for (Standard oldStandard : oldStandards) {
        if (newStandard.getId().equals(oldStandard.getId())) {
          inserted = false;
        }
      }
      if (inserted) {
        updateBuilders.add(new StandardUpdateActivityBuilder().newStandard(newStandard)
            .actionType(ActionType.INSERT));
      }
    }
    if (newSample instanceof SubmissionSample) {
      SubmissionSample oldSubmission = (SubmissionSample) oldSample;
      SubmissionSample newSubmission = (SubmissionSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("status")
          .oldValue(oldSubmission.getStatus()).newValue(newSubmission.getStatus()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("service")
          .oldValue(oldSubmission.getService()).newValue(newSubmission.getService()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("massDetectionInstrument")
          .oldValue(oldSubmission.getMassDetectionInstrument())
          .newValue(newSubmission.getMassDetectionInstrument()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("source")
          .oldValue(oldSubmission.getSource()).newValue(newSubmission.getSource()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("additionalPrice")
          .oldValue(oldSubmission.getAdditionalPrice())
          .newValue(newSubmission.getAdditionalPrice()));
    }
    if (newSample instanceof ProteicSample) {
      ProteicSample oldProteic = (ProteicSample) oldSample;
      ProteicSample newProteic = (ProteicSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("project")
          .oldValue(oldProteic.getProject()).newValue(newProteic.getProject()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("experience")
          .oldValue(oldProteic.getExperience()).newValue(newProteic.getExperience()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("goal")
          .oldValue(oldProteic.getGoal()).newValue(newProteic.getGoal()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("taxonomy")
          .oldValue(oldProteic.getTaxonomy()).newValue(newProteic.getTaxonomy()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("protein")
          .oldValue(oldProteic.getProtein()).newValue(newProteic.getProtein()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("molecularWeight")
          .oldValue(oldProteic.getMolecularWeight()).newValue(newProteic.getMolecularWeight()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("postTranslationModification")
          .oldValue(oldProteic.getPostTranslationModification())
          .newValue(newProteic.getPostTranslationModification()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("sampleNumberProtein")
          .oldValue(oldProteic.getSampleNumberProtein())
          .newValue(newProteic.getSampleNumberProtein()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteolyticDigestionMethod")
          .oldValue(oldProteic.getProteolyticDigestionMethod())
          .newValue(newProteic.getProteolyticDigestionMethod()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("usedProteolyticDigestionMethod")
          .oldValue(oldProteic.getUsedProteolyticDigestionMethod())
          .newValue(newProteic.getUsedProteolyticDigestionMethod()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("otherProteolyticDigestionMethod")
          .oldValue(oldProteic.getOtherProteolyticDigestionMethod())
          .newValue(newProteic.getOtherProteolyticDigestionMethod()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteinIdentification")
          .oldValue(oldProteic.getProteinIdentification())
          .newValue(newProteic.getProteinIdentification()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteinIdentificationLink")
          .oldValue(oldProteic.getProteinIdentificationLink())
          .newValue(newProteic.getProteinIdentificationLink()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("mudPitFraction")
          .oldValue(oldProteic.getMudPitFraction()).newValue(newProteic.getMudPitFraction()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteinContent")
          .oldValue(oldProteic.getProteinContent()).newValue(newProteic.getProteinContent()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("enrichmentType")
          .oldValue(oldProteic.getEnrichmentType()).newValue(newProteic.getEnrichmentType()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("otherEnrichmentType")
          .oldValue(oldProteic.getOtherEnrichmentType())
          .newValue(newProteic.getOtherEnrichmentType()));
    }
    if (newSample instanceof GelSample) {
      GelSample oldGel = (GelSample) oldSample;
      GelSample newGel = (GelSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("separation")
          .oldValue(oldGel.getSeparation()).newValue(newGel.getSeparation()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("thickness")
          .oldValue(oldGel.getThickness()).newValue(newGel.getThickness()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("coloration")
          .oldValue(oldGel.getColoration()).newValue(newGel.getColoration()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("otherColoration")
          .oldValue(oldGel.getOtherColoration()).newValue(newGel.getOtherColoration()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("developmentTime")
          .oldValue(oldGel.getDevelopmentTime()).newValue(newGel.getDevelopmentTime()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("developmentTimeUnit")
          .oldValue(oldGel.getDevelopmentTimeUnit()).newValue(newGel.getDevelopmentTimeUnit()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("decoloration")
          .oldValue(oldGel.isDecoloration()).newValue(newGel.isDecoloration()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("weightMarkerQuantity")
          .oldValue(oldGel.getWeightMarkerQuantity()).newValue(newGel.getWeightMarkerQuantity()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteinQuantity")
          .oldValue(oldGel.getProteinQuantity()).newValue(newGel.getProteinQuantity()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("proteinQuantityUnit")
          .oldValue(oldGel.getProteinQuantityUnit()).newValue(newGel.getProteinQuantityUnit()));
    }
    if (newSample instanceof EluateSample) {
      EluateSample oldEluate = (EluateSample) oldSample;
      EluateSample newEluate = (EluateSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("support")
          .oldValue(oldEluate.getSupport()).newValue(newEluate.getSupport()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("volume")
          .oldValue(oldEluate.getVolume()).newValue(newEluate.getVolume()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("quantity")
          .oldValue(oldEluate.getQuantity()).newValue(newEluate.getQuantity()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("quantityUnit")
          .oldValue(oldEluate.getQuantityUnit()).newValue(newEluate.getQuantityUnit()));
      // Contaminants.
      List<Contaminant> oldContaminants = oldEluate.getContaminants() != null
          ? oldEluate.getContaminants() : new ArrayList<Contaminant>();
      List<Contaminant> newContaminants = newEluate.getContaminants() != null
          ? newEluate.getContaminants() : new ArrayList<Contaminant>();
      for (Contaminant oldContaminant : oldContaminants) {
        boolean deleted = true;
        for (Contaminant newContaminant : newContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            deleted = false;
          }
        }
        if (deleted) {
          updateBuilders.add(new ContaminantUpdateActivityBuilder().oldContaminant(oldContaminant)
              .actionType(ActionType.DELETE));
        }
      }
      for (Contaminant oldContaminant : oldContaminants) {
        for (Contaminant newContaminant : newContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            updateBuilders.add(
                new ContaminantUpdateActivityBuilder().newContaminant(newContaminant).column("name")
                    .oldValue(oldContaminant.getName()).newValue(newContaminant.getName()));
            updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
                .column("quantity").oldValue(oldContaminant.getQuantity())
                .newValue(newContaminant.getQuantity()));
            updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
                .column("quantityUnit").oldValue(oldContaminant.getQuantityUnit())
                .newValue(newContaminant.getQuantityUnit()));
            updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
                .column("comments").oldValue(oldContaminant.getComments())
                .newValue(newContaminant.getComments()));
          }
        }
      }
      for (Contaminant newContaminant : newContaminants) {
        boolean inserted = true;
        for (Contaminant oldContaminant : oldContaminants) {
          if (newContaminant.getId().equals(oldContaminant.getId())) {
            inserted = false;
          }
        }
        if (inserted) {
          updateBuilders.add(new ContaminantUpdateActivityBuilder().newContaminant(newContaminant)
              .actionType(ActionType.INSERT));
        }
      }
    }
    if (newSample instanceof MoleculeSample) {
      MoleculeSample oldMolecule = (MoleculeSample) oldSample;
      MoleculeSample newMolecule = (MoleculeSample) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("support")
          .oldValue(oldMolecule.getSupport()).newValue(newMolecule.getSupport()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("formula")
          .oldValue(oldMolecule.getFormula()).newValue(newMolecule.getFormula()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("monoisotopicMass")
          .oldValue(oldMolecule.getMonoisotopicMass()).newValue(newMolecule.getMonoisotopicMass()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("averageMass")
          .oldValue(oldMolecule.getAverageMass()).newValue(newMolecule.getAverageMass()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("solutionSolvent")
          .oldValue(oldMolecule.getSolutionSolvent()).newValue(newMolecule.getSolutionSolvent()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("otherSolvent")
          .oldValue(oldMolecule.getOtherSolvent()).newValue(newMolecule.getOtherSolvent()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("toxicity")
          .oldValue(oldMolecule.getToxicity()).newValue(newMolecule.getToxicity()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("lightSensitive")
          .oldValue(oldMolecule.isLightSensitive()).newValue(newMolecule.isLightSensitive()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("storageTemperature")
          .oldValue(oldMolecule.getStorageTemperature())
          .newValue(newMolecule.getStorageTemperature()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("lowResolution")
          .oldValue(oldMolecule.isLowResolution()).newValue(newMolecule.isLowResolution()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("highResolution")
          .oldValue(oldMolecule.isHighResolution()).newValue(newMolecule.isHighResolution()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("msms")
          .oldValue(oldMolecule.isMsms()).newValue(newMolecule.isMsms()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("exactMsms")
          .oldValue(oldMolecule.isExactMsms()).newValue(newMolecule.isExactMsms()));
      // Structure.
      Structure oldStructure = oldMolecule.getStructure();
      Structure newStructure = newMolecule.getStructure();
      if (newStructure != null) {
        updateBuilders.add(new SampleUpdateActivityBuilder().column("structure")
            .oldValue(oldStructure.getFilename()).newValue(newStructure.getFilename()));
      }
      // Solvents.
      List<SampleSolvent> oldSolvents = oldMolecule.getSolventList() != null
          ? oldMolecule.getSolventList() : new ArrayList<SampleSolvent>();
      List<SampleSolvent> newSolvents = newMolecule.getSolventList() != null
          ? newMolecule.getSolventList() : new ArrayList<SampleSolvent>();
      for (SampleSolvent solvent : oldSolvents) {
        if (!newSolvents.contains(solvent)) {
          updateBuilders.add(new SolventUpdateActivityBuilder().recordId(solvent.getId())
              .actionType(ActionType.DELETE));
        }
      }
      for (SampleSolvent solvent : newSolvents) {
        if (!oldSolvents.contains(solvent)) {
          updateBuilders.add(new SolventUpdateActivityBuilder().recordId(solvent.getId())
              .actionType(ActionType.INSERT));
        }
      }
    }
    if (newSample instanceof Control) {
      Control oldControl = (Control) oldSample;
      Control newControl = (Control) newSample;
      updateBuilders.add(new SampleUpdateActivityBuilder().column("controlType")
          .oldValue(oldControl.getControlType()).newValue(newControl.getControlType()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("support")
          .oldValue(oldControl.getSupport()).newValue(newControl.getSupport()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("volume")
          .oldValue(oldControl.getVolume()).newValue(newControl.getVolume()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("quantity")
          .oldValue(oldControl.getQuantity()).newValue(newControl.getQuantity()));
      updateBuilders.add(new SampleUpdateActivityBuilder().column("quantityUnit")
          .oldValue(oldControl.getQuantityUnit()).newValue(newControl.getQuantityUnit()));
    }

    // Keep updateBuilders that did not change.
    final Collection<UpdateActivity> updates = new ArrayList<UpdateActivity>();
    for (UpdateActivityBuilder builder : updateBuilders) {
      if (builder.isChanged()) {
        updates.add(builder.build());
      }
    }

    if (!updates.isEmpty()) {
      Activity activity = new Activity();
      activity.setActionType(ActionType.UPDATE);
      activity.setRecordId(newSample.getId());
      activity.setUser(user);
      activity.setTableName("sample");
      activity.setJustification(justification);
      activity.setUpdates(new LinkedList<UpdateActivity>(updates));
      return Optional.of(activity);
    } else {
      return Optional.absent();
    }
  }
}
