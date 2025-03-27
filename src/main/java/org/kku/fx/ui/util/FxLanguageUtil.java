package org.kku.fx.ui.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.kku.common.conf.Language;
import org.kku.common.util.Log;

import javafx.scene.image.Image;

public class FxLanguageUtil
{
  private static Map<String, Image> iconByNameMap = new HashMap<>();
  
  private FxLanguageUtil()
  {
  }
  
  public static Image getFlagImage(Language language)
  {
    return iconByNameMap.computeIfAbsent(language.getFlag(), flag -> {
      String flagName;

      flagName = "/flags/" + flag + ".png";
      try (InputStream is = Language.class.getResourceAsStream(flagName))
      {
        return is == null ? null : new Image(is);
      }
      catch (Exception e)
      {
        Log.log.error(e, "Cannot load image " + flagName);
        throw new RuntimeException(e);
      }
    });
  }
}
