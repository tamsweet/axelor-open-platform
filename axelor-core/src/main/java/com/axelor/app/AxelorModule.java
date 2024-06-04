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

import com.axelor.ui.QuickMenuCreator;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.Multibinder;

/**
 * The entry-point of application module.
 *
 * <p>Application module can provide an implementation of {@link AxelorModule} to configure {@link
 * Guice} bindings and do some initialization when application starts.
 */

 /*
  * 
  Purpose
This class serves as a base class for building modules within the Axelor framework (or a similar system). It provides convenient methods for defining and registering "quick menu" creators, which are likely components responsible for generating elements in a user interface's quick menu or navigation bar.
Explanation
	1. Abstract Class
		○ public abstract class AxelorModule extends AbstractModule { ... }:
			§ This declares an abstract class named AxelorModule.
			§ It extends another class called AbstractModule, which is presumably provided by the underlying framework to help structure and organize modules.
			§ Being abstract, you cannot create instances of AxelorModule directly; instead, you create concrete subclasses that specialize its behavior.
	2. Multibinder for QuickMenuCreators
		○ protected Multibinder<QuickMenuCreator> quickMenuBinder():
			§ This method creates a Multibinder, a concept from Google Guice (a dependency injection framework).
			§ A Multibinder is used to collect multiple implementations of a particular interface or class.
			§ In this case, it's collecting implementations of the QuickMenuCreator interface. This suggests that there might be multiple ways to create quick menu items.
			§ The binder is obtained using binder(), which is likely a method inherited from AbstractModule for managing bindings within the module.
			§ The method returns the created Multibinder, allowing other methods within the class to access and use it.
	3. Adding Quick Menu Items
		○ protected void addQuickMenu(Class<? extends QuickMenuCreator> quickMenuClass):
			§ This method is designed to be called by subclasses to easily register a QuickMenuCreator implementation.
			§ It takes a Class object representing a class that implements QuickMenuCreator.
			§ quickMenuBinder().addBinding().to(quickMenuClass);:
				□ This line adds a binding to the Multibinder created by quickMenuBinder().
				□ It essentially tells the framework: "Whenever you need a QuickMenuCreator, consider using an instance of quickMenuClass."
How it Works (Example)
Let's imagine a module that wants to add two quick menu items: "Create Customer" and "Generate Report." Here's how it might use AxelorModule:
Java
public class MyModule extends AxelorModule {

@Override
    protected void configure() {
        addQuickMenu(CreateCustomerMenuCreator.class); // Register "Create Customer"
        addQuickMenu(GenerateReportMenuCreator.class); // Register "Generate Report"
    }
}

 Use code with caution.
content_copy
In this example:
	• CreateCustomerMenuCreator and GenerateReportMenuCreator are classes that implement QuickMenuCreator.
	• The configure() method is likely an abstract method from AbstractModule, which you override in your concrete module to define its configuration.
	• By calling addQuickMenu() for each creator class, you tell the framework to include these items in the application's quick menu.

  */
public abstract class AxelorModule extends AbstractModule {

  protected Multibinder<QuickMenuCreator> quickMenuBinder() {
    return Multibinder.newSetBinder(binder(), QuickMenuCreator.class);
  }

  protected void addQuickMenu(Class<? extends QuickMenuCreator> quickMenuClass) {
    quickMenuBinder().addBinding().to(quickMenuClass);
  }
}
