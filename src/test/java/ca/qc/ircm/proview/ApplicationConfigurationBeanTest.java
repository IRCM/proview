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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import ca.qc.ircm.proview.security.PasswordVersion;
import ca.qc.ircm.proview.test.config.RetryOnFail;
import ca.qc.ircm.proview.test.config.Rules;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.naming.ConfigurationException;

public class ApplicationConfigurationBeanTest {
  private static final String CONFIGURATION_FILE = "proview/proview.ini";
  private ApplicationConfigurationBean applicationConfigurationBean;
  private String originalUserhome;
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(temporaryFolder);

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() throws Throwable {
    originalUserhome = System.getProperty("user.home");
    System.setProperty("user.home", temporaryFolder.getRoot().getPath());
    Path configuration = Paths.get(getClass().getResource("/test-configuration.ini").toURI());
    replaceConfiguration(configuration);
  }

  @After
  public void afterTest() {
    System.setProperty("user.home", originalUserhome);
  }

  private void replaceConfiguration(Path configuration)
      throws IOException, ConfigurationException, URISyntaxException {
    Path destination = temporaryFolder.getRoot().toPath().resolve(CONFIGURATION_FILE);
    Files.createDirectories(destination.getParent());
    Files.copy(configuration, destination, StandardCopyOption.REPLACE_EXISTING);
    applicationConfigurationBean = new ApplicationConfigurationBean(null);
  }

  private Path getUserHome() {
    return Paths.get(System.getProperty("user.home"));
  }

  private Path getHome() {
    return Paths.get(System.getProperty("user.home")).resolve("proview");
  }

  @Test
  public void getLogFile() {
    assertEquals(getHome().resolve("proview.log"), applicationConfigurationBean.getLogFile());
  }

  @Test
  public void getLogFile_CustomHome() throws Throwable {
    Path configurationSource = Paths.get(getClass().getResource("/test-configuration.ini").toURI());
    Path home = Files.createDirectory(temporaryFolder.getRoot().toPath().resolve("myhome"));
    Path configurationDestination = home.resolve("proview.ini");
    Files.copy(configurationSource, configurationDestination);
    applicationConfigurationBean =
        new ApplicationConfigurationBean(null, configurationDestination.toString());

    assertEquals(getUserHome().resolve("myhome/proview.log"),
        applicationConfigurationBean.getLogFile());
  }

  @Test
  public void getUrl() {
    assertEquals("http://localhost/myurl/subpath?param1=abc",
        applicationConfigurationBean.getUrl("/myurl/subpath?param1=abc"));
  }

  @Test
  public void getRealmName() {
    assertEquals("proview", applicationConfigurationBean.getRealmName());
  }

  @Test
  public void getCipherKey() {
    assertEquals("aBdYTTi4HFsRvEZlFzMXTQ==", applicationConfigurationBean.getCipherKey());
  }

  @Test
  public void getPasswordVersion() throws Throwable {
    PasswordVersion passwordVersion = applicationConfigurationBean.getPasswordVersion();

    assertEquals(4, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
  }

  @Test
  public void getPasswordVersions() throws Throwable {
    List<PasswordVersion> passwordVersions = applicationConfigurationBean.getPasswordVersions();

    assertEquals(3, passwordVersions.size());
    PasswordVersion passwordVersion = passwordVersions.get(0);
    assertEquals(4, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
    passwordVersion = passwordVersions.get(1);
    assertEquals(3, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(1234, passwordVersion.getIterations());
    passwordVersion = passwordVersions.get(2);
    assertEquals(2, passwordVersion.getVersion());
    assertEquals("MD5", passwordVersion.getAlgorithm());
    assertEquals(2120, passwordVersion.getIterations());
  }

  @Test
  public void isEmailEnabled() throws Throwable {
    boolean emailEnabled = applicationConfigurationBean.isEmailEnabled();

    assertEquals(true, emailEnabled);
  }

  @Test
  public void getEmailServer() throws Throwable {
    String emailServer = applicationConfigurationBean.getEmailServer();

    assertEquals("myemailserver.com", emailServer);
  }

  @Test
  public void getEmailSender() throws Throwable {
    String emailSender = applicationConfigurationBean.getEmailSender();

    assertEquals("proview@ircm.qc.ca", emailSender);
  }

  @Test
  public void getEmailErrorReceiver() throws Throwable {
    String emailErrorReceiver = applicationConfigurationBean.getEmailErrorReceiver();

    assertEquals("christian.poitras@ircm.qc.ca", emailErrorReceiver);
  }

  @Test
  public void getAddress() throws Throwable {
    String address = applicationConfigurationBean.getAddress();

    assertEquals("110 avenue des Pins Ouest", address);
  }

  @Test
  public void getAddress_Undefined() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-noaddress.ini").toURI());
    replaceConfiguration(configuration);

    String address = applicationConfigurationBean.getAddress();

    assertEquals(null, address);
  }

  @Test
  public void getTown() throws Throwable {
    String town = applicationConfigurationBean.getTown();

    assertEquals("Montréal", town);
  }

  @Test
  public void getTown_Undefined() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-noaddress.ini").toURI());
    replaceConfiguration(configuration);

    String town = applicationConfigurationBean.getTown();

    assertEquals(null, town);
  }

  @Test
  public void getState() throws Throwable {
    String state = applicationConfigurationBean.getState();

    assertEquals("Québec", state);
  }

  @Test
  public void getState_Undefined() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-noaddress.ini").toURI());
    replaceConfiguration(configuration);

    String state = applicationConfigurationBean.getState();

    assertEquals(null, state);
  }

