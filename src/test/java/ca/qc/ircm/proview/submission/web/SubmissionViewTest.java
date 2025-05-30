package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.UPLOAD;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionFileProperties.FILENAME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_IOEXCEPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_OVER_MAXIMUM;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_SIZE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.REMOVE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.fireEvent;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.web.UploadInternationalization.englishUploadI18N;
import static ca.qc.ircm.proview.web.UploadInternationalization.frenchUploadI18N;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import jakarta.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SubmissionView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionView.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private SubmissionView view;
  @MockitoBean
  private SubmissionService service;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private EntityManager entityManager;
  @Mock
  private BeforeEvent beforeEvent;
  @Mock
  private DataProviderListener<SubmissionFile> filesDataProviderListener;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  private final Locale locale = ENGLISH;
  private Submission submission;
  private final String experiment = "my test experiment";
  private List<SubmissionFile> files;
  private final String comment = "comment first line\nSecond line";
  private final Random random = new Random();

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    files = IntStream.range(0, 3).mapToObj(i -> {
      SubmissionFile file = new SubmissionFile();
      file.setFilename(RandomStringUtils.insecure().nextAlphanumeric(10));
      byte[] content = new byte[1024];
      random.nextBytes(content);
      file.setContent(content);
      return file;
    }).collect(Collectors.toList());
    submission = repository.findById(1L).orElseThrow();
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    view = navigate(SubmissionView.class);
  }

  private void setFields(Submission submission) {
    switch (submission.getService()) {
      case LC_MS_MS -> {
        SubmissionSample sample = submission.getSamples().get(0);
        view.lcmsmsSubmissionForm.experiment.setValue(submission.getExperiment());
        view.lcmsmsSubmissionForm.goal.setValue(submission.getGoal());
        view.lcmsmsSubmissionForm.taxonomy.setValue(submission.getTaxonomy());
        view.lcmsmsSubmissionForm.protein.setValue(submission.getProtein());
        view.lcmsmsSubmissionForm.molecularWeight.setValue(
            String.valueOf(sample.getMolecularWeight()));
        view.lcmsmsSubmissionForm.postTranslationModification.setValue(
            submission.getPostTranslationModification());
        view.lcmsmsSubmissionForm.sampleType.setValue(sample.getType());
        view.lcmsmsSubmissionForm.samplesCount.setValue(
            String.valueOf(submission.getSamples().size()));
        view.lcmsmsSubmissionForm.samplesNames.setValue(
            submission.getSamples().stream().map(Sample::getName)
                .collect(Collectors.joining(", ")));
        view.lcmsmsSubmissionForm.quantity.setValue(sample.getQuantity());
        view.lcmsmsSubmissionForm.volume.setValue(sample.getVolume());
        view.lcmsmsSubmissionForm.separation.setValue(submission.getSeparation());
        view.lcmsmsSubmissionForm.thickness.setValue(submission.getThickness());
        view.lcmsmsSubmissionForm.coloration.setValue(submission.getColoration());
        view.lcmsmsSubmissionForm.otherColoration.setValue(submission.getOtherColoration());
        view.lcmsmsSubmissionForm.developmentTime.setValue(submission.getDevelopmentTime());
        view.lcmsmsSubmissionForm.destained.setValue(submission.isDecoloration());
        view.lcmsmsSubmissionForm.weightMarkerQuantity.setValue(
            String.valueOf(submission.getWeightMarkerQuantity()));
        view.lcmsmsSubmissionForm.proteinQuantity.setValue(submission.getProteinQuantity());
        view.lcmsmsSubmissionForm.digestion.setValue(submission.getDigestion());
        view.lcmsmsSubmissionForm.usedDigestion.setValue(submission.getUsedDigestion());
        view.lcmsmsSubmissionForm.otherDigestion.setValue(submission.getOtherDigestion());
        view.lcmsmsSubmissionForm.proteinContent.setValue(submission.getProteinContent());
        view.lcmsmsSubmissionForm.instrument.setValue(submission.getInstrument());
        view.lcmsmsSubmissionForm.identification.setValue(submission.getIdentification());
        view.lcmsmsSubmissionForm.identificationLink.setValue(submission.getIdentificationLink());
        view.lcmsmsSubmissionForm.quantification.setValue(submission.getQuantification());
        view.lcmsmsSubmissionForm.quantificationComment.setValue(
            submission.getQuantificationComment());
      }
      case INTACT_PROTEIN -> {
        SubmissionSample sample = submission.getSamples().get(0);
        view.intactProteinSubmissionForm.experiment.setValue(submission.getExperiment());
        view.intactProteinSubmissionForm.goal.setValue(submission.getGoal());
        view.intactProteinSubmissionForm.taxonomy.setValue(submission.getTaxonomy());
        view.intactProteinSubmissionForm.protein.setValue(submission.getProtein());
        view.intactProteinSubmissionForm.molecularWeight.setValue(
            String.valueOf(sample.getMolecularWeight()));
        view.intactProteinSubmissionForm.postTranslationModification.setValue(
            submission.getPostTranslationModification());
        view.intactProteinSubmissionForm.sampleType.setValue(sample.getType());
        view.intactProteinSubmissionForm.samplesCount.setValue(
            String.valueOf(submission.getSamples().size()));
        view.intactProteinSubmissionForm.samplesNames.setValue(
            submission.getSamples().stream().map(Sample::getName)
                .collect(Collectors.joining(", ")));
        view.intactProteinSubmissionForm.quantity.setValue(sample.getQuantity());
        view.intactProteinSubmissionForm.volume.setValue(sample.getVolume());
        view.intactProteinSubmissionForm.injection.setValue(submission.getInjectionType());
        view.intactProteinSubmissionForm.source.setValue(submission.getSource());
        view.intactProteinSubmissionForm.instrument.setValue(submission.getInstrument());
      }
      case SMALL_MOLECULE -> {
        SubmissionSample sample = submission.getSamples().get(0);
        view.smallMoleculeSubmissionForm.sampleType.setValue(sample.getType());
        view.smallMoleculeSubmissionForm.sampleName.setValue(sample.getName());
        view.smallMoleculeSubmissionForm.solvent.setValue(submission.getSolutionSolvent());
        view.smallMoleculeSubmissionForm.formula.setValue(submission.getFormula());
        view.smallMoleculeSubmissionForm.monoisotopicMass.setValue(
            String.valueOf(submission.getMonoisotopicMass()));
        view.smallMoleculeSubmissionForm.averageMass.setValue(
            String.valueOf(submission.getAverageMass()));
        view.smallMoleculeSubmissionForm.toxicity.setValue(submission.getToxicity());
        view.smallMoleculeSubmissionForm.lightSensitive.setValue(submission.isLightSensitive());
        view.smallMoleculeSubmissionForm.storageTemperature.setValue(
            submission.getStorageTemperature());
        view.smallMoleculeSubmissionForm.highResolution.setValue(submission.isHighResolution());
        view.smallMoleculeSubmissionForm.solvents.setValue(new HashSet<>(submission.getSolvents()));
        view.smallMoleculeSubmissionForm.otherSolvent.setValue(submission.getOtherSolvent());
      }
    }
    view.comment.setValue(comment);
    files.forEach(
        file -> view.addFile(file.getFilename(), new ByteArrayInputStream(file.getContent())));
  }

  private Submission lcmsmsSubmission() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    submission.setExperiment("my experiment");
    submission.setGoal("my goal");
    submission.setTaxonomy("my taxon");
    submission.setProtein("my protein");
    submission.setPostTranslationModification("glyco");
    List<SubmissionSample> samples = IntStream.range(0, 2)
        .mapToObj(i -> new SubmissionSample("my sample " + (i + 1))).peek(sa -> {
          sa.setType(SampleType.SOLUTION);
          sa.setMolecularWeight(12.3);
          sa.setQuantity("13g");
          sa.setVolume("9 ml");
        }).collect(Collectors.toList());
    submission.setSamples(samples);
    submission.setSeparation(GelSeparation.TWO_DIMENSION);
    submission.setThickness(GelThickness.TWO);
    submission.setColoration(GelColoration.SYPRO);
    submission.setOtherColoration("my coloration");
    submission.setDevelopmentTime("20s");
    submission.setDecoloration(true);
    submission.setWeightMarkerQuantity(5.1);
    submission.setProteinQuantity("11g");
    submission.setDigestion(ProteolyticDigestion.DIGESTED);
    submission.setUsedDigestion("my used digestion");
    submission.setOtherDigestion("my other digestion");
    submission.setProteinContent(ProteinContent.LARGE);
    submission.setInstrument(MassDetectionInstrument.Q_EXACTIVE);
    submission.setIdentification(ProteinIdentification.UNIPROT);
    submission.setIdentificationLink("http://www.unitprot.org/mydatabase");
    submission.setQuantification(Quantification.SILAC);
    submission.setQuantificationComment("Heavy: Lys8, Arg10\nMedium: Lys4, Arg6");
    submission.setComment(comment);
    return submission;
  }

  private Submission intactProteinSubmission() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    submission.setExperiment("my experiment");
    submission.setGoal("my goal");
    submission.setTaxonomy("my taxon");
    submission.setProtein("my protein");
    submission.setPostTranslationModification("glyco");
    List<SubmissionSample> samples = IntStream.range(0, 2)
        .mapToObj(i -> new SubmissionSample("my sample " + (i + 1))).peek(sa -> {
          sa.setType(SampleType.SOLUTION);
          sa.setMolecularWeight(12.3);
          sa.setQuantity("13g");
          sa.setVolume("9 ml");
        }).collect(Collectors.toList());
    submission.setSamples(samples);
    submission.setInjectionType(InjectionType.LC_MS);
    submission.setSource(MassDetectionInstrumentSource.LDTD);
    submission.setInstrument(MassDetectionInstrument.Q_EXACTIVE);
    submission.setComment(comment);
    return submission;
  }

  private Submission smallMoleculeSubmission() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    List<SubmissionSample> samples = IntStream.range(0, 1)
        .mapToObj(i -> new SubmissionSample("my sample " + (i + 1)))
        .peek(sa -> sa.setType(SampleType.SOLUTION)).collect(Collectors.toList());
    submission.setSamples(samples);
    submission.setSolutionSolvent("ethanol");
    submission.setFormula("ch3oh");
    submission.setMonoisotopicMass(18.1);
    submission.setAverageMass(18.2);
    submission.setToxicity("poison");
    submission.setLightSensitive(true);
    submission.setStorageTemperature(StorageTemperature.MEDIUM);
    submission.setHighResolution(true);
    submission.setSolvents(Arrays.asList(Solvent.ACETONITRILE, Solvent.CHCL3));
    submission.setOtherSolvent("acetone");
    submission.setComment(comment);
    return submission;
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(SERVICE, view.service.getId().orElse(""));
    assertEquals(LC_MS_MS.name(), view.lcmsms.getId().orElse(""));
    assertEquals(SMALL_MOLECULE.name(), view.smallMolecule.getId().orElse(""));
    assertEquals(INTACT_PROTEIN.name(), view.intactProtein.getId().orElse(""));
    assertEquals(COMMENT, view.comment.getId().orElse(""));
    assertEquals(UPLOAD, view.upload.getId().orElse(""));
    assertEquals(FILES, view.files.getId().orElse(""));
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0, ""),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertEquals(view.getTranslation(SERVICE_PREFIX + LC_MS_MS.name()), view.lcmsms.getLabel());
    assertEquals(view.getTranslation(SERVICE_PREFIX + SMALL_MOLECULE.name()),
        view.smallMolecule.getLabel());
    assertEquals(view.getTranslation(SERVICE_PREFIX + INTACT_PROTEIN.name()),
        view.intactProtein.getLabel());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + COMMENT), view.comment.getLabel());
    validateEquals(englishUploadI18N(), view.upload.getI18n());
    HeaderRow headerRow = view.files.getHeaderRows().get(0);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FILENAME),
        headerRow.getCell(view.filename).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + REMOVE),
        headerRow.getCell(view.remove).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0, ""),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertEquals(view.getTranslation(SERVICE_PREFIX + LC_MS_MS.name()), view.lcmsms.getLabel());
    assertEquals(view.getTranslation(SERVICE_PREFIX + SMALL_MOLECULE.name()),
        view.smallMolecule.getLabel());
    assertEquals(view.getTranslation(SERVICE_PREFIX + INTACT_PROTEIN.name()),
        view.intactProtein.getLabel());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + COMMENT), view.comment.getLabel());
    validateEquals(frenchUploadI18N(), view.upload.getI18n());
    HeaderRow headerRow = view.files.getHeaderRows().get(0);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FILENAME),
        headerRow.getCell(view.filename).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + REMOVE),
        headerRow.getCell(view.remove).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void services() {
    assertEquals(3, view.service.getTabCount());
    assertEquals(view.lcmsms, view.service.getTabAt(0));
    assertEquals(view.smallMolecule, view.service.getTabAt(1));
    assertEquals(view.intactProtein, view.service.getTabAt(2));
  }

  @Test
  public void upload() {
    assertEquals(MAXIMUM_FILES_COUNT, view.upload.getMaxFiles());
    assertEquals(MAXIMUM_FILES_SIZE, view.upload.getMaxFileSize());
  }

  @Test
  public void upload_File() {
    view.uploadBuffer = mock(MultiFileMemoryBuffer.class);
    int filesize = 84325;
    byte[] content = new byte[filesize];
    random.nextBytes(content);
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    when(view.uploadBuffer.getInputStream(any())).thenReturn(input);
    String filename = "test_file.txt";
    String mimeType = "text/plain";
    SucceededEvent event = new SucceededEvent(view.upload, filename, mimeType, filesize);
    fireEvent(view.upload, event);
    Optional<SubmissionFile> newFile = view.files.getListDataView().getItems()
        .filter(sf -> sf.getFilename().equals(filename)).findFirst();
    assertTrue(newFile.isPresent());
    assertArrayEquals(content, newFile.get().getContent());
    verify(view.uploadBuffer).getInputStream(filename);
  }

  @Test
  public void files_Columns() {
    assertEquals(2, view.files.getColumns().size());
    assertNotNull(view.files.getColumnByKey(FILENAME));
    assertNotNull(view.files.getColumnByKey(REMOVE));
  }

  @Test
  public void files_ColumnsValueProvider() {
    files.forEach(file -> view.files.getListDataView().addItem(file));
    for (int i = 0; i < files.size(); i++) {
      SubmissionFile file = files.get(i);
      Component filenameComponent = test(view.files).getCellComponent(i, view.filename.getKey());
      assertInstanceOf(Anchor.class, filenameComponent);
      Anchor filenameAnchor = (Anchor) filenameComponent;
      assertEquals(file.getFilename(), filenameAnchor.getText());
      assertEquals(file.getFilename(), filenameAnchor.getElement().getAttribute("download"));
      assertTrue(filenameAnchor.getHref().startsWith("VAADIN/dynamic/resource"));
      Component removeComponent = test(view.files).getCellComponent(i, view.remove.getKey());
      assertInstanceOf(Button.class, removeComponent);
      Button removeButton = (Button) removeComponent;
      assertEquals("", removeButton.getText());
      validateIcon(VaadinIcon.TRASH.create(), removeButton.getIcon());
    }
    for (SubmissionFile file : files) {
      ((Button) test(view.files).getCellComponent(0, view.remove.getKey())).click();
      assertFalse(view.files.getListDataView().getItems().map(SubmissionFile::getFilename)
          .anyMatch(name -> name.equals(file.getFilename())));
    }
  }

  @Test
  public void files_FilenameColumnComparator() {
    Comparator<SubmissionFile> filenameComparator = test(view.files).getColumn(FILENAME)
        .getComparator(SortDirection.ASCENDING);
    assertEquals(0, filenameComparator.compare(new SubmissionFile("éê"), new SubmissionFile("ee")));
    assertTrue(filenameComparator.compare(new SubmissionFile("a"), new SubmissionFile("e")) < 0);
    assertTrue(filenameComparator.compare(new SubmissionFile("a"), new SubmissionFile("é")) < 0);
    assertTrue(filenameComparator.compare(new SubmissionFile("e"), new SubmissionFile("a")) > 0);
    assertTrue(filenameComparator.compare(new SubmissionFile("é"), new SubmissionFile("a")) > 0);
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void addFile() {
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    SubmissionFile file = files.get(0);
    view.addFile(file.getFilename(), new ByteArrayInputStream(file.getContent()));
    List<SubmissionFile> files = items(view.files);
    assertEquals(1, files.size());
    assertEquals(file.getFilename(), files.get(0).getFilename());
    assertArrayEquals(file.getContent(), files.get(0).getContent());
    verify(filesDataProviderListener).onDataChange(any());
  }

  @Test
  public void addFile_IoException() {
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    SubmissionFile file = files.get(0);
    InputStream input = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("test");
      }
    };
    view.addFile(file.getFilename(), input);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FILES_IOEXCEPTION, file.getFilename()),
        test(notification).getText());
    assertTrue(items(view.files).isEmpty());
    verify(filesDataProviderListener, never()).onDataChange(any());
  }

  @Test
  public void addFile_OverMaximumCount() {
    SubmissionFile file = files.get(0);
    IntStream.range(0, MAXIMUM_FILES_COUNT).forEach(
        i -> view.addFile(file.getFilename() + i, new ByteArrayInputStream(file.getContent())));
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    view.addFile(file.getFilename() + MAXIMUM_FILES_COUNT,
        new ByteArrayInputStream(file.getContent()));
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FILES_OVER_MAXIMUM, MAXIMUM_FILES_COUNT),
        test(notification).getText());
    List<SubmissionFile> files = items(view.files);
    assertEquals(MAXIMUM_FILES_COUNT, files.size());
    for (int i = 0; i < MAXIMUM_FILES_COUNT; i++) {
      assertEquals(file.getFilename() + i, files.get(i).getFilename());
    }
    verify(filesDataProviderListener, never()).onDataChange(any());
  }

  @Test
  public void removeFile_New() {
    files.forEach(
        file -> view.addFile(file.getFilename(), new ByteArrayInputStream(file.getContent())));
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    view.removeFile(items(view.files).get(0));
    List<SubmissionFile> files = items(view.files);
    assertEquals(this.files.size() - 1, files.size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(this.files.get(i + 1).getFilename(), files.get(i).getFilename());
      assertArrayEquals(this.files.get(i + 1).getContent(), files.get(i).getContent());
    }
    verify(filesDataProviderListener).onDataChange(any());
  }

  @Test
  public void removeFile_Existing() {
    view.setParameter(beforeEvent, 1L);
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    view.removeFile(submission.getFiles().get(0));
    List<SubmissionFile> files = items(view.files);
    assertEquals(submission.getFiles().size() - 1, files.size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(submission.getFiles().get(i + 1).getFilename(), files.get(i).getFilename());
      assertArrayEquals(submission.getFiles().get(i + 1).getContent(), files.get(i).getContent());
    }
    verify(filesDataProviderListener).onDataChange(any());
  }

  @Test
  public void save_LcmsmsValidationFail() {
    view.service.setSelectedTab(view.lcmsms);

    view.save();

    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_SmallMoleculeValidationFail() {
    view.service.setSelectedTab(view.smallMolecule);

    view.save();

    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_IntactProteinValidationFail() {
    view.service.setSelectedTab(view.intactProtein);

    view.save();

    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_NewLcmsms() {
    view.service.setSelectedTab(view.lcmsms);
    Submission submission = lcmsmsSubmission();
    setFields(submission);

    view.save();

    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission saved = submissionCaptor.getValue();
    assertEquals(0, saved.getId());
    assertEquals(Service.LC_MS_MS, saved.getService());
    assertEquals(submission.getExperiment(), saved.getExperiment());
    assertEquals(submission.getGoal(), saved.getGoal());
    assertEquals(submission.getTaxonomy(), saved.getTaxonomy());
    assertEquals(submission.getProtein(), saved.getProtein());
    assertEquals(submission.getPostTranslationModification(),
        saved.getPostTranslationModification());
    assertEquals(submission.getSamples().size(), saved.getSamples().size());
    for (int i = 0; i < submission.getSamples().size(); i++) {
      assertEquals(submission.getSamples().get(i).getType(), saved.getSamples().get(i).getType());
      assertEquals(submission.getSamples().get(i).getName(), saved.getSamples().get(i).getName());
      assertEquals(submission.getSamples().get(i).getQuantity(),
          saved.getSamples().get(i).getQuantity());
      assertEquals(submission.getSamples().get(i).getVolume(),
          saved.getSamples().get(i).getVolume());
      assertEquals(submission.getSamples().get(i).getMolecularWeight(),
          saved.getSamples().get(i).getMolecularWeight());
    }
    assertEquals(submission.getSeparation(), saved.getSeparation());
    assertEquals(submission.getThickness(), saved.getThickness());
    assertEquals(submission.getColoration(), saved.getColoration());
    assertEquals(submission.getOtherColoration(), saved.getOtherColoration());
    assertEquals(submission.getDevelopmentTime(), saved.getDevelopmentTime());
    assertEquals(submission.isDecoloration(), saved.isDecoloration());
    assertEquals(submission.getWeightMarkerQuantity(), saved.getWeightMarkerQuantity());
    assertEquals(submission.getProteinQuantity(), saved.getProteinQuantity());
    assertEquals(submission.getDigestion(), saved.getDigestion());
    assertEquals(submission.getUsedDigestion(), saved.getUsedDigestion());
    assertEquals(submission.getOtherDigestion(), saved.getOtherDigestion());
    assertEquals(submission.getProteinContent(), saved.getProteinContent());
    assertEquals(submission.getIdentification(), saved.getIdentification());
    assertEquals(submission.getIdentificationLink(), saved.getIdentificationLink());
    assertEquals(submission.getQuantification(), saved.getQuantification());
    assertEquals(submission.getQuantificationComment(), saved.getQuantificationComment());
    assertEquals(comment, saved.getComment());
    assertEquals(files.size(), saved.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void save_UpdateLcmsms() {
    Submission database = repository.findById(1L).orElseThrow();
    entityManager.detach(database);
    when(service.get(anyLong())).thenReturn(Optional.of(database));
    final List<SubmissionFile> oldFiles = database.getFiles();
    view.setParameter(beforeEvent, 1L);
    view.service.setSelectedTab(view.lcmsms);
    Submission submission = lcmsmsSubmission();
    setFields(submission);

    view.save();

    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission saved = submissionCaptor.getValue();
    assertEquals(1L, saved.getId());
    assertEquals(Service.LC_MS_MS, saved.getService());
    assertEquals(submission.getExperiment(), saved.getExperiment());
    assertEquals(submission.getGoal(), saved.getGoal());
    assertEquals(submission.getTaxonomy(), saved.getTaxonomy());
    assertEquals(submission.getProtein(), saved.getProtein());
    assertEquals(submission.getPostTranslationModification(),
        saved.getPostTranslationModification());
    assertEquals(submission.getSamples().size(), saved.getSamples().size());
    for (int i = 0; i < submission.getSamples().size(); i++) {
      assertEquals(submission.getSamples().get(i).getType(), saved.getSamples().get(i).getType());
      assertEquals(submission.getSamples().get(i).getName(), saved.getSamples().get(i).getName());
      assertEquals(submission.getSamples().get(i).getQuantity(),
          saved.getSamples().get(i).getQuantity());
      assertEquals(submission.getSamples().get(i).getVolume(),
          saved.getSamples().get(i).getVolume());
      assertEquals(submission.getSamples().get(i).getMolecularWeight(),
          saved.getSamples().get(i).getMolecularWeight());
    }
    assertEquals(submission.getSeparation(), saved.getSeparation());
    assertEquals(submission.getThickness(), saved.getThickness());
    assertEquals(submission.getColoration(), saved.getColoration());
    assertEquals(submission.getOtherColoration(), saved.getOtherColoration());
    assertEquals(submission.getDevelopmentTime(), saved.getDevelopmentTime());
    assertEquals(submission.isDecoloration(), saved.isDecoloration());
    assertEquals(submission.getWeightMarkerQuantity(), saved.getWeightMarkerQuantity());
    assertEquals(submission.getProteinQuantity(), saved.getProteinQuantity());
    assertEquals(submission.getDigestion(), saved.getDigestion());
    assertEquals(submission.getUsedDigestion(), saved.getUsedDigestion());
    assertEquals(submission.getOtherDigestion(), saved.getOtherDigestion());
    assertEquals(submission.getProteinContent(), saved.getProteinContent());
    assertEquals(submission.getIdentification(), saved.getIdentification());
    assertEquals(submission.getIdentificationLink(), saved.getIdentificationLink());
    assertEquals(submission.getQuantification(), saved.getQuantification());
    assertEquals(submission.getQuantificationComment(), saved.getQuantificationComment());
    assertEquals(Service.LC_MS_MS, saved.getService());
    assertEquals(comment, saved.getComment());
    assertEquals(oldFiles.size() + files.size(), saved.getFiles().size());
    for (int i = 0; i < saved.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  public void save_NewSmallMolecule() {
    view.service.setSelectedTab(view.smallMolecule);
    Submission submission = smallMoleculeSubmission();
    setFields(submission);

    view.save();

    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission saved = submissionCaptor.getValue();
    assertEquals(0, saved.getId());
    assertEquals(Service.SMALL_MOLECULE, saved.getService());
    assertEquals(1, saved.getSamples().size());
    assertEquals(submission.getSamples().get(0).getType(), saved.getSamples().get(0).getType());
    assertEquals(submission.getSamples().get(0).getName(), saved.getSamples().get(0).getName());
    assertEquals(submission.getSolutionSolvent(), saved.getSolutionSolvent());
    assertEquals(submission.getFormula(), saved.getFormula());
    assertEquals(submission.getMonoisotopicMass(), saved.getMonoisotopicMass());
    assertEquals(submission.getAverageMass(), saved.getAverageMass());
    assertEquals(submission.getToxicity(), saved.getToxicity());
    assertEquals(submission.isLightSensitive(), saved.isLightSensitive());
    assertEquals(submission.getStorageTemperature(), saved.getStorageTemperature());
    assertEquals(submission.isHighResolution(), saved.isHighResolution());
    assertEquals(submission.getSolvents().size(), saved.getSolvents().size());
    for (Solvent solvent : submission.getSolvents()) {
      assertTrue(saved.getSolvents().contains(solvent), solvent.name());
    }
    assertEquals(submission.getOtherSolvent(), saved.getOtherSolvent());
    assertEquals(comment, saved.getComment());
    assertEquals(files.size(), saved.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void save_UpdateSmallMolecule() {
    Submission database = repository.findById(1L).orElseThrow();
    entityManager.detach(database);
    when(service.get(anyLong())).thenReturn(Optional.of(database));
    final List<SubmissionFile> oldFiles = database.getFiles();
    view.setParameter(beforeEvent, 1L);
    view.service.setSelectedTab(view.smallMolecule);
    Submission submission = smallMoleculeSubmission();
    setFields(submission);

    view.save();

    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission saved = submissionCaptor.getValue();
    assertEquals(1L, saved.getId());
    assertEquals(Service.SMALL_MOLECULE, saved.getService());
    assertEquals(1, saved.getSamples().size());
    assertEquals(submission.getSamples().get(0).getType(), saved.getSamples().get(0).getType());
    assertEquals(submission.getSamples().get(0).getName(), saved.getSamples().get(0).getName());
    assertEquals(submission.getSolutionSolvent(), saved.getSolutionSolvent());
    assertEquals(submission.getFormula(), saved.getFormula());
    assertEquals(submission.getMonoisotopicMass(), saved.getMonoisotopicMass());
    assertEquals(submission.getAverageMass(), saved.getAverageMass());
    assertEquals(submission.getToxicity(), saved.getToxicity());
    assertEquals(submission.isLightSensitive(), saved.isLightSensitive());
    assertEquals(submission.getStorageTemperature(), saved.getStorageTemperature());
    assertEquals(submission.isHighResolution(), saved.isHighResolution());
    assertEquals(submission.getSolvents().size(), saved.getSolvents().size());
    for (Solvent solvent : submission.getSolvents()) {
      assertTrue(saved.getSolvents().contains(solvent), solvent.name());
    }
    assertEquals(submission.getOtherSolvent(), saved.getOtherSolvent());
    assertEquals(comment, saved.getComment());
    assertEquals(oldFiles.size() + files.size(), saved.getFiles().size());
    for (int i = 0; i < saved.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  public void save_NewIntactProtein() {
    view.service.setSelectedTab(view.intactProtein);
    Submission submission = intactProteinSubmission();
    setFields(submission);

    view.save();

    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission saved = submissionCaptor.getValue();
    assertEquals(0, saved.getId());
    assertEquals(Service.INTACT_PROTEIN, saved.getService());
    assertEquals(submission.getExperiment(), saved.getExperiment());
    assertEquals(submission.getGoal(), saved.getGoal());
    assertEquals(submission.getTaxonomy(), saved.getTaxonomy());
    assertEquals(submission.getProtein(), saved.getProtein());
    assertEquals(submission.getPostTranslationModification(),
        saved.getPostTranslationModification());
    assertEquals(submission.getSamples().size(), saved.getSamples().size());
    for (int i = 0; i < submission.getSamples().size(); i++) {
      assertEquals(submission.getSamples().get(i).getType(), saved.getSamples().get(i).getType());
      assertEquals(submission.getSamples().get(i).getName(), saved.getSamples().get(i).getName());
      assertEquals(submission.getSamples().get(i).getQuantity(),
          saved.getSamples().get(i).getQuantity());
      assertEquals(submission.getSamples().get(i).getVolume(),
          saved.getSamples().get(i).getVolume());
      assertEquals(submission.getSamples().get(i).getMolecularWeight(),
          saved.getSamples().get(i).getMolecularWeight());
    }
    assertEquals(submission.getInjectionType(), saved.getInjectionType());
    assertEquals(submission.getSource(), saved.getSource());
    assertEquals(submission.getInstrument(), saved.getInstrument());
    assertEquals(comment, saved.getComment());
    assertEquals(files.size(), saved.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void save_UpdateIntactProtein() {
    Submission database = repository.findById(1L).orElseThrow();
    entityManager.detach(database);
    when(service.get(anyLong())).thenReturn(Optional.of(database));
    final List<SubmissionFile> oldFiles = database.getFiles();
    view.setParameter(beforeEvent, 1L);
    view.service.setSelectedTab(view.intactProtein);
    Submission submission = intactProteinSubmission();
    setFields(submission);

    view.save();

    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission saved = submissionCaptor.getValue();
    assertEquals(1L, saved.getId());
    assertEquals(Service.INTACT_PROTEIN, saved.getService());
    assertEquals(submission.getExperiment(),
        view.intactProteinSubmissionForm.experiment.getValue());
    assertEquals(submission.getExperiment(), saved.getExperiment());
    assertEquals(submission.getGoal(), saved.getGoal());
    assertEquals(submission.getTaxonomy(), saved.getTaxonomy());
    assertEquals(submission.getProtein(), saved.getProtein());
    assertEquals(submission.getPostTranslationModification(),
        saved.getPostTranslationModification());
    assertEquals(submission.getSamples().size(), saved.getSamples().size());
    for (int i = 0; i < submission.getSamples().size(); i++) {
      assertEquals(submission.getSamples().get(i).getType(), saved.getSamples().get(i).getType());
      assertEquals(submission.getSamples().get(i).getName(), saved.getSamples().get(i).getName());
      assertEquals(submission.getSamples().get(i).getQuantity(),
          saved.getSamples().get(i).getQuantity());
      assertEquals(submission.getSamples().get(i).getVolume(),
          saved.getSamples().get(i).getVolume());
      assertEquals(submission.getSamples().get(i).getMolecularWeight(),
          saved.getSamples().get(i).getMolecularWeight());
    }
    assertEquals(submission.getInjectionType(), saved.getInjectionType());
    assertEquals(submission.getSource(), saved.getSource());
    assertEquals(submission.getInstrument(), saved.getInstrument());
    assertEquals(comment, saved.getComment());
    assertEquals(oldFiles.size() + files.size(), saved.getFiles().size());
    for (int i = 0; i < saved.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), saved.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), saved.getFiles().get(i).getContent());
    }
    $(SubmissionsView.class).id(SubmissionsView.ID);
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, saved.getExperiment()),
        test(notification).getText());
  }

  @Test
  public void setParameter() {
    String comment = "my test comment";
    Submission submission = repository.findById(163L).orElseThrow();
    submission.setComment(comment);
    submission.setSubmissionDate(LocalDateTime.now().minusMinutes(1));
    when(service.get(anyLong())).thenReturn(Optional.of(submission));

    view.setParameter(beforeEvent, 163L);

    verify(service).get(163L);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 1, submission.getExperiment()),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertFalse(view.comment.isReadOnly());
    assertTrue(view.upload.isVisible());
    assertTrue(view.files.getColumnByKey(REMOVE).isVisible());
    assertTrue(view.save.isEnabled());
    assertEquals(submission, view.lcmsmsSubmissionForm.getSubmission());
    assertEquals(submission, view.smallMoleculeSubmissionForm.getSubmission());
    assertEquals(submission, view.intactProteinSubmissionForm.getSubmission());
    assertEquals(comment, view.comment.getValue());
    List<SubmissionFile> files = items(view.files);
    assertEquals(submission.getFiles().size(), files.size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(submission.getFiles().get(i).getFilename(), files.get(i).getFilename());
      assertArrayEquals(submission.getFiles().get(i).getContent(), files.get(i).getContent());
    }
  }

  @Test
  public void setParameter_ReadOnly() {
    Submission submission = repository.findById(35L).orElseThrow();
    when(service.get(anyLong())).thenReturn(Optional.of(submission));

    view.setParameter(beforeEvent, 35L);

    verify(service).get(35L);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 1, submission.getExperiment()),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertTrue(view.comment.isReadOnly());
    assertFalse(view.upload.isVisible());
    assertFalse(view.files.getColumnByKey(REMOVE).isVisible());
    assertFalse(view.save.isEnabled());
    assertEquals(submission, view.lcmsmsSubmissionForm.getSubmission());
    assertEquals(submission, view.smallMoleculeSubmissionForm.getSubmission());
    assertEquals(submission, view.intactProteinSubmissionForm.getSubmission());
    assertEquals(Objects.toString(submission.getComment(), ""), view.comment.getValue());
    List<SubmissionFile> files = items(view.files);
    assertEquals(submission.getFiles().size(), files.size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(submission.getFiles().get(i).getFilename(), files.get(i).getFilename());
      assertArrayEquals(submission.getFiles().get(i).getContent(), files.get(i).getContent());
    }
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(service.get(anyLong())).thenReturn(Optional.empty());

    view.setParameter(beforeEvent, 2L);

    verify(service).get(2L);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0, ""),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertFalse(view.comment.isReadOnly());
    assertTrue(view.upload.isVisible());
    assertTrue(view.files.getColumnByKey(REMOVE).isVisible());
    assertTrue(view.save.isEnabled());
    Submission submission = view.getSubmission();
    assertEquals(submission, view.lcmsmsSubmissionForm.getSubmission());
    assertEquals(submission, view.smallMoleculeSubmissionForm.getSubmission());
    assertEquals(submission, view.intactProteinSubmissionForm.getSubmission());
    assertEquals(0, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals(ProteinContent.SMALL, submission.getProteinContent());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(ProteinIdentification.REFSEQ, submission.getIdentification());
    assertEquals(1, submission.getSamples().size());
    assertEquals(SampleType.SOLUTION, submission.getSamples().get(0).getType());
    assertNull(submission.getComment());
    assertEquals("", view.comment.getValue());
  }

  @Test
  public void setParameter_Null() {
    view.setParameter(beforeEvent, null);

    verify(service, never()).get(anyLong());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0, ""),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertFalse(view.comment.isReadOnly());
    assertTrue(view.upload.isVisible());
    assertTrue(view.files.getColumnByKey(REMOVE).isVisible());
    assertTrue(view.save.isEnabled());
    Submission submission = view.getSubmission();
    assertEquals(submission, view.lcmsmsSubmissionForm.getSubmission());
    assertEquals(submission, view.smallMoleculeSubmissionForm.getSubmission());
    assertEquals(submission, view.intactProteinSubmissionForm.getSubmission());
    assertEquals(0, submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals(StorageTemperature.MEDIUM, submission.getStorageTemperature());
    assertEquals(GelSeparation.ONE_DIMENSION, submission.getSeparation());
    assertEquals(GelThickness.ONE, submission.getThickness());
    assertEquals(ProteolyticDigestion.TRYPSIN, submission.getDigestion());
    assertEquals(ProteinContent.SMALL, submission.getProteinContent());
    assertEquals(InjectionType.LC_MS, submission.getInjectionType());
    assertEquals(MassDetectionInstrumentSource.ESI, submission.getSource());
    assertEquals(ProteinIdentification.REFSEQ, submission.getIdentification());
    assertEquals(1, submission.getSamples().size());
    assertEquals(SampleType.SOLUTION, submission.getSamples().get(0).getType());
    assertNull(submission.getComment());
    assertEquals("", view.comment.getValue());
  }
}
