package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleServiceImplTest {
  private SampleServiceImpl sampleServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    sampleServiceImpl = new SampleServiceImpl(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get_Gel() throws Throwable {
    Sample sample = sampleServiceImpl.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof GelSample);
    GelSample gelSample = (GelSample) sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("IRC20101015_1", gelSample.getLims());
    assertEquals("FAM119A_band_01", gelSample.getName());
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
    assertEquals(ProteolyticDigestion.TRYPSIN, gelSample.getProteolyticDigestionMethod());
    assertEquals(null, gelSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, gelSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, gelSample.getProteinIdentification());
    assertEquals(null, gelSample.getProteinIdentificationLink());
    assertEquals(null, gelSample.getEnrichmentType());
    assertEquals(null, gelSample.getOtherEnrichmentType());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
    assertEquals(null, gelSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.XLARGE, gelSample.getProteinContent());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, gelSample.getMassDetectionInstrument());
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
  }

  @Test
  public void get_Eluate() throws Throwable {
    Sample sample = sampleServiceImpl.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof EluateSample);
    EluateSample eluateSample = (EluateSample) sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("IRC20111013_2", eluateSample.getLims());
    assertEquals("CAP_20111013_01", eluateSample.getName());
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
    assertEquals(ProteolyticDigestion.TRYPSIN, eluateSample.getProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getUsedProteolyticDigestionMethod());
    assertEquals(null, eluateSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, eluateSample.getProteinIdentification());
    assertEquals(null, eluateSample.getProteinIdentificationLink());
    assertEquals(null, eluateSample.getEnrichmentType());
    assertEquals(null, eluateSample.getOtherEnrichmentType());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals(null, eluateSample.getMudPitFraction());
    assertEquals(ProteicSample.ProteinContent.MEDIUM, eluateSample.getProteinContent());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, eluateSample.getMassDetectionInstrument());
    assertEquals(Service.LC_MS_MS, eluateSample.getService());
    assertEquals(null, eluateSample.getPrice());
    assertEquals(null, eluateSample.getAdditionalPrice());
    assertEquals("human", eluateSample.getTaxonomy());
    assertEquals(null, eluateSample.getProtein());
    assertEquals(null, eluateSample.getMolecularWeight());
    assertEquals(null, eluateSample.getPostTranslationModification());
    assertEquals("1.5 mg", eluateSample.getQuantity());
    assertEquals((Double) 50.0, eluateSample.getVolume());
  }

  @Test
  public void get_Molecule() throws Throwable {
    Sample sample = sampleServiceImpl.get(443L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof MoleculeSample);
    MoleculeSample moleculeSample = (MoleculeSample) sample;
    assertEquals((Long) 443L, moleculeSample.getId());
    assertEquals("IRC20111013_3", moleculeSample.getLims());
    assertEquals("CAP_20111013_05", moleculeSample.getName());
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
  public void get_Control() throws Throwable {
    Sample sample = sampleServiceImpl.get(444L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof Control);
    Control control = (Control) sample;
    assertEquals((Long) 444L, control.getId());
    assertEquals("CONTROL.1", control.getLims());
    assertEquals("control_01", control.getName());
    assertEquals((Long) 4L, control.getOriginalContainer().getId());
    assertEquals(Sample.Support.GEL, control.getSupport());
    assertEquals(Sample.Type.CONTROL, control.getType());
    assertEquals(Control.ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertEquals(null, control.getComments());
    assertEquals(null, control.getVolume());
    assertEquals(null, control.getQuantity());
  }

  @Test
  public void get_Null() throws Throwable {
    Sample sample = sampleServiceImpl.get(null);

    assertNull(sample);
  }

  @Test
  public void linkedToResults_True() throws Throwable {
    Sample sample = entityManager.find(Sample.class, 442L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleServiceImpl.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(true, linkedToResults);
  }

  @Test
  public void linkedToResults_False() throws Throwable {
    Sample sample = entityManager.find(Sample.class, 447L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleServiceImpl.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(false, linkedToResults);
  }

  @Test
  public void linkedToResults_WithHidden() throws Throwable {
    Sample sample = new EluateSample(445L);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    boolean linkedToResults = sampleServiceImpl.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(true, linkedToResults);
  }

  @Test
  public void linkedToResults_NoHidden() throws Throwable {
    Sample sample = new EluateSample(445L);
    when(authorizationService.hasAdminRole()).thenReturn(false);

    boolean linkedToResults = sampleServiceImpl.linkedToResults(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(false, linkedToResults);
  }

  @Test
  public void linkedToResults_Null() throws Throwable {
    boolean linkedToResults = sampleServiceImpl.linkedToResults(null);

    assertEquals(false, linkedToResults);
  }
}
