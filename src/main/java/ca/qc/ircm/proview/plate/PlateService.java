package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Autowired
  private PlateRepository repository;
  @Autowired
  private WellRepository wellRepository;
  @Autowired
  private JPAQueryFactory queryFactory;
  @Autowired
  private PlateActivityService plateActivityService;
  @Autowired
  private ActivityService activityService;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private TemplateEngine emailTemplateEngine;

  protected PlateService() {
  }

  /**
   * Returns plate with specified id.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<Plate> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Returns submission's plate.
   *
   * @param submission
   *          submission
   * @return submission's plate
   */
  @PreAuthorize("hasPermission(#submission, 'read')")
  public Optional<Plate> get(Submission submission) {
    if (submission == null) {
      return Optional.empty();
    }

    return repository.findBySubmission(submission);
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
    User user = authenticatedUser.getUser().orElse(null);

    if (authenticatedUser.hasRole(UserRole.ADMIN)) {
      return repository.countByName(name) == 0;
    } else {
      JPAQuery<Long> query = queryFactory.select(plate.id);
      query.from(plate);
      query.where(plate.name.eq(name));
      query.where(plate.submission.user.eq(user));
      return query.fetchFirst() == null;
    }
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

    String templateLocation = "/plate/print.html";
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
  public Optional<LocalDateTime> lastTreatmentOrAnalysisDate(Plate plate) {
    if (plate == null) {
      return Optional.empty();
    }

    LocalDateTime treatmentInstant = queryFactory.select(treatment.insertTime.max()).from(treatment)
        .where(treatment.treatedSamples.any().container.in(plate.getWells())
            .or(treatment.treatedSamples.any().destinationContainer.in(plate.getWells())))
        .where(treatment.deleted.eq(false)).fetchFirst();
    LocalDateTime analysisInstant = queryFactory.select(msAnalysis.insertTime.max())
        .from(msAnalysis).where(msAnalysis.acquisitions.any().container.in(plate.getWells()))
        .where(msAnalysis.deleted.eq(false)).fetchFirst();
    return max(treatmentInstant, analysisInstant);
  }

  private Optional<LocalDateTime> max(LocalDateTime... instants) {
    return Arrays.asList(instants).stream().filter(instant -> instant != null)
        .sorted((i1, i2) -> i2.compareTo(i1)).findFirst();
  }

  /**
   * Insert plate and it's wells into database.
   *
   * @param plate
   *          plate to insert
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void insert(Plate plate) {
    plate.setInsertTime(LocalDateTime.now());
    initWellList(plate);
    repository.saveAndFlush(plate);

    // Log insertion of plate.
    Activity activity = plateActivityService.insert(plate);
    activityService.insert(activity);
  }

  private void initWellList(Plate plate) {
    plate.initWells();
    plate.getWells().forEach(well -> well.setTimestamp(LocalDateTime.now()));
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
