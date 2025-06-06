package org.kku.fx.ui.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;

public class ImageUtil
{
  private final static int IMAGE_MAX_BYTES = 1000000;

  private ImageUtil()
  {
  }

  public static Image create(InputStream is) throws IOException
  {
    byte[] bytes;

    if (is != null)
    {
      bytes = is.readNBytes(IMAGE_MAX_BYTES);
      if (bytes.length > 0)
      {
        return new Image(new ByteArrayInputStream(bytes));
      }
    }

    return null;
  }
}
