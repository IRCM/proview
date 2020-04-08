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

package ca.qc.ircm.proview.files;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class GuidelinesConfigurationTest {
  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;

  private Path path(String filename) {
    return Paths.get(System.getProperty("user.dir")).resolve("guidelines").resolve(filename);
  }

  @Test
  public void categories_Null() {
    List<Category> categories = guidelinesConfiguration.categories(null);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Guidelines", category.name());
    List<Guideline> guidelines = category.guidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Gel-free sample preparation", guideline.name());
    assertEquals(path("Guidelines_Gel-free_sample_preparation.doc"), guideline.path());
    category = categories.get(1);
    assertEquals("Protocols", category.name());
    guidelines = category.guidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Gel staining protocols", guideline.name());
    assertEquals(path("Gel staining protocols.doc"), guideline.path());
    guideline = guidelines.get(1);
    assertEquals("Immunoprecipitation Magnetic Dynabeads protocol", guideline.name());
    assertEquals(path("Immunoprecipitation Magnetic Dynabeads protocol.docx"), guideline.path());
  }

  @Test
  public void categories_English() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.ENGLISH);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Guidelines", category.name());
    List<Guideline> guidelines = category.guidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Gel-free sample preparation", guideline.name());
    assertEquals(path("Guidelines_Gel-free_sample_preparation.doc"), guideline.path());
    category = categories.get(1);
    assertEquals("Protocols", category.name());
    guidelines = category.guidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Gel staining protocols", guideline.name());
    assertEquals(path("Gel staining protocols.doc"), guideline.path());
    guideline = guidelines.get(1);
    assertEquals("Immunoprecipitation Magnetic Dynabeads protocol", guideline.name());
    assertEquals(path("Immunoprecipitation Magnetic Dynabeads protocol.docx"), guideline.path());
  }

  @Test
  public void categories_French() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.FRENCH);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Directives", category.name());
    List<Guideline> guidelines = category.guidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Préparation des échantillons sans gel", guideline.name());
    assertEquals(path("Directives_de_préparation_des_échantillons_sans_gel.doc"),
        guideline.path());
    category = categories.get(1);
    assertEquals("Protocoles", category.name());
    guidelines = category.guidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Protocoles coloration de gels", guideline.name());
    assertEquals(path("Protocoles coloration de gels.doc"), guideline.path());
    guideline = guidelines.get(1);
    assertEquals("Protocole Immunoprecipitation Dynabeads", guideline.name());
    assertEquals(path("Protocole Immunoprecipitation Dynabeads.docx"), guideline.path());
  }

  @Test
  public void categories_Italian() {
    List<Category> categories = guidelinesConfiguration.categories(Locale.ITALIAN);
    assertEquals(2, categories.size());
    Category category = categories.get(0);
    assertEquals("Linee guida", category.name());
    List<Guideline> guidelines = category.guidelines();
    assertEquals(1, guidelines.size());
    Guideline guideline = guidelines.get(0);
    assertEquals("Preparazione del campione senza gel", guideline.name());
    assertEquals(path("Preparazione_del_campione_senza_gel.doc"), guideline.path());
    category = categories.get(1);
    assertEquals("Protocolli", category.name());
    guidelines = category.guidelines();
    assertEquals(2, guidelines.size());
    guideline = guidelines.get(0);
    assertEquals("Protocolli di colorazione gel", guideline.name());
    assertEquals(path("Protocolli_di_colorazione_gel.doc"), guideline.path());
    guideline = guidelines.get(1);
    assertEquals("Protocollo di immunoprecipitazione magnetica Dynabeads", guideline.name());
    assertEquals(path("Protocollo_di_immunoprecipitazione_magnetica_Dynabeads.docx"),
        guideline.path());
  }
}
