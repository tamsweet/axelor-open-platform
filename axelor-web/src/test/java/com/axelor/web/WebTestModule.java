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

import com.axelor.web.db.Repository;
import com.axelor.web.service.RestService;
import com.axelor.web.service.ViewService;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Guice module for configuring servlets and filters for web testing.
 * <p>
 * This module sets up the necessary components for testing web-based applications,
 * including:
 * <ul>
 *   <li>Data loading servlet ({@link DataLoaderServlet}) for initializing test data.</li>
 *   <li>Persistence filter ({@link PersistFilter}) for managing transactions during tests.</li>
 *   <li>REST service ({@link RestService}) for handling API requests.</li>
 *   <li>View service ({@link ViewService}) for rendering views (potentially for UI testing).</li>
 *   <li>{@link ObjectMapperResolver}:  Eagerly loaded singleton for resolving object mappers, likely for JSON serialization/deserialization.</li>
 * </ul>
 * <p>
 * Additionally, it installs a {@link TestModule} (not shown here) for providing other
 * test-specific configurations.
 */
public class WebTestModule extends ServletModule {

  /**
   * Servlet for loading test data on application initialization.
   * <p>
   * This servlet is marked as a {@link Singleton}, ensuring only one instance
   * exists. It injects a {@link Repository} (not shown here) and loads data
   * from it when the servlet is initialized.
   */
  @Singleton
  @SuppressWarnings("all") // Suppress warnings related to the repository injection
  public static class DataLoaderServlet extends HttpServlet {

      @Inject
      private Repository repository; // Repository for loading test data

      /**
       * Initializes the servlet by calling the superclass's init method
       * and then loading data from the repository.
       *
       * @throws ServletException if there is an error during initialization.
       */
      @Override
      public void init() throws ServletException {
          super.init();
          repository.load();
      }
  }

  /**
   * Configures servlets and filters for the web test environment.
   * <p>
   * This method performs the following configurations:
   * <ul>
   *   <li>Installs a {@link TestModule} for additional test configurations.</li>
   *   <li>Sets up a {@link PersistFilter} to intercept all requests ("/*").</li>
   *   <li>Binds {@link RestService} and {@link ViewService} to the Guice injector.</li>
   *   <li>Binds {@link ObjectMapperResolver} as an eager singleton.</li>
   *   <li>Maps the "_init" path to the {@link DataLoaderServlet}.</li>
   * </ul>
   */
  @Override
  protected void configureServlets() {
      install(new TestModule());
      filter("*").through(PersistFilter.class);

      bind(RestService.class);
      bind(ViewService.class);

      bind(ObjectMapperResolver.class).asEagerSingleton();

      serve("_init").with(DataLoaderServlet.class);
  }
}