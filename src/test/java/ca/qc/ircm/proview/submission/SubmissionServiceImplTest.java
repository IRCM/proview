package ca.qc.ircm.proview.submission;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.mail.HtmlEmail;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.GelSample;
import ca.qc.ircm.proview.sample.GelSample.Coloration;
import ca.qc.ircm.proview.sample.GelSample.Separation;
import ca.qc.ircm.proview.sample.GelSample.Thickness;
import ca.qc.ircm.proview.sample.MoleculeSample;
import ca.qc.ircm.proview.sample.MoleculeSample.StorageTemperature;
import ca.qc.ircm.proview.sample.ProteicSample;
import ca.qc.ircm.proview.sample.ProteicSample.EnrichmentType;
import ca.qc.ircm.proview.sample.ProteicSample.MudPitFraction;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinContent;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.Structure;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionServiceImplTest {
  private static final Pattern LIMS_PATTERN = Pattern.compile("\\w{4}\\d{8}_\\d\\w{3}");
  private SubmissionServiceImpl submissionServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private TemplateEngine templateEngine;
  @Mock
  private SubmissionActivityService submissionActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private TubeService tubeService;
  @Mock
  private EmailService emailService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private PricingEvaluator pricingEvaluator;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  @Captor
  private ArgumentCaptor<HtmlEmail> htmlEmailCaptor;
  private User user;
  private final Random random = new Random();
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    submissionServiceImpl = new SubmissionServiceImpl(entityManager, queryFactory,
        submissionActivityService, activityService, pricingEvaluator, templateEngine, tubeService,
        emailService, authorizationService);
    user = entityManager.find(User.class, 4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    optionalActivity = Optional.of(activity);
  }

  private Optional<Submission> find(Collection<Submission> submissions, long id) {
    return submissions.stream().filter(s -> s.getId() == id).findFirst();
  }

  private SampleSolvent find(Collection<SampleSolvent> solvents, Solvent solvent) {
    for (SampleSolvent ssolvent : solvents) {
      if (ssolvent.getSolvent() == solvent) {
        return ssolvent;
      }
    }
    return null;
  }

  private <S extends Sample> S findByName(Collection<S> samples, String name) {
    for (S sample : samples) {
      if (sample.getName().equals(name)) {
        return sample;
      }
    }
    return null;
  }

  @Test
  public void get() throws Throwable {
    Submission submission = submissionServiceImpl.get(1L);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        submission.getSubmissionDate());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals((Long) 3L, submission.getUser().getId());
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    GelSample gelSample = (GelSample) samples.get(0);
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("IRC20101015_1", gelSample.getLims());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(true, gelSample.getOriginalContainer() instanceof Tube);
    assertEquals(Sample.Support.GEL, gelSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, gelSample.getType());
    assertEquals("Philippe", gelSample.getComments());
    assertEquals(SampleStatus.ANALYSED, gelSample.getStatus());
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
    assertEquals(submission, gelSample.getSubmission());
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
    assertEquals(false, gelSample.isDecoloration());
    assertEquals(null, gelSample.getWeightMarkerQuantity());
    assertEquals(null, gelSample.getProteinQuantity());
  }

  @Test
  public void get_Null() throws Throwable {
    Submission submission = submissionServiceImpl.get(null);

    assertNull(submission);
  }

  @Test
  public void report() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.nameContains("CAP");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    Submission submission = find(submissions, 32).get();
    assertEquals((Long) 32L, submission.getId());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals("cap_project", ((ProteicSample) sample).getProject());
    assertEquals("cap_experience", ((ProteicSample) sample).getExperience());
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 33).get();
    assertEquals((Long) 33L, submission.getId());
    sample = submission.getSamples().get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    Map<Submission, Boolean> linkedToResults = report.getLinkedToResults();
    assertEquals(true, linkedToResults.get(find(submissions, 32).get()));
    assertEquals(false, linkedToResults.get(find(submissions, 33).get()));
  }

  @Test
  public void report_All() throws Throwable {
    final SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_ExperienceContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.experienceContains("cap_experience");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_LaboratoryContains_1() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratoryContains("ircm");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_LaboratoryContains_2() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratoryContains("ircm2");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Laboratory_1() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratory(new Laboratory(1L));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Laboratory_2() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratory(new Laboratory(2L));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_LimsContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.limsContains("RC20111013");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_MaximalSubmissionDate() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.maximalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_MinimalSubmissionDate() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.minimalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_NameContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.nameContains("AP_20111013");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_ProjectContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.projectContains("cap_project");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Status() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.statuses(Arrays.asList(SampleStatus.DATA_ANALYSIS));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Status_Multiple() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.statuses(
        Arrays.asList(SampleStatus.DATA_ANALYSIS, SampleStatus.TO_APPROVE));
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Support_Gel() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.GEL);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Support_Solution() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.SOLUTION);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Support_Molecule_Low() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.MOLECULE_LOW);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_Support_Molecule_High() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.MOLECULE_HIGH);
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_User() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    filter.user(user);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_UserContains_LastName_1() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("coulombe");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_UserContains_LastName_2() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("anderson");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void report_UserContains_FirstName() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("benoit");
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(filter.build());

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void report_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    SubmissionService.Report report = submissionServiceImpl.report(null);

    verify(authorizationService).checkUserRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.nameContains("CAP");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    Submission submission = find(submissions, 32).get();
    assertEquals((Long) 32L, submission.getId());
    SubmissionSample sample = submission.getSamples().get(0);
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
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 33).get();
    assertEquals((Long) 33L, submission.getId());
    sample = submission.getSamples().get(0);
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
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 34).get();
    assertEquals((Long) 34L, submission.getId());
    sample = submission.getSamples().get(0);
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
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 17, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    Map<Submission, Boolean> linkedToResults = report.getLinkedToResults();
    assertEquals(true, linkedToResults.get(find(submissions, 32).get()));
    assertEquals(false, linkedToResults.get(find(submissions, 33).get()));
    assertEquals(true, linkedToResults.get(find(submissions, 34).get()));
  }

  @Test
  public void adminReport_All() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_ExperienceContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.experienceContains("cap_experience");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_LaboratoryContains_1() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratoryContains("ircm");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_LaboratoryContains_2() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratoryContains("ircm2");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Laboratory() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.laboratory(new Laboratory(2L));

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_LimsContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.limsContains("RC20111013");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_MaximalSubmissionDate() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.maximalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_MinimalSubmissionDate() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.minimalSubmissionDate(
        LocalDateTime.of(2011, 10, 15, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_NameContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.nameContains("AP_20111013");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_ProjectContains() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.projectContains("cap_project");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Status() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.statuses(Arrays.asList(SampleStatus.DATA_ANALYSIS));

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Status_Multiple() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.statuses(
        Arrays.asList(SampleStatus.DATA_ANALYSIS, SampleStatus.ANALYSED));

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Support_Gel() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.GEL);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Support_Solution() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.SOLUTION);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Support_MoleculeLow() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.MOLECULE_LOW);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Support_MoleculeHigh() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.MOLECULE_HIGH);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_Support_IntactProtein() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.support(SubmissionSampleService.Support.INTACT_PROTEIN);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_User() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    filter.user(user);

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_UserContains_FirstName() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("benoit");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_UserContains_LastName() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("poitras");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_UserContains_FullName_1() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("Benoit Coulombe");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_UserContains_FullName_2() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("Benoit Coulombe 2");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_UserContains_FullName_3() throws Throwable {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    filter.userContains("Christian Poitras");

    SubmissionService.Report report = submissionServiceImpl.adminReport(filter.build());

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertFalse(find(submissions, 1).isPresent());
    assertFalse(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertFalse(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void adminReport_NullFilter() throws Throwable {
    SubmissionService.Report report = submissionServiceImpl.adminReport(null);

    verify(authorizationService).checkAdminRole();
    List<Submission> submissions = report.getSubmissions();
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  @Deprecated
  public void gelImages() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 1L);

    List<GelImage> images = submissionServiceImpl.gelImages(submission);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals(1, images.size());
    GelImage image = images.get(0);
    assertEquals("frag.jpg", image.getFilename());
    Path expectedContent = Paths.get(getClass().getResource("/submission/frag.jpg").toURI());
    assertArrayEquals(Files.readAllBytes(expectedContent), image.getContent());
  }

  @Test
  @Deprecated
  public void gelImages_Null() throws Throwable {
    List<GelImage> images = submissionServiceImpl.gelImages(null);

    assertEquals(0, images.size());
  }

  @Test
  public void insert_GelSubmission() throws Exception {
    // Create new submission.
    GelSample sample = new GelSample();
    sample.setName("unit_test_gel_01");
    sample.setComments("comments");
    sample.setService(Service.LC_MS_MS);
    sample.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    sample.setSource(Source.ESI);
    sample.setProject("project");
    sample.setExperience("experience");
    sample.setGoal("goal");
    sample.setTaxonomy("human");
    sample.setProtein("protein");
    sample.setMolecularWeight(120.0);
    sample.setPostTranslationModification("my_modification");
    sample.setSampleNumberProtein(10);
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    sample.setUsedProteolyticDigestionMethod("trypsine was not used");
    sample.setOtherProteolyticDigestionMethod("other digestion");
    sample.setProteinIdentification(ProteinIdentification.NCBINR);
    sample.setProteinIdentificationLink("http://localhost/my_site");
    sample.setMudPitFraction(MudPitFraction.EIGHT);
    sample.setProteinContent(ProteinContent.MEDIUM);
    sample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    sample.setOtherEnrichmentType("other enrichment");
    sample.setSeparation(Separation.ONE_DIMENSION);
    sample.setThickness(Thickness.ONE);
    sample.setColoration(Coloration.COOMASSIE);
    sample.setOtherColoration("other coloration");
    sample.setDevelopmentTime("5.0 min");
    sample.setDecoloration(true);
    sample.setWeightMarkerQuantity(20.0);
    sample.setProteinQuantity("20.0 μg");
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    GelImage gelImage = new GelImage();
    gelImage.setFilename("my_gel_image.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    gelImage.setContent(imageContent);
    List<GelImage> gelImages = new LinkedList<GelImage>();
    gelImages.add(gelImage);
    final Set<String> excludes = new HashSet<>();
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            @SuppressWarnings("unchecked")
            Collection<String> methodExcludes = (Collection<String>) invocation.getArguments()[1];
            excludes.addAll(methodExcludes);
            return "unit_test_gel_01";
          }
        });
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);
    submission.setGelImages(gelImages);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(tubeService).generateTubeName(eq(sample), anyCollectionOf(String.class));
    assertEquals(true, excludes.isEmpty());
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(sample, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    assertTrue(samples.get(0) instanceof GelSample);
    GelSample gelSample = (GelSample) samples.get(0);
    assertNotNull(gelSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(gelSample.getLims()).matches());
    assertEquals("unit_test_gel_01", gelSample.getName());
    assertEquals("comments", gelSample.getComments());
    assertEquals(Service.LC_MS_MS, gelSample.getService());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, gelSample.getMassDetectionInstrument());
    assertEquals(Source.ESI, gelSample.getSource());
    assertEquals("project", gelSample.getProject());
    assertEquals("experience", gelSample.getExperience());
    assertEquals("goal", gelSample.getGoal());
    assertEquals("human", gelSample.getTaxonomy());
    assertEquals("protein", gelSample.getProtein());
    assertEquals(new Double(120.0), gelSample.getMolecularWeight());
    assertEquals("my_modification", gelSample.getPostTranslationModification());
    assertEquals(new Integer(10), gelSample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.TRYPSIN, gelSample.getProteolyticDigestionMethod());
    assertEquals("trypsine was not used", gelSample.getUsedProteolyticDigestionMethod());
    assertEquals("other digestion", gelSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, gelSample.getProteinIdentification());
    assertEquals("http://localhost/my_site", gelSample.getProteinIdentificationLink());
    assertEquals(MudPitFraction.EIGHT, gelSample.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, gelSample.getProteinContent());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, gelSample.getEnrichmentType());
    assertEquals("other enrichment", gelSample.getOtherEnrichmentType());
    assertEquals(Separation.ONE_DIMENSION, gelSample.getSeparation());
    assertEquals(Thickness.ONE, gelSample.getThickness());
    assertEquals(Coloration.COOMASSIE, gelSample.getColoration());
    assertEquals("other coloration", gelSample.getOtherColoration());
    assertEquals("5.0 min", gelSample.getDevelopmentTime());
    assertEquals(true, gelSample.isDecoloration());
    assertEquals(new Double(20.0), gelSample.getWeightMarkerQuantity());
    assertEquals("20.0 μg", gelSample.getProteinQuantity());
    Tube tube = (Tube) gelSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_gel_01", tube.getName());
    assertEquals(gelSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    gelImages = submission.getGelImages();
    assertEquals(1, gelImages.size());
    gelImage = gelImages.get(0);
    assertEquals("my_gel_image.jpg", gelImage.getFilename());
    assertArrayEquals(imageContent, gelImage.getContent());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).sendHtmlEmail(any(HtmlEmail.class));
  }

  @Test
  public void insert_EluateSubmission() throws Exception {
    // Create new submission.
    EluateSample sample = new EluateSample();
    sample.setName("unit_test_eluate_01");
    sample.setComments("comments");
    sample.setService(Service.LC_MS_MS);
    sample.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    sample.setSource(Source.ESI);
    sample.setProject("project");
    sample.setExperience("experience");
    sample.setGoal("goal");
    sample.setTaxonomy("human");
    sample.setProtein("protein");
    sample.setMolecularWeight(120.0);
    sample.setPostTranslationModification("my_modification");
    sample.setSampleNumberProtein(10);
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    sample.setUsedProteolyticDigestionMethod("trypsine was not used");
    sample.setOtherProteolyticDigestionMethod("other digestion");
    sample.setProteinIdentification(ProteinIdentification.NCBINR);
    sample.setProteinIdentificationLink("http://localhost/my_site");
    sample.setMudPitFraction(MudPitFraction.EIGHT);
    sample.setProteinContent(ProteinContent.MEDIUM);
    sample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    sample.setOtherEnrichmentType("other enrichment");
    sample.setSupport(Support.SOLUTION);
    sample.setVolume(10.0);
    sample.setQuantity("2.0 μg");
    EluateSample sample2 = new EluateSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setComments("comments");
    sample2.setService(Service.LC_MS_MS);
    sample2.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    sample2.setSource(Source.ESI);
    sample2.setProject("project");
    sample2.setExperience("experience");
    sample2.setGoal("goal");
    sample2.setTaxonomy("human");
    sample2.setProtein("protein");
    sample2.setMolecularWeight(120.0);
    sample2.setPostTranslationModification("my_modification");
    sample2.setSampleNumberProtein(10);
    sample2.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    sample2.setUsedProteolyticDigestionMethod("trypsine was not used");
    sample2.setOtherProteolyticDigestionMethod("other digestion");
    sample2.setProteinIdentification(ProteinIdentification.NCBINR);
    sample2.setProteinIdentificationLink("http://localhost/my_site");
    sample2.setMudPitFraction(MudPitFraction.EIGHT);
    sample2.setProteinContent(ProteinContent.MEDIUM);
    sample2.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    sample2.setOtherEnrichmentType("other enrichment");
    sample2.setSupport(Support.SOLUTION);
    sample2.setVolume(10.0);
    sample2.setQuantity("2.0 μg");
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<Contaminant>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComments("comments");
    List<Standard> standards = new ArrayList<Standard>();
    standards.add(standard);
    sample.setStandards(standards);
    final Set<String> excludes1 = new HashSet<>();
    final Set<String> excludes2 = new HashSet<>();
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            @SuppressWarnings("unchecked")
            Collection<String> methodExcludes = (Collection<String>) invocation.getArguments()[1];
            excludes1.addAll(methodExcludes);
            return "unit_test_eluate_01";
          }
        }).thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            @SuppressWarnings("unchecked")
            Collection<String> methodExcludes = (Collection<String>) invocation.getArguments()[1];
            excludes2.addAll(methodExcludes);
            return "unit_test_eluate_02";
          }
        });
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(tubeService).generateTubeName(eq(sample), anyCollectionOf(String.class));
    assertEquals(true, excludes1.isEmpty());
    verify(tubeService).generateTubeName(eq(sample2), anyCollectionOf(String.class));
    assertEquals(1, excludes2.size());
    assertEquals(true, excludes2.contains("unit_test_eluate_01"));
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(sample, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(samples.get(0) instanceof EluateSample);
    EluateSample eluateSample = (EluateSample) findByName(samples, "unit_test_eluate_01");
    assertNotNull(eluateSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(eluateSample.getLims()).matches());
    assertEquals("unit_test_eluate_01", eluateSample.getName());
    assertEquals("comments", eluateSample.getComments());
    assertEquals(Service.LC_MS_MS, eluateSample.getService());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, eluateSample.getMassDetectionInstrument());
    assertEquals(Source.ESI, eluateSample.getSource());
    assertEquals("project", eluateSample.getProject());
    assertEquals("experience", eluateSample.getExperience());
    assertEquals("goal", eluateSample.getGoal());
    assertEquals("human", eluateSample.getTaxonomy());
    assertEquals("protein", eluateSample.getProtein());
    assertEquals(new Double(120.0), eluateSample.getMolecularWeight());
    assertEquals("my_modification", eluateSample.getPostTranslationModification());
    assertEquals(new Integer(10), eluateSample.getSampleNumberProtein());
    assertEquals(ProteolyticDigestion.TRYPSIN, eluateSample.getProteolyticDigestionMethod());
    assertEquals("trypsine was not used", eluateSample.getUsedProteolyticDigestionMethod());
    assertEquals("other digestion", eluateSample.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, eluateSample.getProteinIdentification());
    assertEquals("http://localhost/my_site", eluateSample.getProteinIdentificationLink());
    assertEquals(MudPitFraction.EIGHT, eluateSample.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, eluateSample.getProteinContent());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, eluateSample.getEnrichmentType());
    assertEquals("other enrichment", eluateSample.getOtherEnrichmentType());
    assertEquals(Support.SOLUTION, eluateSample.getSupport());
    assertEquals(new Double(10.0), eluateSample.getVolume());
    assertEquals("2.0 μg", eluateSample.getQuantity());
    contaminants = eluateSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comments", contaminant.getComments());
    standards = eluateSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comments", standard.getComments());
    Tube tube = (Tube) eluateSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_eluate_01", tube.getName());
    assertEquals(eluateSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    List<GelImage> gelImages = submission.getGelImages();
    assertEquals(0, gelImages.size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    // Validate email that is sent to proteomic users.
    verify(emailService).sendHtmlEmail(any(HtmlEmail.class));
  }

  @Test
  public void insert_MoleculeSubmission() throws Exception {
    // Create new submission.
    MoleculeSample sample = new MoleculeSample();
    sample.setName("unit_test_molecule_01");
    sample.setComments("comments");
    sample.setService(Service.LC_MS_MS);
    sample.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    sample.setSource(Source.ESI);
    sample.setFormula("h2o");
    sample.setMonoisotopicMass(18.0);
    sample.setAverageMass(18.1);
    sample.setSolutionSolvent("ch3oh");
    sample.setOtherSolvent("chrisanol");
    sample.setToxicity("none");
    sample.setLightSensitive(true);
    sample.setStorageTemperature(StorageTemperature.LOW);
    List<SampleSolvent> solvents = new ArrayList<SampleSolvent>();
    solvents.add(new SampleSolvent(Solvent.ACETONITRILE));
    sample.setSolventList(solvents);
    sample.setSupport(Support.SOLUTION);
    sample.setLowResolution(true);
    sample.setHighResolution(true);
    sample.setMsms(true);
    sample.setExactMsms(true);
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    Structure structure = new Structure();
    structure.setFilename("structure.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    structure.setContent(imageContent);
    sample.setStructure(structure);
    final Set<String> excludes = new HashSet<>();
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenAnswer(new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            @SuppressWarnings("unchecked")
            Collection<String> methodExcludes = (Collection<String>) invocation.getArguments()[1];
            excludes.addAll(methodExcludes);
            return "unit_test_molecule_01";
          }
        });
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(tubeService).generateTubeName(eq(sample), anyCollectionOf(String.class));
    assertEquals(true, excludes.isEmpty());
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(sample, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    assertTrue(samples.get(0) instanceof MoleculeSample);
    MoleculeSample moleculeSample = (MoleculeSample) samples.get(0);
    assertNotNull(moleculeSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(moleculeSample.getLims()).matches());
    assertEquals("unit_test_molecule_01", moleculeSample.getName());
    assertEquals("comments", moleculeSample.getComments());
    assertEquals(Service.LC_MS_MS, moleculeSample.getService());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP,
        moleculeSample.getMassDetectionInstrument());
    assertEquals(Source.ESI, moleculeSample.getSource());
    assertEquals("h2o", moleculeSample.getFormula());
    assertEquals(new Double(18.0), moleculeSample.getMonoisotopicMass());
    assertEquals(new Double(18.1), moleculeSample.getAverageMass());
    assertEquals("ch3oh", moleculeSample.getSolutionSolvent());
    assertEquals("chrisanol", moleculeSample.getOtherSolvent());
    assertEquals("none", moleculeSample.getToxicity());
    assertEquals(true, moleculeSample.isLightSensitive());
    assertEquals(StorageTemperature.LOW, moleculeSample.getStorageTemperature());
    assertEquals(1, moleculeSample.getSolventList().size());
    assertNotNull(find(moleculeSample.getSolventList(), Solvent.ACETONITRILE));
    assertEquals(Support.SOLUTION, moleculeSample.getSupport());
    assertEquals(true, moleculeSample.isLowResolution());
    assertEquals(true, moleculeSample.isHighResolution());
    assertEquals(true, moleculeSample.isMsms());
    assertEquals(true, moleculeSample.isExactMsms());
    structure = moleculeSample.getStructure();
    assertEquals("structure.jpg", structure.getFilename());
    assertArrayEquals(imageContent, structure.getContent());
    Tube tube = (Tube) moleculeSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_molecule_01", tube.getName());
    assertEquals(moleculeSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    List<GelImage> gelImages = submission.getGelImages();
    assertEquals(0, gelImages.size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    verify(emailService).sendHtmlEmail(any(HtmlEmail.class));
  }

  @Test
  public void insert_Email() throws Exception {
    // Create new submission.
    EluateSample sample = new EluateSample();
    sample.setName("unit_test_eluate_01");
    sample.setComments("comments");
    sample.setService(Service.LC_MS_MS);
    sample.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    sample.setSource(Source.ESI);
    sample.setProject("project");
    sample.setExperience("experience");
    sample.setGoal("goal");
    sample.setTaxonomy("human");
    sample.setProtein("protein");
    sample.setMolecularWeight(120.0);
    sample.setPostTranslationModification("my_modification");
    sample.setSampleNumberProtein(10);
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    sample.setUsedProteolyticDigestionMethod("trypsine was not used");
    sample.setOtherProteolyticDigestionMethod("other digestion");
    sample.setProteinIdentification(ProteinIdentification.NCBINR);
    sample.setProteinIdentificationLink("http://localhost/my_site");
    sample.setMudPitFraction(MudPitFraction.EIGHT);
    sample.setProteinContent(ProteinContent.MEDIUM);
    sample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    sample.setOtherEnrichmentType("other enrichment");
    sample.setSupport(Support.SOLUTION);
    sample.setVolume(10.0);
    sample.setQuantity("2.0 μg");
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<Contaminant>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComments("comments");
    List<Standard> standards = new ArrayList<Standard>();
    standards.add(standard);
    sample.setStandards(standards);
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenReturn("unit_test_eluate_01");
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(any(Submission.class));
    verify(activityService).insert(activity);
    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).sendHtmlEmail(htmlEmailCaptor.capture());
    List<HtmlEmail> htmlEmails = htmlEmailCaptor.getAllValues();
    Collection<String> receivers = new HashSet<String>();
    for (HtmlEmail email : htmlEmails) {
      receivers.addAll(email.getReceivers());
    }
    assertEquals(true, receivers.contains("christian.poitras@ircm.qc.ca"));
    assertEquals(true, receivers.contains("liam.li@ircm.qc.ca"));
    assertEquals(true, receivers.contains("jackson.smith@ircm.qc.ca"));
    assertEquals(false, receivers.contains("benoit.coulombe@ircm.qc.ca"));
    HtmlEmail htmlEmail = htmlEmails.get(0);
    assertEquals("New samples were submitted", htmlEmail.getSubject());
    assertEquals(true, htmlEmail.getHtmlMessage().contains("unit_test_eluate_01"));
    assertEquals(true, htmlEmail.getTextMessage().contains("unit_test_eluate_01"));
    assertFalse(htmlEmail.getTextMessage().contains("???"));
    assertFalse(htmlEmail.getHtmlMessage().contains("???"));
  }

  @Test
  public void update_NewOwner() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);
    User user = entityManager.find(User.class, 4L);
    Instant newInstant = Instant.now();
    submission.setSubmissionDate(newInstant);
    when(submissionActivityService.update(any(Submission.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionServiceImpl.update(submission, user, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, 1L);
    entityManager.refresh(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(newInstant, submission.getSubmissionDate());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(user, submissionLogged.getUser());
    assertEquals((Long) 1L, submissionLogged.getLaboratory().getId());
    assertEquals(newInstant, submissionLogged.getSubmissionDate());
  }

  @Test
  public void update_NullOwner() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);
    final User user = new User(3L, "benoit.coulombe@ircm.qc.ca");
    Instant newInstant = Instant.now();
    submission.setSubmissionDate(newInstant);
    when(submissionActivityService.update(any(Submission.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionServiceImpl.update(submission, null, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, 1L);
    entityManager.refresh(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(user, submission.getUser());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(newInstant, submission.getSubmissionDate());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(user, submissionLogged.getUser());
    assertEquals((Long) 2L, submissionLogged.getLaboratory().getId());
    assertEquals(newInstant, submissionLogged.getSubmissionDate());
  }

  @Test
  public void update_SetOwnerInSubmission() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);
    final User oldUser = new User(3L, "benoit.coulombe@ircm.qc.ca");
    Instant newInstant = Instant.now();
    submission.setSubmissionDate(newInstant);
    submission.setUser(new User(4L, "jackson.smith@ircm.qc.ca"));
    when(submissionActivityService.update(any(Submission.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionServiceImpl.update(submission, null, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, 1L);
    entityManager.refresh(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(oldUser, submission.getUser());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(newInstant, submission.getSubmissionDate());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(oldUser, submissionLogged.getUser());
    assertEquals((Long) 2L, submissionLogged.getLaboratory().getId());
    assertEquals(newInstant, submissionLogged.getSubmissionDate());
  }
}
