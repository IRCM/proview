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
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SECOND_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link PrintSubmissionView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PrintSubmissionViewTest extends SpringUIUnitTest {
  private PrintSubmissionView view;
  @MockBean
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(PrintSubmissionView.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    when(service.get(any())).thenReturn(repository.findById(164L));
    view = navigate(PrintSubmissionView.class, 164L);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SECOND_HEADER, view.secondHeader.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    AppResources resources = new AppResources(PrintSubmissionView.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    Submission submission = repository.findById(33L).get();
    when(service.get(any())).thenReturn(Optional.of(submission));

    view.setParameter(mock(BeforeEvent.class), 33L);

    verify(service).get(33L);
    assertEquals(Service.SMALL_MOLECULE.getLabel(locale), view.secondHeader.getText());
    assertEquals(submission, view.printContent.getSubmission());
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(service.get(any())).thenReturn(Optional.empty());

    view.setParameter(mock(BeforeEvent.class), 35L);

    verify(service).get(35L);
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
    assertNull(view.printContent.getSubmission());
  }

  @Test
  public void setParameter_NullParameter() {
    when(service.get(any())).thenReturn(Optional.empty());

    view.setParameter(mock(BeforeEvent.class), null);

    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
    assertNull(view.printContent.getSubmission());
  }
}
