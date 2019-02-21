package ca.qc.ircm.proview.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Preference repository.
 */
public interface PreferenceRepository
    extends JpaRepository<Preference, Long>, QueryDslPredicateExecutor<Preference> {
  void deleteByRefererAndName(String referer, String name);
}
