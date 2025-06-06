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
    Image image;

    image = iconByNameMap.computeIfAbsent(language.getFlag(), flag -> {
      try (InputStream is = language.getFlagResourceAsStream())
      {
        return ImageUtil.create(is);
      }
      catch (Exception e)
      {
        Log.log.error(e, "Problem with inputstream flag: " + flag);
        return null;
      }
    });

    return image;
  }
}
