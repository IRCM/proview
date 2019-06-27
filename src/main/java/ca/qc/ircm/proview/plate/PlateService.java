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

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.text.MessageResource;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Services for plates.
 */
@Service
@Transactional
public class PlateService {
  public static final String PLATE = "plate";
  @Inject
  private PlateRepository repository;
  @Inject
  private WellRepository wellRepository;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private PlateActivityService plateActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private TemplateEngine emailTemplateEngine;
  @Inject
  private ApplicationConfiguration applicationConfiguration;

  protected PlateService() {
  }

  /**
   * Returns plate with specified id.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject, 'read')")
  public Plate get(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findById(id).orElse(null);
  }

  /**
   * Selects all plates passing filter.
   *
   * @param filter
   *          filters plates
   * @return all plates passing filter
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Plate> all(PlateFilter filter) {
    if (filter == null) {
      filter = new PlateFilter();
    }
    return Lists.newArrayList(repository.findAll(filter.predicate()));
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          plate's name
   * @return true if name is available in database, false otherwise
   */
  @PreAuthorize("hasAuthority('" + USER + "')")
  public boolean nameAvailable(String name) {
    if (name == null) {
      return false;
    }
    User user = authorizationService.getCurrentUser();

    if (authorizationService.hasRole(UserRole.ADMIN)) {
      return repository.countByName(name) == 0;
    } else {
      JPAQuery<Long> query = queryFactory.select(plate.id);
      query.from(plate);
      query.from(submissionSample);
      query.where(plate.name.eq(name));
      query.where(submissionSample.submission.user.eq(user));
      query.where(plate.wells.any().id.eq(submissionSample.originalContainer.id));
      return query.fetchCount() == 0;
    }
  }

  /**
   * Creates an Excel workbook for plate.
   *
   * @param plate
   *          plate
   * @param locale
   *          user's locale
   * @return workbook for plate
   * @throws IOException
   *           could not create workbook
   */
  @CheckReturnValue
  public Workbook workbook(Plate plate, Locale locale) throws IOException {
    if (plate == null) {
      plate = new Plate();
      plate.initWells();
    }
    if (locale == null) {
      locale = Locale.getDefault();
    }
    final MessageResource resources = new MessageResource(PlateService.class, locale);
    Workbook workbook = new XSSFWorkbook(applicationConfiguration.getPlateTemplate());
    Font normalFont = workbook.createFont();
    normalFont.setColor(HSSFColor.BLACK.index);
    CellStyle normalStyle = workbook.createCellStyle();
    normalStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
    normalStyle.setFont(normalFont);
    Font bannedFont = workbook.createFont();
    bannedFont.setColor(HSSFColor.WHITE.index);
    CellStyle bannedStyle = workbook.createCellStyle();
    bannedStyle.setFillBackgroundColor(HSSFColor.RED.index);
    bannedStyle.setFont(bannedFont);
    Sheet sheet = workbook.getSheetAt(0);
    sheet.getRow(0).getCell(0).setCellValue(resources.message(PLATE));
    for (int row = 0; row < plate.getRowCount(); row++) {
      Row wrow = sheet.getRow(row + 1);
      if (wrow == null) {
        wrow = sheet.createRow(row);
      }
      for (int column = 0; column < plate.getColumnCount(); column++) {
        Well well = plate.well(row, column);
        Sample sample = plate.well(row, column).getSample();
        Cell cell = wrow.getCell(column + 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(sample != null ? sample.getName() : "");
        cell.setCellStyle(well.isBanned() ? bannedStyle : normalStyle);
      }
    }
    return workbook;
  }

  /**
   * Returns printable version of plate in HTML.
   *
   * @param plate
   *          plate
   * @param locale
   *          user's locale
   * @return printable version of plate in HTML
   */
  public String print(Plate plate, Locale locale) {
    if (plate == null || locale == null) {
      return "";
    }

    Context context = new Context();
    context.setLocale(locale);
    context.setVariable("plate", plate);
    context.setVariable("locale", locale);

    String templateLocation = "/" + PlateService.class.getName().replace(".", "/") + "_Print.html";
    String content = emailTemplateEngine.process(templateLocation, context);
    return content;
  }

  /**
   * Returns moment of last treatment or analysis made on any sample on plate, whichever is the most
   * recent.
   *
   * @param plate
   *          plate
   * @return moment of last treatment or analysis made on any sample on plate, whichever is the most
   *         recent
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Instant lastTreatmentOrAnalysisDate(Plate plate) {
    if (plate == null) {
      return null;
    }

    Instant treatmentInstant = queryFactory.select(treatment.insertTime.max()).from(treatment)
        .where(treatment.treatedSamples.any().container.in(plate.getWells())
            .or(treatment.treatedSamples.any().destinationContainer.in(plate.getWells())))
        .where(treatment.deleted.eq(false)).fetchFirst();
    Instant analysisInstant = queryFactory.select(msAnalysis.insertTime.max()).from(msAnalysis)
        .where(msAnalysis.acquisitions.any().container.in(plate.getWells()))
        .where(msAnalysis.deleted.eq(false)).fetchFirst();
    return max(treatmentInstant, analysisInstant);
  }

  private Instant max(Instant... instants) {
    return Arrays.asList(instants).stream().filter(instant -> instant != null)
        .sorted((i1, i2) -> i2.compareTo(i1)).findFirst().orElse(null);
  }

  /**
   * Insert plate and it's wells into database.
   *
   * @param plate
   *          plate to insert
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void insert(Plate plate) {
    plate.setInsertTime(Instant.now());
    initWellList(plate);
    repository.saveAndFlush(plate);

    // Log insertion of plate.
    Activity activity = plateActivityService.insert(plate);
    activityService.insert(activity);
  }

  private void initWellList(Plate plate) {
    plate.initWells();
    plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
  }

  /**
   * Update plate.
   *
   * @param plate
   *          plate
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void update(Plate plate) {
    repository.save(plate);

    Optional<Activity> optionalActivity = plateActivityService.update(plate);
    optionalActivity.ifPresent(activity -> activityService.insert(activity));
  }

  /**
   * Bans multiple wells to prevent them from being used. Wells that will be banned are wells that
   * are located from <code>from parameter</code> up to <code>to parameter</code>. If a well was
   * already banned, no change is made to that well.
   *
   * @param plate
   *          plate were wells are located
   * @param from
   *          first well to ban
   * @param to
   *          last well to ban
   * @param explanation
   *          explanation for banning wells
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void ban(Plate plate, WellLocation from, WellLocation to, String explanation) {
    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(true);
    }

    // Log change.
    Activity activity = plateActivityService.ban(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      wellRepository.save(well);
    }
  }

  /**
   * Reactivates multiple wells that were banned. Wells that will be reactivated are wells that are
   * located from <code>from parameter</code> up to <code>to parameter</code>. If a well was not
   * banned, no change is made to that well.
   *
   * @param plate
   *          plate were wells are located
   * @param from
   *          first well to reactivate
   * @param to
   *          last well to reactivate
   * @param explanation
   *          explanation for reactivating wells
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void activate(Plate plate, WellLocation from, WellLocation to, String explanation) {
    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(false);
    }

    // Log change.
    Activity activity = plateActivityService.activate(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      wellRepository.save(well);
    }
  }
}
