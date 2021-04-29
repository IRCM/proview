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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_IOEXCEPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_OVER_MAXIMUM;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.DataProviderListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionViewPresenterTest extends AbstractViewTestCase {
  @Autowired
  private SubmissionViewPresenter presenter;
  @Mock
  private SubmissionView view;
  @MockBean
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  @Mock
  private DataProviderListener<SubmissionFile> filesDataProviderListener;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SubmissionView.class, locale);
  private Submission submission;
  private String experiment = "my test experiment";
  private List<SubmissionFile> files;
  private String comment = "comment first line\nSecond line";
  private Random random = new Random();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    view.header = new H2();
    view.service = new Tabs();
    view.lcmsms = new Tab();
    view.smallMolecule = new Tab();
    view.intactProtein = new Tab();
    view.service.add(view.lcmsms, view.smallMolecule, view.intactProtein);
    view.files = new Grid<>();
    view.comment = new TextArea();
    view.lcmsmsSubmissionForm = mock(LcmsmsSubmissionForm.class);
    view.smallMoleculeSubmissionForm = mock(SmallMoleculeSubmissionForm.class);
    view.intactProteinSubmissionForm = mock(IntactProteinSubmissionForm.class);
    submission = repository.findById(1L).orElse(null);
    when(service.get(any())).thenReturn(Optional.of(submission));
    presenter.init(view);
    presenter.localeChange(locale);
    files = IntStream.range(0, 2).mapToObj(i -> {
      SubmissionFile file = new SubmissionFile();
      file.setFilename(RandomStringUtils.randomAlphanumeric(10));
      byte[] content = new byte[1024];
      random.nextBytes(content);
      file.setContent(content);
      return file;
    }).collect(Collectors.toList());
  }

  private void setFields() {
    view.comment.setValue(comment);
    files.forEach(file -> presenter.addFile(file.getFilename(),
        new ByteArrayInputStream(file.getContent()), locale));
  }

  @Test
  public void addFile() {
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    SubmissionFile file = files.get(0);
    presenter.addFile(file.getFilename(), new ByteArrayInputStream(file.getContent()), locale);
    List<SubmissionFile> files = items(view.files);
    assertEquals(1, files.size());
    assertEquals(file.getFilename(), files.get(0).getFilename());
    assertArrayEquals(file.getContent(), files.get(0).getContent());
    verify(filesDataProviderListener).onDataChange(any());
  }

  @Test
  public void addFile_IoException() throws IOException {
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    SubmissionFile file = files.get(0);
    InputStream input = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("test");
      }
    };
    presenter.addFile(file.getFilename(), input, locale);
    verify(view).showNotification(resources.message(FILES_IOEXCEPTION, file.getFilename()));
    assertTrue(items(view.files).isEmpty());
    verify(filesDataProviderListener, never()).onDataChange(any());
  }

  @Test
  public void addFile_OverMaximumCount() {
    SubmissionFile file = files.get(0);
    IntStream.range(0, MAXIMUM_FILES_COUNT).forEach(i -> {
      presenter.addFile(file.getFilename() + i, new ByteArrayInputStream(file.getContent()),
          locale);
    });
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    presenter.addFile(file.getFilename() + MAXIMUM_FILES_COUNT,
        new ByteArrayInputStream(file.getContent()), locale);
    verify(view).showNotification(resources.message(FILES_OVER_MAXIMUM, MAXIMUM_FILES_COUNT));
    List<SubmissionFile> files = items(view.files);
    assertEquals(MAXIMUM_FILES_COUNT, files.size());
    for (int i = 0; i < MAXIMUM_FILES_COUNT; i++) {
      assertEquals(file.getFilename() + i, files.get(i).getFilename());
    }
    verify(filesDataProviderListener, never()).onDataChange(any());
  }

  @Test
  public void removeFile_New() {
    files.forEach(file -> presenter.addFile(file.getFilename(),
        new ByteArrayInputStream(file.getContent()), locale));
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    presenter.removeFile(items(view.files).get(0));
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
    presenter.setParameter(1L);
    view.files.getDataProvider().addDataProviderListener(filesDataProviderListener);
    presenter.removeFile(submission.getFiles().get(0));
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
    when(view.smallMoleculeSubmissionForm.isValid()).thenReturn(true);
    when(view.intactProteinSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.lcmsmsSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_LcmsmsAndOtherValidationsFail() {
    view.service.setSelectedTab(view.lcmsms);
    when(view.lcmsmsSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.lcmsmsSubmissionForm).isValid();
    verify(service).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_SmallMoleculeValidationFail() {
    view.service.setSelectedTab(view.smallMolecule);
    when(view.lcmsmsSubmissionForm.isValid()).thenReturn(true);
    when(view.intactProteinSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.smallMoleculeSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_SmallMoleculeAndOtherValidationsFail() {
    view.service.setSelectedTab(view.smallMolecule);
    when(view.smallMoleculeSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.smallMoleculeSubmissionForm).isValid();
    verify(service).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_IntactProteinValidationFail() {
    view.service.setSelectedTab(view.intactProtein);
    when(view.lcmsmsSubmissionForm.isValid()).thenReturn(true);
    when(view.smallMoleculeSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.intactProteinSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_IntactProteinAndOtherValidationsFail() {
    view.service.setSelectedTab(view.intactProtein);
    when(view.intactProteinSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.intactProteinSubmissionForm).isValid();
    verify(service).insert(any());
    verify(service, never()).update(any(), any());
  }

  @Test
  public void save_NewLcmsms() {
    view.service.setSelectedTab(view.lcmsms);
    setFields();
    when(view.lcmsmsSubmissionForm.isValid()).thenReturn(true);
    doAnswer(i -> {
      ((Submission) i.getArgument(0)).setExperiment(experiment);
      return null;
    }).when(service).insert(any());

    presenter.save(locale);

    verify(view.lcmsmsSubmissionForm).isValid();
    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission submission = submissionCaptor.getValue();
    assertNull(submission.getId());
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(files.size(), submission.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void save_UpdateLcmsms() {
    final List<SubmissionFile> oldFiles = submission.getFiles();
    presenter.setParameter(34L);
    view.service.setSelectedTab(view.lcmsms);
    setFields();
    when(view.lcmsmsSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.lcmsmsSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission submission = submissionCaptor.getValue();
    assertEquals(Service.LC_MS_MS, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(oldFiles.size() + files.size(), submission.getFiles().size());
    for (int i = 0; i < submission.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void save_NewSmallMolecule() {
    view.service.setSelectedTab(view.smallMolecule);
    setFields();
    when(view.smallMoleculeSubmissionForm.isValid()).thenReturn(true);
    doAnswer(i -> {
      ((Submission) i.getArgument(0)).setExperiment(experiment);
      return null;
    }).when(service).insert(any());

    presenter.save(locale);

    verify(view.smallMoleculeSubmissionForm).isValid();
    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission submission = submissionCaptor.getValue();
    assertNull(submission.getId());
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(files.size(), submission.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void save_UpdateSmallMolecule() {
    final List<SubmissionFile> oldFiles = submission.getFiles();
    presenter.setParameter(34L);
    view.service.setSelectedTab(view.smallMolecule);
    setFields();
    when(view.smallMoleculeSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.smallMoleculeSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission submission = submissionCaptor.getValue();
    assertEquals(Service.SMALL_MOLECULE, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(oldFiles.size() + files.size(), submission.getFiles().size());
    for (int i = 0; i < submission.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void save_NewIntactProtein() {
    view.service.setSelectedTab(view.intactProtein);
    setFields();
    when(view.intactProteinSubmissionForm.isValid()).thenReturn(true);
    doAnswer(i -> {
      ((Submission) i.getArgument(0)).setExperiment(experiment);
      return null;
    }).when(service).insert(any());

    presenter.save(locale);

    verify(view.intactProteinSubmissionForm).isValid();
    verify(service).insert(submissionCaptor.capture());
    verify(service, never()).update(any(), any());
    Submission submission = submissionCaptor.getValue();
    assertNull(submission.getId());
    assertEquals(Service.INTACT_PROTEIN, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(files.size(), submission.getFiles().size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(files.get(i).getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(files.get(i).getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void save_UpdateIntactProtein() {
    final List<SubmissionFile> oldFiles = submission.getFiles();
    presenter.setParameter(34L);
    view.service.setSelectedTab(view.intactProtein);
    setFields();
    when(view.intactProteinSubmissionForm.isValid()).thenReturn(true);

    presenter.save(locale);

    verify(view.intactProteinSubmissionForm).isValid();
    verify(service, never()).insert(any());
    verify(service).update(submissionCaptor.capture(), eq(null));
    Submission submission = submissionCaptor.getValue();
    assertEquals(Service.INTACT_PROTEIN, submission.getService());
    assertEquals(comment, submission.getComment());
    assertEquals(oldFiles.size() + files.size(), submission.getFiles().size());
    for (int i = 0; i < submission.getFiles().size(); i++) {
      SubmissionFile expected =
          i < oldFiles.size() ? oldFiles.get(i) : files.get(i - oldFiles.size());
      assertEquals(expected.getFilename(), submission.getFiles().get(i).getFilename());
      assertArrayEquals(expected.getContent(), submission.getFiles().get(i).getContent());
    }
    verify(ui).navigate(SubmissionsView.class);
    verify(view).showNotification(resources.message(SAVED, submission.getExperiment()));
  }

  @Test
  public void setParameter() {
    String comment = "my test comment";
    submission.setComment(comment);

    presenter.setParameter(34L);

    verify(service).get(34L);
    verify(view.lcmsmsSubmissionForm).setSubmission(submission);
    verify(view.smallMoleculeSubmissionForm).setSubmission(submission);
    verify(view.intactProteinSubmissionForm).setSubmission(submission);
    assertEquals(comment, view.comment.getValue());
    List<SubmissionFile> files = items(view.files);
    assertEquals(submission.getFiles().size(), files.size());
    for (int i = 0; i < files.size(); i++) {
      assertEquals(submission.getFiles().get(i).getFilename(), files.get(i).getFilename());
      assertArrayEquals(submission.getFiles().get(i).getContent(), files.get(i).getContent());
    }
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(service.get(any())).thenReturn(Optional.empty());

    presenter.setParameter(2L);

    verify(service).get(2L);
    verify(view.lcmsmsSubmissionForm, atLeastOnce()).setSubmission(submissionCaptor.capture());
    Submission lcmsms = submissionCaptor.getValue();
    verify(view.smallMoleculeSubmissionForm, atLeastOnce())
        .setSubmission(submissionCaptor.capture());
    Submission smallMolecule = submissionCaptor.getValue();
    verify(view.intactProteinSubmissionForm, atLeastOnce())
        .setSubmission(submissionCaptor.capture());
    Submission intactProtein = submissionCaptor.getValue();
    assertSame(lcmsms, smallMolecule);
    assertSame(lcmsms, intactProtein);
    Submission submission = lcmsms;
    assertNull(submission.getId());
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
    presenter.setParameter(null);

    verifyNoInteractions(service);
    verify(view.lcmsmsSubmissionForm).setSubmission(submissionCaptor.capture());
    verify(view.smallMoleculeSubmissionForm).setSubmission(submissionCaptor.capture());
    verify(view.intactProteinSubmissionForm).setSubmission(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    for (Submission same : submissionCaptor.getAllValues()) {
      assertSame(submission, same);
    }
    assertNull(submission.getId());
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
