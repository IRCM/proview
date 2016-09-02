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
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.mail.HtmlEmail;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.GelSample;
import ca.qc.ircm.proview.sample.GelSample.Coloration;
import ca.qc.ircm.proview.sample.GelSample.DevelopmentTimeUnit;
import ca.qc.ircm.proview.sample.GelSample.Separation;
import ca.qc.ircm.proview.sample.GelSample.Thickness;
import ca.qc.ircm.proview.sample.MoleculeSample;
import ca.qc.ircm.proview.sample.MoleculeSample.StorageTemperature;
import ca.qc.ircm.proview.sample.ProteicSample;
import ca.qc.ircm.proview.sample.ProteicSample.EnrichmentType;
import ca.qc.ircm.proview.sample.ProteicSample.MudPitFraction;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinContent;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteicSample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.Structure;
import ca.qc.ircm.proview.sample.SubmissionSample;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

  private <S extends Sample> S findByName(Collection<S> samples, String name) {
    for (S sample : samples) {
      if (sample.getName().equals(name)) {
        return sample;
      }
    }
    return null;
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
    assertEquals(submission, gelSample.getSubmission());
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
  public void get_Null() throws Throwable {
    Submission submission = submissionServiceImpl.get(null);

    assertNull(submission);
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
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSINE);
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
    sample.setDevelopmentTime(5.0);
    sample.setDevelopmentTimeUnit(DevelopmentTimeUnit.MINUTES);
    sample.setDecoloration(true);
    sample.setWeightMarkerQuantity(20.0);
    sample.setProteinQuantity("20.0");
    sample.setProteinQuantityUnit(Sample.QuantityUnit.MICRO_GRAMS);
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
    assertEquals(ProteolyticDigestion.TRYPSINE, gelSample.getProteolyticDigestionMethod());
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
    assertEquals(new Double(5.0), gelSample.getDevelopmentTime());
    assertEquals(DevelopmentTimeUnit.MINUTES, gelSample.getDevelopmentTimeUnit());
    assertEquals(true, gelSample.isDecoloration());
    assertEquals(new Double(20.0), gelSample.getWeightMarkerQuantity());
    assertEquals("20.0", gelSample.getProteinQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, gelSample.getProteinQuantityUnit());
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
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSINE);
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
    sample.setQuantity("2.0");
    sample.setQuantityUnit(Sample.QuantityUnit.MICRO_GRAMS);
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
    sample2.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSINE);
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
    sample2.setQuantity("2.0");
    sample2.setQuantityUnit(Sample.QuantityUnit.MICRO_GRAMS);
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    samples.add(sample2);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0");
    contaminant.setQuantityUnit(Contaminant.QuantityUnit.MICRO_GRAMS);
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<Contaminant>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0");
    standard.setQuantityUnit(Standard.QuantityUnit.MICRO_GRAMS);
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
    assertEquals(ProteolyticDigestion.TRYPSINE, eluateSample.getProteolyticDigestionMethod());
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
    assertEquals("2.0", eluateSample.getQuantity());
    assertEquals(Sample.QuantityUnit.MICRO_GRAMS, eluateSample.getQuantityUnit());
    contaminants = eluateSample.getContaminants();
    assertEquals(1, contaminants.size());
    contaminant = contaminants.get(0);
    assertEquals("contaminant1", contaminant.getName());
    assertEquals("1.0", contaminant.getQuantity());
    assertEquals(Contaminant.QuantityUnit.MICRO_GRAMS, contaminant.getQuantityUnit());
    assertEquals("comments", contaminant.getComments());
    standards = eluateSample.getStandards();
    assertEquals(1, standards.size());
    standard = standards.get(0);
    assertEquals("standard1", standard.getName());
    assertEquals("1.0", standard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, standard.getQuantityUnit());
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
    sample.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSINE);
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
    sample.setQuantity("2.0");
    sample.setQuantityUnit(Sample.QuantityUnit.MICRO_GRAMS);
    List<SubmissionSample> samples = new LinkedList<SubmissionSample>();
    samples.add(sample);
    Contaminant contaminant = new Contaminant();
    contaminant.setName("contaminant1");
    contaminant.setQuantity("1.0");
    contaminant.setQuantityUnit(Contaminant.QuantityUnit.MICRO_GRAMS);
    contaminant.setComments("comments");
    List<Contaminant> contaminants = new ArrayList<Contaminant>();
    contaminants.add(contaminant);
    sample.setContaminants(contaminants);
    Standard standard = new Standard();
    standard.setName("standard1");
    standard.setQuantity("1.0");
    standard.setQuantityUnit(Standard.QuantityUnit.MICRO_GRAMS);
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
