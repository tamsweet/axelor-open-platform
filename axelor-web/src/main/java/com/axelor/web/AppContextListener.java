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

import com.axelor.app.AppSettings;
import com.axelor.app.AvailableAppSettings;
import com.axelor.app.internal.AppLogger;
import com.axelor.meta.loader.ViewWatcher;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.SessionCookieConfig;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/** Servlet context listener. 
 * 
 * 
 * Sure, here's a succinct explanation of the AppContextListener class and its methods:

AppContextListener extends GuiceServletContextListener, which is a listener class provided by the Google Guice framework for handling servlet context events.
ResteasyDeployment deployment; - This is a private field that will be used to manage the deployment of a RESTEasy application.
configureRestEasy(ServletContextEvent servletContextEvent) - This method is used to configure the RESTEasy application. It sets up the RESTEasy provider factory, resource method registry, and dispatcher. It also configures the session cookie and sets up the RESTEasy deployment.
configureCookie(ServletContextEvent servletContextEvent) - This method configures the session cookie to be HTTP-only and secure, if specified in the application settings.

*/
public class AppContextListener extends GuiceServletContextListener {

  private ResteasyDeployment deployment;

  private void configureRestEasy(ServletContextEvent servletContextEvent) {
    final ServletContext context = servletContextEvent.getServletContext();
    final ListenerBootstrap config = new ListenerBootstrap(context);

    final Map<Class<?>, Object> map = ResteasyContext.getContextDataMap();
    map.put(ServletContext.class, context);

    final Injector injector = (Injector) context.getAttribute(Injector.class.getName());

    // use custom registry for hotswap-agent support
    final ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
    final ResourceMethodRegistry registry =
        new ResourceMethodRegistry(providerFactory) {

          @Override
          @SuppressWarnings("all")
          public void addPerRequestResource(Class clazz) {
            final Binding<?> binding = injector.getBinding(clazz);
            if (binding == null) {
              super.addPerRequestResource(clazz);
            } else {
              super.addResourceFactory(new GuiceResourceFactory(binding.getProvider(), clazz));
            }
          }
        };

    final Dispatcher dispatcher = new SynchronousDispatcher(providerFactory, registry);

    deployment = config.createDeployment();
    deployment.setProviderFactory(providerFactory);
    deployment.setAsyncJobServiceEnabled(false);
    deployment.setDispatcher(dispatcher);
    deployment.start();

    context.setAttribute(ResteasyDeployment.class.getName(), deployment);

    final ModuleProcessor processor = new ModuleProcessor(registry, providerFactory);

    // process all injectors
    Injector current = injector;
    while (current != null) {
      processor.processInjector(current);
      current = injector.getParent();
    }
  }

  private void configureCookie(ServletContextEvent servletContextEvent) {
    final ServletContext context = servletContextEvent.getServletContext();
    final SessionCookieConfig cookieConfig = context.getSessionCookieConfig();
    cookieConfig.setHttpOnly(true);
    cookieConfig.setSecure(
        AppSettings.get().getBoolean(AvailableAppSettings.SESSION_COOKIE_SECURE, false));
  }

  private void beforeStart(ServletContextEvent servletContextEvent) {
    AppLogger.install();
  }

  private void afterStart(ServletContextEvent servletContextEvent) {
    configureCookie(servletContextEvent);
    configureRestEasy(servletContextEvent);
  }

  private void beforeStop(ServletContextEvent servletContextEvent) {
    ViewWatcher.getInstance().stop();
    servletContextEvent.getServletContext().removeAttribute(ResteasyDeployment.class.getName());
    deployment.stop();
  }

  private void afterStop(ServletContextEvent servletContextEvent) {
    AppLogger.uninstall();
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    this.beforeStart(servletContextEvent);
    super.contextInitialized(servletContextEvent);
    this.afterStart(servletContextEvent);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    this.beforeStop(servletContextEvent);
    super.contextDestroyed(servletContextEvent);
    this.afterStop(servletContextEvent);
  }

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new AppServletModule());
  }
}
