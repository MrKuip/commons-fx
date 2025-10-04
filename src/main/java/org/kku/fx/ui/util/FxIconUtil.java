package org.kku.fx.ui.util;

import org.kku.iconify.ui.AbstractIcon.IconSize;
import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcons;
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
    return FxIcons.create(iconName).size(iconSize);
  }
}
