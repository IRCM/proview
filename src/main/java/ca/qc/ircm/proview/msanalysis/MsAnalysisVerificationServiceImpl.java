package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.utils.xml.StackSaxHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Default implementation of MS analysis services.
 */
@Component
public class MsAnalysisVerificationServiceImpl implements MsAnalysisVerificationService {
  private static class Verification {
    private final String name;
    private Map<MassDetectionInstrument, Set<Source>> applies = new HashMap<>();

    public Verification(String name) {
      this.name = name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof Verification)) {
        return false;
      }
      Verification other = (Verification) obj;
      if (name == null) {
        if (other.name != null) {
          return false;
        }
      } else if (!name.equals(other.name)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "Verification [name=" + name + ", applies=" + applies + "]";
    }

    public void addApply(MassDetectionInstrument instrument) {
      applies.put(instrument, null);
    }

    public void addApply(MassDetectionInstrument instrument, Source source) {
      if (!applies.containsKey(instrument)) {
        applies.put(instrument, new HashSet<Source>());
      }
      applies.get(instrument).add(source);
    }

    public boolean applies(MassDetectionInstrument instrument, Source source) {
      if (source == null) {
        return applies.containsKey(instrument);
      } else {
        return applies.get(instrument) == null ? applies.containsKey(instrument)
            : applies.get(instrument).contains(source);
      }
    }

    public String getName() {
      return name;
    }
  }

  private Map<VerificationType, Set<Verification>> verifications;

  protected MsAnalysisVerificationServiceImpl() {
  }

  protected MsAnalysisVerificationServiceImpl(boolean test) {
    init();
  }

  @PostConstruct
  protected void init() {
    loadVerifications();
  }

  private void loadVerifications() {
    verifications = new HashMap<>();
    URL file = getClass().getResource("/msanalysis_verifications.xml");
    StackSaxHandler handler = new StackSaxHandler() {
      private VerificationType type;
      private Verification verification;
      private MassDetectionInstrument instrument;
      private boolean anySourceForInstrument;

      @Override
      protected void startElement(String elementName, Attributes attributes) throws SAXException {
        switch (elementName) {
          case "verifications":
            type = Enum.valueOf(VerificationType.class, attribute("type"));
            verifications.put(type, new LinkedHashSet<Verification>());
            break;
          case "verification":
            verification = new Verification(attribute("name"));
            break;
          case "instrument":
            anySourceForInstrument = false;
            instrument = Enum.valueOf(MassDetectionInstrument.class, attribute("name"));
            break;
          default:
            break;
        }
      }

      @Override
      protected void endElement(String elementName) {
        switch (elementName) {
          case "verification":
            verifications.get(type).add(verification);
            break;
          case "instrument":
            if (!anySourceForInstrument) {
              verification.addApply(instrument);
            }
            break;
          case "source":
            anySourceForInstrument = true;
            verification.addApply(instrument, Enum.valueOf(Source.class, attribute("name")));
            break;
          default:
            break;
        }
      }
    };
    try (InputStream input = file.openStream()) {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      parser.parse(input, handler);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new IllegalStateException("Could not create " + getClass().getSimpleName(), e);
    }
  }

  @Override
  public Map<VerificationType, Set<String>> verifications(MassDetectionInstrument instrument,
      Source source) {
    Map<VerificationType, Set<String>> verifications = new HashMap<>();
    for (VerificationType verificationType : VerificationType.values()) {
      verifications.put(verificationType, new HashSet<String>());
      for (Verification verification : this.verifications.get(verificationType)) {
        if (verification.applies(instrument, source)) {
          verifications.get(verificationType).add(verification.getName());
        }
      }
    }
    return verifications;
  }
}