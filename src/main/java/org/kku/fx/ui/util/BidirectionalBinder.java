package org.kku.fx.ui.util;

import org.kku.common.util.AppProperties.AppProperty;
import javafx.beans.property.Property;

public class BidirectionalBinder<T>
{
  private boolean updating = false;

  public void bindBidirectional(Property<T> p1, AppProperty<T> p2)
  {
    p1.setValue(p2.get());

    p1.addListener((obs, oldVal, newVal) -> {
      if (!updating)
      {
        try
        {
          updating = true;
          p2.set(newVal);
        }
        finally
        {
          updating = false;
        }
      }
    });

    p2.addListener((oldVal, newVal) -> {
      if (!updating)
      {
        try
        {
          updating = true;
          p1.setValue(newVal);
        }
        finally
        {
          updating = false;
        }
      }
    });
  }
}
