package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.ID;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.MailService;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.Permission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range.Bound;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SubmissionService}.
 */
@ServiceTestAnnotations
@WithMockUser
public class SubmissionServiceTest extends AbstractServiceTestCase {

  private static final String READ = "read";
  private static final String WRITE = "write";
  private static final String INJECTION_TYPE_PREFIX = messagePrefix(InjectionType.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX = messagePrefix(
      MassDetectionInstrument.class);
  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX = messagePrefix(
      MassDetectionInstrumentSource.class);
  private static final String PROTEIN_IDENTIFICATION_PREFIX = messagePrefix(
      ProteinIdentification.class);
  private static final String PROTEOLYTIC_DIGESTION_PREFIX = messagePrefix(
      ProteolyticDigestion.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String GEL_COLORATION_PREFIX = messagePrefix(GelColoration.class);
  private static final String GEL_SEPARATION_PREFIX = messagePrefix(GelSeparation.class);
  private static final String GEL_THICKNESS_PREFIX = messagePrefix(GelThickness.class);
  private static final String PROTEIN_CONTENT_PREFIX = messagePrefix(ProteinContent.class);
  private static final String QUANTIFICATION_PREFIX = messagePrefix(Quantification.class);
  private static final String STORAGE_TEMPERATURE_PREFIX = messagePrefix(StorageTemperature.class);
  private static final String SOLVENT_PREFIX = messagePrefix(Solvent.class);
  private static final String PHONE_NUMBER_PREFIX = messagePrefix(PhoneNumber.class);
  private final Random random = new Random();
  @Autowired
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private SubmissionSampleRepository sampleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private PlateRepository plateRepository;
  @Autowired
  private MessageSource messageSource;
  @MockitoBean
  private SubmissionActivityService submissionActivityService;
  @MockitoBean
  private ActivityService activityService;
  @MockitoBean
  private MailService mailService;
  @MockitoBean
  private AuthenticatedUser authenticatedUser;
  @MockitoBean
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

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() throws Throwable {
    user = userRepository.findById(4L).orElseThrow();
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(mailService.htmlEmail()).thenReturn(email);
    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
  }

  private Optional<SubmissionFile> findFile(List<SubmissionFile> files, String filename) {
    return files.stream().filter(file -> file.getFilename().equals(filename)).findFirst();
  }

  private byte[] getResourceContent(String resource) throws IOException, URISyntaxException {
    Path path = Paths.get(Objects.requireNonNull(getClass().getResource(resource)).toURI());
    return Files.readAllBytes(path);
  }

  @Test
  public void get() throws Throwable {
    Submission submission = service.get(1L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 1L, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("Human", submission.getTaxonomy());
    assertEquals("G100429", submission.getExperiment());
    assertNull(submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertNull(submission.getSource());
    assertNull(submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertNull(submission.getUsedDigestion());
    assertNull(submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertNull(submission.getIdentificationLink());
    assertFalse(submission.isHighResolution());
    assertEquals(ProteinContent.XLARGE, submission.getProteinContent());
    assertNull(submission.getProtein());
    assertNull(submission.getPostTranslationModification());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(GelColoration.SILVER, submission.getColoration());
    assertNull(submission.getOtherColoration());
    assertNull(submission.getDevelopmentTime());
    assertFalse(submission.isDecoloration());
    assertNull(submission.getWeightMarkerQuantity());
    assertNull(submission.getProteinQuantity());
    assertNull(submission.getQuantification());
    assertNull(submission.getQuantificationComment());
    assertNull(submission.getContaminants());
    assertNull(submission.getStandards());
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
    assertEquals(SampleType.GEL, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.ANALYSED, sample.getStatus());
    assertNull(sample.getNumberProtein());
    assertNull(sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals((Long) 1L, file.getId());
    assertEquals("protocol.txt", file.getFilename());
    assertArrayEquals(Files.readAllBytes(
            Paths.get(Objects.requireNonNull(getClass().getResource("/submissionfile1.txt")).toURI())),
        file.getContent());
    file = submission.getFiles().get(1);
    assertEquals((Long) 2L, file.getId());
    assertEquals("frag.jpg", file.getFilename());
    assertArrayEquals(Files.readAllBytes(
            Paths.get(Objects.requireNonNull(getClass().getResource("/gelimages1.png")).toURI())),
        file.getContent());
  }

  @Test
  public void get_33() throws Throwable {
    Submission submission = service.get(33L).orElseThrow();

    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(READ));
    assertEquals((Long) 33L, submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertNull(submission.getTaxonomy());
    assertEquals("CAP_20111013_05", submission.getExperiment());
    assertNull(submission.getGoal());
    assertNull(submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertNull(submission.getInjectionType());
    assertNull(submission.getDigestion());
    assertNull(submission.getUsedDigestion());
    assertNull(submission.getOtherDigestion());
    assertNull(submission.getIdentification());
    assertNull(submission.getIdentificationLink());
    assertFalse(submission.isHighResolution());
    assertNull(submission.getProteinContent());
    assertNull(submission.getProtein());
    assertNull(submission.getPostTranslationModification());
    assertNull(submission.getSeparation());
    assertNull(submission.getThickness());
    assertNull(submission.getColoration());
    assertNull(submission.getOtherColoration());
    assertNull(submission.getDevelopmentTime());
    assertFalse(submission.isDecoloration());
    assertNull(submission.getWeightMarkerQuantity());
    assertNull(submission.getProteinQuantity());
    assertEquals("C100H100O100", submission.getFormula());
    assertNotNull(submission.getMonoisotopicMass());
    assertEquals(654.654, submission.getMonoisotopicMass(), 0.0001);
    assertNotNull(submission.getAverageMass());
    assertEquals(654.654, submission.getAverageMass(), 0.0001);
    assertEquals("MeOH/TFA 0.1%", submission.getSolutionSolvent());
    assertNotNull(submission.getSolvents());
    assertEquals(1, submission.getSolvents().size());
    assertTrue(submission.getSolvents().contains(Solvent.METHANOL));
    assertNull(submission.getOtherSolvent());
    assertNull(submission.getToxicity());
    assertFalse(submission.isLightSensitive());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertNull(submission.getQuantification());
    assertNull(submission.getQuantificationComment());
    assertNull(submission.getContaminants());
    assertNull(submission.getStandards());
    assertNull(submission.getComment());
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
    assertEquals(SampleType.SOLUTION, sample.getType());
    assertEquals(Sample.Category.SUBMISSION, sample.getCategory());
    assertEquals(SampleStatus.WAITING, sample.getStatus());
    assertNull(sample.getNumberProtein());
    assertNull(sample.getMolecularWeight());
    assertEquals(submission, sample.getSubmission());
    assertEquals(1, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals((Long) 3L, file.getId());
    assertEquals("glucose.png", file.getFilename());
    assertArrayEquals(getResourceContent("/sample/glucose.png"), file.getContent());
  }

  @Test
  public void get_Id0() {
    assertFalse(service.get(0).isPresent());
  }

  @Test
  public void all() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    List<Submission> submissions = service.all(new SubmissionFilter(), Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
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
  public void all_User() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    List<Submission> submissions = service.all(new SubmissionFilter(), Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
  }

  @Test
  public void all_Manager() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);

    List<Submission> submissions = service.all(new SubmissionFilter(), Pageable.unpaged()).toList();

    verify(authenticatedUser).hasPermission(user.getLaboratory(), Permission.WRITE);
    assertEquals(18, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertFalse(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertFalse(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_Admin() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);

    List<Submission> submissions = service.all(new SubmissionFilter(), Pageable.unpaged()).toList();

    assertEquals(20, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_Filter() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    List<Submission> submissions = service.all(new SubmissionFilter(), Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
  }

  @Test
  public void all_FilterExperiment() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.experimentContains = "exp";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 32).isPresent());
  }

  @Test
  public void all_FilterUser() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.userContains = "it";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(4, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
  }

  @Test
  public void all_FilterUserEmail() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.userContains = "er.anderson";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(16, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterUserName() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.userContains = "er anderson";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(16, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterDirector() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.directorContains = "Coulombe";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(19, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterService() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.service = Service.LC_MS_MS;

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(19, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 36).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterSampleName() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.anySampleNameContains = "POLR2B";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(4, submissions.size());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterSampleStatus() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.anySampleStatus = SampleStatus.WAITING;

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(2, submissions.size());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterInstrument() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
  }

  @Test
  public void all_FilterDateRange() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.closed(LocalDate.of(2014, 10, 16), LocalDate.of(2018, 6, 1));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(6, submissions.size());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterDateRange_Closed() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.closed(LocalDate.of(2014, 10, 17), LocalDate.of(2018, 5, 3));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(6, submissions.size());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterDateRange_Open() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.open(LocalDate.of(2014, 10, 17), LocalDate.of(2018, 5, 3));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(4, submissions.size());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
  }

  @Test
  public void all_FilterDateRange_LeftOnly_Inclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.rightUnbounded(Bound.inclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(6, submissions.size());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterDateRange_LeftOnly_Exclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.rightUnbounded(Bound.exclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(5, submissions.size());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterDateRange_RightOnly_Inclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.leftUnbounded(Bound.inclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(10, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
  }

  @Test
  public void all_FilterDateRange_RightOnly_Exclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dateRange = Range.leftUnbounded(Bound.exclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(9, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.closed(LocalDate.of(2014, 10, 1),
        LocalDate.of(2014, 11, 1));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_Closed() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.closed(LocalDate.of(2014, 10, 17),
        LocalDate.of(2014, 10, 24));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_Open() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.open(LocalDate.of(2014, 10, 17),
        LocalDate.of(2014, 10, 24));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 155).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_LeftOnly_Inclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.rightUnbounded(
        Bound.inclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_LeftOnly_Exclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.rightUnbounded(
        Bound.exclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(2, submissions.size());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_RightOnly_Inclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.leftUnbounded(
        Bound.inclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();
    System.out.println(submissions);

    assertEquals(2, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 153).isPresent());
  }

  @Test
  public void all_FilterDataAvailableDateRange_RightOnly_Exclusive() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.dataAvailableDateRange = Range.leftUnbounded(
        Bound.exclusive(LocalDate.of(2014, 10, 17)));

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 35).isPresent());
  }

  @Test
  public void all_FilterHidden_False() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.hidden = false;

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(19, submissions.size());
    assertTrue(find(submissions, 1).isPresent());
    assertTrue(find(submissions, 32).isPresent());
    assertTrue(find(submissions, 33).isPresent());
    assertTrue(find(submissions, 34).isPresent());
    assertTrue(find(submissions, 35).isPresent());
    assertTrue(find(submissions, 147).isPresent());
    assertTrue(find(submissions, 148).isPresent());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
    assertTrue(find(submissions, 153).isPresent());
    assertTrue(find(submissions, 154).isPresent());
    assertTrue(find(submissions, 155).isPresent());
    assertTrue(find(submissions, 156).isPresent());
    assertTrue(find(submissions, 161).isPresent());
    assertTrue(find(submissions, 162).isPresent());
    assertTrue(find(submissions, 163).isPresent());
    assertTrue(find(submissions, 164).isPresent());
  }

  @Test
  public void all_FilterHidden_True() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.hidden = true;

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 36).isPresent());
  }

  @Test
  public void all_FilterExperimentAndSampleName() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.experimentContains = "exper";
    filter.anySampleNameContains = "17";

    List<Submission> submissions = service.all(filter, Pageable.unpaged()).toList();

    assertEquals(1, submissions.size());
    assertTrue(find(submissions, 34).isPresent());
  }

  @Test
  public void all_FilterPage() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();

    List<Submission> submissions = service.all(filter, PageRequest.of(1, 3, Sort.by(ASC, ID)))
        .toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 149).isPresent());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
  }

  @Test
  public void all_FilterPageJoin() {
    User user = new User(10L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.anySampleNameContains = "POLR2A";

    List<Submission> submissions = service.all(filter, PageRequest.of(1, 3, Sort.by(ASC, ID)))
        .toList();

    assertEquals(3, submissions.size());
    assertTrue(find(submissions, 150).isPresent());
    assertTrue(find(submissions, 151).isPresent());
    assertTrue(find(submissions, 152).isPresent());
  }

  @Test
  public void all_SortExperiment() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();

    List<Submission> submissions = service.all(filter, Pageable.unpaged(Sort.by(ASC, EXPERIMENT)))
        .toList();

    assertEquals(3, submissions.size());
    assertEquals((Long) 33L, submissions.get(0).getId());
    assertEquals((Long) 32L, submissions.get(1).getId());
    assertEquals((Long) 1L, submissions.get(2).getId());
  }

  @Test
  public void all_FilterExperimentSortDate() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();
    filter.experimentContains = "POLR2B";

    List<Submission> submissions = service.all(filter,
        Pageable.unpaged(Sort.by(ASC, SUBMISSION_DATE))).toList();

    assertEquals(3, submissions.size());
    assertEquals(162, submissions.get(0).getId());
    assertEquals(161, submissions.get(1).getId());
    assertEquals(163, submissions.get(2).getId());
  }

  @Test
  public void all_SortExperimentAndDate() {
    User user = new User(1L);
    user.setLaboratory(new Laboratory(1L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    SubmissionFilter filter = new SubmissionFilter();

    List<Submission> submissions = service.all(filter,
        Pageable.unpaged(Sort.by(Order.asc(EXPERIMENT), Order.asc(SUBMISSION_DATE)))).toList();

    assertEquals(20, submissions.size());
    assertEquals(33, submissions.get(0).getId());
    assertEquals(32, submissions.get(1).getId());
    assertEquals(34, submissions.get(2).getId());
    assertEquals(35, submissions.get(3).getId());
    assertEquals(36, submissions.get(4).getId());
    assertEquals(1, submissions.get(5).getId());
    assertEquals(147, submissions.get(6).getId());
    assertEquals(148, submissions.get(7).getId());
    assertEquals(149, submissions.get(8).getId());
    assertEquals(150, submissions.get(9).getId());
    assertEquals(151, submissions.get(10).getId());
    assertEquals(152, submissions.get(11).getId());
    assertEquals(153, submissions.get(12).getId());
    assertEquals(154, submissions.get(13).getId());
    assertEquals(155, submissions.get(14).getId());
    assertEquals(156, submissions.get(15).getId());
    assertEquals(162, submissions.get(16).getId());
    assertEquals(161, submissions.get(17).getId());
    assertEquals(163, submissions.get(18).getId());
    assertEquals(164, submissions.get(19).getId());
  }

  @Test
  @WithAnonymousUser
  public void all_AccessDenied() {
    assertThrows(AccessDeniedException.class,
        () -> service.all(new SubmissionFilter(), Pageable.unpaged()));
  }

  @Test
  public void count_Filter() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));

    long count = service.count(new SubmissionFilter());

    assertEquals(3, count);
  }

  @Test
  public void count_FilterExperiment() {
    User user = new User(3L);
    user.setLaboratory(new Laboratory(2L));
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
    SubmissionFilter filter = new SubmissionFilter();
    filter.experimentContains = "exp";

    long count = service.count(filter);

    assertEquals(1, count);
  }

  @Test
  @WithAnonymousUser
  public void count_AccessDenied() {
    SubmissionFilter filter = mock(SubmissionFilter.class);
    when(filter.predicate()).thenReturn(submission.isNotNull());

    assertThrows(AccessDeniedException.class, () -> service.count(filter));
  }

  private Submission submissionForPrint(Service service) {
    Submission submission = new Submission();
    submission.setService(service);
    submission.setSubmissionDate(LocalDateTime.now().minusDays(2));
    submission.setUser(userRepository.findById(3L).orElseThrow());
    submission.setLaboratory(laboratoryRepository.findById(2L).orElseThrow());
    SubmissionSample sample1 = new SubmissionSample("first sample");
    sample1.setQuantity("15 ug");
    sample1.setVolume("10 ul");
    sample1.setType(SampleType.SOLUTION);
    sample1.setMolecularWeight(150.0);
    sample1.setNumberProtein(5);
    sample1.setMolecularWeight(8.9);
    SubmissionSample sample2 = new SubmissionSample("second sample");
    sample2.setQuantity("15 ug");
    sample2.setVolume("10 ul");
    sample2.setType(SampleType.SOLUTION);
    sample2.setMolecularWeight(150.0);
    sample2.setNumberProtein(6);
    sample2.setMolecularWeight(9.3);
    if (service == Service.SMALL_MOLECULE) {
      submission.setSamples(new ArrayList<>(List.of(sample1)));
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
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("my trypsin");
    submission.setOtherDigestion("other trypsin");
    submission.setInjectionType(InjectionType.DIRECT_INFUSION);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setProteinContent(ProteinContent.LARGE);
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setIdentification(ProteinIdentification.REFSEQ);
    submission.setIdentificationLink("https://www.ncbi.nlm.nih.gov/home/download/");
    submission.setQuantification(Quantification.LABEL_FREE);
    submission.setQuantificationComment("quantification comment\nsecond line");
    submission.setHighResolution(true);
    submission.setSolvents(new ArrayList<>(Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3)));
    submission.setOtherSolvent("other solvent");
    submission.setComment("my comment\nsecond line");
    SubmissionFile file1 = new SubmissionFile();
    file1.setFilename("file_1.txt");
    byte[] fileContent = new byte[1024];
    random.nextBytes(fileContent);
    file1.setContent(fileContent);
    SubmissionFile file2 = new SubmissionFile();
    file2.setFilename("file_2.xlsx");
    random.nextBytes(fileContent);
    file2.setContent(fileContent);
    submission.setFiles(new ArrayList<>(Arrays.asList(file1, file2)));
    return submission;
  }

  private String formatMultiline(@Nullable String comment) {
    return comment != null ? comment.replaceAll("\\r?\\n", "<br>") : "";
  }

  @Test
  public void print_LcmsmsSolution() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Sample firstSample = submission.getSamples().get(0);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("??"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    PhoneNumber phoneNumber = submission.getUser().getPhoneNumbers().get(0);
    assertTrue(content.contains(messageSource.getMessage(PHONE_NUMBER_PREFIX + "value",
        new Object[]{phoneNumber.getNumber(),
            Optional.ofNullable(phoneNumber.getExtension()).map(ex -> ex.isEmpty() ? 0 : 1).orElse(
                0), phoneNumber.getExtension()}, locale)));
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
    assertNotNull(submission.getGoal());
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertNotNull(submission.getTaxonomy());
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + firstSample.getType().name(), null, locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertNotNull(submission.getProtein());
    assertTrue(content.contains(submission.getProtein()));
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(
        content.contains(String.valueOf(submission.getSamples().get(0).getMolecularWeight())));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertNotNull(submission.getPostTranslationModification());
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertTrue(content.contains("class=\"sample-quantity\""));
    assertNotNull(firstSample.getQuantity());
    assertTrue(content.contains(firstSample.getQuantity()));
    assertTrue(content.contains("class=\"sample-volume\""));
    assertNotNull(firstSample.getVolume());
    assertTrue(content.contains(firstSample.getVolume()));
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
    assertTrue(content.contains("class=\"digestion\""));
    assertNotNull(submission.getDigestion());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + submission.getDigestion().name(),
            null, locale)));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertFalse(content.contains("class=\"otherDigestion\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertTrue(content.contains("class=\"proteinContent\""));
    assertNotNull(submission.getProteinContent());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEIN_CONTENT_PREFIX + submission.getProteinContent().name(),
            null, locale)));
    assertTrue(content.contains("class=\"instrument\""));
    assertNotNull(submission.getInstrument());
    assertTrue(content.contains(messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_PREFIX + submission.getInstrument().name(), null, locale)));
    assertTrue(content.contains("class=\"identification\""));
    assertNotNull(submission.getIdentification());
    assertTrue(content.contains(messageSource.getMessage(
        PROTEIN_IDENTIFICATION_PREFIX + submission.getIdentification().name(), null, locale)));
    assertFalse(content.contains("class=\"identificationLink\""));
    assertTrue(content.contains("class=\"quantification\""));
    assertNotNull(submission.getQuantification());
    assertTrue(content.contains(
        messageSource.getMessage(QUANTIFICATION_PREFIX + submission.getQuantification().name(),
            null, locale)));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertFalse(content.contains("class=\"highResolution\""));
    assertFalse(content.contains("class=\"solvent\""));
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertTrue(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertTrue(content.contains("href=\"files-" + i + "\""));
    }
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_LcmsmsSolution_NoPhone() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    repository.save(submission);
    Locale locale = Locale.getDefault();
    submission.getUser().getPhoneNumbers().clear();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"user-phone\""));
  }

  @Test
  public void print_LcmsmsSolution_NoGoal() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    Locale locale = Locale.getDefault();
    submission.setGoal(null);
    repository.save(submission);

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"goal\""));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinName() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProtein(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"protein\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoMolecularWeight() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setMolecularWeight(null));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoPostTranslationModification() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setPostTranslationModification(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoSampleQuantity() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setQuantity(null));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-quantity\""));
  }

  @Test
  public void print_LcmsmsSolution_NoSampleVolume() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setVolume(null));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsDry() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.DRY));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsBeads() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.BIOID_BEADS));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"sample-volume\""));
  }

  @Test
  public void print_LcmsmsSolution_NoDigestion() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setDigestion(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"digestion\""));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertFalse(content.contains("class=\"otherDigestion\""));
  }

  @Test
  public void print_LcmsmsSolution_Digested() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setDigestion(ProteolyticDigestion.DIGESTED);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"digestion\""));
    assertNotNull(submission.getDigestion());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + submission.getDigestion().name(),
            null, locale)));
    assertTrue(content.contains("class=\"usedDigestion\""));
    assertNotNull(submission.getUsedDigestion());
    assertTrue(content.contains(submission.getUsedDigestion()));
    assertFalse(content.contains("class=\"otherDigestion\""));
  }

  @Test
  public void print_LcmsmsSolution_OtherDigestion() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setDigestion(ProteolyticDigestion.OTHER);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"digestion\""));
    assertNotNull(submission.getDigestion());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + submission.getDigestion().name(),
            null, locale)));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertTrue(content.contains("class=\"otherDigestion\""));
    assertNotNull(submission.getOtherDigestion());
    assertTrue(content.contains(submission.getOtherDigestion()));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinContent() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setProteinContent(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"proteinContent\""));
  }

  @Test
  public void print_LcmsmsSolution_NoMassDetectionInstrument() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setInstrument(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"instrument\""));
    assertTrue(content.contains(
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + "NULL", null, locale)
            .replaceAll("'", "&#39;")));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinIdentification() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setIdentification(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"identification\""));
    assertFalse(content.contains("class=\"identificationLink\""));
  }

  @Test
  public void print_LcmsmsSolution_OtherProteinIdentificationLink() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setIdentification(ProteinIdentification.OTHER);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"identification\""));
    assertTrue(content.contains("class=\"identificationLink\""));
    assertNotNull(submission.getIdentificationLink());
    assertTrue(content.contains(submission.getIdentificationLink()));
  }

  @Test
  public void print_LcmsmsSolution_NoProteinIdentificationLink() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setIdentification(ProteinIdentification.OTHER);
    submission.setIdentificationLink(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"identification\""));
    assertTrue(content.contains("class=\"identificationLink\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_NoQuantification() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantificationComment\""));
  }

  @Test
  public void print_LcmsmsSolution_SilacQuantification() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.SILAC);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.quantificationComment", null,
            locale)));
    assertTrue(content.contains(formatMultiline(submission.getQuantificationComment())));
  }

  @Test
  public void print_LcmsmsSolution_SilacQuantificationNoComment() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.SILAC);
    submission.setQuantificationComment(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.quantificationComment", null,
            locale)));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsSolution_TmtQuantification() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.TMT);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.quantificationComment.TMT", null,
            locale)));
    assertTrue(content.contains(formatMultiline(submission.getQuantificationComment())));
  }

  @Test
  public void print_LcmsmsSolution_TmtQuantificationNoComment() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setQuantification(Quantification.TMT);
    submission.setQuantificationComment(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"quantification\""));
    assertTrue(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.quantificationComment.TMT", null,
            locale)));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsGel() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("??"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    PhoneNumber phoneNumber = submission.getUser().getPhoneNumbers().get(0);
    assertTrue(content.contains(messageSource.getMessage(PHONE_NUMBER_PREFIX + "value",
        new Object[]{phoneNumber.getNumber(),
            Optional.ofNullable(phoneNumber.getExtension()).map(ex -> ex.isEmpty() ? 0 : 1).orElse(
                0), phoneNumber.getExtension()}, locale)));
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
    assertNotNull(submission.getGoal());
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertNotNull(submission.getTaxonomy());
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(messageSource.getMessage(
        SAMPLE_TYPE_PREFIX + submission.getSamples().get(0).getType().name(), null, locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertNotNull(submission.getProtein());
    assertTrue(content.contains(submission.getProtein()));
    assertTrue(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(
        content.contains(String.valueOf(submission.getSamples().get(0).getMolecularWeight())));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertNotNull(submission.getPostTranslationModification());
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertFalse(content.contains("class=\"sample-quantity\""));
    assertFalse(content.contains("class=\"sample-volume\""));
    assertTrue(content.contains("class=\"separation\""));
    assertNotNull(submission.getSeparation());
    assertTrue(content.contains(
        messageSource.getMessage(GEL_SEPARATION_PREFIX + submission.getSeparation().name(), null,
            locale)));
    assertTrue(content.contains("class=\"thickness\""));
    assertNotNull(submission.getThickness());
    assertTrue(content.contains(
        messageSource.getMessage(GEL_THICKNESS_PREFIX + submission.getThickness().name(), null,
            locale)));
    assertTrue(content.contains("class=\"coloration\""));
    assertNotNull(submission.getColoration());
    assertTrue(content.contains(
        messageSource.getMessage(GEL_COLORATION_PREFIX + submission.getColoration().name(), null,
            locale)));
    assertFalse(content.contains("class=\"otherColoration\""));
    assertTrue(content.contains("class=\"developmentTime\""));
    assertNotNull(submission.getDevelopmentTime());
    assertTrue(content.contains(submission.getDevelopmentTime()));
    assertTrue(content.contains("class=\"decoloration\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.decoloration.true", null, locale)));
    assertTrue(content.contains("class=\"weightMarkerQuantity\""));
    assertTrue(content.contains(String.valueOf(submission.getWeightMarkerQuantity())));
    assertTrue(content.contains("class=\"proteinQuantity\""));
    assertNotNull(submission.getProteinQuantity());
    assertTrue(content.contains(submission.getProteinQuantity()));
    assertFalse(content.contains("class=\"solutionSolvent\""));
    assertFalse(content.contains("class=\"formula\""));
    assertFalse(content.contains("class=\"monoisotopicMass\""));
    assertFalse(content.contains("class=\"averageMass\""));
    assertFalse(content.contains("class=\"toxicity\""));
    assertFalse(content.contains("class=\"lightSensitive\""));
    assertFalse(content.contains("class=\"storageTemperature\""));
    assertTrue(content.contains("class=\"digestion\""));
    assertNotNull(submission.getDigestion());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEOLYTIC_DIGESTION_PREFIX + submission.getDigestion().name(),
            null, locale)));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertFalse(content.contains("class=\"otherDigestion\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertTrue(content.contains("class=\"proteinContent\""));
    assertNotNull(submission.getProteinContent());
    assertTrue(content.contains(
        messageSource.getMessage(PROTEIN_CONTENT_PREFIX + submission.getProteinContent().name(),
            null, locale)));
    assertTrue(content.contains("class=\"instrument\""));
    assertNotNull(submission.getInstrument());
    assertTrue(content.contains(messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_PREFIX + submission.getInstrument().name(), null, locale)));
    assertTrue(content.contains("class=\"identification\""));
    assertNotNull(submission.getIdentification());
    assertTrue(content.contains(messageSource.getMessage(
        PROTEIN_IDENTIFICATION_PREFIX + submission.getIdentification().name(), null, locale)));
    assertFalse(content.contains("class=\"identificationLink\""));
    assertTrue(content.contains("class=\"quantification\""));
    assertNotNull(submission.getQuantification());
    assertTrue(content.contains(
        messageSource.getMessage(QUANTIFICATION_PREFIX + submission.getQuantification().name(),
            null, locale)));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertFalse(content.contains("class=\"highResolution\""));
    assertFalse(content.contains("class=\"solvent\""));
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertTrue(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertTrue(content.contains("href=\"files-" + i + "\""));
    }
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_LcmsmsGel_NoSeparation() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setSeparation(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"separation\""));
  }

  @Test
  public void print_LcmsmsGel_NoThickness() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setThickness(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"thickness\""));
  }

  @Test
  public void print_LcmsmsGel_NoColoration() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"coloration\""));
  }

  @Test
  public void print_LcmsmsGel_OtherColoration() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(GelColoration.OTHER);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertTrue(content.contains("class=\"coloration\""));
    assertTrue(content.contains("class=\"otherColoration\""));
    assertNotNull(submission.getOtherColoration());
    assertTrue(content.contains(submission.getOtherColoration()));
  }

  @Test
  public void print_LcmsmsGel_NoOtherColoration() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setColoration(GelColoration.OTHER);
    submission.setOtherColoration(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertTrue(content.contains("class=\"coloration\""));
    assertTrue(content.contains("class=\"otherColoration\""));
    assertFalse(content.contains("null"));
  }

  @Test
  public void print_LcmsmsGel_NoDevelopmentTime() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setDevelopmentTime(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"developmentTime\""));
  }

  @Test
  public void print_LcmsmsGel_NoWeightMarkerQuantity() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setWeightMarkerQuantity(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"weightMarkerQuantity\""));
  }

  @Test
  public void print_LcmsmsGel_NoProteinQuantity() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().forEach(sample -> sample.setType(SampleType.GEL));
    submission.setProteinQuantity(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"proteinQuantity\""));
  }

  @Test
  public void print_Lcmsms_NoFiles() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.setFiles(new ArrayList<>());
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"files section\""));
  }

  @Test
  public void print_SmallMolecule() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("??"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    PhoneNumber phoneNumber = submission.getUser().getPhoneNumbers().get(0);
    assertTrue(content.contains(messageSource.getMessage(PHONE_NUMBER_PREFIX + "value",
        new Object[]{phoneNumber.getNumber(),
            Optional.ofNullable(phoneNumber.getExtension()).map(ex -> ex.isEmpty() ? 0 : 1).orElse(
                0), phoneNumber.getExtension()}, locale)));
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
    assertTrue(content.contains(messageSource.getMessage(
        SAMPLE_TYPE_PREFIX + submission.getSamples().get(0).getType().name(), null, locale)));
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
    assertNotNull(submission.getSolutionSolvent());
    assertTrue(content.contains(submission.getSolutionSolvent()));
    assertTrue(content.contains("class=\"formula\""));
    assertNotNull(submission.getFormula());
    assertTrue(content.contains(submission.getFormula()));
    assertTrue(content.contains("class=\"monoisotopicMass\""));
    assertTrue(content.contains(String.valueOf(submission.getMonoisotopicMass())));
    assertTrue(content.contains("class=\"averageMass\""));
    assertTrue(content.contains(String.valueOf(submission.getAverageMass())));
    assertTrue(content.contains("class=\"toxicity\""));
    assertNotNull(submission.getToxicity());
    assertTrue(content.contains(submission.getToxicity()));
    assertTrue(content.contains("class=\"lightSensitive\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.lightSensitive.true", null, locale)));
    assertTrue(content.contains("class=\"storageTemperature\""));
    assertNotNull(submission.getStorageTemperature());
    assertTrue(content.contains(messageSource.getMessage(
        STORAGE_TEMPERATURE_PREFIX + submission.getStorageTemperature().name(), null, locale)));
    assertFalse(content.contains("class=\"digestion\""));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertFalse(content.contains("class=\"otherDigestion\""));
    assertFalse(content.contains("class=\"injectionType\""));
    assertFalse(content.contains("class=\"source\""));
    assertFalse(content.contains("class=\"proteinContent\""));
    assertFalse(content.contains("class=\"instrument\""));
    assertFalse(content.contains("class=\"identification\""));
    assertFalse(content.contains("class=\"identificationLink\""));
    assertFalse(content.contains("class=\"quantification\""));
    assertFalse(content.contains("class=\"quantificationComment\""));
    assertTrue(content.contains("class=\"highResolution\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.highResolution.true", null, locale)));
    assertTrue(content.contains("class=\"solvent\""));
    for (Solvent solvent : Solvent.values()) {
      assertEquals(submission.getSolvents().stream().anyMatch(ss -> ss == solvent),
          content.contains(messageSource.getMessage(SOLVENT_PREFIX + solvent.name(), null, locale)),
          solvent.name());
    }
    assertTrue(content.contains("class=\"comment\""));
    assertTrue(content.contains(formatMultiline(submission.getComment())));
    assertFalse(content.contains("class=\"samples-details section\""));
    assertTrue(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertTrue(content.contains("href=\"files-" + i + "\""));
    }
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_SmallMolecule_NoSolutionSolvent() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setSolutionSolvent(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"solutionSolvent\""));
  }

  @Test
  public void print_SmallMolecule_NoFormula() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setFormula(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"formula\""));
  }

  @Test
  public void print_SmallMolecule_NoMonoisotopicMass() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setMonoisotopicMass(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"monoisotopicMass\""));
  }

  @Test
  public void print_SmallMolecule_NoAverageMass() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setAverageMass(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"averageMass\""));
  }

  @Test
  public void print_SmallMolecule_NoToxicity() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setToxicity(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"toxicity\""));
  }

  @Test
  public void print_SmallMolecule_NotLightSensitive() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setLightSensitive(false);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"lightSensitive\""));
  }

  @Test
  public void print_SmallMolecule_NoStorageTemperature() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setStorageTemperature(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"storageTemperature\""));
  }

  @Test
  public void print_SmallMolecule_NotHighResolution() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setHighResolution(false);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"highResolution\""));
    assertTrue(content.contains(
        messageSource.getMessage("submission.print.submission.highResolution.false", null,
            locale)));
  }

  @Test
  public void print_SmallMolecule_NoSolvent() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setSolvents(new ArrayList<>());
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"solvent\""));
  }

  @Test
  public void print_SmallMolecule_OtherSolvent() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    Solvent ssolvent = Solvent.OTHER;
    submission.getSolvents().add(ssolvent);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertTrue(content.contains("class=\"solvent\""));
    assertFalse(content.contains(
        messageSource.getMessage(SOLVENT_PREFIX + Solvent.OTHER.name(), null, locale)));
    for (Solvent solvent : Solvent.values()) {
      assertEquals(submission.getSolvents().stream().anyMatch(ss -> ss == solvent),
          content.contains(
              solvent == Solvent.OTHER ? Objects.requireNonNull(submission.getOtherSolvent())
                  : messageSource.getMessage(SOLVENT_PREFIX + solvent.name(), null, locale)),
          solvent.name());
    }
  }

  @Test
  public void print_SmallMolecule_NoFiles() {
    Submission submission = submissionForPrint(Service.SMALL_MOLECULE);
    submission.setFiles(new ArrayList<>());
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertFalse(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertFalse(content.contains("href=\"files-" + i + "\""));
    }
  }

  @Test
  public void print_IntactProtein() {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    Sample firstSample = submission.getSamples().get(0);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("??"));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertTrue(content.contains("class=\"submissionDate\""));
    assertTrue(
        content.contains(dateFormatter.format(submission.getSubmissionDate().toLocalDate())));
    assertTrue(content.contains("class=\"user-name\""));
    assertTrue(content.contains(submission.getUser().getName()));
    assertTrue(content.contains("class=\"user-phone\""));
    PhoneNumber phoneNumber = submission.getUser().getPhoneNumbers().get(0);
    assertTrue(content.contains(messageSource.getMessage(PHONE_NUMBER_PREFIX + "value",
        new Object[]{phoneNumber.getNumber(),
            Optional.ofNullable(phoneNumber.getExtension()).map(ex -> ex.isEmpty() ? 0 : 1).orElse(
                0), phoneNumber.getExtension()}, locale)));
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
    assertNotNull(submission.getGoal());
    assertTrue(content.contains(submission.getGoal()));
    assertTrue(content.contains("class=\"taxonomy\""));
    assertNotNull(submission.getTaxonomy());
    assertTrue(content.contains(submission.getTaxonomy()));
    assertTrue(content.contains("class=\"sample-type\""));
    assertTrue(content.contains(
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + firstSample.getType().name(), null, locale)));
    assertTrue(content.contains("class=\"protein\""));
    assertNotNull(submission.getProtein());
    assertTrue(content.contains(submission.getProtein()));
    assertFalse(content.contains("class=\"sample-molecularWeight\""));
    assertTrue(content.contains("class=\"postTranslationModification\""));
    assertNotNull(submission.getPostTranslationModification());
    assertTrue(content.contains(submission.getPostTranslationModification()));
    assertTrue(content.contains("class=\"sample-quantity\""));
    assertNotNull(firstSample.getQuantity());
    assertTrue(content.contains(firstSample.getQuantity()));
    assertTrue(content.contains("class=\"sample-volume\""));
    assertNotNull(firstSample.getVolume());
    assertTrue(content.contains(firstSample.getVolume()));
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
    assertFalse(content.contains("class=\"digestion\""));
    assertFalse(content.contains("class=\"usedDigestion\""));
    assertFalse(content.contains("class=\"otherDigestion\""));
    assertTrue(content.contains("class=\"injectionType\""));
    assertNotNull(submission.getInjectionType());
    assertTrue(content.contains(
        messageSource.getMessage(INJECTION_TYPE_PREFIX + submission.getInjectionType().name(), null,
            locale)));
    assertTrue(content.contains("class=\"source\""));
    assertNotNull(submission.getSource());
    assertTrue(content.contains(messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + submission.getSource().name(), null, locale)));
    assertFalse(content.contains("class=\"proteinContent\""));
    assertTrue(content.contains("class=\"instrument\""));
    assertNotNull(submission.getInstrument());
    assertTrue(content.contains(messageSource.getMessage(
        MASS_DETECTION_INSTRUMENT_PREFIX + submission.getInstrument().name(), null, locale)));
    assertFalse(content.contains("class=\"identification\""));
    assertFalse(content.contains("class=\"identificationLink\""));
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
    assertTrue(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertTrue(content.contains("href=\"files-" + i + "\""));
    }
    assertFalse(content.contains("class=\"plate-information section"));
  }

  @Test
  public void print_IntactProtein_NoInjectionType() {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    submission.setInjectionType(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"injectionType\""));
  }

  @Test
  public void print_IntactProtein_NoSource() {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    submission.setSource(null);
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"source\""));
  }

  @Test
  public void print_IntactProtein_NoFiles() {
    Submission submission = submissionForPrint(Service.INTACT_PROTEIN);
    submission.setFiles(new ArrayList<>());
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);

    assertFalse(content.contains("class=\"files section\""));
    for (int i = 0; i < submission.getFiles().size(); i++) {
      assertFalse(content.contains("href=\"files-" + i + "\""));
    }
  }

  @Test
  public void print_Plate() {
    Submission submission = repository.findById(163L).orElseThrow();
    Plate plate = plateRepository.findBySubmission(submission).orElseThrow();
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
  public void print_NoSample() {
    Submission submission = submissionForPrint(Service.LC_MS_MS);
    submission.getSamples().clear();
    repository.save(submission);
    Locale locale = Locale.getDefault();

    String content = service.print(submission, locale);
    assertEquals("", content);
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
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
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
    assertNotEquals(0, submission.getId());
    submission = repository.findById(submission.getId()).orElseThrow();
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals("trypsine was not used", submission.getUsedDigestion());
    assertEquals("other digestion", submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertEquals("http://localhost/my_site", submission.getIdentificationLink());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(GelColoration.COOMASSIE, submission.getColoration());
    assertEquals("other coloration", submission.getOtherColoration());
    assertEquals("5.0 min", submission.getDevelopmentTime());
    assertTrue(submission.isDecoloration());
    assertEquals((Double) 20.0, submission.getWeightMarkerQuantity());
    assertEquals("20.0 μg", submission.getProteinQuantity());
    assertEquals("comment", submission.getComment());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(LocalDateTime.now().plusMinutes(2).isAfter(submission.getSubmissionDate()));
    assertTrue(LocalDateTime.now().minusMinutes(2).isBefore(submission.getSubmissionDate()));
    assertNull(submission.getSampleDeliveryDate());
    assertNull(submission.getDigestionDate());
    assertNull(submission.getAnalysisDate());
    assertNull(submission.getDataAvailableDate());
    assertEquals(0, submission.getVersion());
    samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals("unit_test_gel_01", submissionSample.getName());
    assertEquals((Integer) 10, submissionSample.getNumberProtein());
    assertEquals((Double) 120.0, submissionSample.getMolecularWeight());
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").orElseThrow();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "my_gel_image.jpg").orElseThrow();
    assertEquals("my_gel_image.jpg", file.getFilename());
    assertArrayEquals(imageContent, file.getContent());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    // Validate email that is sent to proteomic users.
    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService).send(email);
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
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setContaminants("contaminant1 - 1.0 μg (comment)");
    submission.setStandards("standard1 - 1.0 μg (comment)");
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
    assertNotEquals(0, submission.getId());
    submission = repository.findById(submission.getId()).orElseThrow();
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals("trypsine was not used", submission.getUsedDigestion());
    assertEquals("other digestion", submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertEquals("http://localhost/my_site", submission.getIdentificationLink());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("contaminant1 - 1.0 μg (comment)", submission.getContaminants());
    assertEquals("standard1 - 1.0 μg (comment)", submission.getStandards());
    assertEquals("comment", submission.getComment());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(LocalDateTime.now().plusMinutes(2).isAfter(submission.getSubmissionDate()));
    assertTrue(LocalDateTime.now().minusMinutes(2).isBefore(submission.getSubmissionDate()));
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
    assertEquals((Integer) 10, submissionSample.getNumberProtein());
    assertEquals((Double) 120.0, submissionSample.getMolecularWeight());
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
    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService).send(email);
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
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setContaminants("contaminant1 - 1.0 μg (comment)");
    submission.setStandards("standard1 - 1.0 μg (comment)");
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
    assertNotEquals(0, submission.getId());
    submission = repository.findById(submission.getId()).orElseThrow();
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals("trypsine was not used", submission.getUsedDigestion());
    assertEquals("other digestion", submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertEquals("http://localhost/my_site", submission.getIdentificationLink());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("contaminant1 - 1.0 μg (comment)", submission.getContaminants());
    assertEquals("standard1 - 1.0 μg (comment)", submission.getStandards());
    assertEquals("comment", submission.getComment());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(LocalDateTime.now().plusMinutes(2).isAfter(submission.getSubmissionDate()));
    assertTrue(LocalDateTime.now().minusMinutes(2).isBefore(submission.getSubmissionDate()));
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
    assertEquals((Integer) 10, submissionSample.getNumberProtein());
    assertEquals((Double) 120.0, submissionSample.getMolecularWeight());
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
    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService).send(email);
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
    submission.setService(Service.SMALL_MOLECULE);
    submission.setExperiment("");
    submission.setGoal(null);
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setHighResolution(true);
    submission.setFormula("h2o");
    submission.setMonoisotopicMass(18.0);
    submission.setAverageMass(18.1);
    submission.setSolutionSolvent("ch3oh");
    submission.setToxicity("none");
    submission.setLightSensitive(true);
    submission.setStorageTemperature(StorageTemperature.LOW);
    submission.setSolvents(List.of(Solvent.ACETONITRILE));
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
    assertNotEquals(0, submission.getId());
    submission = repository.findById(submission.getId()).orElseThrow();
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals((Long) 1L, submission.getLaboratory().getId());
    assertNotNull(submission.getSubmissionDate());
    assertTrue(LocalDateTime.now().plusMinutes(2).isAfter(submission.getSubmissionDate()));
    assertTrue(LocalDateTime.now().minusMinutes(2).isBefore(submission.getSubmissionDate()));
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals("unit_test_molecule_01", submission.getExperiment());
    assertNull(submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertTrue(submission.isHighResolution());
    assertEquals("h2o", submission.getFormula());
    assertEquals((Double) 18.0, submission.getMonoisotopicMass());
    assertEquals((Double) 18.1, submission.getAverageMass());
    assertEquals("ch3oh", submission.getSolutionSolvent());
    assertEquals(1, submission.getSolvents().size());
    assertTrue(submission.getSolvents().contains(Solvent.ACETONITRILE));
    assertEquals("chrisanol", submission.getOtherSolvent());
    assertEquals("none", submission.getToxicity());
    assertTrue(submission.isLightSensitive());
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
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").orElseThrow();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "structure.jpg").orElseThrow();
    assertEquals("structure.jpg", file.getFilename());
    assertArrayEquals(imageContent, file.getContent());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission, submissionLogged);

    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService).send(email);
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
    when(submissionActivityService.insert(any(Submission.class))).thenReturn(activity);
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
    submission.setProteinContent(ProteinContent.MEDIUM);
    submission.setContaminants("contaminant1 - 1.0 μg (comment)");
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
    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService, atLeastOnce()).send(email);
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, never()).addTo("robert.stlouis@ircm.qc.ca");
    verify(email, never()).addTo("benoit.coulombe@ircm.qc.ca");
    verify(email).setSubject("New samples were submitted");
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains("unit_test_eluate_01"));
    assertTrue(htmlContent.contains("unit_test_eluate_01"));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
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
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
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

    assertThrows(AccessDeniedException.class, () -> service.insert(submission));
  }

  @Test
  public void update() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    LocalDateTime newDate = LocalDateTime.now();
    submission.setSubmissionDate(newDate);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertNull(submission.getSource());
    assertNull(submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertNull(submission.getUsedDigestion());
    assertNull(submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertNull(submission.getIdentificationLink());
    assertNull(submission.getProtein());
    assertNull(submission.getPostTranslationModification());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("cap_contaminant - 3 μg (some_comment)", submission.getContaminants());
    assertEquals("cap_standard - 3 μg (some_comment)", submission.getStandards());
    assertNull(submission.getComment());
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
    assertNull(submissionSample.getNumberProtein());
    assertNull(submissionSample.getMolecularWeight());
    assertEquals(0, submission.getFiles().size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_Sample() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.getSamples().get(0).setName("unit_test_01");
    submission.getSamples().get(0).setVolume("20.0 μl");
    submission.getSamples().get(0).setQuantity("2.0 μg");
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(1, samples.size());
    SubmissionSample submissionSample = samples.get(0);
    assertEquals((Long) 447L, submissionSample.getId());
    assertEquals("unit_test_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("20.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertNull(submissionSample.getNumberProtein());
    assertNull(submissionSample.getMolecularWeight());
    assertEquals(0, submission.getFiles().size());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_NewSamples() {
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
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.setSamples(samples);

    service.update(submission, null);

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq(null));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    samples = submission.getSamples();
    assertEquals(2, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals((Integer) 10, submissionSample.getNumberProtein());
    assertEquals((Double) 120.0, submissionSample.getMolecularWeight());
    assertFalse(sampleRepository.findById(447L).isPresent());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getValue();
    assertEquals(submission.getId(), submissionLogged.getId());
  }

  @Test
  public void update_UpdateUser() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    User user = userRepository.findById(4L).orElseThrow();
    submission.setUser(user);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    assertThrows(IllegalArgumentException.class, () -> service.update(submission, null));
  }

  @Test
  public void update_Received() {
    Submission submission = repository.findById(149L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);

    assertThrows(IllegalArgumentException.class, () -> service.update(submission, null));
  }

  @Test
  public void update_AfterReceived() {
    Submission submission = repository.findById(147L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);

    assertThrows(IllegalArgumentException.class, () -> service.update(submission, null));
  }

  @Test
  public void update_Email() throws Exception {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(submission, null);

    repository.flush();
    // Validate email that is sent to proteomic users.
    verify(mailService, atLeastOnce()).htmlEmail();
    verify(mailService, atLeastOnce()).send(email);
    verify(email).addTo("christian.poitras@ircm.qc.ca");
    verify(email).addTo("liam.li@ircm.qc.ca");
    verify(email).addTo("jackson.smith@ircm.qc.ca");
    verify(email, never()).addTo("benoit.coulombe@ircm.qc.ca");
    verify(email).setSubject("Submission was updated");
    verify(email).setText(stringCaptor.capture(), stringCaptor.capture());
    String textContent = stringCaptor.getAllValues().get(0);
    String htmlContent = stringCaptor.getAllValues().get(1);
    assertTrue(textContent.contains("CAP_20111116_01"));
    assertTrue(htmlContent.contains("CAP_20111116_01"));
    assertFalse(textContent.contains("???"));
    assertFalse(htmlContent.contains("???"));
  }

  @Test
  public void update_Admin() {
    Submission submission = repository.findById(1L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.setService(Service.LC_MS_MS);
    submission.setTaxonomy("human");
    submission.setExperiment("experiment");
    submission.setGoal("goal");
    submission.setInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    submission.setSource(MassDetectionInstrumentSource.ESI);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setDigestion(ProteolyticDigestion.TRYPSIN);
    submission.setUsedDigestion("trypsine was not used");
    submission.setOtherDigestion("other digestion");
    submission.setIdentification(ProteinIdentification.NCBINR);
    submission.setIdentificationLink("http://localhost/my_site");
    submission.setProtein("protein");
    submission.setPostTranslationModification("my_modification");
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
    User user = userRepository.findById(4L).orElseThrow();
    submission.setUser(user);
    LocalDateTime newDate = LocalDateTime.now();
    submission.setSubmissionDate(newDate);
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(submission, "unit_test");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = repository.findById(1L).orElseThrow();
    verify(activityService).insert(activity);
    assertEquals((Long) 1L, submission.getId());
    assertEquals(user.getId(), submission.getUser().getId());
    assertEquals(user.getLaboratory().getId(), submission.getLaboratory().getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals("human", submission.getTaxonomy());
    assertEquals("experiment", submission.getExperiment());
    assertEquals("goal", submission.getGoal());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, submission.getInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals("trypsine was not used", submission.getUsedDigestion());
    assertEquals("other digestion", submission.getOtherDigestion());
    assertEquals(ProteinIdentification.NCBINR, submission.getIdentification());
    assertEquals("http://localhost/my_site", submission.getIdentificationLink());
    assertEquals("protein", submission.getProtein());
    assertEquals("my_modification", submission.getPostTranslationModification());
    assertEquals(ProteinContent.MEDIUM, submission.getProteinContent());
    assertEquals("comment", submission.getComment());
    assertEquals(newDate, submission.getSubmissionDate());
    files = submission.getFiles();
    assertEquals(2, files.size());
    file = findFile(files, "my_file.docx").orElseThrow();
    assertEquals("my_file.docx", file.getFilename());
    assertArrayEquals(fileContent, file.getContent());
    file = findFile(files, "my_gel_image.jpg").orElseThrow();
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
  public void update_NewSample_Admin() {
    SubmissionSample sample = new SubmissionSample();
    sample.setName("unit_test_eluate_01");
    sample.setType(SampleType.SOLUTION);
    sample.setVolume("10.0 μl");
    sample.setQuantity("2.0 μg");
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    Submission submission = repository.findById(147L).orElseThrow();
    detach(submission);
    submission.getSamples().forEach(this::detach);
    submission.getSamples().add(sample);
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(submission, "unit_test");

    repository.flush();
    verify(permissionEvaluator).hasPermission(any(), eq(submission), eq(WRITE));
    verify(submissionActivityService).update(submissionCaptor.capture(), eq("unit_test"));
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    List<SubmissionSample> samples = submission.getSamples();
    assertEquals(3, samples.size());
    assertTrue(find(samples, "unit_test_eluate_01").isPresent());
    SubmissionSample submissionSample = find(samples, "unit_test_eluate_01").get();
    assertNotEquals(0, submissionSample.getId());
    assertEquals("unit_test_eluate_01", submissionSample.getName());
    assertEquals(SampleType.SOLUTION, submissionSample.getType());
    assertEquals("10.0 μl", submissionSample.getVolume());
    assertEquals("2.0 μg", submissionSample.getQuantity());
    assertEquals((Integer) 10, submissionSample.getNumberProtein());
    assertEquals((Double) 120.0, submissionSample.getMolecularWeight());

    // Validate log.
    Submission submissionLogged = submissionCaptor.getAllValues().get(0);
    assertEquals((Long) 147L, submissionLogged.getId());
    assertEquals((Long) 559L, submissionLogged.getSamples().get(0).getId());
    assertEquals((Long) 560L, submissionLogged.getSamples().get(1).getId());
    assertNotEquals(0, submissionLogged.getSamples().get(2).getId());
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  public void hide() {
    Submission submission = repository.findById(147L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.hide(submission);

    repository.flush();
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    verify(submissionActivityService).update(submission, null);
    assertTrue(submission.isHidden());
  }

  @Test
  @WithAnonymousUser
  public void hide_AccessDenied_Anonymous() {
    Submission submission = repository.findById(147L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    assertThrows(AccessDeniedException.class, () -> service.hide(submission));
  }

  @Test
  @WithMockUser(authorities = {UserRole.USER, UserRole.MANAGER})
  public void hide_AccessDenied() {
    Submission submission = repository.findById(147L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    assertThrows(AccessDeniedException.class, () -> service.hide(submission));
  }

  @Test
  @WithMockUser(authorities = UserRole.ADMIN)
  @SuppressWarnings("unchecked")
  public void show() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity),
        Optional.empty());

    service.show(submission);

    repository.flush();
    verify(activityService).insert(activity);
    submission = repository.findById(submission.getId()).orElseThrow();
    verify(submissionActivityService).update(submission, null);
    assertFalse(submission.isHidden());
  }

  @Test
  @WithAnonymousUser
  @SuppressWarnings("unchecked")
  public void show_AccessDenied_Anonymous() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity),
        Optional.empty());

    assertThrows(AccessDeniedException.class, () -> service.show(submission));
  }

  @Test
  @WithMockUser(authorities = {UserRole.USER, UserRole.MANAGER})
  @SuppressWarnings("unchecked")
  public void show_AccessDenied() {
    Submission submission = repository.findById(36L).orElseThrow();
    detach(submission);
    when(submissionActivityService.update(any(), any())).thenReturn(Optional.of(activity),
        Optional.empty());

    assertThrows(AccessDeniedException.class, () -> service.show(submission));
  }
}
