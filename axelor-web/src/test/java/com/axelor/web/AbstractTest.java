/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.web;

import com.axelor.test.WebServer;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTest {

  private static WebServer server;

  /**
   * Sets up the test environment by creating a WebServer instance if it doesn't exist, 
   * and starting the server.
   *
   * @param  None
   * @return None
   */
  public static void setUp() {
    if (server == null) {
      server = WebServer.create(new WebTestModule());
    }
    server.start();
  }

  /**
   * Tears down the server after all tests have been executed.
   *
   * This method checks if the server is not null and stops it if it is not.
   *
   * @throws None
   */
  public static void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  /**
   * Returns an Invocation.Builder object for making JSON requests to the specified path.
   *
   * @param  path  the path to the desired resource
   * @return       an Invocation.Builder object for making JSON requests
   */
    return server
        .target()
        .path(path)
        .request(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);
  }
}
