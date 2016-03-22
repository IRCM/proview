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

package ca.qc.ircm.proview.logging;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.ApplicationConfigurationBean;
import ca.qc.ircm.proview.SpringConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.nio.file.Path;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Configures Log4j.
 */
public class Log4jConfiguration implements ServletContextListener {
  private static final String PATTERN = "%d{ISO8601} - %-5p - %m - %c - (%x)%n";
  private static final String MAX_FILE_SIZE = "2024KB";
  private static final int MAX_BACKUP = 2;

  @Override
  public void contextInitialized(ServletContextEvent event) {
    SpringConfiguration springConfiguration = new SpringConfiguration();
    ApplicationConfiguration applicationConfiguration =
        new ApplicationConfigurationBean(null, springConfiguration.configuration());
    Path file = applicationConfiguration.getLogFile();

    RollingFileAppender fileAppender = new RollingFileAppender();
    fileAppender.setMaxFileSize(MAX_FILE_SIZE);
    fileAppender.setMaxBackupIndex(MAX_BACKUP);
    fileAppender.setFile(file.toString());
    fileAppender.setAppend(true);
    fileAppender.setThreshold(Level.INFO);
    fileAppender.setLayout(new PatternLayout(PATTERN));
    fileAppender.activateOptions();

    Path debugFile = file.getParent().resolve(baseName(file) + "-debug.log");
    RollingFileAppender debugFileAppender = new RollingFileAppender();
    debugFileAppender.setMaxFileSize(MAX_FILE_SIZE);
    debugFileAppender.setMaxBackupIndex(MAX_BACKUP);
    debugFileAppender.setFile(debugFile.toString());
    debugFileAppender.setAppend(true);
    debugFileAppender.setLayout(new PatternLayout(PATTERN));
    debugFileAppender.activateOptions();

    ConsoleAppender consoleAppender = new ConsoleAppender();
    consoleAppender.setLayout(new PatternLayout(PATTERN));
    consoleAppender.activateOptions();

    Logger.getLogger("ca.qc.ircm.proview").setLevel(Level.DEBUG);
    Logger.getRootLogger().setLevel(Level.WARN);
    Logger.getRootLogger().removeAllAppenders();
    Logger.getRootLogger().addAppender(fileAppender);
    Logger.getRootLogger().addAppender(debugFileAppender);
    Logger.getRootLogger().addAppender(consoleAppender);
  }

  private String baseName(Path path) {
    String filename = path.getFileName().toString();
    int dotIndex = filename.indexOf('.');
    if (dotIndex >= 0) {
      return filename.substring(dotIndex);
    } else {
      return filename;
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }
}
