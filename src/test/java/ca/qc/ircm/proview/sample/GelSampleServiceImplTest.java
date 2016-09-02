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
import ca.qc.ircm.proview.sample.GelSample.Coloration;
import ca.qc.ircm.proview.sample.GelSample.DevelopmentTimeUnit;
import ca.qc.ircm.proview.sample.GelSample.Separation;
import ca.qc.ircm.proview.sample.GelSample.Thickness;
import ca.qc.ircm.proview.sample.ProteicSample.EnrichmentType;
import ca.qc.ircm.proview.sample.ProteicSample.MudPitFraction;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinContent;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteicSample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample.QuantityUnit;
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
public class GelSampleServiceImplTest {
  private GelSampleServiceImpl gelSampleServiceImpl;
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
    gelSampleServiceImpl = new GelSampleServiceImpl(entityManager, sampleActivityService,
        activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get_Id() {
    GelSample sample = gelSampleServiceImpl.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(sample);
    assertEquals((Long) 1L, sample.getId());
    assertEquals("IRC20101015_1", sample.getLims());
    assertEquals(Sample.Type.SUBMISSION, sample.getType());
    assertEquals("FAM119A_band_01", sample.getName());
    assertEquals("Philippe", sample.getComments());
    assertEquals(ServiceType.PROTEIC, sample.getServiceType());
    assertEquals(Sample.Support.GEL, sample.getSupport());
    assertEquals(SubmissionSample.Status.ANALYSED, sample.getStatus());
    assertEquals("Coulombe", sample.getProject());
    assertEquals("G100429", sample.getExperience());
    assertEquals(null, sample.getGoal());
    assertEquals(null, sample.getSource());
    assertEquals(null, sample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.TRYPSINE, sample.getProteolyticDigestionMethod());
    assertEquals(null, sample.getUsedProteolyticDigestionMethod());
    assertEquals(null, sample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, sample.getProteinIdentification());
    assertEquals(null, sample.getProteinIdentificationLink());
    assertEquals(null, sample.getEnrichmentType());
    assertEquals(null, sample.getOtherEnrichmentType());
    assertEquals((Long) 1L, sample.getSubmission().getId());
    assertEquals(null, sample.getMudPitFraction());
    assertEquals(ProteinContent.XLARGE, sample.getProteinContent());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, sample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals("Human", sample.getTaxonomy());
    assertEquals(null, sample.getProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getPostTranslationModification());
    assertEquals(Sample.Support.GEL, sample.getSupport());
    assertEquals(Separation.ONE_DIMENSION, sample.getSeparation());
    assertEquals(Thickness.ONE, sample.getThickness());
    assertEquals(Coloration.SILVER, sample.getColoration());
    assertEquals(null, sample.getOtherColoration());
    assertEquals(null, sample.getDevelopmentTime());
    assertEquals(DevelopmentTimeUnit.SECONDS, sample.getDevelopmentTimeUnit());
    assertEquals(false, sample.isDecoloration());
    assertEquals(null, sample.getWeightMarkerQuantity());
    assertEquals(null, sample.getProteinQuantity());
    assertEquals(QuantityUnit.MICRO_GRAMS, sample.getProteinQuantityUnit());
    assertEquals(null, sample.getPrice());
    assertEquals(null, sample.getAdditionalPrice());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 1L, sample.getOriginalContainer().getId());
  }

  @Test
  public void get_NullId() {
    GelSample sample = gelSampleServiceImpl.get(null);

    assertNull(sample);
  }

