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

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

/**
 * Security configuration.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = SecurityConfiguration.PREFIX)
public class SecurityConfiguration {
  public static final String PREFIX = "security";
  private static final String HEX_BEGIN_TOKEN = "0x";
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
  private String cipherKey;
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
    if (passwords.isEmpty()) {
      logger.error("No password configuration");
      valid = false;
    }
    Set<Integer> versions = new HashSet<>();
    for (int i = 0; i < passwords.size(); i++) {
      PasswordVersion passwordVersion = passwords.get(i);
      int version = passwordVersion.getVersion();
      String algorithm = passwordVersion.getAlgorithm();
      int iterations = passwordVersion.getIterations();
      if (algorithm == null) {
        logger.error("Algorithm undefined for password", algorithm, i);
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

  public List<PasswordVersion> getPasswordVersions() {
    return passwords;
  }

  public PasswordVersion getPasswordVersion() {
    return !passwords.isEmpty() ? passwords.get(0) : null;
  }

  public byte[] getCipherKeyBytes() {
    return toBytes(cipherKey);
  }

  private byte[] toBytes(String value) {
    if (value.startsWith(HEX_BEGIN_TOKEN)) {
      return Hex.decode(value.substring(HEX_BEGIN_TOKEN.length()));
    } else {
      return Base64.decode(value);
    }
  }

  public String getCipherKey() {
    return cipherKey;
  }

  public void setCipherKey(String cipherKey) {
    this.cipherKey = cipherKey;
  }

  public List<PasswordVersion> getPasswords() {
    return passwords;
  }

  public void setPasswords(List<PasswordVersion> passwords) {
    this.passwords = passwords;
  }
}
