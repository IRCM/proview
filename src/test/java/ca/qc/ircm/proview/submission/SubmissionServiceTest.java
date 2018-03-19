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

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.SearchUtils.findSampleSolvent;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionServiceTest {
  private SubmissionService submissionService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private TemplateEngine emailTemplateEngine;
  @Mock
  private SubmissionActivityService submissionActivityService;
  @Mock
  private ActivityService activityService;
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
  @Captor
  private ArgumentCaptor<Instant> instantCaptor;
  private User user;
  private final Random random = new Random();
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    submissionService = new SubmissionService(entityManager, queryFactory,
        submissionActivityService, activityService, pricingEvaluator, emailTemplateEngine,
        emailService, authorizationService);
    user = entityManager.find(User.class, 4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(emailService.htmlEmail()).thenReturn(email);
    optionalActivity = Optional.of(activity);
  }

  private Optional<SubmissionFile> findFile(List<SubmissionFile> files, String filename) {
    return files.stream().filter(file -> file.getFilename().equals(filename)).findFirst();
  }

  private byte[] getResourceContent(String resource) throws IOException, URISyntaxException {
    Path path = Paths.get(getClass().getResource(resource).toURI());
    return Files.readAllBytes(path);
  }

  @Test
  public void get() throws Throwable {
    Submission submission = submissionService.get(1L);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("Human", submission.getTaxonomy());
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
    assertEquals(null, submission.getQuantificationComment());
    assertEquals("Philippe", submission.getComment());
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
    assertEquals("FAM119A_band_01", sample.getName());
    assertEquals((Long) 1L, sample.getOriginalContainer().getId());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals(SampleType.GEL, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals((Long) 1L, file.getId());
    assertEquals("protocol.txt", file.getFilename());
    assertArrayEquals(
        Files.readAllBytes(Paths.get(getClass().getResource("/submissionfile1.txt").toURI())),
        file.getContent());
    file = submission.getFiles().get(1);
    assertEquals((Long) 2L, file.getId());
    assertEquals("frag.jpg", file.getFilename());
    assertArrayEquals(
        Files.readAllBytes(Paths.get(getClass().getResource("/gelimages1.png").toURI())),
        file.getContent());
  }

  @Test
  public void get_33() throws Throwable {
    Submission submission = submissionService.get(33L);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals((Long) 33L, submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
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
    assertTrue(findSampleSolvent(submission.getSolvents(), Solvent.METHANOL).isPresent());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationComment());
    assertEquals(null, submission.getComment());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals((Long) 3L, submission.getUser().getId());
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals((Long) 3L, sample.getOriginalContainer().getId());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals(SampleType.SOLUTION, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
    assertEquals(1, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals((Long) 3L, file.getId());
    assertEquals("glucose.png", file.getFilename());
    assertArrayEquals(getResourceContent("/sample/glucose.png"), file.getContent());
  }

  @Test
  public void get_Null() throws Throwable {
    Submission submission = submissionService.get(null);

    assertNull(submission);
  }

  @Test
  public void all() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = submissionService.all();

    verify(authorizationService).checkUserRole();
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    Submission submission = find(submissions, 32).get();
    assertEquals((Long) 32L, submission.getId());
    assertEquals("cap_experience", submission.getExperience());
    assertEquals("cap_goal", submission.getGoal());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals(SampleStatus.DATA_ANALYSIS, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 33).get();
    assertEquals((Long) 33L, submission.getId());
    sample = submission.getSamples().get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        sample.getSubmission().getSubmissionDate());
  }

  @Test
  public void all_User() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = submissionService.all();

    verify(authorizationService).checkUserRole();
    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
  }

  @Test
  public void all_Manager() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasLaboratoryManagerPermission(any(Laboratory.class)))
        .thenReturn(true);

    List<Submission> submissions = submissionService.all();

    verify(authorizationService).checkUserRole();
    assertEquals(18, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void all_Admin() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasAdminRole()).thenReturn(true);

    List<Submission> submissions = submissionService.all();

    verify(authorizationService).checkUserRole();
    assertEquals(19, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void all_Filter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = mock(SubmissionFilter.class);

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    verify(filter).addConditions(any());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
  }

  @Test
  public void all_FilterExperiment() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.experienceContains = "exp";

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertTrue(find(submissions, 32).isPresent());
    assertFalse(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
  }

  @Test
  public void all_FilterOffset() throws Throwable {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.id.asc());
    filter.offset = 2;
    filter.limit = 3;

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
  }

  @Test
  public void all_FilterOffsetJoin() throws Throwable {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.anySampleNameContains = "POLR2A";
    filter.sortOrders = Arrays.asList(submission.id.asc());
    filter.offset = 2;
    filter.limit = 3;

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
  }

  @Test
  public void all_SortExperience() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.experience.asc());

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals((Long) 33L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  public void all_SortSampleName() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().name.asc());

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals((Long) 32L, submissions.get(0).getId());
    assertEquals((Long) 33L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  public void all_SortSampleStatus() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().status.asc());

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals((Long) 33L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  public void all_SortResults() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().status.desc());

    List<Submission> submissions = submissionService.all(filter);

    verify(authorizationService).checkUserRole();
    assertEquals((Long) 1L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 33L, submissions.get(2).getId());
  }

  @Test
  public void all_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = submissionService.all(null);

    verify(authorizationService).checkUserRole();
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
  }

  @Test
  public void count_Filter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = mock(SubmissionFilter.class);

    int count = submissionService.count(filter);

    verify(authorizationService).checkUserRole();
    verify(filter).addCountConditions(any());
    assertEquals(3, count);
  }

  @Test
  public void count_FilterExperiment() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.experienceContains = "exp";

    int count = submissionService.count(filter);

    verify(authorizationService).checkUserRole();
    assertEquals(1, count);
  }

  @Test
  public void count_FilterOffset() throws Throwable {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.offset = 2;
    filter.limit = 2;

    int count = submissionService.count(filter);

    verify(authorizationService).checkUserRole();
    assertEquals(15, count);
  }

  @Test
  public void count_FilterOffsetJoin() throws Throwable {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.anySampleNameContains = "POLR2A";
    filter.sortOrders = Arrays.asList(submission.id.asc());
    filter.offset = 2;
    filter.limit = 3;

    int count = submissionService.count(filter);

    verify(authorizationService).checkUserRole();
    assertEquals(10, count);
  }

  @Test
  public void count_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    int count = submissionService.count(null);

    verify(authorizationService).checkUserRole();
    assertEquals(3, count);
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
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
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
    submission.setComment("comment");
    submission.setSamples(samples);
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    SubmissionFile gelImage = new SubmissionFile();
    gelImage.setFilename("my_gel_image.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    gelImage.setContent(imageContent);
    files.add(gelImage);
    submission.setFiles(files);

    submissionService.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
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
    assertEquals("comment", submission.getComment());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals("unit_test_gel_01", submissionSample.getName());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_gel_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(1, tube.getVersion());
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").get();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "my_gel_image.jpg").get();
    assertEquals("my_gel_image.jpg", file.getFilename());
    assertArrayEquals(imageContent, file.getContent());

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
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setType(SampleType.SOLUTION);
    sample2.setVolume("10.0 μl");
    sample2.setQuantity("2.0 μg");
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
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
    submission.setComment("comment");
    submission.setSamples(samples);
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    submission.setFiles(files);

    submissionService.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
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
    assertEquals("comment", submission.getComment());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comment", contaminant.getComment());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comment", standard.getComment());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_eluate_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(1, tube.getVersion());
    files = submission.getFiles();
    assertEquals(1, files.size());
    file = files.get(0);
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());

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
    Plate plate = new Plate();
    plate.initWells();
    plate.setName("unit_test_plate");
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setOriginalContainer(plate.well(0, 0));
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setType(SampleType.SOLUTION);
    sample2.setVolume("10.0 μl");
    sample2.setQuantity("2.0 μg");
    sample2.setOriginalContainer(plate.well(1, 0));
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
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
    submission.setComment("comment");
    submission.setSamples(samples);
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    submission.setFiles(files);

    submissionService.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
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
    assertEquals("comment", submission.getComment());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comment", contaminant.getComment());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comment", standard.getComment());
    Well well = (Well) submissionSample.getOriginalContainer();
    assertNotNull(well);
    assertEquals("unit_test_plate", well.getPlate().getName());
    assertTrue(well.getPlate().isSubmission());
    assertEquals(96, well.getPlate().getWells().size());
    for (Well plateWell : well.getPlate().getWells()) {
      if (plateWell != well.getPlate().well(0, 0) && plateWell != well.getPlate().well(1, 0)) {
        assertNull(plateWell.getSample());
      }
      assertEquals(1, plateWell.getVersion());
    }
    assertEquals(submissionSample, well.getSample());
    assertEquals(sample2, well.getPlate().well(1, 0).getSample());
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals(false, well.isBanned());
    assertEquals(1, well.getVersion());
    files = submission.getFiles();
    assertEquals(1, files.size());
    file = files.get(0);
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());

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
    sample.setType(SampleType.SOLUTION);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
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
    List<SampleSolvent> solvents = new ArrayList<>();
    solvents.add(new SampleSolvent(Solvent.ACETONITRILE));
    submission.setSolvents(solvents);
    submission.setOtherSolvent("chrisanol");
    submission.setComment("comment");
    submission.setSamples(samples);
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    SubmissionFile structure = new SubmissionFile();
    structure.setFilename("structure.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    structure.setContent(imageContent);
    files.add(structure);
    submission.setFiles(files);

    submissionService.insert(submission);

    entityManager.flush();
    verify(authorizationService).checkUserRole();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(user, submission.getUser());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(Instant.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(Instant.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    assertEquals(Service.LC_MS_MS, submission.getService());
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
    assertTrue(findSampleSolvent(submission.getSolvents(), Solvent.ACETONITRILE).isPresent());
    assertEquals("chrisanol", submission.getOtherSolvent());
    assertEquals("none", submission.getToxicity());
    assertEquals(true, submission.isLightSensitive());
    assertEquals(StorageTemperature.LOW, submission.getStorageTemperature());
    assertEquals("comment", submission.getComment());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals("unit_test_molecule_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_molecule_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(1, tube.getVersion());
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").get();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "structure.jpg").get();
    assertEquals("structure.jpg", file.getFilename());
    assertArrayEquals(imageContent, file.getContent());

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
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
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
    submission.setComment("comment");
    submission.setSamples(samples);
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    submission.setFiles(files);

    submissionService.insert(submission);

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
  public void update() throws Exception {
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.setExperience("experience");
    submission.setGoal("goal");
    when(submissionActivityService.update(any(Submission.class))).thenReturn(activity);

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    verify(submissionActivityService).update(submissionCaptor.capture());
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experience", submission.getExperience());
    assertEquals("goal", submission.getGoal());
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
    assertEquals(null, submission.getProtein());
    assertEquals(null, submission.getPostTranslationModification());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals(null, submission.getComment());
    assertEquals(LocalDate.of(2011, 11, 16), toLocalDate(submission.getSubmissionDate()));
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals((Long) 447L, submissionSample.getId());
    assertEquals("CAP_20111116_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("50 μl", submissionSample.getVolume());
    assertEquals("1.5 μg", submissionSample.getQuantity());
    assertEquals(null, submissionSample.getNumberProtein());
    assertEquals(null, submissionSample.getMolecularWeight());
    List<Contaminant> contaminants = submissionSample.getContaminants();
    assertEquals(1, submissionSample.getContaminants().size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals((Long) 3L, contaminant.getId());
    assertEquals("cap_contaminant", contaminant.getName());
    assertEquals("3 μg", contaminant.getQuantity());
    assertEquals("some_comment", contaminant.getComment());
    List<Standard> standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    Standard standard = standards.get(0);
    assertEquals((Long) 5L, standard.getId());
    assertEquals("cap_standard", standard.getName());
    assertEquals("3 μg", standard.getQuantity());
    assertEquals("some_comment", standard.getComment());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertEquals((Long) 9L, tube.getId());
    assertEquals("CAP_20111116_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(0, submission.getFiles().size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_Sample() throws Exception {
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sample -> {
      entityManager.detach(sample);
      entityManager.detach(sample.getOriginalContainer());
    });
    submission.setExperience("experience");
    submission.setGoal("goal");
    submission.getSamples().get(0).setName("unit_test_01");
    submission.getSamples().get(0).setVolume("20.0 μl");
    submission.getSamples().get(0).setQuantity("2.0 μg");
    when(submissionActivityService.update(any(Submission.class))).thenReturn(activity);

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    verify(submissionActivityService).update(submissionCaptor.capture());
    verify(activityService).insert(activity);
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals((Long) 447L, submissionSample.getId());
    assertEquals("unit_test_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("20.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(null, submissionSample.getNumberProtein());
    assertEquals(null, submissionSample.getMolecularWeight());
    List<Contaminant> contaminants = submissionSample.getContaminants();
    assertEquals(1, submissionSample.getContaminants().size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals((Long) 3L, contaminant.getId());
    assertEquals("cap_contaminant", contaminant.getName());
    assertEquals("3 μg", contaminant.getQuantity());
    assertEquals("some_comment", contaminant.getComment());
    List<Standard> standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    Standard standard = standards.get(0);
    assertEquals((Long) 5L, standard.getId());
    assertEquals("cap_standard", standard.getName());
    assertEquals("3 μg", standard.getQuantity());
    assertEquals("some_comment", standard.getComment());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertEquals((Long) 9L, tube.getId());
    assertEquals("unit_test_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(0, submission.getFiles().size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_NewSamples() throws Exception {
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setType(SampleType.SOLUTION);
    sample2.setVolume("10.0 μl");
    sample2.setQuantity("2.0 μg");
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.update(any(Submission.class))).thenReturn(activity);
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.setSamples(samples);

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    verify(submissionActivityService).update(submissionCaptor.capture());
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comment", contaminant.getComment());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comment", standard.getComment());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_eluate_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());
    assertEquals(1, tube.getVersion());
    assertNull(entityManager.find(SubmissionSample.class, 447L));
    assertNull(entityManager.find(Tube.class, 9L));

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_NewSample_Plate() throws Exception {
    // Create new submission.
    Plate plate = new Plate();
    plate.initWells();
    plate.setName("unit_test_plate");
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setOriginalContainer(plate.well(0, 0));
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    SubmissionSample sample2 = new SubmissionSample();
    sample2.setName("unit_test_eluate_02");
    sample2.setType(SampleType.SOLUTION);
    sample2.setVolume("10.0 μl");
    sample2.setQuantity("2.0 μg");
    sample2.setOriginalContainer(plate.well(1, 0));
    sample2.setNumberProtein(10);
    sample2.setMolecularWeight(120.0);
    List<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    when(submissionActivityService.update(any(Submission.class))).thenReturn(activity);
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.setSamples(samples);

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    verify(submissionActivityService).update(submissionCaptor.capture());
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comment", contaminant.getComment());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comment", standard.getComment());
    Well well = (Well) submissionSample.getOriginalContainer();
    assertNotNull(well);
    assertEquals("unit_test_plate", well.getPlate().getName());
    assertEquals(96, well.getPlate().getWells().size());
    assertEquals(submissionSample, well.getSample());
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals(false, well.isBanned());
    assertEquals(1, well.getVersion());
    assertNull(entityManager.find(SubmissionSample.class, 447L));
    assertNull(entityManager.find(Tube.class, 9L));

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_UpdateUser() throws Exception {
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    User user = entityManager.find(User.class, 4L);
    submission.setUser(user);

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
  }

  @Test
  public void update_UpdateDate() throws Exception {
    Submission submission = entityManager.find(Submission.class, 36L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.setSubmissionDate(Instant.now());

    submissionService.update(submission);

    entityManager.flush();
    verify(authorizationService).checkSubmissionWritePermission(submission);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    assertEquals(LocalDate.of(2011, 11, 16), toLocalDate(submission.getSubmissionDate()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_NotToApprove() throws Exception {
    Submission submission = entityManager.find(Submission.class, 147L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });

    submissionService.update(submission);
  }

  @Test
  public void forceUpdate() throws Exception {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
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
    submission.setComment("comment");
    SubmissionFile file = new SubmissionFile();
    file.setFilename("my_file.docx");
    byte[] fileContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      fileContent[i] = (byte) random.nextInt();
    }
    file.setContent(fileContent);
    List<SubmissionFile> files = new LinkedList<>();
    files.add(file);
    SubmissionFile gelImage = new SubmissionFile();
    gelImage.setFilename("my_gel_image.jpg");
    byte[] imageContent = new byte[512];
    for (int i = 0; i < 512; i++) {
      imageContent[i] = (byte) random.nextInt();
    }
    gelImage.setContent(imageContent);
    files.add(gelImage);
    submission.setFiles(files);
    User user = entityManager.find(User.class, 4L);
    submission.setUser(user);
    Instant newInstant = Instant.now();
    submission.setSubmissionDate(newInstant);
    when(submissionActivityService.forceUpdate(any(Submission.class), any(String.class),
        any(Submission.class))).thenReturn(optionalActivity);

    submissionService.forceUpdate(submission, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(submissionActivityService).forceUpdate(submissionCaptor.capture(), eq("unit_test"),
        submissionCaptor.capture());
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, 1L);
    entityManager.refresh(submission);
    verify(activityService).insert(activity);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(user, submission.getUser());
    assertEquals(user.getLaboratory().getId(), submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
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
    assertEquals("comment", submission.getComment());
    assertEquals(newInstant, submission.getSubmissionDate());
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").get();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "my_gel_image.jpg").get();
    assertEquals("my_gel_image.jpg", file.getFilename());
    assertArrayEquals(imageContent, file.getContent());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getAllValues().get(0);
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(user, submissionLogged.getUser());
    assertEquals(user.getLaboratory().getId(), submissionLogged.getLaboratory().getId());
    assertEquals(newInstant, submissionLogged.getSubmissionDate());
    Submission oldSubmissionLogged = submissionCaptor.getAllValues().get(1);
    assertEquals((Long) 1L, oldSubmissionLogged.getId());
    assertEquals((Long) 3L, oldSubmissionLogged.getUser().getId());
    assertEquals((Long) 2L, oldSubmissionLogged.getLaboratory().getId());
    assertEquals(LocalDate.of(2010, 10, 15), toLocalDate(oldSubmissionLogged.getSubmissionDate()));
  }

  @Test
  public void forceUpdate_NewSample() throws Exception {
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0 μg");
    contaminant.setComment("comment");
    List<Contaminant> contaminants = new ArrayList<>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0 μg");
    standard.setComment("comment");
    List<Standard> standards = new ArrayList<>();
    standards.add(standard);
    sample.setStandards(standards);
    Submission submission = entityManager.find(Submission.class, 147L);
    entityManager.detach(submission);
    submission.getSamples().forEach(sa -> {
      entityManager.detach(sa);
      entityManager.detach(sa.getOriginalContainer());
    });
    submission.getSamples().add(sample);
    when(submissionActivityService.forceUpdate(any(Submission.class), any(String.class),
        any(Submission.class))).thenReturn(optionalActivity);

    submissionService.forceUpdate(submission, "unit_test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(submissionActivityService).forceUpdate(submissionCaptor.capture(), eq("unit_test"),
        submissionCaptor.capture());
    verify(pricingEvaluator).computePrice(eq(submission), instantCaptor.capture());
    assertEquals(submission.getSubmissionDate(), instantCaptor.getValue());
    verify(activityService).insert(activity);
    submission = entityManager.find(Submission.class, submission.getId());
    entityManager.refresh(submission);
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(3, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals(new Integer(10), submissionSample.getNumberProtein());
    assertEquals(new Double(120.0), submissionSample.getMolecularWeight());
    contaminants = submissionSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0 μg", contaminant.getQuantity());
    assertEquals("comment", contaminant.getComment());
    standards = submissionSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0 μg", standard.getQuantity());
    assertEquals("comment", standard.getComment());
    Tube tube = (Tube) submissionSample.getOriginalContainer();
    assertNotNull(tube);
    assertEquals("unit_test_eluate_01", tube.getName());
    assertEquals(submissionSample, tube.getSample());
    assertEquals(false, tube.isBanned());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getAllValues().get(0);
    assertEquals((Long) 147L, submissionLogged.getId());
    assertEquals((Long) 559L, submissionLogged.getSamples().get(0).getId());
    assertEquals((Long) 560L, submissionLogged.getSamples().get(1).getId());
    assertNotNull(submissionLogged.getSamples().get(2).getId());
    Submission oldSubmissionLogged = submissionCaptor.getAllValues().get(1);
    assertEquals((Long) 147L, oldSubmissionLogged.getId());
    assertEquals((Long) 559L, oldSubmissionLogged.getSamples().get(0).getId());
    assertEquals((Long) 560L, oldSubmissionLogged.getSamples().get(1).getId());
  }

  @Test
  public void approve() throws Exception {
    Submission submission1 = entityManager.find(Submission.class, 147L);
    submission1.getSamples().stream().forEach(sample -> sample.setStatus(SampleStatus.TO_APPROVE));
    Submission submission2 = entityManager.find(Submission.class, 163L);
    when(submissionActivityService.approve(any())).thenReturn(optionalActivity);

    submissionService.approve(Arrays.asList(submission1, submission2));

    verify(authorizationService).checkApproverRole();
    for (SubmissionSample sample : submission1.getSamples()) {
      assertEquals(SampleStatus.APPROVED, sample.getStatus());
    }
    for (SubmissionSample sample : submission2.getSamples()) {
      assertEquals(SampleStatus.APPROVED, sample.getStatus());
    }
    entityManager.flush();
    verify(submissionActivityService).approve(submission1);
    verify(submissionActivityService).approve(submission2);
    verify(activityService, times(2)).insert(activity);
    submission1 = entityManager.find(Submission.class, submission1.getId());
    submission2 = entityManager.find(Submission.class, submission2.getId());
    for (SubmissionSample sample : submission1.getSamples()) {
      assertEquals(SampleStatus.APPROVED, sample.getStatus());
    }
    for (SubmissionSample sample : submission2.getSamples()) {
      assertEquals(SampleStatus.APPROVED, sample.getStatus());
    }
  }

  @Test
  public void approve_SomeAlreadyApproved() throws Exception {
    Submission submission1 = entityManager.find(Submission.class, 147L);
    Submission submission2 = entityManager.find(Submission.class, 163L);
    when(submissionActivityService.approve(submission1)).thenReturn(Optional.empty());
    when(submissionActivityService.approve(submission2)).thenReturn(optionalActivity);

    submissionService.approve(Arrays.asList(submission1, submission2));

    verify(authorizationService).checkApproverRole();
    entityManager.flush();
    verify(submissionActivityService).approve(submission1);
    verify(submissionActivityService).approve(submission2);
    verify(activityService).insert(activity);
    submission1 = entityManager.find(Submission.class, submission1.getId());
    submission2 = entityManager.find(Submission.class, submission2.getId());
    for (SubmissionSample sample : submission1.getSamples()) {
      assertEquals(SampleStatus.DIGESTED, sample.getStatus());
    }
    for (SubmissionSample sample : submission2.getSamples()) {
      assertEquals(SampleStatus.APPROVED, sample.getStatus());
    }
  }
}
