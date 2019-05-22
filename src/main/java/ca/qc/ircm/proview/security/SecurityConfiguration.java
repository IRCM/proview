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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security configuration.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = SecurityConfiguration.PREFIX)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
  public static final String PREFIX = "security";
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
  private int lockAttemps;
  private Duration lockDuration;
  private int disableSignAttemps;
  private String rememberMeKey;
  private List<PasswordVersion> passwords;

  @PostConstruct
  protected void processPasswords() {
    boolean valid = validatePasswordConfiguration();
    if (!valid) {
      throw new IllegalStateException("Password configuration is invalid");
    }
    Collections.sort(passwords, new PasswordVersionComparator());
    Collections.reverse(passwords);
  }

  private boolean validatePasswordConfiguration() {
    boolean valid = true;
    Set<Integer> versions = new HashSet<>();
    for (int i = 0; i < passwords.size(); i++) {
      PasswordVersion passwordVersion = passwords.get(i);
      int version = passwordVersion.getVersion();
      String algorithm = passwordVersion.getAlgorithm();
      int iterations = passwordVersion.getIterations();
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

  /**
   * Returns security context.
   *
   * @return security context
   */
  public SecurityContext getSecurityContext() {
    return SecurityContextHolder.getContext();
  }

  public int getLockAttemps() {
    return lockAttemps;
  }

  public void setLockAttemps(int lockAttemps) {
    this.lockAttemps = lockAttemps;
  }

  public Duration getLockDuration() {
    return lockDuration;
  }

  public void setLockDuration(Duration lockDuration) {
    this.lockDuration = lockDuration;
  }

  public int getDisableSignAttemps() {
    return disableSignAttemps;
  }

  public void setDisableSignAttemps(int disableSignAttemps) {
    this.disableSignAttemps = disableSignAttemps;
  }

  public String getRememberMeKey() {
    return rememberMeKey;
  }

  public void setRememberMeKey(String rememberMeKey) {
    this.rememberMeKey = rememberMeKey;
  }

  public List<PasswordVersion> getPasswords() {
    return passwords;
  }

  public void setPasswords(List<PasswordVersion> passwords) {
    this.passwords = passwords;
  }
}
