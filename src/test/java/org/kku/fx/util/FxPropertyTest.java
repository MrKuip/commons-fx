package org.kku.fx.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.kku.common.util.AppProperties;
import org.kku.common.util.AppProperties.AppProperty;
import org.kku.common.util.AppProperties.AppPropertyType;
import org.kku.common.util.Converters;
import javafx.beans.property.ObjectProperty;

public class FxPropertyTest
{
  public FxPropertyTest()
  {
  }

  @Test
  void test() throws Exception
  {
    AppProperties appProperties;
    AppPropertyType<String> type;
    AppProperty<String> property;
    String subject;
    String propertyKey;
    String propertyValue;
    ObjectProperty<String> fxProperty;

    propertyKey = "Test";
    propertyValue = "Test1";
    subject = "TestSubject";

    appProperties = AppProperties.get(getClass());

    type = appProperties.createAppPropertyType(propertyKey, Converters.getStringConverter());
    property = type.forSubject(subject, propertyValue);
    fxProperty = FxProperty.property(property);

    assertEquals(property.get(), propertyValue);
    assertEquals(fxProperty.get(), propertyValue);

    propertyValue = "Test2";
    property.set(propertyValue);
    assertEquals(property.get(), propertyValue);
    assertEquals(fxProperty.get(), propertyValue);

    propertyValue = "Test3";
    fxProperty.set(propertyValue);
    assertEquals(property.get(), propertyValue);
    assertEquals(fxProperty.get(), propertyValue);
  }
}
