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
package com.axelor.app;

import com.axelor.event.EventModule;
import com.axelor.inject.Beans;
import com.axelor.inject.logger.LoggerModule;
import com.axelor.meta.MetaScanner;
import com.axelor.meta.db.repo.MetaJsonReferenceUpdater;
import com.axelor.meta.loader.ModuleManager;
import com.axelor.meta.loader.ViewObserver;
import com.axelor.meta.loader.ViewWatcherObserver;
import com.axelor.meta.service.ViewProcessor;
import com.axelor.report.ReportEngineProvider;
import com.axelor.ui.QuickMenuCreator;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The application module scans the classpath and finds all the {@link AxelorModule} and installs
 * them in the dependency order.
 */
public class AppModule extends AbstractModule {

  private static Logger log = LoggerFactory.getLogger(AppModule.class);

  @Override
  protected void configure() {

    // initialize Beans helps
    bind(Beans.class).asEagerSingleton();

    // report engine
    bind(IReportEngine.class).toProvider(ReportEngineProvider.class);

    // Observe changes for views
    bind(ViewObserver.class);

    // Observe updates to fix m2o names in json values
    bind(MetaJsonReferenceUpdater.class);

    // Logger injection support
    install(new LoggerModule());

    // events support
    install(new EventModule());

    // Init QuickMenuCreator
    Multibinder.newSetBinder(binder(), QuickMenuCreator.class);

    // View processor binder
    /*
     * This Java code snippet is using the Guice framework to create a Multibinder that binds multiple instances of the ViewProcessor class. The Multibinder is created using the Multibinder.newSetBinder method, which takes two arguments: the binder object and the type of the binding (ViewProcessor.class in this case). The Multibinder is assigned to the variable viewProcessorBinder. 
     * This allows multiple instances of ViewProcessor to be bound to the same type in the Guice dependency injection framework.
     * 
     */
    final Multibinder<ViewProcessor> viewProcessorBinder =
        Multibinder.newSetBinder(binder(), ViewProcessor.class);

    bind(AppSettingsObserver.class);
    bind(ViewWatcherObserver.class);

    /*
    This Java code snippet is using the Stream API to perform the following operations:
    
    It retrieves a list of module names from ModuleManager.getResolution().
    For each module name, it uses MetaScanner.findSubTypesOf() to find all subtypes of AxelorModule that have the given module name.
    It collects all the subtypes into a list.
    In summary, this code snippet is finding all subtypes of AxelorModule that have a module name from ModuleManager.getResolution().
    */    final List<Class<? extends AxelorModule>> moduleClasses =
        ModuleManager.getResolution().stream()
            .flatMap(name -> MetaScanner.findSubTypesOf(name, AxelorModule.class).find().stream())
            .collect(Collectors.toList());

    if (moduleClasses.isEmpty()) {
      return;
    }

    log.info("Configuring app modules...");
/*
 * 
 * This code snippet iterates over a list of moduleClasses which are subtypes of AxelorModule. 
 * For each module in the list, it tries to create an instance of the module class using its default constructor. 
 * If successful, it logs a debug message indicating that the module is being configured. 
 * If an exception occurs during the instantiation or configuration process, it throws a RuntimeException wrapping the original exception.
 */
    for (Class<? extends AxelorModule> module : moduleClasses) {
      try {
        log.debug("Configure module: {}", module.getName());
        install(module.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    // Configure view processors
/*
 * This Java code snippet is using the Stream API to perform the following operations:
 
 It retrieves a list of module names from ModuleManager.getResolution().
 For each module name, it uses MetaScanner.findSubTypesOf() to find all subtypes of ViewProcessor that have the given module name.
 It collects all the subtypes into a list.
 In summary, this code snippet is finding all subtypes of ViewProcessor that have a module name from ModuleManager.getResolution(). The resulting list is stored in viewProcessorClasses.
 */
    final List<Class<? extends ViewProcessor>> viewProcessorClasses =
        ModuleManager.getResolution().stream()
            .flatMap(name -> MetaScanner.findSubTypesOf(name, ViewProcessor.class).find().stream())
            .collect(Collectors.toList());
/*
 * 
 * This Java code snippet checks if the viewProcessorClasses list is not empty. 
 * If it's not empty, it logs the names of the view processors using a logger. 
 * Then, for each view processor class in the list, it binds the class to the viewProcessorBinder using the addBinding().to() method. 
 * The binding is done to allow the view processors to be injected and used later in the application.
 */
    if (!viewProcessorClasses.isEmpty()) {
      log.atInfo()
          .setMessage("View processors: {}")
          .addArgument(
              () ->
                  viewProcessorClasses.stream()
                      .map(Class::getSimpleName)
                      .collect(Collectors.joining(", ")))
          .log();
      viewProcessorClasses.forEach(
          viewProcessor -> viewProcessorBinder.addBinding().to(viewProcessor));
    }
  }
}
