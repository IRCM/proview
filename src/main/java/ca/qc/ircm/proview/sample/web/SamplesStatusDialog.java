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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.web.WebConstants.CANCEL;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.web.SubmissionDialog;
import ca.qc.ircm.proview.web.SavedEvent;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Samples status dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SamplesStatusDialog extends Dialog
    implements LocaleChangeObserver, NotificationComponent {
  private static final long serialVersionUID = -5878136560444849327L;
  public static final String ID = "samples-status-dialog";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialog.class);
  protected H2 header = new H2();
  protected Grid<SubmissionSample> samples = new Grid<>();
  protected Column<SubmissionSample> name;
  protected Column<SubmissionSample> status;
  protected Button save = new Button();
  protected Button cancel = new Button();
  protected Map<SubmissionSample, ComboBox<SampleStatus>> statusFields = new HashMap<>();
  private SamplesStatusDialogPresenter presenter;

  protected SamplesStatusDialog(SamplesStatusDialogPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    logger.debug("submission dialog");
    setId(ID);
    VerticalLayout layout = new VerticalLayout();
    layout.setMaxWidth("90em");
    layout.setMinWidth("22em");
    add(layout);
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    layout.add(header, samples, buttonsLayout);
    buttonsLayout.add(save, cancel);
    header.addClassName(HEADER);
    samples.addClassName(SAMPLES);
    ValueProvider<SubmissionSample, String> sampleName =
        sample -> Objects.toString(sample.getName(), "");
    name = samples.addColumn(sampleName, NAME).setKey(NAME)
        .setComparator((s1, s2) -> normalize(sampleName.apply(s1))
            .compareToIgnoreCase(normalize(sampleName.apply(s2))));
    status = samples.addColumn(new ComponentRenderer<>(sample -> status(sample)), STATUS)
        .setKey(STATUS).setSortable(false);
    save.addThemeName(SUCCESS);
    save.addClassName(SAVE);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save());
    cancel.addClassName(CANCEL);
    cancel.setIcon(VaadinIcon.CLOSE.create());
    cancel.addClickListener(e -> presenter.cancel());
    presenter.init(this);
  }

  ComboBox<SampleStatus> status(SubmissionSample sample) {
    ComboBox<SampleStatus> status = new ComboBox<>();
    status.addClassName(STATUS);
    status.setItems(SampleStatus.values());
    status.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    statusFields.put(sample, status);
    return status;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources webResources = new AppResources(WebConstants.class, getLocale());
    final AppResources sampleResources = new AppResources(Sample.class, getLocale());
    final AppResources submissionSampleResources =
        new AppResources(SubmissionSample.class, getLocale());
    String nameHeader = sampleResources.message(NAME);
    name.setHeader(nameHeader).setFooter(nameHeader);
    String statusHeader = submissionSampleResources.message(STATUS);
    status.setHeader(statusHeader).setFooter(statusHeader);
    save.setText(webResources.message(SAVE));
    cancel.setText(webResources.message(CANCEL));
    updateHeader();
    presenter.localeChange(getLocale());
  }

  /**
   * Adds listener to be informed when a submission was saved.
   *
   * @param listener
   *          listener
   * @return listener registration
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Registration
      addSavedListener(ComponentEventListener<SavedEvent<SamplesStatusDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
    updateHeader();
  }

  private void updateHeader() {
    final AppResources resources = new AppResources(SamplesStatusDialog.class, getLocale());
    Submission submission = presenter.getSubmission();
    if (submission != null && submission.getId() != null) {
      header.setText(resources.message(HEADER, submission.getExperiment()));
    } else {
      header.setText(resources.message(HEADER));
    }
  }
}
