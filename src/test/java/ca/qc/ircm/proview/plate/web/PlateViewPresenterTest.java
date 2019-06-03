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

package ca.qc.ircm.proview.plate.web;

import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.HEADER;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PLATE_PANEL;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PRINT;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PRINT_MIME;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.themes.ValoTheme;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateViewPresenterTest {
  @Inject
  private PlateViewPresenter presenter;
  @Inject
  private PlateRepository repository;
  @MockBean
  private PlateService plateService;
  @Mock
  private PlateView view;
  @Mock
  private PlateComponent plateComponent;
  @Value("${spring.application.name}")
  private String applicationName;
  private PlateViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlateView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Plate> plates;
  private String platePrint;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    design = new PlateViewDesign();
    view.design = design;
    view.plateComponent = plateComponent;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(plateService.get(any(Long.class)))
        .thenAnswer(i -> repository.findById(i.getArgument(0)).orElse(null));
    plates = new ArrayList<>();
    plates.add(repository.findById(26L).orElse(null));
    plates.add(repository.findById(107L).orElse(null));
    when(plateService.all(any())).thenReturn(plates);
    platePrint = RandomStringUtils.randomAlphanumeric(1000);
    when(plateService.print(any(), any())).thenReturn(platePrint);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.plateComponentPanel.getStyleName().contains(PLATE_PANEL));
    assertTrue(design.print.getStyleName().contains(PRINT));
  }

  @Test
  public void caption() {
    presenter.init(view);
    presenter.enter("26");

    Plate plate = repository.findById(26L).orElse(null);
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(plate.getName(), design.plateComponentPanel.getCaption());
    assertEquals(resources.message(PRINT), design.print.getCaption());
  }

  @Test
  public void caption_NoPlate() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals("", design.plateComponentPanel.getCaption());
  }

  @Test
  public void print() throws Throwable {
    presenter.init(view);
    presenter.enter("");

    verify(plateService).print(null, locale);
    assertEquals(1, design.print.getExtensions().size());
    containsInstanceOf(design.print.getExtensions(), BrowserWindowOpener.class);
    BrowserWindowOpener opener =
        (BrowserWindowOpener) design.print.getExtensions().iterator().next();
    assertTrue(opener.getResource() instanceof StreamResource);
    StreamResource resource = (StreamResource) opener.getResource();
    assertEquals(PRINT_MIME, resource.getMIMEType());
    assertEquals(0, resource.getCacheTime());
    assertEquals("plate-print-.html", resource.getStream().getFileName());
    ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
    IOUtils.copy(resource.getStream().getStream(), actualOutput);
    assertEquals(platePrint, new String(actualOutput.toByteArray(), StandardCharsets.UTF_8));
  }

  @Test
  public void print_Plate() throws Throwable {
    presenter.init(view);
    presenter.enter("26");
    final Plate plate = repository.findById(26L).orElse(null);

    verify(plateService).print(plate, locale);
    assertEquals(1, design.print.getExtensions().size());
    containsInstanceOf(design.print.getExtensions(), BrowserWindowOpener.class);
    BrowserWindowOpener opener =
        (BrowserWindowOpener) design.print.getExtensions().iterator().next();
    assertTrue(opener.getResource() instanceof StreamResource);
    StreamResource resource = (StreamResource) opener.getResource();
    assertEquals(PRINT_MIME, resource.getMIMEType());
    assertEquals(0, resource.getCacheTime());
    assertEquals("plate-print-" + plate.getName() + ".html", resource.getStream().getFileName());
    ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
    IOUtils.copy(resource.getStream().getStream(), actualOutput);
    assertEquals(platePrint, new String(actualOutput.toByteArray(), StandardCharsets.UTF_8));
  }

  @Test
  public void print_UpdatePlate() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    presenter.init(view);
    presenter.enter("26");

    verify(plateService).print(plate, locale);
    assertEquals(1, design.print.getExtensions().size());
    containsInstanceOf(design.print.getExtensions(), BrowserWindowOpener.class);
    BrowserWindowOpener opener =
        (BrowserWindowOpener) design.print.getExtensions().iterator().next();
    assertTrue(opener.getResource() instanceof StreamResource);
    StreamResource resource = (StreamResource) opener.getResource();
    assertEquals(PRINT_MIME, resource.getMIMEType());
    assertEquals(0, resource.getCacheTime());
    assertEquals("plate-print-" + plate.getName() + ".html", resource.getStream().getFileName());
    ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
    IOUtils.copy(resource.getStream().getStream(), actualOutput);
    assertEquals(platePrint, new String(actualOutput.toByteArray(), StandardCharsets.UTF_8));
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals("", design.plateComponentPanel.getCaption());
    verify(plateComponent, never()).setValue(any());
  }

  @Test
  public void enter_Plate() {
    presenter.init(view);
    presenter.enter("26");

    Plate plate = repository.findById(26L).orElse(null);
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(plate.getName(), design.plateComponentPanel.getCaption());
    verify(plateComponent).setValue(plate);
  }
}
