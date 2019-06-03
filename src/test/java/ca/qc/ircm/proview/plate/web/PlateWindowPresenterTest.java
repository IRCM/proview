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

import static ca.qc.ircm.proview.plate.web.PlateWindowPresenter.PRINT;
import static ca.qc.ircm.proview.plate.web.PlateWindowPresenter.PRINT_MIME;
import static ca.qc.ircm.proview.plate.web.PlateWindowPresenter.TITLE;
import static ca.qc.ircm.proview.plate.web.PlateWindowPresenter.WINDOW_STYLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.CloseWindowOnViewChange.CloseWindowOnViewChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateWindowPresenterTest extends AbstractComponentTestCase {
  @Inject
  private PlateWindowPresenter presenter;
  @Inject
  private PlateRepository repository;
  @MockBean
  private PlateService plateService;
  @Mock
  private PlateWindow view;
  @Mock
  private PlateComponent plateComponent;
  private PlateWindowDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(PlateWindow.class, locale);
  private String platePrint;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new PlateWindowDesign();
    view.design = design;
    view.plateComponent = plateComponent;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getUI()).thenReturn(ui);
    platePrint = RandomStringUtils.randomAlphanumeric(1000);
    when(plateService.print(any(), any())).thenReturn(platePrint);
  }

  @Test
  public void styles() {
    presenter.init(view);

    verify(view).addStyleName(WINDOW_STYLE);
    assertTrue(design.print.getStyleName().contains(PRINT));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setCaption(resources.message(TITLE, ""));
    assertEquals(resources.message(PRINT), design.print.getCaption());
  }

  @Test
  public void windowSize() {
    presenter.init(view);

    verify(view).setWidth(any());
    verify(view).setHeight(any());
  }

  @Test
  public void closeWindowOnViewChange() {
    presenter.init(view);

    verify(navigator).addViewChangeListener(any(CloseWindowOnViewChangeListener.class));
  }

  @Test
  public void setValue() {
    Plate plate = repository.findById(26L).orElse(null);
    presenter.init(view);
    presenter.setValue(plate);

    verify(view).setCaption(resources.message(TITLE, ""));
    design.plateLayout.setCaption(plate.getName());
    verify(plateComponent).setValue(plate);
  }

  @Test
  public void print() throws Throwable {
    Plate plate = repository.findById(26L).orElse(null);
    presenter.init(view);
    presenter.setValue(plate);

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
}
