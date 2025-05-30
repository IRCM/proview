package ca.qc.ircm.proview.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Forgot password repository.
 */
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

  List<ForgotPassword> findByUserEmail(String email);
}
