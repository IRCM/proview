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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeRepository;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.text.MessageResource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@WithMockUser
public class SubmissionServiceTest extends AbstractServiceTestCase {
  private static final String READ = "read";
  private static final String WRITE = "write";
  @Inject
  private SubmissionService service;
  @Inject
  private SubmissionRepository repository;
  @Inject
  private SubmissionSampleRepository sampleRepository;
  @Inject
  private UserRepository userRepository;
  @Inject
  private LaboratoryRepository laboratoryRepository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private PlateRepository plateRepository;
  @MockBean
  private SubmissionActivityService submissionActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
  private EmailService emailService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private PermissionEvaluator permissionEvaluator;
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
    user = userRepository.findById(4L).orElse(null);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(emailService.htmlEmail()).thenReturn(email);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
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
    Submission submission = service.get(1L);

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 1L, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("Human", submission.getTaxonomy());
    assertEquals("G100429", submission.getExperiment());
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
    assertEquals(LocalDateTime.of(2010, 10, 15, 0, 0, 0, 0), submission.getSubmissionDate());
    assertEquals(LocalDate.of(2010, 12, 9), submission.getSampleDeliveryDate());
    assertEquals(LocalDate.of(2010, 12, 11), submission.getDigestionDate());
    assertEquals(LocalDate.of(2010, 12, 13), submission.getAnalysisDate());
    assertEquals(LocalDate.of(2010, 12, 15), submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
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
    Submission submission = service.get(33L);

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 33L, submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
    assertEquals(null, submission.getExperiment());
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
    assertTrue(submission.getSolvents().contains(Solvent.METHANOL));
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationComment());
    assertEquals(null, submission.getComment());
    assertEquals(LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0), submission.getSubmissionDate());
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
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
    assertEquals(SampleStatus.WAITING, sample.getStatus());
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
  public void get_NullId() throws Throwable {
    Submission submission = service.get((Long) null);

    assertNull(submission);
  }

  @Test
  public void get_Plate() throws Throwable {
    Plate plate = plateRepository.findById(123L).orElse(null);

    Submission submission = service.get(plate);

    assertEquals((Long) 163L, submission.getId());
    verify(permissionEvaluator).hasPermission(any(), eq(plate), eq(READ));
  }

  @Test
  public void get_PlateNotSubmision() throws Throwable {
    Plate plate = plateRepository.findById(26L).orElse(null);

    Submission submission = service.get(plate);

    assertNull(submission);
    verify(permissionEvaluator).hasPermission(any(), eq(plate), eq(READ));
  }

  @Test
  public void get_NullPlate() throws Throwable {
    Submission submission = service.get((Plate) null);

    assertNull(submission);
  }

  @Test
  public void all() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = service.all(null);

    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    Submission submission = find(submissions, 32).get();
    assertEquals((Long) 32L, submission.getId());
    assertEquals("cap_experiment", submission.getExperiment());
    assertEquals("cap_goal", submission.getGoal());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 442L, sample.getId());
    assertEquals("CAP_20111013_01", sample.getName());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertEquals(LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0),
        sample.getSubmission().getSubmissionDate());
    submission = find(submissions, 33).get();
    assertEquals((Long) 33L, submission.getId());
    sample = submission.getSamples().get(0);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(SampleStatus.WAITING, sample.getStatus());
    assertEquals(LocalDateTime.of(2011, 10, 13, 0, 0, 0, 0),
        sample.getSubmission().getSubmissionDate());
  }

  @Test
  public void all_User() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = service.all(null);

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
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);

    List<Submission> submissions = service.all(null);

    verify(authorizationService).hasPermission(user.getLaboratory(), BasePermission.WRITE);
    assertEquals(18, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
  }

  @Test
  public void all_Admin() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);

    List<Submission> submissions = service.all(null);

    assertEquals(20, submissions.size());
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
    when(filter.predicate()).thenReturn(submission.isNotNull());

    List<Submission> submissions = service.all(filter);

    verify(filter).predicate();
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
    filter.experimentContains = "exp";

    List<Submission> submissions = service.all(filter);

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

    List<Submission> submissions = service.all(filter);

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
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

    List<Submission> submissions = service.all(filter);

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
  }

  @Test
  public void all_SortExperiment() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.experiment.asc());

    List<Submission> submissions = service.all(filter);

    assertEquals((Long) 33L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  @Ignore
  public void all_SortSampleName() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().name.asc());

    List<Submission> submissions = service.all(filter);

    assertEquals((Long) 32L, submissions.get(0).getId());
    assertEquals((Long) 33L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  @Ignore
  public void all_SortSampleStatus() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().status.asc());

    List<Submission> submissions = service.all(filter);

    assertEquals((Long) 33L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  @Ignore
  public void all_SortResults() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.sortOrders = Arrays.asList(submission.samples.any().status.desc());

    List<Submission> submissions = service.all(filter);

    assertEquals((Long) 1L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 33L, submissions.get(2).getId());
  }

  @Test
  public void all_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<Submission> submissions = service.all(null);

    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void all_AccessDenied() {
    SubmissionFilter filter = mock(SubmissionFilter.class);
    when(filter.predicate()).thenReturn(submission.isNotNull());

    service.all(filter);
  }

  @Test
  public void count_Filter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = mock(SubmissionFilter.class);
    when(filter.predicate()).thenReturn(submission.isNotNull());

    int count = service.count(filter);

    verify(filter).predicate();
    assertEquals(3, count);
  }

  @Test
  public void count_FilterExperiment() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);
    SubmissionFilter filter = new SubmissionFilter();
    filter.experimentContains = "exp";

    int count = service.count(filter);

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

    int count = service.count(filter);

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

    int count = service.count(filter);

    assertEquals(10, count);
  }

  @Test
  public void count_NullFilter() throws Throwable {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authorizationService.getCurrentUser()).thenReturn(user);

    int count = service.count(null);

    assertEquals(3, count);
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void count_AccessDenied() {
    SubmissionFilter filter = mock(SubmissionFilter.class);
    when(filter.predicate()).thenReturn(submission.isNotNull());

    service.count(filter);
  }

  private Submission submissionForPrint(Service service) {
    Submission submission = new Submission();
    submission.setService(service);
    submission.setSubmissionDate(LocalDateTime.now().minus(2, ChronoUnit.DAYS));
    submission.setUser(userRepository.findById(3L).orElse(null));
    submission.setLaboratory(laboratoryRepository.findById(2L).orElse(null));
    SubmissionSample sample1 = new SubmissionSample(1L, "first sample");
    sample1.setQuantity("15 ug");
    sample1.setVolume("10 ul");
    sample1.setType(SampleType.SOLUTION);
    sample1.setMolecularWeight(150.0);
    sample1.setOriginalContainer(new Tube(1L, "first sample"));
    sample1.setNumberProtein(5);
    sample1.setMolecularWeight(8.9);
    SubmissionSample sample2 = new SubmissionSample(2L, "second sample");
    sample2.setQuantity("15 ug");
    sample2.setVolume("10 ul");
    sample2.setType(SampleType.SOLUTION);
    sample2.setMolecularWeight(150.0);
    sample2.setOriginalContainer(new Tube(2L, "second sample"));
    sample2.setNumberProtein(6);
    sample2.setMolecularWeight(9.3);
    if (service == Service.SMALL_MOLECULE) {
      submission.setSamples(new ArrayList<>(Arrays.asList(sample1)));
    } else {
      submission.setSamples(new ArrayList<>(Arrays.asList(sample1, sample2)));
    }
    submission.setExperiment("my experiment");
    submission.setGoal("my goal");
    submission.setTaxonomy("human");
    submission.setProtein("POLR2A");
    submission.setPostTranslationModification("phospho");
    submission.setSeparation(GelSeparation.ONE_DIMENSION);
    submission.setThickness(GelThickness.ONE);
    submission.setColoration(GelColoration.COOMASSIE);
    submission.setOtherColoration("my coloration");
    submission.setDevelopmentTime("5 sec");
    submission.setDecoloration(true);
    submission.setWeightMarkerQuantity(12.5);
    submission.setProteinQuantity("5 ug");
    submission.setSolutionSolvent("H2O");
    submission.setFormula("CH3OH");
    submission.setMonoisotopicMass(10.2);
    submission.setAverageMass(11.2);
    submission.setToxicity("can kill");
    submission.setLightSensitive(true);
    submission.setStorageTemperature(StorageTemperature.MEDIUM);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
    submission.setUsedProteolyticDigestionMethod("my trypsin");
    submission.setOtherProteolyticDigestionMethod("other trypsin");
    submission.setInjectionType(InjectionType.DIRECT_INFUSION);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setProteinContent(ProteinContent.LARGE);
    submission.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setProteinIdentification(ProteinIdentification.REFSEQ);
    submission.setProteinIdentificationLink("https://www.ncbi.nlm.nih.gov/home/download/");
    submission.setQuantification(Quantification.LABEL_FREE);
    submission.setQuantificationComment("quantification comment\nsecond line");
    submission.setHighResolution(true);
    submission.setSolvents(new ArrayList<>(Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3)));
    submission.setOtherSolvent("other solvent");
    submission.setComment("my comment\nsecond line");
    return submission;
  }

  private String formatMultiline(String comment) {
    return comment != null ? comment.replaceAll("\\r?\\n", "<br>") : "";
  }

  @Test
  public void print_LcmsmsSolution() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"platform\""));
    assertTrue(content.contains(resources.message("platform")));
    assertTrue(content.contains("class=\"service\""));
    assertTrue(content.contains(submission.getService().getLabel(locale)));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    assertTrue(content.contains(submission.getUser().getPhoneNumbers().get(0).getValue(locale)));
    assertTrue(content.contains("class=\"laboratory-name\""));
    assertTrue(content.contains(submission.getLaboratory().getName()));
    assertTrue(content.contains("class=\"laboratory-director\""));
    assertTrue(content.contains(submission.getLaboratory().getDirector()));
    assertTrue(content.contains("class=\"user-email\""));
    assertTrue(content.contains(submission.getUser().getEmail()));
    assertTrue(content.contains("class=\"sample-count\""));
    assertTrue(content.contains(String.valueOf(submission.getSamples().size())));
    assertTrue(content.contains("class=\"sample-name\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(sample.getName()));
    }
    assertTrue(content.contains("class=\"experiment\""));
    assertTrue(content.contains(submission.getExperiment()));
    assertTrue(content.contains("class=\"goal\""));
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(submission.getSamples().get(0).getType().getLabel(locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertTrue(content.contains(submission.getProtein()));
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(
        content.contains(String.valueOf(submission.getSamples().get(0).getMolecularWeight())));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertTrue(content.contains("class=\"sample-quantity\""));
    assertTrue(content.contains(submission.getSamples().get(0).getQuantity()));
    assertTrue(content.contains("class=\"sample-volume\""));
    assertTrue(content.contains(submission.getSamples().get(0).getVolume()));
    assertFalse(content.contains("class=\"separation\""));
    assertFalse(content.contains("class=\"thickness\""));
    assertFalse(content.contains("class=\"coloration\""));
    assertFalse(content.contains("class=\"otherColoration\""));
    assertFalse(content.contains("class=\"developmentTime\""));
    assertFalse(content.contains("class=\"decoloration\""));
    assertFalse(content.contains("class=\"weightMarkerQuantity\""));
    assertFalse(content.contains("class=\"proteinQuantity\""));
    assertFalse(content.contains("class=\"solutionSolvent\""));
    assertFalse(content.contains("class=\"formula\""));
    assertFalse(content.contains("class=\"monoisotopicMass\""));
    assertFalse(content.contains("class=\"averageMass\""));
    assertFalse(content.contains("class=\"toxicity\""));
    assertFalse(content.contains("class=\"lightSensitive\""));
    assertFalse(content.contains("class=\"storageTemperature\""));
    assertTrue(content.contains("class=\"proteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getProteolyticDigestionMethod().getLabel(locale)));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertTrue(content.contains("class=\"proteinContent\""));
    assertTrue(content.contains(submission.getProteinContent().getLabel(locale)));
    assertTrue(content.contains("class=\"massDetectionInstrument\""));
    assertTrue(content.contains(submission.getMassDetectionInstrument().getLabel(locale)));
    assertTrue(content.contains("class=\"proteinIdentification\""));
    assertTrue(content.contains(submission.getProteinIdentification().getLabel(locale)));
    assertFalse(content.contains("class=\"proteinIdentificationLink\""));
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains(submission.getQuantification().getLabel(locale)));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertFalse(content.contains("class=\"highResolution\""));
    assertFalse(content.contains("class=\"solvent\""));
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_LcmsmsSolution_NoDate() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setSubmissionDate(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"submissionDate\""));
  }

  @Test
  public void print_LcmsmsSolution_NoLaboratory() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setLaboratory(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"laboratory-name\""));
    assertFalse(content.contains("class=\"laboratory-director\""));
  }

  @Test
  public void print_LcmsmsSolution_NoUser() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setUser(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"user-name\""));
    assertFalse(content.contains("class=\"user-phone\""));
    assertFalse(content.contains("class=\"user-email\""));
  }

  @Test
  public void print_LcmsmsSolution_NoPhone() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Locale locale = Locale.getDefault();
    submission.getUser().getPhoneNumbers().clear();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"user-phone\""));
  }

  @Test
  public void print_LcmsmsSolution_NoExperiment() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Locale locale = Locale.getDefault();
    submission.setExperiment(null);

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"experiment\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoGoal() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Locale locale = Locale.getDefault();
    submission.setGoal(null);

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"goal\""));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinName() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProtein(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"protein\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoMolecularWeight() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setMolecularWeight(null));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoPostTranslationModification() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setPostTranslationModification(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoSampleQuantity() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setQuantity(null));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-quantity\""));
  }

  @Test
  public void print_LcmsmsSolution_NoSampleVolume() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setVolume(null));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsDry() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.DRY));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsBeads() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.BIOID_BEADS));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsSolution_NoDigestion() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteolyticDigestionMethod(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"proteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
  }

  @Test
  public void print_LcmsmsSolution_Digested() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.DIGESTED);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"proteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getProteolyticDigestionMethod().getLabel(locale)));
    assertTrue(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getUsedProteolyticDigestionMethod()));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
  }

  @Test
  public void print_LcmsmsSolution_OtherDigestion() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteolyticDigestionMethod(ProteolyticDigestion.OTHER);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"proteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getProteolyticDigestionMethod().getLabel(locale)));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertTrue(content.contains("class=\"otherProteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getOtherProteolyticDigestionMethod()));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinContent() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteinContent(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"proteinContent\""));
  }

  @Test
  public void print_LcmsmsSolution_NoMassDetectionInstrument() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setMassDetectionInstrument(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"massDetectionInstrument\""));
    assertTrue(
        content.contains(MassDetectionInstrument.getNullLabel(locale).replaceAll("'", "&#39;")));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinIdentification() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteinIdentification(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"proteinIdentification\""));
    assertFalse(content.contains("class=\"proteinIdentificationLink\""));
  }

  @Test
  public void print_LcmsmsSolution_OtherProteinIdentificationLink() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteinIdentification(ProteinIdentification.OTHER);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"proteinIdentification\""));
    assertTrue(content.contains("class=\"proteinIdentificationLink\""));
    assertTrue(content.contains(submission.getProteinIdentificationLink()));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinIdentificationLink() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteinIdentification(ProteinIdentification.OTHER);
    submission.setProteinIdentificationLink(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"proteinIdentification\""));
    assertTrue(content.contains("class=\"proteinIdentificationLink\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoQuantification() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantificationComment\""));
  }

  @Test
  public void print_LcmsmsSolution_SilacQuantification() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.SILAC);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(resources.message("submission.quantificationComment")));
    assertTrue(content.contains(formatMultiline(submission.getQuantificationComment())));
  }

  @Test
  public void print_LcmsmsSolution_SilacQuantificationNoComment() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.SILAC);
    submission.setQuantificationComment(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(resources.message("submission.quantificationComment")));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_TmtQuantification() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.TMT);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(resources.message("submission.quantificationComment.TMT")));
    assertTrue(content.contains(formatMultiline(submission.getQuantificationComment())));
  }

  @Test
  public void print_LcmsmsSolution_TmtQuantificationNoComment() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.TMT);
    submission.setQuantificationComment(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(resources.message("submission.quantificationComment.TMT")));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsGel() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"platform\""));
    assertTrue(content.contains(resources.message("platform")));
    assertTrue(content.contains("class=\"service\""));
    assertTrue(content.contains(submission.getService().getLabel(locale)));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    assertTrue(content.contains(submission.getUser().getPhoneNumbers().get(0).getValue(locale)));
    assertTrue(content.contains("class=\"laboratory-name\""));
    assertTrue(content.contains(submission.getLaboratory().getName()));
    assertTrue(content.contains("class=\"laboratory-director\""));
    assertTrue(content.contains(submission.getLaboratory().getDirector()));
    assertTrue(content.contains("class=\"user-email\""));
    assertTrue(content.contains(submission.getUser().getEmail()));
    assertTrue(content.contains("class=\"sample-count\""));
    assertTrue(content.contains(String.valueOf(submission.getSamples().size())));
    assertTrue(content.contains("class=\"sample-name\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(sample.getName()));
    }
    assertTrue(content.contains("class=\"experiment\""));
    assertTrue(content.contains(submission.getExperiment()));
    assertTrue(content.contains("class=\"goal\""));
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(submission.getSamples().get(0).getType().getLabel(locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertTrue(content.contains(submission.getProtein()));
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(
        content.contains(String.valueOf(submission.getSamples().get(0).getMolecularWeight())));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertFalse(content.contains("class=\"sample-quantity\""));
    assertFalse(content.contains("class=\"sample-volume\""));
    assertTrue(content.contains("class=\"separation\""));
    assertTrue(content.contains(submission.getSeparation().getLabel(locale)));
    assertTrue(content.contains("class=\"thickness\""));
    assertTrue(content.contains(submission.getThickness().getLabel(locale)));
    assertTrue(content.contains("class=\"coloration\""));
    assertTrue(content.contains(submission.getColoration().getLabel(locale)));
    assertFalse(content.contains("class=\"otherColoration\""));
    assertTrue(content.contains("class=\"developmentTime\""));
    assertTrue(content.contains(submission.getDevelopmentTime()));
    assertTrue(content.contains("class=\"decoloration\""));
    assertTrue(content.contains(resources.message("submission.decoloration.true")));
    assertTrue(content.contains("class=\"weightMarkerQuantity\""));
    assertTrue(content.contains(String.valueOf(submission.getWeightMarkerQuantity())));
    assertTrue(content.contains("class=\"proteinQuantity\""));
    assertTrue(content.contains(submission.getProteinQuantity()));
    assertFalse(content.contains("class=\"solutionSolvent\""));
    assertFalse(content.contains("class=\"formula\""));
    assertFalse(content.contains("class=\"monoisotopicMass\""));
    assertFalse(content.contains("class=\"averageMass\""));
    assertFalse(content.contains("class=\"toxicity\""));
    assertFalse(content.contains("class=\"lightSensitive\""));
    assertFalse(content.contains("class=\"storageTemperature\""));
    assertTrue(content.contains("class=\"proteolyticDigestionMethod\""));
    assertTrue(content.contains(submission.getProteolyticDigestionMethod().getLabel(locale)));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertTrue(content.contains("class=\"proteinContent\""));
    assertTrue(content.contains(submission.getProteinContent().getLabel(locale)));
    assertTrue(content.contains("class=\"massDetectionInstrument\""));
    assertTrue(content.contains(submission.getMassDetectionInstrument().getLabel(locale)));
    assertTrue(content.contains("class=\"proteinIdentification\""));
    assertTrue(content.contains(submission.getProteinIdentification().getLabel(locale)));
    assertFalse(content.contains("class=\"proteinIdentificationLink\""));
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains(submission.getQuantification().getLabel(locale)));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertFalse(content.contains("class=\"highResolution\""));
    assertFalse(content.contains("class=\"solvent\""));
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_LcmsmsGel_NoSeparation() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setSeparation(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"separation\""));
  }

  @Test
  public void print_LcmsmsGel_NoThickness() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setThickness(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"thickness\""));
  }

  @Test
  public void print_LcmsmsGel_NoColoration() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"coloration\""));
  }

  @Test
  public void print_LcmsmsGel_OtherColoration() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(GelColoration.OTHER);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertTrue(content.contains("class=\"coloration\""));
    assertTrue(content.contains("class=\"otherColoration\""));
    assertTrue(content.contains(submission.getOtherColoration()));
  }

  @Test
  public void print_LcmsmsGel_NoOtherColoration() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(GelColoration.OTHER);
    submission.setOtherColoration(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertTrue(content.contains("class=\"coloration\""));
    assertTrue(content.contains("class=\"otherColoration\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsGel_NoDevelopmentTime() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setDevelopmentTime(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"developmentTime\""));
  }

  @Test
  public void print_LcmsmsGel_NoWeightMarkerQuantity() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setWeightMarkerQuantity(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"weightMarkerQuantity\""));
  }

  @Test
  public void print_LcmsmsGel_NoProteinQuantity() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setProteinQuantity(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"proteinQuantity\""));
  }

  @Test
  public void print_SmallMolecule() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"platform\""));
    assertTrue(content.contains(resources.message("platform")));
    assertTrue(content.contains("class=\"service\""));
    assertTrue(content.contains(submission.getService().getLabel(locale)));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    assertTrue(content.contains(submission.getUser().getPhoneNumbers().get(0).getValue(locale)));
    assertTrue(content.contains("class=\"laboratory-name\""));
    assertTrue(content.contains(submission.getLaboratory().getName()));
    assertTrue(content.contains("class=\"laboratory-director\""));
    assertTrue(content.contains(submission.getLaboratory().getDirector()));
    assertTrue(content.contains("class=\"user-email\""));
    assertTrue(content.contains(submission.getUser().getEmail()));
    assertFalse(content.contains("class=\"sample-count\""));
    assertTrue(content.contains("class=\"sample-name\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(sample.getName()));
    }
    assertFalse(content.contains("class=\"experiment\""));
    assertFalse(content.contains("class=\"goal\""));
    assertFalse(content.contains("class=\"taxonomy\""));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(submission.getSamples().get(0).getType().getLabel(locale)));
    assertFalse(content.contains("class=\"protein\""));
    assertFalse(content.contains("class=\"sample-molecularWeight\""));
    assertFalse(content.contains("class=\"postTranslationModification\""));
    assertFalse(content.contains("class=\"sample-quantity\""));
    assertFalse(content.contains("class=\"sample-volume\""));
    assertFalse(content.contains("class=\"separation\""));
    assertFalse(content.contains("class=\"thickness\""));
    assertFalse(content.contains("class=\"coloration\""));
    assertFalse(content.contains("class=\"otherColoration\""));
    assertFalse(content.contains("class=\"developmentTime\""));
    assertFalse(content.contains("class=\"decoloration\""));
    assertFalse(content.contains("class=\"weightMarkerQuantity\""));
    assertFalse(content.contains("class=\"proteinQuantity\""));
    assertTrue(content.contains("class=\"solutionSolvent\""));
    assertTrue(content.contains(submission.getSolutionSolvent()));
    assertTrue(content.contains("class=\"formula\""));
    assertTrue(content.contains(submission.getFormula()));
    assertTrue(content.contains("class=\"monoisotopicMass\""));
    assertTrue(content.contains(String.valueOf(submission.getMonoisotopicMass())));
    assertTrue(content.contains("class=\"averageMass\""));
    assertTrue(content.contains(String.valueOf(submission.getAverageMass())));
    assertTrue(content.contains("class=\"toxicity\""));
    assertTrue(content.contains(submission.getToxicity()));
    assertTrue(content.contains("class=\"lightSensitive\""));
    assertTrue(content.contains(resources.message("submission.lightSensitive.true")));
    assertTrue(content.contains("class=\"storageTemperature\""));
    assertTrue(content.contains(submission.getStorageTemperature().getLabel(locale)));
    assertFalse(content.contains("class=\"proteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertFalse(content.contains("class=\"proteinContent\""));
    assertFalse(content.contains("class=\"massDetectionInstrument\""));
    assertFalse(content.contains("class=\"proteinIdentification\""));
    assertFalse(content.contains("class=\"proteinIdentificationLink\""));
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains("class=\"highResolution\""));
    assertTrue(content.contains(resources.message("submission.highResolution.true")));
    assertTrue(content.contains("class=\"solvent\""));
    for (Solvent solvent : Solvent.values()) {
      assertEquals(solvent.name(),
          submission.getSolvents().stream().filter(ss -> ss == solvent).findAny().isPresent(),
          content.contains(solvent.getLabel(locale)));
    }
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_SmallMolecule_NoSolutionSolvent() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setSolutionSolvent(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"solutionSolvent\""));
  }

  @Test
  public void print_SmallMolecule_NoFormula() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setFormula(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"formula\""));
  }

  @Test
  public void print_SmallMolecule_NoMonoisotopicMass() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setMonoisotopicMass(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"monoisotopicMass\""));
  }

  @Test
  public void print_SmallMolecule_NoAverageMass() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setAverageMass(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"averageMass\""));
  }

  @Test
  public void print_SmallMolecule_NoToxicity() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setToxicity(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"toxicity\""));
  }

  @Test
  public void print_SmallMolecule_NotLightSensitive() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setLightSensitive(false);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"lightSensitive\""));
  }

  @Test
  public void print_SmallMolecule_NoStorageTemperature() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setStorageTemperature(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"storageTemperature\""));
  }

  @Test
  public void print_SmallMolecule_NotHighResolution() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setHighResolution(false);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertTrue(content.contains("class=\"highResolution\""));
    assertTrue(content.contains(resources.message("submission.highResolution.false")));
  }

  @Test
  public void print_SmallMolecule_NoSolvent() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setSolvents(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"solvent\""));
  }

  @Test
  public void print_SmallMolecule_OtherSolvent() throws Exception {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    Solvent ssolvent = Solvent.OTHER;
    submission.getSolvents().add(ssolvent);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"solvent\""));
    assertFalse(content.contains(Solvent.OTHER.getLabel(locale)));
    for (Solvent solvent : Solvent.values()) {
      assertEquals(solvent.name(),
          submission.getSolvents().stream().filter(ss -> ss == solvent).findAny().isPresent(),
          content.contains(
              solvent == Solvent.OTHER ? submission.getOtherSolvent() : solvent.getLabel(locale)));
    }
  }

  @Test
  public void print_IntactProtein() throws Exception {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    MessageResource resources =
        new MessageResource(SubmissionService.class.getName() + "_Print", locale);
    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"platform\""));
    assertTrue(content.contains(resources.message("platform")));
    assertTrue(content.contains("class=\"service\""));
    assertTrue(content.contains(submission.getService().getLabel(locale)));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    assertTrue(content.contains(submission.getUser().getPhoneNumbers().get(0).getValue(locale)));
    assertTrue(content.contains("class=\"laboratory-name\""));
    assertTrue(content.contains(submission.getLaboratory().getName()));
    assertTrue(content.contains("class=\"laboratory-director\""));
    assertTrue(content.contains(submission.getLaboratory().getDirector()));
    assertTrue(content.contains("class=\"user-email\""));
    assertTrue(content.contains(submission.getUser().getEmail()));
    assertTrue(content.contains("class=\"sample-count\""));
    assertTrue(content.contains(String.valueOf(submission.getSamples().size())));
    assertTrue(content.contains("class=\"sample-name\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(sample.getName()));
    }
    assertTrue(content.contains("class=\"experiment\""));
    assertTrue(content.contains(submission.getExperiment()));
    assertTrue(content.contains("class=\"goal\""));
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(submission.getSamples().get(0).getType().getLabel(locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertTrue(content.contains(submission.getProtein()));
    assertFalse(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertTrue(content.contains("class=\"sample-quantity\""));
    assertTrue(content.contains(submission.getSamples().get(0).getQuantity()));
    assertTrue(content.contains("class=\"sample-volume\""));
    assertTrue(content.contains(submission.getSamples().get(0).getVolume()));
    assertFalse(content.contains("class=\"separation\""));
    assertFalse(content.contains("class=\"thickness\""));
    assertFalse(content.contains("class=\"coloration\""));
    assertFalse(content.contains("class=\"otherColoration\""));
    assertFalse(content.contains("class=\"developmentTime\""));
    assertFalse(content.contains("class=\"decoloration\""));
    assertFalse(content.contains("class=\"weightMarkerQuantity\""));
    assertFalse(content.contains("class=\"proteinQuantity\""));
    assertFalse(content.contains("class=\"solutionSolvent\""));
    assertFalse(content.contains("class=\"formula\""));
    assertFalse(content.contains("class=\"monoisotopicMass\""));
    assertFalse(content.contains("class=\"averageMass\""));
    assertFalse(content.contains("class=\"toxicity\""));
    assertFalse(content.contains("class=\"lightSensitive\""));
    assertFalse(content.contains("class=\"storageTemperature\""));
    assertFalse(content.contains("class=\"proteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"usedProteolyticDigestionMethod\""));
    assertFalse(content.contains("class=\"otherProteolyticDigestionMethod\""));
    assertTrue(content.contains("class=\"injectionType\""));
    assertTrue(content.contains(submission.getInjectionType().getLabel(locale)));
    assertTrue(content.contains("class=\"source\""));
    assertTrue(content.contains(submission.getSource().getLabel(locale)));
    assertFalse(content.contains("class=\"proteinContent\""));
    assertTrue(content.contains("class=\"massDetectionInstrument\""));
    assertTrue(content.contains(submission.getMassDetectionInstrument().getLabel(locale)));
    assertFalse(content.contains("class=\"proteinIdentification\""));
    assertFalse(content.contains("class=\"proteinIdentificationLink\""));
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertFalse(content.contains("class=\"highResolution\""));
    assertFalse(content.contains("class=\"solvent\""));
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertTrue(content.contains("class=\"samples-details section\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(String.valueOf(sample.getNumberProtein())));
      assertTrue(content.contains(String.valueOf(sample.getMolecularWeight())));
    }
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_IntactProtein_NoInjectionType() throws Exception {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    submission.setInjectionType(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"injectionType\""));
  }

  @Test
  public void print_IntactProtein_NoSource() throws Exception {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    submission.setSource(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"source\""));
  }

  @Test
  public void print_Plate() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Plate plate = new Plate(1L, "my plate");
    plate.initWells();
    IntStream.range(0, submission.getSamples().size()).forEach(i -> {
      submission.getSamples().get(i).setOriginalContainer(plate.getWells().get(i));
      plate.getWells().get(i).setSample(submission.getSamples().get(i));
    });
    plate.getWells().get(0).setBanned(true);
    plate.getWells().get(36).setBanned(true);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("??"));
    assertTrue(content.contains("class=\"submission-plate\""));
    assertTrue(content.contains(plate.getName()));
    assertTrue(content.contains("class=\"plate-information section"));
    assertTrue(content.contains("class=\"plate-name\""));
    assertTrue(content.contains(plate.getName()));
    assertTrue(content.contains("class=\"well active\""));
    assertTrue(content.contains("class=\"well banned\""));
    assertTrue(content.contains("class=\"well-sample-name\""));
    for (SubmissionSample sample : submission.getSamples()) {
      assertTrue(content.contains(sample.getName()));
    }
  }

  @Test
  public void print_NullSubmission() throws Exception {
    assertEquals("", service.print(null, Locale.getDefault()));
  }

  @Test
  public void print_NoService() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setService(null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertEquals("", content);
  }

  @Test
  public void print_NoSample() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().clear();
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertEquals("", content);
  }

  @Test
  public void print_FirstSampleNull() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().set(0, null);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertEquals("", content);
  }

  @Test
  public void print_NoSampleType() throws Exception {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(null));
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertEquals("", content);
  }

  @Test
  public void print_NullLocale() throws Exception {
    Submission submission = repository.findById(1L).orElse(null);

    assertEquals("", service.print(submission, null));
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
    submission.setExperiment("experiment");
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

    service.insert(submission);

    repository.flush();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = repository.findById(submission.getId()).orElse(null);
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
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
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(
        LocalDateTime.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
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
    assertEquals(0, tube.getVersion());
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
    submission.setExperiment("experiment");
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

    service.insert(submission);

    repository.flush();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = repository.findById(submission.getId()).orElse(null);
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
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
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(
        LocalDateTime.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    SubmissionSample submissionSample = submission.getSamples().get(0);
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
    assertEquals(0, tube.getVersion());
    assertEquals("unit_test_eluate_02", submission.getSamples().get(1).getName());
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
    submission.setExperiment("experiment");
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

    service.insert(submission);

    repository.flush();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = repository.findById(submission.getId()).orElse(null);
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
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
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(
        LocalDateTime.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    SubmissionSample submissionSample = submission.getSamples().get(0);
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
    assertNotNull(well.getPlate().getSubmission().getId());
    assertEquals(96, well.getPlate().getWells().size());
    for (Well plateWell : well.getPlate().getWells()) {
      if (plateWell != well.getPlate().well(0, 0) && plateWell != well.getPlate().well(1, 0)) {
        assertNull(plateWell.getSample());
      }
      assertEquals(0, plateWell.getVersion());
    }
    assertEquals(submissionSample, well.getSample());
    assertEquals(sample2, well.getPlate().well(1, 0).getSample());
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    assertEquals(false, well.isBanned());
    assertEquals(0, well.getVersion());
    assertEquals("unit_test_eluate_02", submission.getSamples().get(1).getName());
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
    submission.setExperiment("experiment");
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
    submission.setSolvents(Arrays.asList(Solvent.ACETONITRILE));
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

    service.insert(submission);

    repository.flush();
    verify(submissionActivityService).insert(submissionCaptor.capture());
    verify(activityService).insert(activity);
    assertNotNull(submission.getId());
    submission = repository.findById(submission.getId()).orElse(null);
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(
        LocalDateTime.now().plus(2, ChronoUnit.MINUTES).isAfter(submission.getSubmissionDate()));
    assertTrue(
        LocalDateTime.now().minus(2, ChronoUnit.MINUTES).isBefore(submission.getSubmissionDate()));
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("experiment", submission.getExperiment());
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
    assertTrue(submission.getSolvents().contains(Solvent.ACETONITRILE));
    assertEquals("chrisanol", submission.getOtherSolvent());
    assertEquals("none", submission.getToxicity());
    assertEquals(true, submission.isLightSensitive());
    assertEquals(StorageTemperature.LOW, submission.getStorageTemperature());
    assertEquals("comment", submission.getComment());
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
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
    assertEquals(0, tube.getVersion());
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
    submission.setExperiment("experiment");
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

    service.insert(submission);

    repository.flush();
    verify(submissionActivityService).insert(any(Submission.class));
    verify(activityService).insert(activity);
    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService, atLeastOnce()).send(email);
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, never()).addTo("robert.stlouis@ircm.qc.ca");
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

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void insert_AccessDenied() {
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
    submission.setExperiment("experiment");
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

    service.insert(submission);
  }

  @Test
  public void update() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    LocalDateTime newDate = LocalDateTime.now();
    submission.setSubmissionDate(newDate);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
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
    assertEquals(newDate, submission.getSubmissionDate());
    assertEquals(1, submission.getVersion());
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
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sample -> {
      detach(sample);
      detach(sample.getOriginalContainer());
    });
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.getSamples().get(0).setName("unit_test_01");
    submission.getSamples().get(0).setVolume("20.0 μl");
    submission.getSamples().get(0).setQuantity("2.0 μg");
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
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
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.setSamples(samples);

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
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
    assertEquals(0, tube.getVersion());
    assertNull(sampleRepository.findById(447L).orElse(null));
    assertNull(tubeRepository.findById(9L).orElse(null));

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
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.setSamples(samples);

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
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
    assertEquals(0, well.getVersion());
    assertNull(sampleRepository.findById(447L).orElse(null));
    assertNull(tubeRepository.findById(9L).orElse(null));

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_UpdateUser() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    User user = userRepository.findById(4L).orElse(null);
    submission.setUser(user);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_Received() throws Exception {
    Submission submission = repository.findById(149L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });

    service.update(submission, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_AfterReceived() throws Exception {
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });

    service.update(submission, null);
  }

  @Test
  public void update_Email() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, null);

    repository.flush();
    // Validate email that is sent to proteomic users.
    verify(emailService, atLeastOnce()).htmlEmail();
    verify(emailService, atLeastOnce()).send(email);
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, never()).addTo("benoit.coulombe@ircm.qc.ca");
    verify(email).setSubject("Submission was updated");
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertEquals(true, textContent.contains("CAP_20111116_01"));
    assertEquals(true, htmlContent.contains("CAP_20111116_01"));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void update_Admin() throws Exception {
    Submission submission = repository.findById(1L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setExperiment("experiment");
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
    User user = userRepository.findById(4L).orElse(null);
    submission.setUser(user);
    LocalDateTime newDate = LocalDateTime.now();
    submission.setSubmissionDate(newDate);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, "unit_test");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = repository.findById(1L).orElse(null);
    verify(activityService).insert(activity);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals(user.getLaboratory().getId(), submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
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
    assertEquals(newDate, submission.getSubmissionDate());
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
    assertEquals((Long) 1L, submissionLogged.getId());
    assertEquals(user.getId(), submissionLogged.getUser().getId());
    assertEquals(user.getLaboratory().getId(), submissionLogged.getLaboratory().getId());
    assertEquals(newDate, submissionLogged.getSubmissionDate());
  }

  @Test
  public void update_NewSample_Admin() throws Exception {
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
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    submission.getSamples().forEach(sa -> {
      detach(sa);
      detach(sa.getOriginalContainer());
    });
    submission.getSamples().add(sample);
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.update(submission, "unit_test");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
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
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void hide() throws Exception {
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.hide(submission);

    repository.flush();
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
    verify(submissionActivityService).update(submission, null);
    assertTrue(submission.isHidden());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  public void hide_AccessDenied_Anonymous() throws Exception {
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.hide(submission);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  public void hide_AccessDenied() throws Exception {
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity);

    service.hide(submission);
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  @SuppressWarnings("unchecked")
  public void show() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity,
        Optional.empty());

    service.show(submission);

    repository.flush();
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElse(null);
    verify(submissionActivityService).update(submission, null);
    assertFalse(submission.isHidden());
  }

  @Test(expected = AccessDeniedException.class)
  @WithAnonymousUser
  @SuppressWarnings("unchecked")
  public void show_AccessDenied_Anonymous() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity,
        Optional.empty());

    service.show(submission);
  }

  @Test(expected = AccessDeniedException.class)
  @WithMockUser(authorities = { UserRole.USER, UserRole.MANAGER })
  @SuppressWarnings("unchecked")
  public void show_AccessDenied() throws Exception {
    Submission submission = repository.findById(36L).orElse(null);
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(optionalActivity,
        Optional.empty());

    service.show(submission);
  }
}
