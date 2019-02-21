package ca.qc.ircm.proview.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * User preference repository.
 */
public interface UserPreferenceRepository
    extends JpaRepository<UserPreference, Long>, QueryDslPredicateExecutor<UserPreference> {
  void deleteByUserAndPreferenceRefererAndPreferenceName(User user, String referer, String name);
}
