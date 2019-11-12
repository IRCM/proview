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

import ca.qc.ircm.proview.AppResources;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.internal.ReflectTools;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Constants for Web.
 */
public class WebConstants {
  public static final Locale ENGLISH = Locale.CANADA;
  public static final Locale FRENCH = Locale.CANADA_FRENCH;
  public static final Locale DEFAULT_LOCALE = ENGLISH;
  public static final String GENERAL_MESSAGES = "VaadinMessages";
  public static final String APPLICATION_NAME = "application.name";
  public static final String TITLE = "title";
  public static final String THEME = "theme";
  public static final String PRIMARY = "primary";
  public static final String COMPONENTS = "components";
  public static final String FIELD_NOTIFICATION = "field.notification";
  public static final String PLACEHOLDER = "placeholder";
  public static final String REQUIRED = "required";
  public static final String REQUIRED_CAPTION = "required.caption";
  public static final String LABEL_WARNING = "warning";
  public static final String INVALID = "invalid";
  public static final String INVALID_NUMBER = "invalidNumber";
  public static final String INVALID_INTEGER = "invalidInteger";
  public static final String INVALID_EMAIL = "invalidEmail";
  public static final String ALREADY_EXISTS = "alreadyExists";
  public static final String OUT_OF_RANGE = "outOfRange";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String ERROR_TEXT = "error-text";
  public static final String BORDER = "border";
  public static final String RIGHT = "right";
  public static final String CANCEL = "cancel";
  public static final String BUTTON_SKIP_ROW = "skip-row";
  public static final String UPLOAD_STATUS = "uploadStatus";
  public static final String OVER_MAXIMUM_SIZE = "overMaximumSize";
  public static final String BANNED = "banned";
  public static final String ALL = "all";
  public static final String SAVE = "save";
  public static final String ADD = "add";
  public static final String EDIT = "edit";
  public static final String PRINT = "print";
  public static final String UPLOAD = "upload";
  public static final String SAVED_SUBMISSIONS = "savedSubmissions";
  public static final String SAVED_SAMPLES = "savedSamples";
  public static final String SAVED_CONTAINERS = "savedContainers";
  public static final String SAVED_SAMPLE_FROM_MULTIPLE_USERS = "savedSamplesFromMultipleUsers";
  public static final Method VALUE_CHANGE_LISTENER_METHOD =
      ReflectTools.findMethod(ValueChangeListener.class, "valueChanged", ValueChangeEvent.class);

  /**
   * Returns all valid locales for program.
   *
   * @return all valid locales for program
   */
  public static List<Locale> getLocales() {
    List<Locale> locales = new ArrayList<>();
    locales.add(ENGLISH);
    locales.add(FRENCH);
    return locales;
  }

  /**
   * Returns {@link DatePickerI18n} for English.
   *
   * @return {@link DatePickerI18n} for English
   */
  public static DatePickerI18n englishDatePickerI18n() {
    return new DatePickerI18n().setWeek("Week").setCalendar("Calendar").setClear("Clear")
        .setToday("Today").setCancel("Cancel").setFirstDayOfWeek(0)
        .setMonthNames(Arrays.asList("January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"))
        .setWeekdays(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday"))
        .setWeekdaysShort(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
  }

  /**
   * Returns {@link DatePickerI18n} for French.
   *
   * @return {@link DatePickerI18n} for French
   */
  public static DatePickerI18n frenchDatePickerI18n() {
    return new DatePickerI18n().setWeek("Semaine").setCalendar("Calendrier").setClear("Effacer")
        .setToday("Aujourd'hui").setCancel("Annuler").setFirstDayOfWeek(0)
        .setMonthNames(Arrays.asList("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"))
        .setWeekdays(
            Arrays.asList("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"))
        .setWeekdaysShort(Arrays.asList("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"));
  }

  /**
   * Returns {@link UploadI18N} for specified locale. <br>
   * Falls back to English if no instance exists for specified locale.
   *
   * @return {@link UploadI18N} for specified locale, never null
   */
  public static UploadI18N uploadI18N(Locale locale) {
    if (FRENCH.getLanguage().equals(locale.getLanguage())) {
      return frenchUploadI18N();
    }
    return englishUploadI18N();
  }

  /**
   * Returns {@link UploadI18N} for English.
   *
   * @return {@link UploadI18N} for English
   */
  public static UploadI18N englishUploadI18N() {
    final AppResources resources = new AppResources(WebConstants.class, FRENCH);
    return new UploadI18N()
        .setAddFiles(new UploadI18N.AddFiles().setOne("Add file...").setMany("Add files..."))
        .setCancel(resources.message(CANCEL))
        .setDropFiles(
            new UploadI18N.DropFiles().setOne("Drop file here").setMany("Drop files here"))
        .setError(new UploadI18N.Error().setFileIsTooBig("The file is too big")
            .setIncorrectFileType("Wrong file type")
            .setTooManyFiles("Too many files were uploaded"))
        .setUploading(new UploadI18N.Uploading()
            .setError(
                new UploadI18N.Uploading.Error().setForbidden("You are not allowed to upload files")
                    .setServerUnavailable("The server is unavailable")
                    .setUnexpectedServerError("An unexpected error occured on server"))
            .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("Remains: ")
                .setUnknown("Unknown remaining time"))
            .setStatus(new UploadI18N.Uploading.Status().setConnecting("Connecting...")
                .setHeld("Waiting...").setProcessing("Uploading...").setStalled("Stalled...")));
  }

  /**
   * Returns {@link UploadI18N} for French.
   *
   * @return {@link UploadI18N} for French
   */
  public static UploadI18N frenchUploadI18N() {
    final AppResources resources = new AppResources(WebConstants.class, FRENCH);
    return new UploadI18N()
        .setAddFiles(new UploadI18N.AddFiles()
            .setOne("Ajouter un fichier...").setMany("Ajouter fichiers..."))
        .setCancel(resources.message(CANCEL))
        .setDropFiles(new UploadI18N.DropFiles().setOne("Déplacer un fichier ici")
            .setMany("Déplacer des fichiers ici"))
        .setError(new UploadI18N.Error().setFileIsTooBig("Le fichier est trop volumineux")
            .setIncorrectFileType("Le fichier n'est pas du bon type").setTooManyFiles(
                "Trop de fichiers téléchargés"))
        .setUploading(new UploadI18N.Uploading()
            .setError(new UploadI18N.Uploading.Error()
                .setForbidden("Vous n'avez pas la permission de télécharger des fichiers")
                .setServerUnavailable("Le serveur n'est pas disponible")
                .setUnexpectedServerError("Erreur inatendu lors du téléchargement"))
            .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("Il reste ")
                .setUnknown("Temps restant inconnu"))
            .setStatus(new UploadI18N.Uploading.Status().setConnecting("Connexion...")
                .setHeld("En attente...").setProcessing("En cours...").setStalled("Bloqué...")));
  }
}
