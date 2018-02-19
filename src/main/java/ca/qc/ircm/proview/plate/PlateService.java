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

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.plate.QWell.well;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.CheckReturnValue;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Services for plates.
 */
@Service
@Transactional
public class PlateService {
  public static final String PLATE = "plate";
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private PlateActivityService plateActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private ApplicationConfiguration applicationConfiguration;

  protected PlateService() {
  }

  protected PlateService(EntityManager entityManager, JPAQueryFactory queryFactory,
      PlateActivityService plateActivityService, ActivityService activityService,
      AuthorizationService authorizationService,
      ApplicationConfiguration applicationConfiguration) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.plateActivityService = plateActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
    this.applicationConfiguration = applicationConfiguration;
  }

  /**
   * Returns plate with specified id.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  public Plate get(Long id) {
    if (id == null) {
      return null;
    }

    Plate plate = entityManager.find(Plate.class, id);
    authorizationService.checkPlateReadPermission(plate);
    return plate;
  }

  /**
   * Selects all plates passing filter.
   *
   * @param filter
   *          filters plates
   * @return all plates passing filter
   */
  public List<Plate> all(PlateFilter filter) {
    authorizationService.checkAdminRole();

    if (filter == null) {
      filter = new PlateFilter();
    }
    JPAQuery<Plate> query = queryFactory.select(plate);
    query.from(plate);
    if (filter.containsAnySamples != null) {
      query.from(plate.wells, well);
      query.where(well.sample.in(filter.containsAnySamples));
    }
    if (filter.onlyProteomicPlates) {
      query.where(plate.submission.eq(false));
    }
    return query.distinct().fetch();
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          plate's name
   * @return true if name is available in database, false otherwise
   */
  public boolean nameAvailable(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Long> query = queryFactory.select(plate.id);
    query.from(plate);
    query.where(plate.name.eq(name));
    return query.fetchCount() == 0;
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
   * Insert plate and it's wells into database.
   *
   * @param plate
   *          plate to insert
   */
  public void insert(Plate plate) {
    authorizationService.checkAdminRole();

    plate.setInsertTime(Instant.now());
    initWellList(plate);
    entityManager.persist(plate);

    entityManager.flush();
    // Log insertion of plate.
    Activity activity = plateActivityService.insert(plate);
    activityService.insert(activity);
  }

  private void initWellList(Plate plate) {
    plate.initWells();
    plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
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
  public void ban(Plate plate, WellLocation from, WellLocation to, String explanation) {
    authorizationService.checkAdminRole();

    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(true);
    }

    // Log change.
    Activity activity = plateActivityService.ban(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      entityManager.merge(well);
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
  public void activate(Plate plate, WellLocation from, WellLocation to, String explanation) {
    authorizationService.checkAdminRole();

    Collection<Well> wells = plate.wells(from, to);
    for (Well well : wells) {
      well.setBanned(false);
    }

    // Log change.
    Activity activity = plateActivityService.activate(wells, explanation);
    activityService.insert(activity);

    for (Well well : wells) {
      entityManager.merge(well);
    }
  }
}
