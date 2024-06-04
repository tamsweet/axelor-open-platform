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

import com.axelor.event.Observes;
import com.axelor.events.StartupEvent;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppSettingsObserver {

  private static final Logger log = LoggerFactory.getLogger(AppSettingsObserver.class);

    /**
     * Handles the startup event by checking the application settings and logging a warning if certain settings are enabled.
     *
     * @param  event  the startup event
     */

     /*
      * 
      Purpose



Purpose
This class is designed to monitor specific application settings related to security. During the application's startup, it checks if certain potentially risky settings are enabled. If so, it logs a warning message to alert developers or administrators.
Explanation
	1. Logger Initialization
		○ private static final Logger log = LoggerFactory.getLogger(AppSettingsObserver.class);:
			§ This creates a logger object (log) to record information and warnings.
			§ LoggerFactory.getLogger(AppSettingsObserver.class) configures the logger to associate log messages with the AppSettingsObserver class.
	2. AppSettings Retrieval
		○ final AppSettings settings = AppSettings.get();:
			§ This line likely obtains an instance (settings) of another class called AppSettings. We can infer that AppSettings is responsible for managing and providing access to the application's configuration settings.
	3. Settings to Check
		○ ImmutableList.of(AvailableAppSettings.APPLICATION_PERMISSION_DISABLE_RELATIONAL_FIELD, AvailableAppSettings.APPLICATION_PERMISSION_DISABLE_ACTION):
			§ This creates an immutable list (ImmutableList) containing the names of two specific application settings:
				□ APPLICATION_PERMISSION_DISABLE_RELATIONAL_FIELD
				□ APPLICATION_PERMISSION_DISABLE_ACTION
			§ These setting names are likely defined as constants in another class called AvailableAppSettings. We can guess that these settings control security-related features, and enabling them might have negative consequences.
	4. Iterating and Checking Settings
		○ for (final String setting : ...): This loop iterates over each setting name in the ImmutableList.
			§ settings.getBoolean(setting, false):
				□ For each setting, this line retrieves its boolean value from the AppSettings instance.
				□ The second argument (false) acts as a default value, meaning that if the setting is not found, it's assumed to be false (disabled).
	5. Logging Warnings
		○ if (settings.getBoolean(setting, false)) { ... }: If a setting is found to be enabled (true):
			§ log.warn(...): A warning message is logged using the log object. The message indicates that the specific setting (setting) could break security and should be used with caution. It also mentions that this setting might be removed in the future.
Example Output
If the setting APPLICATION_PERMISSION_DISABLE_ACTION is enabled, you would see a log message like:
"APPLICATION_PERMISSION_DISABLE_ACTION" breaks security. Use with caution and 

      */
    final AppSettings settings = AppSettings.get();

    for (final String setting :
        ImmutableList.of(
            AvailableAppSettings.APPLICATION_PERMISSION_DISABLE_RELATIONAL_FIELD,
            AvailableAppSettings.APPLICATION_PERMISSION_DISABLE_ACTION)) {
      if (settings.getBoolean(setting, false)) {
        log.warn(
            "\"{}\" breaks security. Use with caution and as last resort only. Will be removed in the future.",
            setting);
      }
    }
  }
}
