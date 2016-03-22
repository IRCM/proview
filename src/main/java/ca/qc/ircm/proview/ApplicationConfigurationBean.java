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

package ca.qc.ircm.proview;

import ca.qc.ircm.proview.security.PasswordVersion;
import ca.qc.ircm.proview.security.PasswordVersionComparator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Default implementation for {@link ApplicationConfiguration}.
 */
@Component
public class ApplicationConfigurationBean
    implements ApplicationConfiguration, ConfigurationListener {
  private static final String CONFIGURATION_FILE = "proview/proview.ini";
  private static final String APPLICATION_NAME = "proview";
  private static final String REALM_NAME = APPLICATION_NAME;
  private static final String PASSWORD_SECTION = "^password$|^password_.*";
  private static final String DEFAULT_CONFIGURATION_FILE = "/proview.ini";
  private static final String LOG_FILENAME = "proview.log";
  private static final String USER_HOME_PROPERTY = "${user.home}";
  private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigurationBean.class);
  @Resource
  private String configurationFile;
  private HierarchicalINIConfiguration configuration;
  private String home;
  private String userHome;
  private ArrayList<PasswordVersion> passwordVersions = new ArrayList<>();

  public ApplicationConfigurationBean() {
  }

  public ApplicationConfigurationBean(Void notUsed) {
    init();
  }

  public ApplicationConfigurationBean(Void noUsed, String configurationFile) {
    this.configurationFile = configurationFile;
    init();
  }

  @PostConstruct
  protected void init() {
    userHome = System.getProperty("user.home");
    Path configurationFile;
    if (this.configurationFile != null) {
      configurationFile = Paths.get(resolveUserHome(this.configurationFile));
    } else {
      configurationFile = Paths.get(userHome).resolve(CONFIGURATION_FILE);
    }
    home = configurationFile.getParent().toString();
    try {
      if (!Files.exists(configurationFile)) {
        logger.info("No configuration found at {}, copying default configuration",
            configurationFile);
        Path defaultConfiguration =
            Paths.get(getClass().getResource(DEFAULT_CONFIGURATION_FILE).toURI());
        Files.copy(defaultConfiguration, configurationFile);
      }
      logger.info("Loading configuration file {}", configurationFile);
      configuration = new HierarchicalINIConfiguration(configurationFile.toFile());
      configuration.addConfigurationListener(this);
      FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
      configuration.setReloadingStrategy(reloadingStrategy);
      processPasswordVersions();
    } catch (IOException | ConfigurationException | URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void configurationChanged(ConfigurationEvent event) {
    processPasswordVersions();
  }

  private synchronized void processPasswordVersions() {
    ArrayList<PasswordVersion> passwordVersions = new ArrayList<>();
    for (String section : configuration.getSections()) {
      Matcher matcher = Pattern.compile(PASSWORD_SECTION).matcher(section);
      if (matcher.matches()) {
        PasswordVersion passwordVersion = new PasswordVersion();
        passwordVersion.setVersion(configuration.getInt(section + ".version"));
        passwordVersion.setAlgorithm(configuration.getString(section + ".algorithm"));
        passwordVersion.setIterations(configuration.getInt(section + ".iterations"));
        passwordVersions.add(passwordVersion);
      }
    }
    Collections.sort(passwordVersions, new PasswordVersionComparator());
    Collections.reverse(passwordVersions);
    this.passwordVersions = passwordVersions;
  }

  private Path getHome() {
    return Paths.get(home);
  }

  private String resolveUserHome(String value) {
    value = value.replaceAll(Pattern.quote(USER_HOME_PROPERTY), userHome);
    return value;
  }

  private void updateConfigurationIfChanged() {
    configuration.getProperty("test");
  }

  @Override
  public Path getLogFile() {
    return getHome().resolve(LOG_FILENAME);
  }

  @Override
  public String getUrl(String urlEnd) {
    String baseUrl = configuration.getString("main.serverUrl");
    return baseUrl + urlEnd;
  }

  @Override
  public String getRealmName() {
    return REALM_NAME;
  }

  @Override
  public String getCipherKey() {
    return configuration.getString("shiro.cipherKey");
  }

  @Override
  public synchronized PasswordVersion getPasswordVersion() {
    updateConfigurationIfChanged();
    if (!passwordVersions.isEmpty()) {
      return passwordVersions.get(0);
    } else {
      throw new IllegalStateException("No password versions were defined");
    }
  }

  @Override
  public synchronized List<PasswordVersion> getPasswordVersions() {
    updateConfigurationIfChanged();
    return new ArrayList<>(passwordVersions);
  }

  @Override
  public boolean isEmailEnabled() {
    return configuration.getBoolean("mail.enabled");
  }

  @Override
  public String getEmailServer() {
    return configuration.getString("mail.server");
  }

  @Override
  public String getEmailSender() {
    return configuration.getString("mail.sender");
  }

  @Override
  public String getEmailErrorReceiver() {
    return configuration.getString("mail.errorReceiver");
  }

  @Override
  public String getAddress() {
    return configuration.getString("address.address");
  }

  @Override
  public String getTown() {
    return configuration.getString("address.town");
  }

  @Override
  public String getState() {
    return configuration.getString("address.state");
  }

  @Override
  public String getPostalCode() {
    return configuration.getString("address.postalCode");
  }

  @Override
  public String[] getCountries() {
    return configuration.getStringArray("address.country");
  }
}
