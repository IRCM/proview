package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.sample.ProteicSample.EnrichmentType;
import ca.qc.ircm.proview.sample.ProteicSample.MudPitFraction;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinContent;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteicSample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.sample.SubmissionSample.ServiceType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EluateSampleServiceImplTest {
  private EluateSampleServiceImpl eluateSampleServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private SampleActivityService sampleActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    eluateSampleServiceImpl = new EluateSampleServiceImpl(entityManager, sampleActivityService,
        activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get_Id() {
    EluateSample sample = eluateSampleServiceImpl.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(sample);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals(Sample.Type.SUBMISSION, sample.getType());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals(null, sample.getComments());
    assertEquals(ServiceType.PROTEIC, sample.getServiceType());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals(SubmissionSample.Status.DATA_ANALYSIS, sample.getStatus());
    assertEquals("cap_project", sample.getProject());
    assertEquals("cap_experience", sample.getExperience());
    assertEquals("cap_goal", sample.getGoal());
    assertEquals(null, sample.getSource());
    assertEquals(null, sample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.TRYPSINE, sample.getProteolyticDigestionMethod());
    assertEquals(null, sample.getUsedProteolyticDigestionMethod());
    assertEquals(null, sample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, sample.getProteinIdentification());
    assertEquals(null, sample.getProteinIdentificationLink());
    assertEquals(null, sample.getEnrichmentType());
    assertEquals(null, sample.getOtherEnrichmentType());
    assertEquals((Long) 32L, sample.getSubmission().getId());
    assertEquals(null, sample.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, sample.getProteinContent());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, sample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals("human", sample.getTaxonomy());
    assertEquals(null, sample.getProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getPostTranslationModification());
    assertEquals("1.5", sample.getQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, sample.getQuantityUnit());
    assertEquals((Double) 50.0, sample.getVolume());
    assertEquals(null, sample.getPrice());
    assertEquals(null, sample.getAdditionalPrice());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 2L, sample.getOriginalContainer().getId());
  }

  @Test
  public void get_NullId() {
    EluateSample sample = eluateSampleServiceImpl.get(null);

    assertNull(sample);
  }

  @Test
  public void update() {
    EluateSample sample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(sample);
    sample.setComments("my_new_comment");
    sample.setName("new_solution_tag_0001");
    sample.setProject("my_project");
    sample.setExperience("my_experience");
    sample.setGoal("my_goal");
    sample.setSource(Source.LDTD);
    sample.setSampleNumberProtein(2);
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.DIGESTED);
    sample.setUsedProteolyticDigestionMethod("Trypsine");
    sample.setOtherProteolyticDigestionMethod("None");
    sample.setProteinIdentification(ProteinIdentification.OTHER);
    sample.setProteinIdentificationLink("http://cou24/my_database");
    sample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    sample.setOtherEnrichmentType("Phosphopeptides");
    sample.setMudPitFraction(MudPitFraction.TWELVE);
    sample.setProteinContent(ProteinContent.LARGE);
    sample.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    sample.setService(Service.MALDI_MS);
    sample.setTaxonomy("mouse");
    sample.setProtein("my_protein");
    sample.setMolecularWeight(20.0);
    sample.setPostTranslationModification("my_modification");
    sample.setSupport(Support.DRY);
    sample.setQuantity("12");
    sample.setQuantityUnit(Sample.QuantityUnit.PICO_MOL);
    sample.setVolume(70.0);
    sample.setAdditionalPrice(new BigDecimal("21.50"));
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals("my_new_comment", test.getComments());
    assertEquals("new_solution_tag_0001", test.getName());
    assertEquals("my_project", test.getProject());
    assertEquals("my_experience", test.getExperience());
    assertEquals("my_goal", test.getGoal());
    assertEquals(Source.LDTD, test.getSource());
    assertEquals((Integer) 2, test.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.DIGESTED, test.getProteolyticDigestionMethod());
    assertEquals("Trypsine", test.getUsedProteolyticDigestionMethod());
    assertEquals("None", test.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.OTHER, test.getProteinIdentification());
    assertEquals("http://cou24/my_database", test.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, test.getEnrichmentType());
    assertEquals("Phosphopeptides", test.getOtherEnrichmentType());
    assertEquals(MudPitFraction.TWELVE, test.getMudPitFraction());
    assertEquals(ProteinContent.LARGE, test.getProteinContent());
    assertEquals(MassDetectionInstrument.TOF, test.getMassDetectionInstrument());
    assertEquals(Service.MALDI_MS, test.getService());
    assertEquals("mouse", test.getTaxonomy());
    assertEquals("my_protein", test.getProtein());
    assertEquals((Double) 20.0, test.getMolecularWeight());
    assertEquals("my_modification", test.getPostTranslationModification());
    assertEquals(Support.DRY, test.getSupport());
    assertEquals("12", test.getQuantity());
    assertEquals(Sample.QuantityUnit.PICO_MOL, test.getQuantityUnit());
    assertEquals((Double) 70.0, test.getVolume());
    assertEquals(new BigDecimal("21.50").setScale(2), test.getAdditionalPrice().setScale(2));
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertEquals("my_new_comment", newEluateSample.getComments());
    assertEquals("new_solution_tag_0001", newEluateSample.getName());
    assertEquals("my_project", newEluateSample.getProject());
    assertEquals("my_experience", newEluateSample.getExperience());
    assertEquals("my_goal", newEluateSample.getGoal());
    assertEquals(Source.LDTD, newEluateSample.getSource());
    assertEquals((Integer) 2, newEluateSample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.DIGESTED, newEluateSample.getProteolyticDigestionMethod());
    assertEquals("Trypsine", newEluateSample.getUsedProteolyticDigestionMethod());
    assertEquals("None", newEluateSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.OTHER, newEluateSample.getProteinIdentification());
    assertEquals("http://cou24/my_database", newEluateSample.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, newEluateSample.getEnrichmentType());
    assertEquals("Phosphopeptides", newEluateSample.getOtherEnrichmentType());
    assertEquals(MudPitFraction.TWELVE, newEluateSample.getMudPitFraction());
    assertEquals(ProteinContent.LARGE, newEluateSample.getProteinContent());
    assertEquals(MassDetectionInstrument.TOF, newEluateSample.getMassDetectionInstrument());
    assertEquals(Service.MALDI_MS, newEluateSample.getService());
    assertEquals("mouse", newEluateSample.getTaxonomy());
    assertEquals("my_protein", newEluateSample.getProtein());
    assertEquals((Double) 20.0, newEluateSample.getMolecularWeight());
    assertEquals("my_modification", newEluateSample.getPostTranslationModification());
    assertEquals(Support.DRY, newEluateSample.getSupport());
    assertEquals("12", newEluateSample.getQuantity());
    assertEquals(Sample.QuantityUnit.PICO_MOL, newEluateSample.getQuantityUnit());
    assertEquals((Double) 70.0, newEluateSample.getVolume());
    assertEquals(new BigDecimal("21.50").setScale(2),
        newEluateSample.getAdditionalPrice().setScale(2));
  }

  @Test
  public void update_AddContaminant() {
    EluateSample sample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(sample);
    Contaminant insert = new Contaminant();
    insert.setName("my_new_contaminant");
    insert.setQuantity("3");
    insert.setQuantityUnit(Contaminant.QuantityUnit.MICRO_GRAMS);
    insert.setComments("some_comments");
    sample.getContaminants().add(insert);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant insertion.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getContaminants().size());
    Contaminant testContaminant = test.getContaminants().get(0);
    assertEquals("my_new_contaminant", testContaminant.getName());
    assertEquals("3", testContaminant.getQuantity());
    assertEquals(Contaminant.QuantityUnit.MICRO_GRAMS, testContaminant.getQuantityUnit());
    assertEquals("some_comments", testContaminant.getComments());
    // Validate activity log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertEquals(1, newEluateSample.getContaminants().size());
    testContaminant = newEluateSample.getContaminants().get(0);
    assertEquals("my_new_contaminant", testContaminant.getName());
    assertEquals("3", testContaminant.getQuantity());
    assertEquals(Contaminant.QuantityUnit.MICRO_GRAMS, testContaminant.getQuantityUnit());
    assertEquals("some_comments", testContaminant.getComments());
  }

  @Test
  public void update_UpdateContaminant() {
    EluateSample sample = entityManager.find(EluateSample.class, 445L);
    entityManager.detach(sample);
    for (Contaminant contaminant : sample.getContaminants()) {
      entityManager.detach(contaminant);
    }
    Contaminant update = sample.getContaminants().get(0);
    update.setName("new_contaminant_name");
    update.setQuantity("1");
    update.setQuantityUnit(Contaminant.QuantityUnit.PICO_MOL);
    update.setComments("new_comments");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant update.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getContaminants().size());
    Contaminant testContaminant = test.getContaminants().get(0);
    assertEquals("new_contaminant_name", testContaminant.getName());
    assertEquals("1", testContaminant.getQuantity());
    assertEquals(Contaminant.QuantityUnit.PICO_MOL, testContaminant.getQuantityUnit());
    assertEquals("new_comments", testContaminant.getComments());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertTrue(newEluateSample.getContaminants().size() == 1);
    testContaminant = newEluateSample.getContaminants().get(0);
    assertEquals("new_contaminant_name", testContaminant.getName());
    assertEquals("1", testContaminant.getQuantity());
    assertEquals(Contaminant.QuantityUnit.PICO_MOL, testContaminant.getQuantityUnit());
    assertEquals("new_comments", testContaminant.getComments());
  }

  @Test
  public void update_RemoveContaminant() {
    EluateSample sample = entityManager.find(EluateSample.class, 445L);
    entityManager.detach(sample);
    sample.getContaminants().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    // Update sample.
    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant deletion.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(0, test.getContaminants().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertEquals(0, newEluateSample.getContaminants().size());
  }

  @Test
  public void update_AddStandard() {
    EluateSample sample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(sample);
    Standard standard = new Standard();
    standard.setName("my_new_standard");
    standard.setQuantity("3");
    standard.setQuantityUnit(Standard.QuantityUnit.MICRO_GRAMS);
    standard.setComments("some_comments");
    sample.getStandards().add(standard);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    // Update sample.
    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard insertion.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3", testStandard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, testStandard.getQuantityUnit());
    assertEquals("some_comments", testStandard.getComments());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertEquals(1, newEluateSample.getStandards().size());
    testStandard = newEluateSample.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3", testStandard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, testStandard.getQuantityUnit());
    assertEquals("some_comments", testStandard.getComments());
  }

  @Test
  public void update_UpdateStandard() {
    EluateSample sample = entityManager.find(EluateSample.class, 445L);
    entityManager.detach(sample);
    for (Standard standard : sample.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = sample.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1");
    standard.setQuantityUnit(Standard.QuantityUnit.PICO_MOL);
    standard.setComments("new_comments");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard update.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1", testStandard.getQuantity());
    assertEquals(Standard.QuantityUnit.PICO_MOL, testStandard.getQuantityUnit());
    assertEquals("new_comments", testStandard.getComments());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertTrue(newEluateSample.getStandards().size() == 1);
    testStandard = newEluateSample.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1", testStandard.getQuantity());
    assertEquals(Standard.QuantityUnit.PICO_MOL, testStandard.getQuantityUnit());
    assertEquals("new_comments", testStandard.getComments());
  }

  @Test
  public void update_RemoveStandard() {
    EluateSample sample = entityManager.find(EluateSample.class, 445L);
    entityManager.detach(sample);
    sample.getStandards().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    eluateSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard deletion.
    EluateSample test = entityManager.find(EluateSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(0, test.getStandards().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof EluateSample);
    EluateSample newEluateSample = (EluateSample) newSample;
    assertEquals(0, newEluateSample.getStandards().size());
  }
}
