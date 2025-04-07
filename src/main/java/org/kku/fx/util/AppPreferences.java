package org.kku.fx.util;

import org.kku.common.util.Converters.Converter;
import org.kku.fx.util.AppProperties.AppProperty;

public class AppPreferences
{
  private final static String PREFERENCES_FILE_NAME = "JDiskUsage.preferences";

  static
  {
  }

  protected AppPreferences()
  {
  }

  static public <T> AppProperty<T> createPreference(String name, Converter<T> converter, T defaultValue)
  {
    return AppProperties.get(PREFERENCES_FILE_NAME).createAppPropertyType(name, converter)
        .forSubject(AppPreferences.class, defaultValue);
  }
}
