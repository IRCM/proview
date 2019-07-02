package ca.qc.ircm.proview.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * User repository.
 */
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
  User findByEmail(String email);

  List<User> findAllByLaboratoryAndManagerTrue(Laboratory laboratory);
}
