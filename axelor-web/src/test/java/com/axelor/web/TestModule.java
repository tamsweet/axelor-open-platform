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

import com.axelor.app.AppModule;
import com.axelor.auth.AuthModule;
import com.axelor.db.JpaModule;
import com.axelor.rpc.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

/**
 * Guice module for configuring the test environment.
 * <p>
 * This module is responsible for setting up the bindings and dependencies required
 * for unit testing the application. It binds an {@code ObjectMapper} for JSON
 * serialization/deserialization, installs a {@code JpaModule} to configure the
 * database connection and entities for testing, and installs other modules
 * (e.g., {@code AuthModule}, {@code AppModule}) to provide test-specific
 * implementations or behaviors.
 */
public class TestModule extends AbstractModule {
    /**
     * Configures the bindings for the test module.
     * <p>
     * This method performs the following configurations:
     * <ul>
     *   <li>Binds {@link ObjectMapper} to {@link ObjectMapperProvider}, providing a
     *       configured instance for testing.</li>
     *   <li>Installs a {@link JpaModule} named "testUnit" with specific options
     *       (true for unit testing, false for data persistence), and scans the
     *       specified packages for database entities.</li>
     *   <li>Installs additional modules:
     *     <ul>
     *       <li>{@link AuthModule}: Likely configures authentication/authorization
     *           for the test environment.</li>
     *       <li>{@link AppModule}: Provides general application-specific bindings
     *           and configuration for testing.</li>
     *     </ul>
     *   </li>
     * </ul>
     */
  @Override
  protected void configure() {

    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class);

    install(
        new JpaModule("testUnit", true, false)
            .scan("com.axelor.auth.db")
            .scan("com.axelor.meta.db")
            .scan("com.axelor.dms.db")
            .scan("com.axelor.web.db"));
    install(new AuthModule());
    install(new AppModule());
  }
}
