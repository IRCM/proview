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

package ca.qc.ircm.proview.test.config;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts database for integration tests.
 */
public class IntegrationTestDatabase {
  private static final Logger logger = LoggerFactory.getLogger(IntegrationTestDatabase.class);

  /**
   * Starts and stops database for integration tests.
   *
   * @param args
   *          first argument is "start" or "stop" database select port, second argument is port
   *          number
   * @throws Throwable
   *           database could not be started
   */
  public static void main(String[] args) throws Throwable {
    if (args.length < 1) {
      return;
    }

    if (args[0].equals("start")) {
      if (args.length != 2) {
        return;
      }
      String port = args[1];
      logger.info("Starting H2 database on port {}", port);
      Server.createTcpServer("-tcpPort", port, "-tcpDaemon").start();
    } else if (args[0].equals("stop")) {
      if (args.length != 2) {
        return;
      }
      String port = args[1];
      logger.info("Stoping H2 database at port {}", port);
      Server.shutdownTcpServer("tcp://localhost:" + port, "", false, true);
    }
  }
}
