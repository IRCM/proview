package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.Sample;

import java.util.List;
import java.util.Locale;

/**
 * Services for activity.
 */
public interface ActivityService {
  /**
   * Returns object linked to activity.
   *
   * @param activity
   *          activity
   * @return object linked to activity
   */
  public Object getRecord(Activity activity);

  /**
   * Selects all activities that matches search parameters. All parameters are optional.
   *
   * @param parameters
   *          search parameters
   * @return all activities that matches search parameters
   */
  public List<Activity> search(ActivitySearchParameters parameters);

  /**
   * Selects all activities of sample's insertion into database. Normally, this should only one
   * activity.
   *
   * @param sample
   *          sample
   * @return all activities of sample's insertion into database
   */
  public List<Activity> allInsertActivities(Sample sample);

  /**
   * Selects all activities of plate's insertion in database.
   *
   * @param plate
   *          plate
   * @return all activities of plate's insertion in database
   */
  public List<Activity> allInsertActivities(Plate plate);

  /**
   * Selects all activities of sample's update in database.
   *
   * @param sample
   *          sample
   * @return all activities of sample's update in database
   */
  public List<Activity> allUpdateActivities(Sample sample);

  /**
   * Selects all activities of plate's spots updates in database.
   *
   * @param plate
   *          plate
   * @return all activities of plate's spots updates in database
   */
  public List<Activity> allUpdateSpotActivities(Plate plate);

  /**
   * Selects treatment activities for sample.
   *
   * @param sample
   *          sample
   * @return treatment activities
   */
  public List<Activity> allTreatmentActivities(Sample sample);

  /**
   * Selects treatment activities for sample.
   *
   * @param plate
   *          plate
   * @return treatment activities
   */
  public List<Activity> allTreatmentActivities(Plate plate);

  /**
   * Selects MS analysis activities for sample.
   *
   * @param sample
   *          sample
   * @return MS analysis activities
   */
  public List<Activity> allMsAnalysisActivities(Sample sample);

  /**
   * Selects MS analysis activities for plate.
   *
   * @param plate
   *          plate
   * @return MS analysis activities
   */
  public List<Activity> allMsAnalysisActivities(Plate plate);

  /**
   * Selects data analysis activities for sample.
   *
   * @param sample
   *          sample
   * @return data analysis activities
   */
  public List<Activity> allDataAnalysisActivities(Sample sample);

  /**
   * Selects all Mascot file histories related to sample.
   *
   * @param sample
   *          sample
   * @return all Mascot file histories related to sample
   */
  public List<Activity> allMascotFileActivities(Sample sample);

  /**
   * Returns description of activity for sample history.
   *
   * @param sample
   *          sample
   * @param activity
   *          activity
   * @param locale
   *          user's locale
   * @return description of activity for sample history
   */
  public String sampleDescription(Sample sample, Activity activity, Locale locale);

  /**
   * Returns description of activity for plate history.
   *
   * @param plate
   *          plate
   * @param activity
   *          activity
   * @param locale
   *          user's locale
   * @return description of activity for plate history
   */
  public String plateDescription(Plate plate, Activity activity, Locale locale);

  /**
   * Insert activity in database.
   *
   * @param activity
   *          activity to log
   */
  public void insert(Activity activity);
}
