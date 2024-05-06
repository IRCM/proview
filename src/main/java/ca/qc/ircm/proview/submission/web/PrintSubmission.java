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

import static ca.qc.ircm.proview.SpotbugsJustifications.CHILD_COMPONENT_EI_EXPOSE_REP;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.web.ByteArrayStreamResourceWriter;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@JsModule("./styles/print-submission-styles.js")
public class PrintSubmission extends VerticalLayout implements LocaleChangeObserver {
  public static final String ID = "print-submission";
  private static final long serialVersionUID = 480796342756791299L;
  private static final Logger logger = LoggerFactory.getLogger(PrintSubmission.class);
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = CHILD_COMPONENT_EI_EXPOSE_REP)
  private Submission submission;
  private transient SubmissionService service;

  @Autowired
  protected PrintSubmission(SubmissionService service) {
    this.service = service;
  }

  @PostConstruct
  void init() {
    setId(ID);
    setPadding(false);
    setSpacing(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    updateSubmission();
  }

  private void updateSubmission() {
    removeAll();
    if (submission != null) {
      String html = service.print(submission, getLocale());
      if (html != null && !html.isEmpty()) {
        for (int i = 0; i < submission.getFiles().size(); i++) {
          SubmissionFile file = submission.getFiles().get(i);
          StreamRegistration streamRegistration =
              VaadinSession.getCurrent().getResourceRegistry().registerResource(new StreamResource(
                  file.getFilename(), new ByteArrayStreamResourceWriter(file.getContent())));
          html = html.replace("href=\"files-" + i + "\"",
              "href=\"" + streamRegistration.getResourceUri() + "\"");
          addDetachListener(e -> streamRegistration.unregister());
        }
        add(new Html(html));
      }
    }
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
    updateSubmission();
  }
}