  @Test
  public void update() {
    GelSample sample = entityManager.find(GelSample.class, 1L);
    entityManager.detach(sample);
    sample.setComments("my_new_comment");
    sample.setName("new_gel_tag_0001");
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
    sample.setSeparation(Separation.TWO_DIMENSION);
    sample.setThickness(Thickness.ONE_HALF);
    sample.setColoration(Coloration.OTHER);
    sample.setOtherColoration("my_coloration");
    sample.setDevelopmentTime(2.0);
    sample.setDevelopmentTimeUnit(DevelopmentTimeUnit.MINUTES);
    sample.setDecoloration(true);
    sample.setWeightMarkerQuantity(2.5);
    sample.setProteinQuantity("12.0");
    sample.setProteinQuantityUnit(Sample.QuantityUnit.PICO_MOL);
    sample.setAdditionalPrice(new BigDecimal("21.50"));
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    gelSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    GelSample test = entityManager.find(GelSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals("my_new_comment", test.getComments());
    assertEquals("new_gel_tag_0001", test.getName());
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
    assertEquals(Separation.TWO_DIMENSION, test.getSeparation());
    assertEquals(Thickness.ONE_HALF, test.getThickness());
    assertEquals(Coloration.OTHER, test.getColoration());
    assertEquals("my_coloration", test.getOtherColoration());
    assertEquals((Double) 2.0, test.getDevelopmentTime());
    assertEquals(DevelopmentTimeUnit.MINUTES, test.getDevelopmentTimeUnit());
    assertEquals(true, test.isDecoloration());
    assertEquals((Double) 2.5, test.getWeightMarkerQuantity());
    assertEquals("12.0", test.getProteinQuantity());
    assertEquals(Sample.QuantityUnit.PICO_MOL, test.getProteinQuantityUnit());
    assertEquals(new BigDecimal("21.50").setScale(2), test.getAdditionalPrice().setScale(2));
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof GelSample);
    GelSample newGelSample = (GelSample) newSample;
    assertEquals("my_new_comment", newGelSample.getComments());
    assertEquals("new_gel_tag_0001", newGelSample.getName());
    assertEquals("my_project", newGelSample.getProject());
    assertEquals("my_experience", newGelSample.getExperience());
    assertEquals("my_goal", newGelSample.getGoal());
    assertEquals(Source.LDTD, newGelSample.getSource());
    assertEquals((Integer) 2, newGelSample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.DIGESTED, newGelSample.getProteolyticDigestionMethod());
    assertEquals("Trypsine", newGelSample.getUsedProteolyticDigestionMethod());
    assertEquals("None", newGelSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.OTHER, newGelSample.getProteinIdentification());
    assertEquals("http://cou24/my_database", newGelSample.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, newGelSample.getEnrichmentType());
    assertEquals("Phosphopeptides", newGelSample.getOtherEnrichmentType());
    assertEquals(MudPitFraction.TWELVE, newGelSample.getMudPitFraction());
    assertEquals(ProteinContent.LARGE, newGelSample.getProteinContent());
    assertEquals(MassDetectionInstrument.TOF, newGelSample.getMassDetectionInstrument());
    assertEquals(Service.MALDI_MS, newGelSample.getService());
    assertEquals("mouse", newGelSample.getTaxonomy());
    assertEquals("my_protein", newGelSample.getProtein());
    assertEquals((Double) 20.0, newGelSample.getMolecularWeight());
    assertEquals("my_modification", newGelSample.getPostTranslationModification());
    assertEquals(Separation.TWO_DIMENSION, newGelSample.getSeparation());
    assertEquals(Thickness.ONE_HALF, newGelSample.getThickness());
    assertEquals(Coloration.OTHER, newGelSample.getColoration());
    assertEquals("my_coloration", newGelSample.getOtherColoration());
    assertEquals((Double) 2.0, newGelSample.getDevelopmentTime());
    assertEquals(DevelopmentTimeUnit.MINUTES, newGelSample.getDevelopmentTimeUnit());
    assertEquals(true, newGelSample.isDecoloration());
    assertEquals((Double) 2.5, newGelSample.getWeightMarkerQuantity());
    assertEquals("12.0", newGelSample.getProteinQuantity());
    assertEquals(Sample.QuantityUnit.PICO_MOL, newGelSample.getProteinQuantityUnit());
    assertEquals(new BigDecimal("21.50").setScale(2),
        newGelSample.getAdditionalPrice().setScale(2));
  }
}
