package ca.qc.ircm.proview.sample;

/**
 * Service class for controls.
 */
public interface ControlService {
  /**
   * Selects control in database.
   *
   * @param id
   *          Database identifier of control
   * @return control in database
   */
  public Control get(Long id);

  /**
   * Inserts a control.
   *
   * @param control
   *          control
   */
  public void insert(Control control);

  /**
   * Updates control information.
   *
   * @param control
   *          control containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(Control control, String justification);
}
