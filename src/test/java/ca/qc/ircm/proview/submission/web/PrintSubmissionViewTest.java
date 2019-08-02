/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SECOND_HEADER;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PrintSubmissionViewTest extends AbstractViewTestCase {
  private PrintSubmissionView view;
  @Mock
  private PrintSubmissionViewPresenter presenter;
  @Autowired
  private PrintSubmission printContent;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(PrintSubmissionView.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new PrintSubmissionView(presenter, printContent);
    view.init();
  }

  @Test
  public void init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SECOND_HEADER, view.secondHeader.getId().orElse(""));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    MessageResource resources = new MessageResource(PrintSubmissionView.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
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
    view.localeChange(mock(LocaleChangeEvent.class));
    Long parameter = 12L;
    Submission submission = new Submission(parameter);
    submission.setService(Service.INTACT_PROTEIN);
    when(presenter.getSubmission()).thenReturn(submission);

    view.setParameter(mock(BeforeEvent.class), parameter);

    verify(presenter).setParameter(parameter);
    assertEquals(Service.INTACT_PROTEIN.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void setParameter_BeforeLocalChange() {
    Long parameter = 12L;
    Submission submission = new Submission(parameter);
    submission.setService(Service.INTACT_PROTEIN);
    when(presenter.getSubmission()).thenReturn(submission);

    view.setParameter(mock(BeforeEvent.class), parameter);
    view.localeChange(mock(LocaleChangeEvent.class));

    verify(presenter).setParameter(parameter);
    assertEquals(Service.INTACT_PROTEIN.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void setParameter_NullId() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Long parameter = 12L;
    Submission submission = new Submission();
    submission.setService(Service.INTACT_PROTEIN);
    when(presenter.getSubmission()).thenReturn(submission);

    view.setParameter(mock(BeforeEvent.class), parameter);

    verify(presenter).setParameter(parameter);
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
  }

  @Test
  public void setParameter_NullParameter() {
    view.localeChange(mock(LocaleChangeEvent.class));

    view.setParameter(mock(BeforeEvent.class), null);

    verify(presenter).setParameter(null);
    assertEquals(Service.LC_MS_MS.getLabel(locale), view.secondHeader.getText());
  }
}
