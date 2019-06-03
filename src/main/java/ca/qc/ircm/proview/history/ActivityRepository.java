package ca.qc.ircm.proview.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Activity repository.
 */
public interface ActivityRepository
    extends JpaRepository<Activity, Long>, QuerydslPredicateExecutor<Activity> {
}