  @Test
  public void getPostalCode() throws Throwable {
    String postalCode = applicationConfigurationBean.getPostalCode();

    assertEquals("H2W 1R7", postalCode);
  }

  @Test
  public void getPostalCode_Undefined() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-noaddress.ini").toURI());
    replaceConfiguration(configuration);

    String postalCode = applicationConfigurationBean.getPostalCode();

    assertEquals(null, postalCode);
  }

  @Test
  public void getCountries() throws Throwable {
    String[] countries = applicationConfigurationBean.getCountries();

    assertEquals(2, countries.length);
    assertEquals("Canada", countries[0]);
    assertEquals("USA", countries[1]);
  }

  @Test
  public void getCountries_Undefined() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-noaddress.ini").toURI());
    replaceConfiguration(configuration);

    String[] countries = applicationConfigurationBean.getCountries();

    assertEquals(0, countries.length);
  }

  @Test
  public void noConfiguration() throws Throwable {
    Path configuration = temporaryFolder.getRoot().toPath().resolve(CONFIGURATION_FILE);
    Files.delete(configuration);
    assertFalse(Files.exists(configuration));
    applicationConfigurationBean = new ApplicationConfigurationBean(null);

    PasswordVersion passwordVersion = applicationConfigurationBean.getPasswordVersion();

    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(1000, passwordVersion.getIterations());
  }

  @Test
  public void customConfiguration() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-absolute.ini").toURI());
    Path destination = temporaryFolder.getRoot().toPath().resolve("custom-configuration.ini");
    Files.createDirectories(destination.getParent());
    Files.copy(configuration, destination, StandardCopyOption.REPLACE_EXISTING);
    applicationConfigurationBean = new ApplicationConfigurationBean(null, destination.toString());

    PasswordVersion passwordVersion = applicationConfigurationBean.getPasswordVersion();

    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
  }

  @Test
  public void customConfiguration_UserHome() throws Throwable {
    Path configuration =
        Paths.get(getClass().getResource("/test-configuration-absolute.ini").toURI());
    Path destination = temporaryFolder.getRoot().toPath().resolve("custom-configuration.ini");
    Files.createDirectories(destination.getParent());
    Files.copy(configuration, destination, StandardCopyOption.REPLACE_EXISTING);
    applicationConfigurationBean = new ApplicationConfigurationBean(null,
        "${user.home}/" + temporaryFolder.getRoot().toPath().relativize(destination).toString());

    PasswordVersion passwordVersion = applicationConfigurationBean.getPasswordVersion();

    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
  }

  @Test
  @RetryOnFail(3)
  public void reload() throws Throwable {
    Path testConfiguration =
        Paths.get(getClass().getResource("/test-configuration-absolute.ini").toURI());
    Path destination = temporaryFolder.getRoot().toPath().resolve(CONFIGURATION_FILE);
    Thread.sleep(1000);
    Files.copy(testConfiguration, destination, StandardCopyOption.REPLACE_EXISTING);
    Thread.sleep(10000);

    PasswordVersion passwordVersion = applicationConfigurationBean.getPasswordVersion();

    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
  }
}
