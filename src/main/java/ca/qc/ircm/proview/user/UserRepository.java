package ca.qc.ircm.proview.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * User repository.
 */
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {
  User findByEmail(String email);

  List<User> findAllByLaboratoryAndManagerTrue(Laboratory laboratory);

  long countByValidFalse();

  long countByValidFalseAndLaboratory(Laboratory laboratory);
}
