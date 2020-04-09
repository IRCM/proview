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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.PRIMARY;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.UPLOAD;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionFileProperties.FILENAME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_SIZE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.REMOVE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.fireEvent;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.web.UploadInternationalization.englishUploadI18N;
import static ca.qc.ircm.proview.web.UploadInternationalization.frenchUploadI18N;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import java.io.ByteArrayInputStream;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionViewTest extends AbstractViewTestCase {
  private SubmissionView view;
  @Mock
  private SubmissionViewPresenter presenter;
  private LcmsmsSubmissionForm lcmsmsSubmissionForm =
      new LcmsmsSubmissionForm(mock(LcmsmsSubmissionFormPresenter.class));
  private SmallMoleculeSubmissionForm smallMoleculeSubmissionForm =
      new SmallMoleculeSubmissionForm(mock(SmallMoleculeSubmissionFormPresenter.class));
  private IntactProteinSubmissionForm intactProteinSubmissionForm =
      new IntactProteinSubmissionForm(mock(IntactProteinSubmissionFormPresenter.class));
  @Mock
  private BeforeEvent beforeEvent;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Anchor, SubmissionFile>> anchorRendererCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Button, SubmissionFile>> buttonRendererCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SubmissionView.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private List<SubmissionFile> files;
  private Random random = new Random();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SubmissionView(presenter, lcmsmsSubmissionForm, smallMoleculeSubmissionForm,
        intactProteinSubmissionForm);
    view.init();
    files = IntStream.range(0, 3).mapToObj(i -> {
      SubmissionFile file = new SubmissionFile();
      file.setFilename(RandomStringUtils.randomAlphanumeric(10));
      byte[] content = new byte[1024];
      random.nextBytes(content);
      file.setContent(content);
      return file;
    }).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element element = view.files.getElement();
    view.files = mock(Grid.class);
    when(view.files.getElement()).thenReturn(element);
    view.filename = mock(Column.class);
    when(view.files.addColumn(any(ComponentRenderer.class), eq(FILENAME)))
        .thenReturn(view.filename);
    when(view.filename.setKey(any())).thenReturn(view.filename);
    when(view.filename.setHeader(any(String.class))).thenReturn(view.filename);
    view.remove = mock(Column.class);
    when(view.files.addColumn(any(ComponentRenderer.class), eq(REMOVE))).thenReturn(view.remove);
    when(view.remove.setKey(any())).thenReturn(view.remove);
    when(view.remove.setHeader(any(String.class))).thenReturn(view.remove);
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SERVICE, view.service.getId().orElse(""));
    assertEquals(LC_MS_MS.name(), view.lcmsms.getId().orElse(""));
    assertEquals(SMALL_MOLECULE.name(), view.smallMolecule.getId().orElse(""));
    assertEquals(INTACT_PROTEIN.name(), view.intactProtein.getId().orElse(""));
    assertEquals(COMMENT, view.comment.getId().orElse(""));
    assertEquals(UPLOAD, view.upload.getId().orElse(""));
    assertEquals(FILES, view.files.getId().orElse(""));
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.getThemeName().contains(PRIMARY));
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void labels() {
    view.filename = mock(Column.class);
    view.remove = mock(Column.class);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
    validateEquals(englishUploadI18N(), view.upload.getI18n());
    verify(view.filename).setHeader(resources.message(FILENAME));
    verify(view.remove).setHeader(resources.message(REMOVE));
    assertEquals(webResources.message(SAVE), view.save.getText());
    verify(presenter).localeChange(locale);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SubmissionView.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.filename = mock(Column.class);
    view.remove = mock(Column.class);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
    validateEquals(frenchUploadI18N(), view.upload.getI18n());
    verify(view.filename).setHeader(resources.message(FILENAME));
    verify(view.remove).setHeader(resources.message(REMOVE));
    assertEquals(webResources.message(SAVE), view.save.getText());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void services() {
    assertEquals(3, view.service.getComponentCount());
    assertEquals(view.lcmsms, view.service.getComponentAt(0));
    assertEquals(view.smallMolecule, view.service.getComponentAt(1));
    assertEquals(view.intactProtein, view.service.getComponentAt(2));
  }

  @Test
  public void upload() {
    assertEquals(MAXIMUM_FILES_COUNT, view.upload.getMaxFiles());
    assertEquals(MAXIMUM_FILES_SIZE, view.upload.getMaxFileSize());
  }

  @Test
  public void upload_File() {
    view.uploadBuffer = mock(MultiFileMemoryBuffer.class);
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
    when(view.uploadBuffer.getInputStream(any())).thenReturn(input);
    String filename = "test_file.txt";
    String mimeType = "text/plain";
    long filesize = 84325;
    SucceededEvent event = new SucceededEvent(view.upload, filename, mimeType, filesize);
    fireEvent(view.upload, event);
    verify(presenter).addFile(filename, input, locale);
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
    view = new SubmissionView(presenter, lcmsmsSubmissionForm, smallMoleculeSubmissionForm,
        intactProteinSubmissionForm);
    mockColumns();
    view.init();
    verify(view.files).addColumn(anchorRendererCaptor.capture(), eq(FILENAME));
    ComponentRenderer<Anchor, SubmissionFile> anchorRenderer = anchorRendererCaptor.getValue();
    for (SubmissionFile file : files) {
      Anchor anchor = anchorRenderer.createComponent(file);
      assertEquals(file.getFilename(), anchor.getText());
      assertEquals(Optional.of("_blank"), anchor.getTarget());
      assertTrue(anchor.getHref().startsWith("VAADIN/dynamic/resource"));
    }
    verify(view.files).addColumn(buttonRendererCaptor.capture(), eq(REMOVE));
    ComponentRenderer<Button, SubmissionFile> buttonRenderer = buttonRendererCaptor.getValue();
    for (SubmissionFile file : files) {
      Button button = buttonRenderer.createComponent(file);
      assertEquals("", button.getText());
      validateIcon(VaadinIcon.TRASH.create(), button.getIcon());
      button.click();
      verify(presenter).removeFile(file);
    }
  }

  @Test
  public void save() {
    view.save.click();
    verify(presenter).save(locale);
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    view.setParameter(beforeEvent, 12L);
    verify(presenter).setParameter(12L);
  }

  @Test
  public void setParameter_Null() {
    view.setParameter(beforeEvent, null);
    verify(presenter).setParameter(null);
  }
}
