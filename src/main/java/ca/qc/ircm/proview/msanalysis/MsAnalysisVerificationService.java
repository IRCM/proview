package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.msanalysis.MsAnalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;

import java.util.Map;
import java.util.Set;

/**
 * MS analysis verification list services.
 */
public interface MsAnalysisVerificationService {
  /**
   * Returns verification list for specified instrument and source.
   *
   * @param instrument
   *          instrument
   * @param source
   *          source
   * @return verification list for specified instrument and source
   */
  public Map<VerificationType, Set<String>> verifications(MassDetectionInstrument instrument,
      Source source);
}
