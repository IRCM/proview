package ca.qc.ircm.proview.user;

import ca.qc.ircm.proview.text.Strings;
import java.util.function.Predicate;

/**
 * Parameters to search users.
 */
public class UserFilter implements Predicate<User> {

  public String emailContains;
  public String nameContains;
  public Boolean active;
  public String laboratoryNameContains;

  @Override
  public boolean test(User user) {
    boolean test = true;
    if (emailContains != null) {
      String emailContainsNormalized = Strings.normalize(emailContains).toLowerCase();
      String email = Strings.normalize(user.getEmail()).toLowerCase();
      test &= email.contains(emailContainsNormalized);
    }
    if (nameContains != null) {
      String nameContainsNormalized = Strings.normalize(nameContains).toLowerCase();
      String name = Strings.normalize(user.getName()).toLowerCase();
      test &= name.contains(nameContainsNormalized);
    }
    if (active != null) {
      test &= user.isActive() == active;
    }
    if (laboratoryNameContains != null) {
      String laboratoryNameContainsNormalized = Strings.normalize(laboratoryNameContains)
          .toLowerCase();
      String laboratoryName = Strings.normalize(user.getLaboratory().getName()).toLowerCase();
      test &= laboratoryName.contains(laboratoryNameContainsNormalized);
    }
    return test;
  }
}
