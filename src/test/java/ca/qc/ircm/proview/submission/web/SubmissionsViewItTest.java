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
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialogElement;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.button.ButtonVariant;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionsView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionsViewItTest extends AbstractTestBenchTestCase {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewItTest.class);
  @Autowired
  private SubmissionRepository repository;
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(SigninView.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(SubmissionsView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
    assertFalse(optional(() -> view.editStatus()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
    assertTrue(optional(() -> view.editStatus()).isPresent());
    assertTrue(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hide() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    Submission submission = repository.findById(164L).get();
    assertTrue(submission.isHidden());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void show() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_SUCCESS.getVariantName()));

    Submission submission = repository.findById(164L).get();
    assertFalse(submission.isHidden());
  }

  @Test
  public void view() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().view(0).click();

    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    assertEquals("POLR3B-Flag", dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void statusDialog() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.SHIFT);

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    AppResources resources = resources(SamplesStatusDialog.class);
    assertEquals(resources.message(SamplesStatusDialog.HEADER, "POLR3B-Flag"),
        dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_Grid() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.ALT);

    assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @Test
  public void add() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.add().click();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.editStatus().click();

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    AppResources resources = resources(SamplesStatusDialog.class);
    assertEquals(resources.message(SamplesStatusDialog.HEADER, "POLR3B-Flag"),
        dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.history().click();

    assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
