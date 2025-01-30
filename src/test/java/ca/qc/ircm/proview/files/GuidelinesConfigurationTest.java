package ca.qc.ircm.proview.files;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link GuidelinesConfiguration}.
 */
@NonTransactionalTestAnnotations
public class GuidelinesConfigurationTest {

  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;

  private Path path(String filename) {
    return Paths.get(System.getProperty("user.dir")).resolve("guidelines").resolve(filename);
  }

  @Test
  public void categories_English() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.ENGLISH);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Guidelines", category.getName());
    List<Guideline> guidelines = category.getGuidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Gel-free sample preparation", guideline.getName());
    assertEquals(path("Guidelines_Gel-free_sample_preparation.doc"), guideline.getPath());
    category = categories.get(1);
    assertEquals("Protocols", category.getName());
    guidelines = category.getGuidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Gel staining protocols", guideline.getName());
    assertEquals(path("Gel staining protocols.doc"), guideline.getPath());
    guideline = guidelines.get(1);
    assertEquals("Immunoprecipitation Magnetic Dynabeads protocol", guideline.getName());
    assertEquals(path("Immunoprecipitation Magnetic Dynabeads protocol.docx"), guideline.getPath());
  }

  @Test
  public void categories_French() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.FRENCH);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Directives", category.getName());
    List<Guideline> guidelines = category.getGuidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Préparation des échantillons sans gel", guideline.getName());
    assertEquals(path("Directives_de_préparation_des_échantillons_sans_gel.doc"),
        guideline.getPath());
    category = categories.get(1);
    assertEquals("Protocoles", category.getName());
    guidelines = category.getGuidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Protocoles coloration de gels", guideline.getName());
    assertEquals(path("Protocoles coloration de gels.doc"), guideline.getPath());
    guideline = guidelines.get(1);
    assertEquals("Protocole Immunoprecipitation Dynabeads", guideline.getName());
    assertEquals(path("Protocole Immunoprecipitation Dynabeads.docx"), guideline.getPath());
  }

  @Test
  public void categories_Italian() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.ITALIAN);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Linee guida", category.getName());
    List<Guideline> guidelines = category.getGuidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Preparazione del campione senza gel", guideline.getName());
    assertEquals(path("Preparazione_del_campione_senza_gel.doc"), guideline.getPath());
    category = categories.get(1);
    assertEquals("Protocolli", category.getName());
    guidelines = category.getGuidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Protocolli di colorazione gel", guideline.getName());
    assertEquals(path("Protocolli_di_colorazione_gel.doc"), guideline.getPath());
    guideline = guidelines.get(1);
    assertEquals("Protocollo di immunoprecipitazione magnetica Dynabeads", guideline.getName());
    assertEquals(path("Protocollo_di_immunoprecipitazione_magnetica_Dynabeads.docx"),
        guideline.getPath());
  }
}
