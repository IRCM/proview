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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.net.URISyntaxException;
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
public class SubmissionServiceTest {
  private static final Pattern LIMS_PATTERN = Pattern.compile("\\w{4}\\d{8}_\\d\\w{3}");
  private SubmissionService submissionServiceImpl;
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
  @Mock
  private MimeMessageHelper email;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private User user;
  private final Random random = new Random();
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    submissionServiceImpl = new SubmissionService(entityManager, queryFactory,
        submissionActivityService, activityService, pricingEvaluator, templateEngine, tubeService,
        emailService, authorizationService);
    user = entityManager.find(User.class, 4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(emailService.htmlEmail()).thenReturn(email);
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

  private byte[] getResourceContent(String resource) throws IOException, URISyntaxException {
    Path path = Paths.get(getClass().getResource(resource).toURI());
    return Files.readAllBytes(path);
  }

  @Test
  public void get() throws Throwable {
    Submission submission = submissionServiceImpl.get(1L);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("Human", submission.getTaxonomy());
    assertEquals("Coulombe", submission.getProject());
    assertEquals("G100429", submission.getExperience());
    assertEquals(null, submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(null, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(null, submission.getOtherEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(ProteinContent.XLARGE, submission.getProteinContent());
    assertEquals(null, submission.getProtein());
    assertEquals(null, submission.getPostTranslationModification());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(GelColoration.SILVER, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals("Philippe", submission.getComments());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals((Long) 3L, submission.getUser().getId());
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 1L, sample.getId());
    assertEquals("IRC20101015_1", sample.getLims());
    assertEquals("FAM119A_band_01", sample.getName());
    assertEquals((Long) 1L, sample.getOriginalContainer().getId());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals(SampleSupport.GEL, sample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, sample.getType());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
  }

  @Test
  public void get_33() throws Throwable {
    Submission submission = submissionServiceImpl.get(33L);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals((Long) 33L, submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(null, submission.getExperience());
    assertEquals(null, submission.getGoal());
    assertEquals(null, submission.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(null, submission.getInjectionType());
    assertEquals(null, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(null, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(null, submission.getOtherEnrichmentType());
    assertEquals(true, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(null, submission.getProteinContent());
    assertEquals(null, submission.getProtein());
    assertEquals(null, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals("C100H100O100", submission.getFormula());
    assertEquals(654.654, submission.getMonoisotopicMass(), 0.0001);
    assertEquals(654.654, submission.getAverageMass(), 0.0001);
    assertEquals("MeOH/TFA 0.1%", submission.getSolutionSolvent());
    assertNotNull(submission.getSolvents());
    assertEquals(1, submission.getSolvents().size());
    assertNotNull(find(submission.getSolvents(), Solvent.METHANOL));
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(null, submission.getComments());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals((Long) 3L, submission.getUser().getId());
    Structure testStructure = submission.getStructure();
    assertEquals("glucose.png", testStructure.getFilename());
    assertArrayEquals(getResourceContent("/sample/glucose.png"), testStructure.getContent());
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals((Long) 3L, sample.getOriginalContainer().getId());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals(SampleSupport.SOLUTION, sample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, sample.getType());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
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
    assertEquals("cap_project", submission.getProject());
    assertEquals("cap_experience", submission.getExperience());
    assertEquals("cap_goal", submission.getGoal());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
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
    filter.statuses(Arrays.asList(SampleStatus.DATA_ANALYSIS, SampleStatus.TO_APPROVE));
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
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("cap_project", submission.getProject());
    assertEquals("cap_experience", submission.getExperience());
    assertEquals("cap_goal", submission.getGoal());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals((Long) 2L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111013_2", sample.getLims());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals(SampleSupport.SOLUTION, sample.getSupport());
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 33).get();
    assertEquals((Long) 33L, submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals((Double) 654.654, submission.getMonoisotopicMass());
    assertEquals("MeOH/TFA 0.1%", submission.getSolutionSolvent());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(1, submission.getSolvents().size());
    assertNotNull(find(submission.getSolvents(), Solvent.METHANOL));
    sample = submission.getSamples().get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals((Long) 2L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("benoit.coulombe@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(SampleSupport.SOLUTION, sample.getSupport());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 34).get();
    assertEquals((Long) 34L, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("cap_project", submission.getProject());
    assertEquals("cap_experience", submission.getExperience());
    assertEquals("cap_goal", submission.getGoal());
    sample = submission.getSamples().get(0);
    assertEquals((Long) 445L, sample.getId());
    assertEquals((Long) 1L, sample.getLaboratory().getId());
    assertEquals("IRCM", sample.getLaboratory().getOrganization());
    assertEquals("christian.poitras@ircm.qc.ca", sample.getUser().getEmail());
    assertEquals("IRC20111017_4", sample.getLims());
    assertEquals("CAP_20111017_01", sample.getName());
    assertEquals(SampleSupport.SOLUTION, sample.getSupport());
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
    filter.statuses(Arrays.asList(SampleStatus.DATA_ANALYSIS, SampleStatus.ANALYSED));

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
  public void insert_GelSubmission() throws Exception {
    // Create new submission.
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_gel_01");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    GelImage gelImage = new GelImage();
    gelImage.setFilename("my_gel_image.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    gelImage.setContent(imageContent);
    List<GelImage> gelImages = new LinkedList<>();
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
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setProject("project");
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    submission.setUsedProteolyticDigestionMethod("trypsine was not used");
    submission.setOtherProteolyticDigestionMethod("other digestion");
    submission.setProteinIdentification(ProteinIdentification.NCBINR);
    submission.setProteinIdentificationLink("http://localhost/my_site");
    submission.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    submission.setOtherEnrichmentType("other enrichment");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setMudPitFraction(MudPitFraction.EIGHT);
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setSeparation(GelSeparation.ONE_DIMENSION);
    submission.setThickness(GelThickness.ONE);
    submission.setColoration(GelColoration.COOMASSIE);
    submission.setOtherColoration("other coloration");
    submission.setDevelopmentTime("5.0 min");
    submission.setDecoloration(true);
    submission.setWeightMarkerQuantity(20.0);
    submission.setProteinQuantity("20.0 μg");
    submission.setComments("comments");
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
    verify(pricingEvaluator).computePrice(submission, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("project", submission.getProject());
    assertEquals("experience", submission.getExperience());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getProteolyticDigestionMethod());
    assertEquals("trypsine was not used", submission.getUsedProteolyticDigestionMethod());
    assertEquals("other digestion", submission.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, submission.getProteinIdentification());
    assertEquals("http://localhost/my_site", submission.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, submission.getEnrichmentType());
    assertEquals("other enrichment", submission.getOtherEnrichmentType());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(MudPitFraction.EIGHT, submission.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(GelColoration.COOMASSIE, submission.getColoration());
    assertEquals("other coloration", submission.getOtherColoration());
    assertEquals("5.0 min", submission.getDevelopmentTime());
    assertEquals(true, submission.isDecoloration());
    assertEquals(new Double(20.0), submission.getWeightMarkerQuantity());
    assertEquals("20.0 μg", submission.getProteinQuantity());
    assertEquals("comments", submission.getComments());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertNotNull(submissionSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(submissionSample.getLims()).matches());
    assertEquals("unit_test_gel_01", submissionSample.getName());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_gel_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
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
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService).send(email);
  }

  @Test
  public void insert_EluateSubmission() throws Exception {
    // Create new submission.
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setSupport(SampleSupport.SOLUTION);
    sample.setVolume(10.0);
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setSupport(SampleSupport.SOLUTION);
    sample2.setVolume(10.0);
    sample2.setQuantity("2.0 μg");
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComments("comments");
    List<Standard> standards = new ArrayList<>();
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
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setProject("project");
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    submission.setUsedProteolyticDigestionMethod("trypsine was not used");
    submission.setOtherProteolyticDigestionMethod("other digestion");
    submission.setProteinIdentification(ProteinIdentification.NCBINR);
    submission.setProteinIdentificationLink("http://localhost/my_site");
    submission.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    submission.setOtherEnrichmentType("other enrichment");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setMudPitFraction(MudPitFraction.EIGHT);
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setComments("comments");
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
    verify(pricingEvaluator).computePrice(submission, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("project", submission.getProject());
    assertEquals("experience", submission.getExperience());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getProteolyticDigestionMethod());
    assertEquals("trypsine was not used", submission.getUsedProteolyticDigestionMethod());
    assertEquals("other digestion", submission.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, submission.getProteinIdentification());
    assertEquals("http://localhost/my_site", submission.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, submission.getEnrichmentType());
    assertEquals("other enrichment", submission.getOtherEnrichmentType());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(MudPitFraction.EIGHT, submission.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("comments", submission.getComments());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    SubmissionSample submissionSample = findByName(samples, "unit_test_eluate_01");
    assertNotNull(submissionSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(submissionSample.getLims()).matches());
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleSupport.SOLUTION, submissionSample.getSupport());
    assertEquals(new Double(10.0), submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comments", contaminant.getComments());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comments", standard.getComments());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_eluate_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    List<GelImage> gelImages = submission.getGelImages();
    assertEquals(0, gelImages.size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService).send(email);
  }

  @Test
  public void insert_EluateSubmission_Plate() throws Exception {
    // Create new submission.
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setSupport(SampleSupport.SOLUTION);
    sample.setVolume(10.0);
    sample.setQuantity("2.0 μg");
    sample.setOriginalContainer(new PlateSpot(0, 0));
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setSupport(SampleSupport.SOLUTION);
    sample2.setVolume(10.0);
    sample2.setQuantity("2.0 μg");
    sample2.setOriginalContainer(new PlateSpot(1, 0));
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComments("comments");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setProject("project");
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    submission.setUsedProteolyticDigestionMethod("trypsine was not used");
    submission.setOtherProteolyticDigestionMethod("other digestion");
    submission.setProteinIdentification(ProteinIdentification.NCBINR);
    submission.setProteinIdentificationLink("http://localhost/my_site");
    submission.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    submission.setOtherEnrichmentType("other enrichment");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setMudPitFraction(MudPitFraction.EIGHT);
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setComments("comments");
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(tubeService, never()).generateTubeName(any(), any());
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(submission, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("project", submission.getProject());
    assertEquals("experience", submission.getExperience());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getProteolyticDigestionMethod());
    assertEquals("trypsine was not used", submission.getUsedProteolyticDigestionMethod());
    assertEquals("other digestion", submission.getOtherProteolyticDigestionMethod());
    assertEquals(ProteinIdentification.NCBINR, submission.getProteinIdentification());
    assertEquals("http://localhost/my_site", submission.getProteinIdentificationLink());
    assertEquals(EnrichmentType.PHOSPHOPEPTIDES, submission.getEnrichmentType());
    assertEquals("other enrichment", submission.getOtherEnrichmentType());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(MudPitFraction.EIGHT, submission.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("comments", submission.getComments());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    SubmissionSample submissionSample = findByName(samples, "unit_test_eluate_01");
    assertNotNull(submissionSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(submissionSample.getLims()).matches());
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleSupport.SOLUTION, submissionSample.getSupport());
    assertEquals(new Double(10.0), submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comments", contaminant.getComments());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comments", standard.getComments());
    PlateSpot spot = (PlateSpot) submissionSample.getOriginalContainer();
    assertNotNull(spot);
    assertEquals(submission.getExperience(), spot.getPlate().getName());
    assertEquals(Plate.Type.SUBMISSION, spot.getPlate().getType());
    assertEquals(96, spot.getPlate().getSpots().size());
    assertEquals(submissionSample, spot.getSample());
    assertEquals(0, spot.getRow());
    assertEquals(0, spot.getColumn());
    assertEquals(false, spot.isBanned());
    List<GelImage> gelImages = submission.getGelImages();
    assertEquals(0, gelImages.size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService).send(email);
  }

  @Test
  public void insert_MoleculeSubmission() throws Exception {
    // Create new submission.
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_molecule_01");
    sample.setSupport(SampleSupport.SOLUTION);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
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
    submission.setService(Service.LC_MS_MS);
    submission.setProject("project");
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setLowResolution(true);
    submission.setHighResolution(true);
    submission.setMsms(true);
    submission.setExactMsms(true);
    submission.setFormula("h2o");
    submission.setMonoisotopicMass(18.0);
    submission.setAverageMass(18.1);
    submission.setSolutionSolvent("ch3oh");
    submission.setToxicity("none");
    submission.setLightSensitive(true);
    submission.setStorageTemperature(StorageTemperature.LOW);
    Structure structure = new Structure();
    structure.setFilename("structure.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    structure.setContent(imageContent);
    submission.setStructure(structure);
    List<SampleSolvent> solvents = new ArrayList<>();
    solvents.add(new SampleSolvent(Solvent.ACETONITRILE));
    submission.setSolvents(solvents);
    submission.setOtherSolvent("chrisanol");
    submission.setComments("comments");
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
    verify(pricingEvaluator).computePrice(submission, instant);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(instant, submission.getSubmissionDate());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("project", submission.getProject());
    assertEquals("experience", submission.getExperience());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(true, submission.isLowResolution());
    assertEquals(true, submission.isHighResolution());
    assertEquals(true, submission.isMsms());
    assertEquals(true, submission.isExactMsms());
    assertEquals("h2o", submission.getFormula());
    assertEquals(new Double(18.0), submission.getMonoisotopicMass());
    assertEquals(new Double(18.1), submission.getAverageMass());
    assertEquals("ch3oh", submission.getSolutionSolvent());
    assertEquals(1, submission.getSolvents().size());
    assertNotNull(find(submission.getSolvents(), Solvent.ACETONITRILE));
    assertEquals("chrisanol", submission.getOtherSolvent());
    assertEquals("none", submission.getToxicity());
    assertEquals(true, submission.isLightSensitive());
    assertEquals(StorageTemperature.LOW, submission.getStorageTemperature());
    structure = submission.getStructure();
    assertEquals("structure.jpg", structure.getFilename());
    assertArrayEquals(imageContent, structure.getContent());
    assertEquals("comments", submission.getComments());
    assertEquals(submission.getId(), submission.getId());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertNotNull(submissionSample.getLims());
    assertEquals(true, LIMS_PATTERN.matcher(submissionSample.getLims()).matches());
    assertEquals("unit_test_molecule_01", submissionSample.getName());
    assertEquals(SampleSupport.SOLUTION, submissionSample.getSupport());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_molecule_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    List<GelImage> gelImages = submission.getGelImages();
    assertEquals(0, gelImages.size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService).send(email);
  }

  @Test
  public void insert_Email() throws Exception {
    // Create new submission.
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setSupport(SampleSupport.SOLUTION);
    sample.setVolume(10.0);
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComments("comments");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenReturn("unit_test_eluate_01");
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setProject("project");
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    submission.setUsedProteolyticDigestionMethod("trypsine was not used");
    submission.setOtherProteolyticDigestionMethod("other digestion");
    submission.setProteinIdentification(ProteinIdentification.NCBINR);
    submission.setProteinIdentificationLink("http://localhost/my_site");
    submission.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    submission.setOtherEnrichmentType("other enrichment");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setMudPitFraction(MudPitFraction.EIGHT);
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setComments("comments");
    submission.setSamples(samples);
    Instant instant = Instant.now();
    submission.setSubmissionDate(instant);

    submissionServiceImpl.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(any(Submission.class));
    verify(activityService).insert(activity);
    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService, atLeastOnce()).send(email);
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, never()).addTo("benoit.coulombe@ircm.qc.ca");
    verify(email).setSubject("New samples were submitted");
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertEquals(true, textContent.contains("unit_test_eluate_01"));
    assertEquals(true, htmlContent.contains("unit_test_eluate_01"));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
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
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(newInstant, submission.getSubmissionDate());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(user.getId(), submissionLogged.getUser().getId());
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
    assertEquals(oldUser.getId(), submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(newInstant, submission.getSubmissionDate());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(oldUser.getId(), submissionLogged.getUser().getId());
    assertEquals((Long) 2L, submissionLogged.getLaboratory().getId());
    assertEquals(newInstant, submissionLogged.getSubmissionDate());
  }
}
