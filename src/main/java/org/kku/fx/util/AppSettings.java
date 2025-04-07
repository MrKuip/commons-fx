package org.kku.fx.util;

import org.kku.common.util.Converters;
import org.kku.common.util.Converters.Converter;
import org.kku.fx.util.AppProperties.AppPropertyType;

public class AppSettings
{
  private final static String SETTINGS_FILE_NAME = "JDiskUsage.settings";

  public static final AppPropertyType<Double> WIDTH;
  public static final AppPropertyType<Double> HEIGHT;
  public static final AppPropertyType<Double> X;
  public static final AppPropertyType<Double> Y;

  static
  {
    WIDTH = createAppPropertyType("WIDTH", Converters.getDoubleConverter());
    HEIGHT = createAppPropertyType("HEIGHT", Converters.getDoubleConverter());
    X = createAppPropertyType("X", Converters.getDoubleConverter());
    Y = createAppPropertyType("Y", Converters.getDoubleConverter());
  }

  protected AppSettings()
  {
  }

  static public <T> AppPropertyType<T> createAppPropertyType(String name, Converter<T> converter)
  {
    return AppProperties.get(SETTINGS_FILE_NAME).createAppPropertyType(name, converter);
  }
}
