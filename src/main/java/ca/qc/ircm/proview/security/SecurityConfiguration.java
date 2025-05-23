package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Security configuration.
 */
@ConfigurationProperties(prefix = SecurityConfiguration.PREFIX)
@EnableMethodSecurity(securedEnabled = true)
public record SecurityConfiguration(int lockAttemps, Duration lockDuration, int disableSignAttemps,
                                    String rememberMeKey, List<PasswordVersion> passwords) {

  public static final String PREFIX = "security";
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

  @UsedBy(SPRING)
  public SecurityConfiguration(int lockAttemps, Duration lockDuration, int disableSignAttemps,
      String rememberMeKey, List<PasswordVersion> passwords) {
    this.lockAttemps = lockAttemps;
    this.lockDuration = lockDuration;
    this.disableSignAttemps = disableSignAttemps;
    this.rememberMeKey = rememberMeKey;
    boolean valid = validatePasswordConfiguration(passwords);
    if (!valid) {
      throw new IllegalStateException("Password configuration is invalid");
    }
    passwords.sort(Comparator.comparingInt(PasswordVersion::version));
    Collections.reverse(passwords);
    this.passwords = passwords;
  }

  private boolean validatePasswordConfiguration(List<PasswordVersion> passwords) {
    boolean valid = true;
    Set<Integer> versions = new HashSet<>();
    for (int i = 0; i < passwords.size(); i++) {
      PasswordVersion passwordVersion = passwords.get(i);
      int version = passwordVersion.version();
      String algorithm = passwordVersion.algorithm();
      int iterations = passwordVersion.iterations();
      Objects.requireNonNull(algorithm, "Algorithm undefined for password " + i);
      if (valid) {
        if (version <= 0) {
          logger.error("Version {} is invalid for password {}", version, i);
          valid = false;
        } else if (!versions.add(version)) {
          logger.error("Version {} already defined in a previous password", algorithm, version);
          valid = false;
        }
        if (iterations <= 0) {
          logger.error("Iterations {} is invalid for password {}", iterations, i);
          valid = false;
        }
        try {
          new SimpleHash(algorithm, "password", "salt", iterations);
        } catch (UnknownAlgorithmException e) {
          logger.error("Algorithm {} is invalid for password {}", algorithm, i);
          valid = false;
        }
      }
    }
    return valid;
  }

  public List<PasswordVersion> passwords() {
    return new ArrayList<>(passwords);
  }
}
