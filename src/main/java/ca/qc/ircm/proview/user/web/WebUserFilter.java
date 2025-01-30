package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import com.vaadin.flow.function.SerializablePredicate;
import java.io.Serial;

/**
 * Serializable {@link UserFilter}.
 */
public class WebUserFilter extends UserFilter implements SerializablePredicate<User> {

  @Serial
  private static final long serialVersionUID = 7763790706767990886L;
}
