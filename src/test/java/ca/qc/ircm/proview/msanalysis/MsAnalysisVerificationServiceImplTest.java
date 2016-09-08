package ca.qc.ircm.proview.msanalysis;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisVerificationServiceImplTest {
  private MsAnalysisVerificationServiceImpl msAnalysisVerificationServiceImpl;

  @Before
  public void beforeTest() {
    msAnalysisVerificationServiceImpl = new MsAnalysisVerificationServiceImpl(true);
  }

  private void compareSets(Set<?> expected, Set<?> actual) {
    for (Object o : expected) {
      assertTrue("Expected " + o + " not in actual", actual.contains(o));
    }
    for (Object o : actual) {
      assertTrue("Expected " + o + " not in actual", expected.contains(o));
    }
    if (expected.size() != actual.size()) {
      fail("Size of sets differ");
    }
  }

  /**
   * Before test.
   */
  @Test
  public void verifications_VelosNsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.VELOS, Source.NSI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_VelosEsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.VELOS, Source.ESI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_VelosLtdt() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.VELOS, Source.LDTD);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_LtqorbitrapNsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.LTQ_ORBI_TRAP, Source.NSI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_LtqorbitrapEsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.LTQ_ORBI_TRAP, Source.ESI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_LtqorbitrapLtdt() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.LTQ_ORBI_TRAP, Source.LDTD);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("heliumQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("drainingVialVolume");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("acquisitionFile");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_QtofNsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.Q_TOF, Source.NSI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("argonQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("openGaz");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("collisionEnergy");
    verificationStrings.add("mcp");
    verificationStrings.add("uncheckedAutoCid");
    verificationStrings.add("checkedGhz");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("clMethod");
    verificationStrings.add("msMethod");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_QtofEsi() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.Q_TOF, Source.ESI);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("argonQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("openGaz");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("collisionEnergy");
    verificationStrings.add("mcp");
    verificationStrings.add("uncheckedAutoCid");
    verificationStrings.add("checkedGhz");
    verificationStrings.add("drainingVialVolume");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("clMethod");
    verificationStrings.add("msMethod");
    verificationStrings.add("volume");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_QtofLdtd() {
    final Map<VerificationType, Set<String>> verifications = msAnalysisVerificationServiceImpl
        .verifications(MassDetectionInstrument.Q_TOF, Source.LDTD);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("coolerTemperature");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("argonQuantity");
    verificationStrings.add("mobilePhaseQuantity");
    verificationStrings.add("openGaz");
    verificationStrings.add("lcPumpPressure");
    verificationStrings.add("vacuum");
    verificationStrings.add("collisionEnergy");
    verificationStrings.add("mcp");
    verificationStrings.add("uncheckedAutoCid");
    verificationStrings.add("drainingVialVolume");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("clMethod");
    verificationStrings.add("msMethod");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }

  @Test
  public void verifications_Tof() {
    final Map<VerificationType, Set<String>> verifications =
        msAnalysisVerificationServiceImpl.verifications(MassDetectionInstrument.TOF, null);

    Set<String> verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("diskSpace");
    verificationStrings.add("qcPassed");
    verificationStrings.add("calibration");
    verificationStrings.add("sonicatedPlaque");
    verificationStrings.add("plaquePositionOnAutoSampler");
    verificationStrings.add("nitrogenQuantity");
    verificationStrings.add("vacuum");
    verificationStrings.add("mcp");
    verificationStrings.add("uncheckedAutoCid");
    verificationStrings.add("checkedGhz");
    verificationStrings.add("spray");
    compareSets(verificationStrings, verifications.get(VerificationType.INSTRUMENT));
    verificationStrings = new LinkedHashSet<>();
    verificationStrings.add("sampleVsSpot");
    verificationStrings.add("msMethod");
    compareSets(verificationStrings, verifications.get(VerificationType.SAMPLE));
  }
}