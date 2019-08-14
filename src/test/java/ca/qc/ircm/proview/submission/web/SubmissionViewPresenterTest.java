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

import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import java.util.Locale;
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
  private Locale locale = ENGLISH;
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);
  private Submission submission;

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
    view.comment = new TextArea();
    view.lcmsmsSubmissionForm = mock(LcmsmsSubmissionForm.class);
    view.smallMoleculeSubmissionForm = mock(SmallMoleculeSubmissionForm.class);
    view.intactProteinSubmissionForm = mock(IntactProteinSubmissionForm.class);
    submission = repository.findById(34L).orElse(null);
    when(service.get(any())).thenReturn(submission);
    presenter.init(view);
    presenter.localeChange(locale);
  }

  @Test
  public void save() {
  }

  @Test
  public void setParameter() {
    presenter.setParameter(34L);

    verify(service).get(34L);
    verify(view.lcmsmsSubmissionForm).setSubmission(submission);
    verify(view.smallMoleculeSubmissionForm).setSubmission(submission);
    verify(view.intactProteinSubmissionForm).setSubmission(submission);
  }

  @Test
  public void setParameter_Null() {
    presenter.setParameter(null);

    verifyZeroInteractions(service);
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
  }
}
