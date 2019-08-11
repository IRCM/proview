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

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionViewTest extends AbstractViewTestCase {
  private SubmissionView view;
  @Mock
  private SubmissionViewPresenter presenter;
  private LcmsmsSubmissionForm lcmsmsSubmissionForm = new LcmsmsSubmissionForm(
      mock(LcmsmsSubmissionFormPresenter.class));
  private SmallMoleculeSubmissionForm smallMoleculeSubmissionForm = new SmallMoleculeSubmissionForm(
      mock(SmallMoleculeSubmissionFormPresenter.class));
  private IntactProteinSubmissionForm intactProteinSubmissionForm = new IntactProteinSubmissionForm(
      mock(IntactProteinSubmissionFormPresenter.class));
  @Mock
  private BeforeEvent beforeEvent;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionView.class, locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SubmissionView(presenter, lcmsmsSubmissionForm, smallMoleculeSubmissionForm,
        intactProteinSubmissionForm);
    view.init();
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
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(SubmissionView.class, locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
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
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    view.setParameter(beforeEvent, 12L);
    verify(presenter).setParameter(12L);
  }
}
