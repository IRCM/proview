package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.MoleculeSample.StorageTemperature;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionSampleServiceImplTest {
  private SubmissionSampleServiceImpl submissionSampleServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SampleActivityService sampleActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private PricingEvaluator pricingEvaluator;
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
    submissionSampleServiceImpl = new SubmissionSampleServiceImpl(entityManager, queryFactory,
        sampleActivityService, activityService, pricingEvaluator, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  private SampleSolvent find(Collection<SampleSolvent> solvents, Solvent solvent) {
    for (SampleSolvent ssolvent : solvents) {
      if (ssolvent.getSolvent() == solvent) {
        return ssolvent;
      }
    }
    return null;
  }

  @Test
  public void get_Gel() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof GelSample);
    GelSample gelSample = (GelSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("IRC20101015_1", gelSample.getLims());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals(true, gelSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.GEL, gelSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, gelSample.getType());
    assertEquals("Philippe", gelSample.getComments());
    assertEquals(SubmissionSample.Status.ANALYSED, gelSample.getStatus());
    assertEquals("Coulombe", gelSample.getProject());
    assertEquals("G100429", gelSample.getExperience());
    assertEquals(null, gelSample.getGoal());
    assertEquals(null, gelSample.getSource());
    assertEquals(null, gelSample.getSampleNumberProtein());
    assertEquals(ProteicSample.ProteolyticDigestion.TRYPSINE,
        gelSample.getProteolyticDigestionMethod());
    assertEquals(null, gelSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, gelSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteicSample.ProteinIdentification.NCBINR, gelSample.getProteinIdentification());
    assertEquals(null, gelSample.getProteinIdentificationLink());
    assertEquals(null, gelSample.getEnrichmentType());
    assertEquals(null, gelSample.getOtherEnrichmentType());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
    assertEquals(null, gelSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.XLARGE, gelSample.getProteinContent());
    assertEquals(MsAnalysis.MassDetectionInstrument.LTQ_ORBI_TRAP,
        gelSample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, gelSample.getService());
    assertEquals(null, gelSample.getPrice());
    assertEquals(null, gelSample.getAdditionalPrice());
    assertEquals("Human", gelSample.getTaxonomy());
    assertEquals(null, gelSample.getProtein());
    assertEquals(null, gelSample.getMolecularWeight());
    assertEquals(null, gelSample.getPostTranslationModification());
    assertEquals(GelSample.Separation.ONE_DIMENSION, gelSample.getSeparation());
    assertEquals(GelSample.Thickness.ONE, gelSample.getThickness());
    assertEquals(GelSample.Coloration.SILVER, gelSample.getColoration());
    assertEquals(null, gelSample.getOtherColoration());
    assertEquals(null, gelSample.getDevelopmentTime());
    assertEquals(GelSample.DevelopmentTimeUnit.SECONDS, gelSample.getDevelopmentTimeUnit());
    assertEquals(false, gelSample.isDecoloration());
    assertEquals(null, gelSample.getWeightMarkerQuantity());
    assertEquals(null, gelSample.getProteinQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, gelSample.getProteinQuantityUnit());
  }

  @Test
  public void get_Eluate() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof EluateSample);
    EluateSample eluateSample = (EluateSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("IRC20111013_2", eluateSample.getLims());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals(true, eluateSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 2L, eluateSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.SOLUTION, eluateSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, eluateSample.getType());
    assertEquals(null, eluateSample.getComments());
    assertEquals(SubmissionSample.Status.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals("cap_project", eluateSample.getProject());
    assertEquals("cap_experience", eluateSample.getExperience());
    assertEquals("cap_goal", eluateSample.getGoal());
    assertEquals(null, eluateSample.getSource());
    assertEquals(null, eluateSample.getSampleNumberProtein());
    assertEquals(ProteicSample.ProteolyticDigestion.TRYPSINE,
        eluateSample.getProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteicSample.ProteinIdentification.NCBINR,
        eluateSample.getProteinIdentification());
    assertEquals(null, eluateSample.getProteinIdentificationLink());
    assertEquals(null, eluateSample.getEnrichmentType());
    assertEquals(null, eluateSample.getOtherEnrichmentType());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals(null, eluateSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.MEDIUM, eluateSample.getProteinContent());
    assertEquals(MsAnalysis.MassDetectionInstrument.LTQ_ORBI_TRAP,
        eluateSample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, eluateSample.getService());
    assertEquals(null, eluateSample.getPrice());
    assertEquals(null, eluateSample.getAdditionalPrice());
    assertEquals("human", eluateSample.getTaxonomy());
    assertEquals(null, eluateSample.getProtein());
    assertEquals(null, eluateSample.getMolecularWeight());
    assertEquals(null, eluateSample.getPostTranslationModification());
    assertEquals("1.5", eluateSample.getQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, eluateSample.getQuantityUnit());
    assertEquals((Double) 50.0, eluateSample.getVolume());
  }

  @Test
  public void get_Molecule() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.get(443L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof MoleculeSample);
    MoleculeSample moleculeSample = (MoleculeSample) sample;
    assertEquals((Long) 443L, moleculeSample.getId());
    assertEquals("IRC20111013_3", moleculeSample.getLims());
    assertEquals("CAP_20111013_05", moleculeSample.getName());
    assertEquals(true, moleculeSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 3L, moleculeSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.SOLUTION, moleculeSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, moleculeSample.getType());
    assertEquals(null, moleculeSample.getComments());
    assertEquals(SubmissionSample.Status.TO_APPROVE, moleculeSample.getStatus());
    assertEquals(MsAnalysis.Source.ESI, moleculeSample.getSource());
    assertEquals(true, moleculeSample.isLowResolution());
    assertEquals(false, moleculeSample.isHighResolution());
    assertEquals(false, moleculeSample.isMsms());
    assertEquals(false, moleculeSample.isExactMsms());
    assertEquals((Long) 33L, moleculeSample.getSubmission().getId());
    assertEquals(null, moleculeSample.getMassDetectionInstrument());
    assertEquals(Service.SMALL_MOLECULE, moleculeSample.getService());
    assertEquals(null, moleculeSample.getPrice());
    assertEquals(null, moleculeSample.getAdditionalPrice());
  }

  @Test
  public void get_NullId() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.get((Long) null);

    assertNull(sample);
  }

  @Test
  public void get_GelByName() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.getSubmission("FAM119A_band_01");

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof GelSample);
    GelSample gelSample = (GelSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("IRC20101015_1", gelSample.getLims());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals(true, gelSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.GEL, gelSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, gelSample.getType());
    assertEquals("Philippe", gelSample.getComments());
    assertEquals(SubmissionSample.Status.ANALYSED, gelSample.getStatus());
    assertEquals("Coulombe", gelSample.getProject());
    assertEquals("G100429", gelSample.getExperience());
    assertEquals(null, gelSample.getGoal());
    assertEquals(null, gelSample.getSource());
    assertEquals(null, gelSample.getSampleNumberProtein());
    assertEquals(ProteicSample.ProteolyticDigestion.TRYPSINE,
        gelSample.getProteolyticDigestionMethod());
    assertEquals(null, gelSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, gelSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteicSample.ProteinIdentification.NCBINR, gelSample.getProteinIdentification());
    assertEquals(null, gelSample.getProteinIdentificationLink());
    assertEquals(null, gelSample.getEnrichmentType());
    assertEquals(null, gelSample.getOtherEnrichmentType());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
    assertEquals(null, gelSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.XLARGE, gelSample.getProteinContent());
    assertEquals(MsAnalysis.MassDetectionInstrument.LTQ_ORBI_TRAP,
        gelSample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, gelSample.getService());
    assertEquals(null, gelSample.getPrice());
    assertEquals(null, gelSample.getAdditionalPrice());
    assertEquals("Human", gelSample.getTaxonomy());
    assertEquals(null, gelSample.getProtein());
    assertEquals(null, gelSample.getMolecularWeight());
    assertEquals(null, gelSample.getPostTranslationModification());
    assertEquals(GelSample.Separation.ONE_DIMENSION, gelSample.getSeparation());
    assertEquals(GelSample.Thickness.ONE, gelSample.getThickness());
    assertEquals(GelSample.Coloration.SILVER, gelSample.getColoration());
    assertEquals(null, gelSample.getOtherColoration());
    assertEquals(null, gelSample.getDevelopmentTime());
    assertEquals(GelSample.DevelopmentTimeUnit.SECONDS, gelSample.getDevelopmentTimeUnit());
    assertEquals(false, gelSample.isDecoloration());
    assertEquals(null, gelSample.getWeightMarkerQuantity());
    assertEquals(null, gelSample.getProteinQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, gelSample.getProteinQuantityUnit());
  }

  @Test
  public void get_EluateByName() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.getSubmission("CAP_20111013_01");

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof EluateSample);
    EluateSample eluateSample = (EluateSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("IRC20111013_2", eluateSample.getLims());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals(true, eluateSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 2L, eluateSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.SOLUTION, eluateSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, eluateSample.getType());
    assertEquals(null, eluateSample.getComments());
    assertEquals(SubmissionSample.Status.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals("cap_project", eluateSample.getProject());
    assertEquals("cap_experience", eluateSample.getExperience());
    assertEquals("cap_goal", eluateSample.getGoal());
    assertEquals(null, eluateSample.getSource());
    assertEquals(null, eluateSample.getSampleNumberProtein());
    assertEquals(ProteicSample.ProteolyticDigestion.TRYPSINE,
        eluateSample.getProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteicSample.ProteinIdentification.NCBINR,
        eluateSample.getProteinIdentification());
    assertEquals(null, eluateSample.getProteinIdentificationLink());
    assertEquals(null, eluateSample.getEnrichmentType());
    assertEquals(null, eluateSample.getOtherEnrichmentType());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals(null, eluateSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.MEDIUM, eluateSample.getProteinContent());
    assertEquals(MsAnalysis.MassDetectionInstrument.LTQ_ORBI_TRAP,
        eluateSample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, eluateSample.getService());
    assertEquals(null, eluateSample.getPrice());
    assertEquals(null, eluateSample.getAdditionalPrice());
    assertEquals("human", eluateSample.getTaxonomy());
    assertEquals(null, eluateSample.getProtein());
    assertEquals(null, eluateSample.getMolecularWeight());
    assertEquals(null, eluateSample.getPostTranslationModification());
    assertEquals("1.5", eluateSample.getQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, eluateSample.getQuantityUnit());
    assertEquals((Double) 50.0, eluateSample.getVolume());
  }

  @Test
  public void get_MoleculeByName() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.getSubmission("CAP_20111013_05");

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof MoleculeSample);
    MoleculeSample moleculeSample = (MoleculeSample) sample;
    assertEquals((Long) 443L, moleculeSample.getId());
    assertEquals("IRC20111013_3", moleculeSample.getLims());
    assertEquals("CAP_20111013_05", moleculeSample.getName());
    assertEquals(true, moleculeSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 3L, moleculeSample.getOriginalContainer().getId());
    assertEquals(Sample.Support.SOLUTION, moleculeSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, moleculeSample.getType());
    assertEquals(null, moleculeSample.getComments());
    assertEquals(SubmissionSample.Status.TO_APPROVE, moleculeSample.getStatus());
    assertEquals(MsAnalysis.Source.ESI, moleculeSample.getSource());
    assertEquals(true, moleculeSample.isLowResolution());
    assertEquals(false, moleculeSample.isHighResolution());
    assertEquals(false, moleculeSample.isMsms());
    assertEquals(false, moleculeSample.isExactMsms());
    assertEquals((Long) 33L, moleculeSample.getSubmission().getId());
    assertEquals(null, moleculeSample.getMassDetectionInstrument());
    assertEquals(Service.SMALL_MOLECULE, moleculeSample.getService());
    assertEquals(null, moleculeSample.getPrice());
    assertEquals(null, moleculeSample.getAdditionalPrice());
  }

  @Test
  public void get_NullName() throws Throwable {
    SubmissionSample sample = submissionSampleServiceImpl.getSubmission((String) null);

    assertNull(sample);
  }

  @Test
  public void exists_True() throws Throwable {
    boolean exists = submissionSampleServiceImpl.exists("CAP_20111013_05");

    verify(authorizationService).checkUserRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_False() throws Throwable {
    boolean exists = submissionSampleServiceImpl.exists("CAP_20111013_80");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Null() throws Throwable {
    boolean exists = submissionSampleServiceImpl.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void report() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.nameContains("CAP");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    Collections.sort(samples,
        new SubmissionSampleComparator(SubmissionSampleService.Sort.LIMS, Locale.CANADA));
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals("cap_project", ((ProteicSample) sample).getProject());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(SubmissionSample.Status.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    sample = samples.get(1);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(SubmissionSample.Status.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    Map<SubmissionSample, Boolean> linkedToResults = report.getLinkedToResults();
    assertEquals(true, linkedToResults.get(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, linkedToResults.get(entityManager.find(SubmissionSample.class, 443L)));
  }

  @Test
  public void report_All() throws Throwable {
    final SampleFilterBean filter = new SampleFilterBean();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_ExperienceContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.experienceContains("cap_experience");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_LaboratoryContains_1() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratoryContains("ircm");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_LaboratoryContains_2() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratoryContains("ircm2");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Laboratory_1() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratory(new Laboratory(1L));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Laboratory_2() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratory(new Laboratory(2L));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_LimsContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.limsContains("RC20111013");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_MaximalSubmissionDate() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.maximalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_MinimalSubmissionDate() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.minimalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_NameContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.nameContains("AP_20111013");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_ProjectContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.projectContains("cap_project");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Status() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.statuses(Arrays.asList(SubmissionSample.Status.DATA_ANALYSIS));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Status_Multiple() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.statuses(
        Arrays.asList(SubmissionSample.Status.DATA_ANALYSIS, SubmissionSample.Status.TO_APPROVE));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Support_Gel() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.GEL);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Support_Solution() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.SOLUTION);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Support_Molecule_Low() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.MOLECULE_LOW);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_Support_Molecule_High() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.MOLECULE_HIGH);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_User() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    filter.user(user);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_UserContains_LastName_1() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("coulombe");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_UserContains_LastName_2() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("anderson");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_UserContains_FirstName() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("benoit");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(filter);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void report_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.report(null);

    verify(authorizationService).checkUserRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));

  }

  @Test
  public void adminReport() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.nameContains("CAP");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    Collections.sort(samples,
        new SubmissionSampleComparator(SubmissionSampleService.Sort.LIMS, Locale.CANADA));
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals((Long) 2L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals("cap_project", ((ProteicSample) sample).getProject());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals(SubmissionSample.Status.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    sample = samples.get(1);
    assertEquals((Long) 443L, sample.getId());
    assertEquals((Long) 2L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals((Double) 654.654, ((MoleculeSample) sample).getMonoisotopicMass());
    assertEquals(1, ((MoleculeSample) sample).getSolventList().size());
    assertNotNull(find(((MoleculeSample) sample).getSolventList(), Solvent.METHANOL));
    assertEquals("MeOH/TFA 0.1%", ((MoleculeSample) sample).getSolutionSolvent());
    assertEquals(StorageTemperature.MEDIUM, ((MoleculeSample) sample).getStorageTemperature());
    assertEquals(MsAnalysis.Source.ESI, ((MoleculeSample) sample).getSource());
    assertEquals(Service.SMALL_MOLECULE, sample.getService());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals(SubmissionSample.Status.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    sample = samples.get(2);
    assertEquals((Long) 445L, sample.getId());
    assertEquals((Long) 1L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("christian.poitras@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111017_4", sample.getLims());
    assertEquals("CAP_20111017_01", sample.getName());
    assertEquals("cap_project", ((ProteicSample) sample).getProject());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals(SubmissionSample.Status.ANALYSED, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 17, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    Map<SubmissionSample, Boolean> linkedToResults = report.getLinkedToResults();
    assertEquals(true, linkedToResults.get(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, linkedToResults.get(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, linkedToResults.get(entityManager.find(SubmissionSample.class, 445L)));
  }

  @Test
  public void adminReport_All() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_ExperienceContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.experienceContains("cap_experience");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_LaboratoryContains_1() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratoryContains("ircm");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_LaboratoryContains_2() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratoryContains("ircm2");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Laboratory() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.laboratory(new Laboratory(2L));

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_LimsContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.limsContains("RC20111013");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_MaximalSubmissionDate() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.maximalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_MinimalSubmissionDate() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.minimalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_NameContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.nameContains("AP_20111013");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_ProjectContains() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.projectContains("cap_project");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Status() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.statuses(Arrays.asList(SubmissionSample.Status.DATA_ANALYSIS));

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Status_Multiple() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.statuses(
        Arrays.asList(SubmissionSample.Status.DATA_ANALYSIS, SubmissionSample.Status.ANALYSED));

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Support_Gel() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.GEL);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Support_Solution() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.SOLUTION);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Support_MoleculeLow() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.MOLECULE_LOW);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Support_MoleculeHigh() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.MOLECULE_HIGH);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_Support_IntactProtein() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.support(SubmissionSampleService.Support.INTACT_PROTEIN);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_User() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    filter.user(user);

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_UserContains_FirstName() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("benoit");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_UserContains_LastName() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("poitras");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_UserContains_FullName_1() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("Benoit Coulombe");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_UserContains_FullName_2() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("Benoit Coulombe 2");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_UserContains_FullName_3() throws Throwable {
    SampleFilterBean filter = new SampleFilterBean();
    filter.userContains("Christian Poitras");

    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(filter);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void adminReport_NullFilter() throws Throwable {
    SubmissionSampleService.Report report = submissionSampleServiceImpl.adminReport(null);

    verify(authorizationService).checkAdminRole();
    List<SubmissionSample> samples = report.getSamples();
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 446L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 447L)));
  }

  @Test
  public void sampleMonitoring() throws Throwable {
    List<SubmissionSample> samples = submissionSampleServiceImpl.sampleMonitoring();

    verify(authorizationService).checkAdminRole();
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 1L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 442L)));
    assertEquals(true, samples.contains(entityManager.find(SubmissionSample.class, 443L)));
    assertEquals(false, samples.contains(entityManager.find(SubmissionSample.class, 445L)));
    Collections.sort(samples,
        new SubmissionSampleComparator(SubmissionSampleService.Sort.LIMS, Locale.CANADA));
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals("CAP_20111013_01", sample.getOriginalContainer().getName());
    sample = samples.get(1);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals(Service.SMALL_MOLECULE, sample.getService());
    assertEquals("CAP_20111013_05", sample.getOriginalContainer().getName());
    sample = samples.get(2);
    assertEquals((Long) 446L, sample.getId());
    assertEquals("IRC20111109_5", sample.getLims());
    assertEquals("CAP_20111109_01", sample.getName());
    assertEquals("christopher.anderson@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(Service.LC_MS_MS, sample.getService());
    assertEquals("CAP_20111109_01", sample.getOriginalContainer().getName());
  }

  @Test
  public void projects() throws Throwable {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<String> projects = submissionSampleServiceImpl.projects();

    verify(authorizationService).checkUserRole();
    assertEquals(2, projects.size());
    assertEquals(true, projects.contains("cap_project"));
    assertEquals(true, projects.contains("Coulombe"));
    assertEquals(false, projects.contains("some_random_string"));
  }

  @Test
  public void updateStatus() throws Throwable {
    SubmissionSample sample1 = entityManager.find(SubmissionSample.class, 443L);
    entityManager.detach(sample1);
    sample1.setStatus(SubmissionSample.Status.TO_DIGEST);
    SubmissionSample sample2 = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample2);
    sample2.setStatus(SubmissionSample.Status.RECEIVED);
    Collection<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleServiceImpl.updateStatus(samples);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    SubmissionSample testSample1 = entityManager.find(SubmissionSample.class, 443L);
    SubmissionSample testSample2 = entityManager.find(SubmissionSample.class, 445L);
    assertEquals(SubmissionSample.Status.TO_DIGEST, testSample1.getStatus());
    assertEquals(SubmissionSample.Status.RECEIVED, testSample2.getStatus());
    verify(sampleActivityService, times(2)).update(sampleCaptor.capture(), isNull(String.class));
    verify(activityService, times(2)).insert(activity);
    SubmissionSample newTestSample1 = (SubmissionSample) sampleCaptor.getAllValues().get(0);
    assertEquals(SubmissionSample.Status.TO_DIGEST, newTestSample1.getStatus());
    SubmissionSample newTestSample2 = (SubmissionSample) sampleCaptor.getAllValues().get(1);
    assertEquals(SubmissionSample.Status.RECEIVED, newTestSample2.getStatus());
  }

  @Test
  public void computePrice() throws Throwable {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    Instant instant = Instant.now();
    when(pricingEvaluator.computePrice(any(SubmissionSample.class), any(Instant.class)))
        .thenReturn(new BigDecimal("45.26"));

    BigDecimal price = submissionSampleServiceImpl.computePrice(sample, instant);

    verify(pricingEvaluator).computePrice(sample, instant);
    assertEquals(new BigDecimal("45.26"), price);
  }

  @Test
  public void computePrice_NullSample() throws Throwable {
    Instant instant = Instant.now();

    BigDecimal price = submissionSampleServiceImpl.computePrice(null, instant);

    verifyZeroInteractions(pricingEvaluator);
    assertNull(price);
  }

  @Test
  public void computePrice_NullDate() throws Throwable {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);

    BigDecimal price = submissionSampleServiceImpl.computePrice(sample, null);

    verifyZeroInteractions(pricingEvaluator);
    assertNull(price);
  }
}
