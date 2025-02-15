package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.PLACEHOLDER;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.englishDatePickerI18n;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.frenchDatePickerI18n;
import static ca.qc.ircm.proview.web.DateRangeField.CLASS_NAME;
import static ca.qc.ircm.proview.web.DateRangeField.FROM;
import static ca.qc.ircm.proview.web.DateRangeField.HELPER;
import static ca.qc.ircm.proview.web.DateRangeField.TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionProperties;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.DateRangeField.Dates;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range.Bound;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link DateRangeField}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class DateRangeFieldTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(DateRangeField.class);
  private DateRangeField dateRange;
  private Locale locale = ENGLISH;

  /**
   * Before each test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    navigate(SubmissionsView.class);
    @SuppressWarnings("unchecked") Grid<Submission> submissions = $(Grid.class).first();
    HeaderRow filtersRow = submissions.getHeaderRows().get(1);
    dateRange = test(
        filtersRow.getCell(submissions.getColumnByKey(SubmissionProperties.DATA_AVAILABLE_DATE))
            .getComponent()).find(DateRangeField.class).first();
  }

  @Test
  public void styles() {
    assertTrue(dateRange.layout.hasClassName(CLASS_NAME));
    assertTrue(dateRange.from.hasClassName(FROM));
    assertTrue(dateRange.from.isClearButtonVisible());
    assertTrue(dateRange.to.hasClassName(TO));
    assertTrue(dateRange.to.isClearButtonVisible());
  }

  @Test
  public void labels() {
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + HELPER), dateRange.getHelperText());
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + property(FROM, PLACEHOLDER)),
        dateRange.from.getPlaceholder());
    validateEquals(englishDatePickerI18n(), dateRange.from.getI18n());
    assertEquals(Locale.CANADA, dateRange.from.getLocale());
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + property(TO, PLACEHOLDER)),
        dateRange.to.getPlaceholder());
    validateEquals(englishDatePickerI18n(), dateRange.to.getI18n());
    assertEquals(Locale.CANADA, dateRange.to.getLocale());
  }

  @Test
  public void localeChange() {
    locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + HELPER), dateRange.getHelperText());
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + property(FROM, PLACEHOLDER)),
        dateRange.from.getPlaceholder());
    validateEquals(frenchDatePickerI18n(), dateRange.from.getI18n());
    assertEquals(Locale.CANADA, dateRange.from.getLocale());
    assertEquals(dateRange.getTranslation(MESSAGES_PREFIX + property(TO, PLACEHOLDER)),
        dateRange.to.getPlaceholder());
    validateEquals(frenchDatePickerI18n(), dateRange.to.getI18n());
    assertEquals(Locale.CANADA, dateRange.to.getLocale());
  }

  @Test
  public void minimumToAfterFromIsSet() {
    LocalDate from = LocalDate.now().minusDays(2);
    dateRange.from.setValue(from);
    assertEquals(from, dateRange.to.getMin());
  }

  @Test
  public void minimumToAfterFromIsCleared() {
    LocalDate from = LocalDate.now().minusDays(2);
    dateRange.from.setValue(from);
    dateRange.from.clear();
    assertNull(dateRange.to.getMin());
  }

  @Test
  public void maximumFromAfterToIsSet() {
    LocalDate to = LocalDate.now().minusDays(2);
    dateRange.to.setValue(to);
    assertEquals(to, dateRange.from.getMax());
  }

  @Test
  public void maximumFromAfterToIsCleared() {
    LocalDate to = LocalDate.now().minusDays(2);
    dateRange.to.setValue(to);
    dateRange.to.clear();
    assertNull(dateRange.from.getMax());
  }

  @Test
  public void validate_FromGreaterThanTo() {
    LocalDate from = LocalDate.now().minusDays(2);
    dateRange.from.setValue(from);
    LocalDate to = LocalDate.now().minusDays(10);
    dateRange.to.setValue(to);
    BinderValidationStatus<Dates> status = dateRange.validateDates();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError = findValidationStatusByField(status,
        dateRange.from);
    assertTrue(optionalError.isPresent());
  }

  @Test
  public void generateModelValue_Empty() {
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.unbounded(), range);
  }

  @Test
  public void generateModelValue_From() {
    LocalDate from = LocalDate.now().minusDays(10);
    dateRange.from.setValue(from);
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.rightUnbounded(Bound.inclusive(from)), range);
  }

  @Test
  public void generateModelValue_To() {
    LocalDate to = LocalDate.now().minusDays(1);
    dateRange.to.setValue(to);
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.leftUnbounded(Bound.inclusive(to)), range);
  }

  @Test
  public void generateModelValue_FromTo() {
    LocalDate from = LocalDate.now().minusDays(10);
    dateRange.from.setValue(from);
    LocalDate to = LocalDate.now().minusDays(1);
    dateRange.to.setValue(to);
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.closed(from, to), range);
  }

  @Test
  public void generateModelValue_FromGreaterThanTo() {
    LocalDate from = LocalDate.now().minusDays(2);
    dateRange.from.setValue(from);
    LocalDate to = LocalDate.now().minusDays(10);
    dateRange.to.setValue(to);
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.leftUnbounded(Bound.inclusive(to)), range);
  }

  @Test
  public void generateModelValue_FromEqualsThan() {
    LocalDate from = LocalDate.now().minusDays(2);
    dateRange.from.setValue(from);
    LocalDate to = LocalDate.now().minusDays(2);
    dateRange.to.setValue(to);
    Range<LocalDate> range = dateRange.generateModelValue();
    assertEquals(Range.just(to), range);
  }

  @Test
  public void setPresentationValue_Empty() {
    dateRange.setPresentationValue(Range.unbounded());
    assertNull(dateRange.from.getValue());
    assertNull(dateRange.to.getValue());
  }

  @Test
  public void setPresentationValue_EmptyAfterOtherValues() {
    LocalDate from = LocalDate.now().minusDays(10);
    dateRange.from.setValue(from);
    LocalDate to = LocalDate.now().minusDays(1);
    dateRange.to.setValue(to);
    dateRange.setPresentationValue(Range.unbounded());
    assertNull(dateRange.from.getValue());
    assertNull(dateRange.to.getValue());
  }

  @Test
  public void setPresentationValue_From() {
    LocalDate from = LocalDate.now().minusDays(10);
    dateRange.setPresentationValue(Range.rightUnbounded(Bound.inclusive(from)));
    assertEquals(from, dateRange.from.getValue());
    assertNull(dateRange.to.getValue());
  }

  @Test
  public void setPresentationValue_To() {
    LocalDate to = LocalDate.now().minusDays(1);
    dateRange.setPresentationValue(Range.leftUnbounded(Bound.inclusive(to)));
    assertNull(dateRange.from.getValue());
    assertEquals(to, dateRange.to.getValue());
  }

  @Test
  public void setPresentationValue_FromTo() {
    LocalDate from = LocalDate.now().minusDays(10);
    LocalDate to = LocalDate.now().minusDays(1);
    dateRange.setPresentationValue(Range.closed(from, to));
    assertEquals(from, dateRange.from.getValue());
    assertEquals(to, dateRange.to.getValue());
  }

  @Test
  public void setPresentationValue_FromEqualsTo() {
    LocalDate date = LocalDate.now().minusDays(2);
    dateRange.setPresentationValue(Range.just(date));
    assertEquals(date, dateRange.from.getValue());
    assertEquals(date, dateRange.to.getValue());
  }

  @Test
  public void addThemeVariants() {
    dateRange.from.getThemeNames().forEach(theme -> dateRange.from.removeThemeName(theme));
    dateRange.to.getThemeNames().forEach(theme -> dateRange.to.removeThemeName(theme));
    assertTrue(dateRange.from.getThemeNames().isEmpty());
    assertTrue(dateRange.to.getThemeNames().isEmpty());
    assertFalse(dateRange.from.hasThemeName(DatePickerVariant.LUMO_SMALL.getVariantName()));
    assertFalse(dateRange.to.hasThemeName(DatePickerVariant.LUMO_SMALL.getVariantName()));
    assertFalse(
        dateRange.from.hasThemeName(DatePickerVariant.LUMO_HELPER_ABOVE_FIELD.getVariantName()));
    assertFalse(
        dateRange.to.hasThemeName(DatePickerVariant.LUMO_HELPER_ABOVE_FIELD.getVariantName()));
    dateRange.addThemeVariants(CustomFieldVariant.LUMO_SMALL,
        CustomFieldVariant.LUMO_HELPER_ABOVE_FIELD, CustomFieldVariant.LUMO_WHITESPACE);
    assertEquals(2, dateRange.from.getThemeNames().size());
    assertEquals(2, dateRange.to.getThemeNames().size());
    assertTrue(dateRange.from.hasThemeName(DatePickerVariant.LUMO_SMALL.getVariantName()));
    assertTrue(dateRange.to.hasThemeName(DatePickerVariant.LUMO_SMALL.getVariantName()));
    assertTrue(
        dateRange.from.hasThemeName(DatePickerVariant.LUMO_HELPER_ABOVE_FIELD.getVariantName()));
    assertTrue(
        dateRange.to.hasThemeName(DatePickerVariant.LUMO_HELPER_ABOVE_FIELD.getVariantName()));
  }

  @Test
  public void removeThemeVariants() {
    dateRange.addThemeVariants(CustomFieldVariant.LUMO_SMALL,
        CustomFieldVariant.LUMO_HELPER_ABOVE_FIELD, CustomFieldVariant.LUMO_WHITESPACE);
    dateRange.removeThemeVariants(CustomFieldVariant.LUMO_SMALL,
        CustomFieldVariant.LUMO_HELPER_ABOVE_FIELD, CustomFieldVariant.LUMO_WHITESPACE);
    assertTrue(dateRange.from.getThemeNames().isEmpty());
    assertTrue(dateRange.to.getThemeNames().isEmpty());
  }
}
