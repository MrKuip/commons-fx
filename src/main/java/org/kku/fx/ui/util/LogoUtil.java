package org.kku.fx.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.kku.common.util.ResourceLoader;
import javafx.scene.image.Image;

public class LogoUtil
{
  private static LogoUtil instance = new LogoUtil();
  private List<Image> m_logoList;

  private LogoUtil()
  {
  }

  public static void init(Class<?> clazz)
  {
    // instance._init(clazz);
  }

  public static List<Image> getLogoList()
  {
    return instance._getLogoList();
  }

  /**
   * Get a logo that has a size that is closest to the maxSize but not greater
   * than the maxSize
   * 
   * @param maxSize
   * @return
   */
  public static Image getLogo(int maxSize)
  {
    return getLogoList().reversed().stream().filter(image -> image.getHeight() <= maxSize).findFirst().get();
  }

  private List<Image> _getLogoList()
  {
    if (m_logoList == null)
    {
      m_logoList = IntStream.rangeClosed(10, 400).mapToObj(px -> load(px)).filter(Objects::nonNull).toList();
    }

    return instance.m_logoList;
  }

  private Image load(int px)
  {
    String name;

    name = "logo/Logo-" + px + "px.png";
    try (InputStream is = ResourceLoader.getInstance().getResourceAsStream(name))
    {
      return ImageUtil.create(is);
    }
    catch (IOException e)
    {
      return null;
    }
  }
}
