/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

  public SecurityConfiguration(int lockAttemps, Duration lockDuration, int disableSignAttemps,
      String rememberMeKey, List<PasswordVersion> passwords) {
    this.lockAttemps = lockAttemps;
    this.lockDuration = lockDuration;
    this.disableSignAttemps = disableSignAttemps;
    this.rememberMeKey = rememberMeKey;
    passwords = passwords != null ? passwords : new ArrayList<>();
    boolean valid = validatePasswordConfiguration(passwords);
    if (!valid) {
      throw new IllegalStateException("Password configuration is invalid");
    }
    Collections.sort(passwords, (pv1, pv2) -> pv1.version() - pv2.version());
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
      if (algorithm == null) {
        logger.error("Algorithm undefined for password {}", i);
        valid = false;
      }
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
