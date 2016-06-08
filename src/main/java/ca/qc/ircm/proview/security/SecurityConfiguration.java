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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

/**
 * Security configuration.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = SecurityConfiguration.PREFIX)
public class SecurityConfiguration {
  public static final String PREFIX = "security";
  public static final String PASSWORD_NAME_PATTERN = "(\\w+)\\.(version|algorithm|iterations)";
  public static final String PASSWORD_VERSION = "version";
  public static final String PASSWORD_ALGORITHM = "algorithm";
  public static final String PASSWORD_ITERATIONS = "iterations";
  private static final String HEX_BEGIN_TOKEN = "0x";
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
  private String cipherKey;
  private Map<String, String> passwords;
  private List<PasswordVersion> passwordVersions;

  @PostConstruct
  protected void processPasswords() {
    passwordVersions = new ArrayList<>();
    Set<String> passwordVersionNames = new HashSet<>();
    Pattern namePattern = Pattern.compile(PASSWORD_NAME_PATTERN);
    for (String key : passwords.keySet()) {
      Matcher matcher = namePattern.matcher(key);
      if (matcher.matches()) {
        passwordVersionNames.add(matcher.group(1));
      }
    }
    boolean valid = validatePasswordConfiguration(passwordVersionNames);
    if (!valid) {
      throw new IllegalStateException("Password configuration is invalid");
    }
    for (String name : passwordVersionNames) {
      PasswordVersion passwordVersion = new PasswordVersion();
      passwordVersion.setVersion(Integer.parseInt(passwords.get(name + "." + PASSWORD_VERSION)));
      passwordVersion.setAlgorithm(passwords.get(name + "." + PASSWORD_ALGORITHM));
      passwordVersion
          .setIterations(Integer.parseInt(passwords.get(name + "." + PASSWORD_ITERATIONS)));
      passwordVersions.add(passwordVersion);
    }
    Collections.sort(passwordVersions, new PasswordVersionComparator());
    Collections.reverse(passwordVersions);
  }

  private boolean validatePasswordConfiguration(Collection<String> passwordVersionNames) {
    boolean valid = true;
    if (passwordVersionNames.isEmpty()) {
      logger.error("No password configuration");
      valid = false;
    }
    for (String name : passwordVersionNames) {
      String version = passwords.get(name + "." + PASSWORD_VERSION);
      String algorithm = passwords.get(name + "." + PASSWORD_ALGORITHM);
      String iterations = passwords.get(name + "." + PASSWORD_ITERATIONS);
      if (algorithm == null) {
        valid = false;
      }
      try {
        Integer.parseInt(version);
      } catch (NumberFormatException e) {
        logger.error("Password version {} is invalid for {}", version, name);
        valid = false;
      }
      try {
        Integer.parseInt(iterations);
      } catch (NumberFormatException e) {
        logger.error("Password iterations {} is invalid for {}", version, name);
        valid = false;
      }
      if (valid) {
        try {
          new SimpleHash(algorithm, "password", "salt", Integer.parseInt(iterations));
        } catch (UnknownAlgorithmException e) {
          logger.error("Password algorithm {} is invalid for {}", algorithm, name);
          valid = false;
        }
      }
    }
    return valid;
  }

  public PasswordVersion getPasswordVersion() {
    return !passwordVersions.isEmpty() ? passwordVersions.get(0) : null;
  }

  public List<PasswordVersion> getPasswordVersions() {
    return passwordVersions;
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

  public Map<String, String> getPasswords() {
    return passwords;
  }

  public void setPasswords(Map<String, String> passwords) {
    this.passwords = passwords;
  }
}
