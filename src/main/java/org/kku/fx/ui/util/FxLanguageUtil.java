package org.kku.fx.ui.util;

import org.kku.common.conf.Language;
import org.kku.iconify.ui.AbstractIcon.IconSize;
import org.kku.iconify.ui.FxIcons;
import javafx.scene.Node;

public class FxLanguageUtil
{
  private FxLanguageUtil()
  {
  }

  public static Node getLanguageFlagNode(Language language)
  {
    return FxIcons.create(language.getFlag()).size(IconSize.REGULAR).getNode();
  }
}
