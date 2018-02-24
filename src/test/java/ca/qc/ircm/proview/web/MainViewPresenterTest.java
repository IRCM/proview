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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainViewPresenter.ANALYSES;
import static ca.qc.ircm.proview.web.MainViewPresenter.BIOMARKER_SITE;
import static ca.qc.ircm.proview.web.MainViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.MainViewPresenter.RESPONSABILITIES;
import static ca.qc.ircm.proview.web.MainViewPresenter.SERVICES;
import static ca.qc.ircm.proview.web.MainViewPresenter.SERVICES_DESCRIPTION;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGNIN;
import static ca.qc.ircm.proview.web.MainViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class MainViewPresenterTest {
  private MainViewPresenter presenter;
  @Mock
  private MainView view;
  @Mock
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;
  private MainViewDesign design;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(MainView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new MainViewPresenter(authorizationService, applicationName);
    design = new MainViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.servicesDescription.getStyleName().contains(SERVICES_DESCRIPTION));
    assertTrue(design.services.getStyleName().contains(SERVICES));
    assertTrue(design.biomarkerSite.getStyleName().contains(BIOMARKER_SITE));
    assertTrue(design.analyses.getStyleName().contains(ANALYSES));
    assertTrue(design.responsabilities.getStyleName().contains(RESPONSABILITIES));
    assertTrue(design.signin.getStyleName().contains(SIGNIN));
    assertTrue(design.signin.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(SERVICES_DESCRIPTION), design.servicesDescription.getValue());
    assertEquals(ContentMode.HTML, design.servicesDescription.getContentMode());
    assertEquals(resources.message(SERVICES), design.services.getValue());
    assertEquals(ContentMode.HTML, design.services.getContentMode());
    assertEquals(resources.message(BIOMARKER_SITE), design.biomarkerSite.getCaption());
    assertEquals(resources.message(ANALYSES), design.analyses.getValue());
    assertEquals(ContentMode.HTML, design.analyses.getContentMode());
    assertEquals(resources.message(RESPONSABILITIES), design.responsabilities.getValue());
    assertEquals(ContentMode.HTML, design.responsabilities.getContentMode());
    assertEquals(resources.message(SIGNIN), design.signin.getCaption());
  }

  @Test
  public void biomarkerSite() {
    presenter.init(view);
    presenter.enter("");

    ExternalResource resource = (ExternalResource) design.biomarkerSite.getResource();
    assertEquals("https://www.translationalproteomics.ca/biomarker-pipeline", resource.getURL());
  }

  @Test
  public void signin() {
    presenter.init(view);
    presenter.enter("");

    design.signin.click();

    verify(view).navigateTo(SigninView.VIEW_NAME);
  }

  @Test
  public void enter_NotSigned() {
    when(authorizationService.isUser()).thenReturn(false);
    presenter.init(view);
    presenter.enter("");

    verify(view, never()).navigateTo(any());
  }

  @Test
  public void enter_User() {
    when(authorizationService.isUser()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }
}
