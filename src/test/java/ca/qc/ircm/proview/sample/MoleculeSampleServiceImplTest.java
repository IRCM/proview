package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.sample.MoleculeSample.StorageTemperature;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.sample.SubmissionSample.ServiceType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MoleculeSampleServiceImplTest {
  private MoleculeSampleServiceImpl moleculeSampleServiceImpl;
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
    moleculeSampleServiceImpl = new MoleculeSampleServiceImpl(entityManager, sampleActivityService,
        activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  private byte[] getResourceContent(String resource) throws IOException, URISyntaxException {
    Path path = Paths.get(getClass().getResource(resource).toURI());
    return Files.readAllBytes(path);
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
  public void get_Id() throws Throwable {
    MoleculeSample sample = moleculeSampleServiceImpl.get(443L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertNotNull(sample);
    assertEquals((Long) 443L, sample.getId());
    assertEquals("IRC20111013_3", sample.getLims());
    assertEquals(Sample.Type.SUBMISSION, sample.getType());
    assertEquals("CAP_20111013_05", sample.getName());
    assertEquals(null, sample.getComments());
    assertEquals(ServiceType.SMALL_MOLECULE, sample.getServiceType());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals(SubmissionSample.Status.TO_APPROVE, sample.getStatus());
    assertEquals(Service.SMALL_MOLECULE, sample.getService());
    assertEquals(null, sample.getMassDetectionInstrument());
    assertEquals(Source.ESI, sample.getSource());
    assertEquals(Sample.Support.SOLUTION, sample.getSupport());
    assertEquals((Long) 33L, sample.getSubmission().getId());
    assertEquals("C100H100O100", sample.getFormula());
    assertEquals((Double) 654.654, sample.getMonoisotopicMass());
    assertEquals((Double) 654.654, sample.getAverageMass());
    assertEquals("MeOH/TFA 0.1%", sample.getSolutionSolvent());
    assertEquals(null, sample.getOtherSolvent());
    assertEquals(null, sample.getToxicity());
    assertEquals(false, sample.isLightSensitive());
    assertEquals(StorageTemperature.MEDIUM, sample.getStorageTemperature());
    Structure testStructure = sample.getStructure();
    assertEquals("glucose.png", testStructure.getFilename());
    assertArrayEquals(getResourceContent("/sample/glucose.png"), testStructure.getContent());
    assertNotNull(sample.getSolventList());
    assertEquals(1, sample.getSolventList().size());
    assertNotNull(find(sample.getSolventList(), Solvent.METHANOL));
    assertEquals(true, sample.isLowResolution());
    assertEquals(false, sample.isHighResolution());
    assertEquals(false, sample.isMsms());
    assertEquals(false, sample.isExactMsms());
    assertEquals(null, sample.getPrice());
    assertEquals(null, sample.getAdditionalPrice());
    assertEquals(true, sample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 3L, sample.getOriginalContainer().getId());
  }

  @Test
  public void get_NullId() throws Throwable {
    MoleculeSample sample = moleculeSampleServiceImpl.get(null);

    assertNull(sample);
  }

  @Test
  public void update() throws Throwable {
    MoleculeSample sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(sample);
    sample.setComments("my_new_comment");
    sample.setName("new_molecule_tag_0001");
    sample.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    sample.setSource(Source.LDTD);
    sample.setSupport(Support.DRY);
    sample.setFormula("h2o");
    sample.setMonoisotopicMass(18.0);
    sample.setAverageMass(18.0);
    sample.setSolutionSolvent(null);
    sample.setOtherSolvent("ch3oh");
    sample.setToxicity("die");
    sample.setLightSensitive(true);
    sample.setStorageTemperature(StorageTemperature.LOW);
    sample.setLowResolution(false);
    sample.setHighResolution(true);
    sample.setMsms(true);
    sample.setExactMsms(true);
    sample.setAdditionalPrice(new BigDecimal("21.50"));
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    moleculeSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    MoleculeSample test = entityManager.find(MoleculeSample.class, sample.getId());
    assertEquals("my_new_comment", test.getComments());
    assertEquals("new_molecule_tag_0001", test.getName());
    assertEquals(MassDetectionInstrument.TOF, test.getMassDetectionInstrument());
    assertEquals(Source.LDTD, test.getSource());
    assertEquals(Support.DRY, test.getSupport());
    assertEquals("h2o", test.getFormula());
    assertEquals((Double) 18.0, test.getMonoisotopicMass());
    assertEquals((Double) 18.0, test.getAverageMass());
    assertEquals(null, test.getSolutionSolvent());
    assertEquals("ch3oh", test.getOtherSolvent());
    assertEquals("die", test.getToxicity());
    assertEquals(true, test.isLightSensitive());
    assertEquals(StorageTemperature.LOW, test.getStorageTemperature());
    assertEquals(false, test.isLowResolution());
    assertEquals(true, test.isHighResolution());
    assertEquals(true, test.isMsms());
    assertEquals(true, test.isExactMsms());
    assertEquals(new BigDecimal("21.50").setScale(2), test.getAdditionalPrice().setScale(2));
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof MoleculeSample);
    MoleculeSample newMoleculeSample = (MoleculeSample) newSample;
    assertEquals("my_new_comment", newMoleculeSample.getComments());
    assertEquals("new_molecule_tag_0001", newMoleculeSample.getName());
    assertEquals(MassDetectionInstrument.TOF, newMoleculeSample.getMassDetectionInstrument());
    assertEquals(Source.LDTD, newMoleculeSample.getSource());
    assertEquals(Support.DRY, newMoleculeSample.getSupport());
    assertEquals("h2o", newMoleculeSample.getFormula());
    assertEquals((Double) 18.0, newMoleculeSample.getMonoisotopicMass());
    assertEquals((Double) 18.0, newMoleculeSample.getAverageMass());
    assertEquals(null, newMoleculeSample.getSolutionSolvent());
    assertEquals("ch3oh", newMoleculeSample.getOtherSolvent());
    assertEquals("die", newMoleculeSample.getToxicity());
    assertEquals(true, newMoleculeSample.isLightSensitive());
    assertEquals(StorageTemperature.LOW, newMoleculeSample.getStorageTemperature());
    assertEquals(false, newMoleculeSample.isLowResolution());
    assertEquals(true, newMoleculeSample.isHighResolution());
    assertEquals(true, newMoleculeSample.isMsms());
    assertEquals(true, newMoleculeSample.isExactMsms());
    assertEquals(new BigDecimal("21.50").setScale(2),
        newMoleculeSample.getAdditionalPrice().setScale(2));
  }

  @Test
  public void update_AddSolvent() throws SaveStructureException {
    MoleculeSample sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(sample);
    sample.getSolventList().add(new SampleSolvent(Solvent.OTHER));
    sample.setOtherSolvent("ch3oh");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    moleculeSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    MoleculeSample test = entityManager.find(MoleculeSample.class, sample.getId());
    assertEquals(2, test.getSolventList().size());
    assertNotNull(find(test.getSolventList(), Solvent.OTHER));
    assertEquals("ch3oh", test.getOtherSolvent());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof MoleculeSample);
    MoleculeSample newMoleculeSample = (MoleculeSample) newSample;
    assertEquals(2, newMoleculeSample.getSolventList().size());
    assertNotNull(find(newMoleculeSample.getSolventList(), Solvent.OTHER));
    assertEquals("ch3oh", newMoleculeSample.getOtherSolvent());
  }

  @Test
  public void update_removeSolvent() throws Throwable {
    MoleculeSample sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(sample);
    sample.getSolventList().remove(find(sample.getSolventList(), Solvent.METHANOL));
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    moleculeSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    MoleculeSample test = entityManager.find(MoleculeSample.class, sample.getId());
    assertEquals(0, test.getSolventList().size());
    assertEquals(false, test.getSolventList().contains(Solvent.METHANOL));
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof MoleculeSample);
    MoleculeSample newMoleculeSample = (MoleculeSample) newSample;
    assertEquals(0, newMoleculeSample.getSolventList().size());
    assertEquals(false, newMoleculeSample.getSolventList().contains(Solvent.METHANOL));
  }

  @Test
  public void update_removeThenInsertSolvent() throws Throwable {
    MoleculeSample sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(sample);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);
    // Remove solvent.
    sample.getSolventList().remove(find(sample.getSolventList(), Solvent.METHANOL));
    moleculeSampleServiceImpl.update(sample, "test changes - remove solvent");
    // Reinsert solvent.
    sample.getSolventList().add(new SampleSolvent(Solvent.METHANOL));

    moleculeSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService, times(2)).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService, times(2)).insert(activity);
    MoleculeSample test = entityManager.find(MoleculeSample.class, sample.getId());
    assertEquals(1, test.getSolventList().size());
    assertNotNull(find(test.getSolventList(), Solvent.METHANOL));
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof MoleculeSample);
    MoleculeSample newMoleculeSample = (MoleculeSample) newSample;
    assertEquals(1, newMoleculeSample.getSolventList().size());
    assertNotNull(find(newMoleculeSample.getSolventList(), Solvent.METHANOL));
  }

  @Test
  public void update_Structure() throws Throwable {
    MoleculeSample sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(sample);
    sample.getStructure().setFilename("new_structure.txt");
    sample.getStructure().setContent(getResourceContent("/sample/new_structure.txt"));
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    moleculeSampleServiceImpl.update(sample, "test changes");

    entityManager.flush();
    sample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.refresh(sample);
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    Structure testStructure = sample.getStructure();
    assertEquals("new_structure.txt", testStructure.getFilename());
    assertArrayEquals(getResourceContent("/sample/new_structure.txt"), testStructure.getContent());
  }
}
