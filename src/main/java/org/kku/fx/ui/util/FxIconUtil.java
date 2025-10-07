package org.kku.fx.ui.util;

import org.kku.iconify.ui.AbstractIcon.IconSize;
import org.kku.iconify.ui.FxIcon;
import javafx.scene.Node;

public class FxIconUtil
{
  private FxIconUtil()
  {
  }

  public static Node createIconNode(String iconName)
  {
    return createIconNode(iconName, IconSize.REGULAR);
  }

  public static Node createIconNode(String iconName, IconSize iconSize)
  {
    return createFxIcon(iconName, iconSize).getNode();
  }

  public static FxIcon createFxIcon(String iconName, IconSize iconSize)
  {
    return new FxIcon(iconName).size(iconSize);
  }
}
