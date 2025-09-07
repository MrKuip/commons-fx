package org.kku.fx.ui.util;

import org.kku.common.conf.Language;
import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcon.IconSize;
import javafx.scene.Node;

public class FxLanguageUtil
{
  private FxLanguageUtil()
  {
  }

  public static Node getLanguageFlagNode(Language language)
  {
    return new FxIcon(language.getFlag()).size(IconSize.REGULAR).getNode();
  }
}
