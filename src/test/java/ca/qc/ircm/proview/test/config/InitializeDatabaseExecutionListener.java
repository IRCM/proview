package ca.qc.ircm.proview.test.config;

import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestExecutionListener;

/**
 * Initialized test database.
 */
@Order(5001)
public class InitializeDatabaseExecutionListener
    implements TestExecutionListener, InjectDependencies {

  /**
   * Matches pass1.
   */
  public static final String PASSWORD_PASS1 =
      "$2a$10$nGJQSCEj1xlQR/C.nEO8G.GQ4/wUCuGrRKNd0AV3oQp3FwzjtfyAq";
  /**
   * Matches pass2.
   */
  public static final String PASSWORD_PASS2 =
      "$2a$10$JU0aj7Cc/7sWVkFXoHbWTuvVWEAwXFT1EhCX4S6Aa9JfSsKqLP8Tu";
}
