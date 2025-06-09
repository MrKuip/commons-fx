package org.kku.fx.util;

import java.util.Objects;
import org.kku.common.util.AppProperties.AppProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public class FxProperty
{
  private FxProperty()
  {
  }

  public static <T> ObjectProperty<T> property(AppProperty<T> appProperty)
  {
    SimpleObjectProperty<T> sop = new SimpleObjectProperty<T>(appProperty.get());

    appProperty.addListener((_, newValue) -> {
      if (!Objects.equals(newValue, sop.get()))
      {
        sop.set(newValue);
      }
    });
    sop.addListener((_, _, newValue) -> {
      if (!Objects.equals(newValue, appProperty.get()))
      {
        appProperty.set(newValue);
      }
    });

    return sop;
  }

  /*
   * Get a changelistener that will set the value of this property
   * 
   * WATCH OUT: This changelistener cannot be parameterized because for instance a
   * double property expects a Changelistener<? extends Number> and NOT
   * ChangeListener<Double>. This won't even compile! The FX team decided on this
   * because of lots of additional code. Now we are left with the baked pears!
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static <T> ChangeListener getChangeListener(AppProperty<T> appProperty)
  {
    return (_, _, newValue) -> {
      appProperty.set((T) newValue);
    };
  }

}
