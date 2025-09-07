package org.kku.fx.ui.util;

import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcon.IconSize;
import javafx.scene.Node;

public class FxIconUtil
{
  private FxIconUtil()
  {
  }

  public static Node createIconNode(String iconName)
  {
    return createIconNode(iconName, IconSize.SMALL);
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
